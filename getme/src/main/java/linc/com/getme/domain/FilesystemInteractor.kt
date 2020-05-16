package linc.com.getme.domain

import android.content.Context
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import linc.com.getme.data.preferences.LocalPreferences
import linc.com.getme.device.StorageHelper
import linc.com.getme.domain.entities.FilesystemEntity
import linc.com.getme.domain.entities.GetMeFilesystemSettings
import linc.com.getme.domain.utils.GetMeInvalidPathException
import linc.com.getme.domain.utils.GetMeNotFoundException
import linc.com.getme.domain.utils.StateManager
import linc.com.getme.ui.models.FilesystemEntityModel

import java.io.File

internal class FilesystemInteractor(
    private val storageHelper: StorageHelper,
    private val stateManager: StateManager,
    private val getMeFilesystemSettings: GetMeFilesystemSettings,
    private val localStorage: LocalFastStorage
) {

    fun execute(): Single<List<FilesystemEntity>> {
        return Single.create {
            if(getMeFilesystemSettings.path != null && getMeFilesystemSettings.path != "root") {
                val valid = usePath(getMeFilesystemSettings.path, getMeFilesystemSettings.allowBackPath)
                if(valid) {
                    it.onSuccess(openDirectory(
                        File(getMeFilesystemSettings.path)
                    ))
                } else {
                    it.onError(GetMeInvalidPathException())
                }
            }

            it.onSuccess(
                getDeviceStorage()
                    .filterNotNull()
                    .map { path -> FilesystemEntity.fromPath(path) }
            )
        }
    }

    fun openFilesystemEntity(filesystemEntity: FilesystemEntity): Single<List<FilesystemEntity>> {
        return Single.create {
            val directoryContent = openDirectory(File(filesystemEntity.path))

            // Prevent opening files
            if(directoryContent != null) {
                stateManager.goTo(filesystemEntity.path)
                it.onSuccess(directoryContent)
            }

        }
    }

    fun openPreviousFilesystemEntity(): Single<List<FilesystemEntity>> {
        return Single.create {
            try {
                stateManager.goBack()
            } catch (e: Exception) {
                it.onError(GetMeNotFoundException())
            }

            if(!stateManager.hasState()) {
                it.onSuccess(emptyList())
            }else if(stateManager.getLast() == "root") {
                val filesystemRootEntities = getDeviceStorage()
                    .filterNotNull()
                    .map {
                        path -> FilesystemEntity.fromPath(path)
                    }
                it.onSuccess(filesystemRootEntities)
            }else {
                it.onSuccess(openDirectory(File(stateManager.getLast())))
            }
        }
    }

    fun prepareResultFiles(filesystemEntities: List<FilesystemEntity>): Single<List<File>> {
        return Single.create {
            val resultFiles = mutableListOf<File>()

            filesystemEntities.forEach { entity ->
                resultFiles.add(File(entity.path))
            }

            it.onSuccess(resultFiles)
        }
    }

    fun prepareResultDirectory(): Single<List<File>> {
        return Single.fromCallable { mutableListOf(File(stateManager.getLast())) }
    }

    fun saveState() {
        localStorage.saveStack(stateManager.getAllStates())
    }

    fun restoreState(): Single<List<FilesystemEntity>> {
        return Single.create {
            usePath(localStorage.getStack().peek(), true)
            localStorage.clearLocalStorage()
            if(stateManager.getLast() == "root") {
                it.onSuccess(
                    getDeviceStorage()
                        .filterNotNull()
                        .map { path -> FilesystemEntity.fromPath(path) }
                )
            } else {
                it.onSuccess(openDirectory(
                    File(stateManager.getLast())
                ))
            }
        }
    }

    /**
     * @return all storage available in the device
     * @sample
     *      ext device(isn't removable): /storage/emulated/0
     *      sd card device(removable): /storage/0000-0000/
     * */
    private fun getDeviceStorage() = mutableListOf<String?>().apply {
        // device: /storage/emulated/0
        add(storageHelper.getExternalStoragePath(true))
        // sd card: /storage/0000-0000
        add(storageHelper.getExternalStoragePath(false))
    }

    private fun openDirectory(directory: File): List<FilesystemEntity>? {
        val filesystemEntities = hashSetOf<FilesystemEntity>()

        // Return null if user try to open file. Manager open only directories
        if(directory.isFile) {
            return null
        }

        for (fileEntry in directory.listFiles()) {
            if(getMeFilesystemSettings.actionType == GetMeFilesystemSettings.ACTION_SELECT_DIRECTORY && fileEntry.isDirectory) {
                // Add only directories
                filesystemEntities.add(FilesystemEntity.fromFile(fileEntry))
                continue
                // Skip elements if action = select directory
            } else if(getMeFilesystemSettings.actionType == GetMeFilesystemSettings.ACTION_SELECT_DIRECTORY && !fileEntry.isDirectory)
                continue


            // If action = select files - check main content or except extensions
            // ! WARNING ! We can use only one function: EXCEPT or MAIN content.
            // In case if user use both - filter main content and ignore except
            //              |                       |
            //              V                       V

            // If user set main content extensions - save directories and all files with this extensions
            if(!getMeFilesystemSettings.mainContent.isNullOrEmpty()) {
                getMeFilesystemSettings.mainContent.forEach { extension ->
                    if(extension == fileEntry.extension || fileEntry.isDirectory)
                        filesystemEntities.add(FilesystemEntity.fromFile(fileEntry))
                }
                continue
            }

            // Add file if user don't filter it by extension or directory action
            filesystemEntities.add(FilesystemEntity.fromFile(fileEntry))

            // If user set except content extensions - remove all files with this extension
            if(!getMeFilesystemSettings.exceptContent.isNullOrEmpty() && getMeFilesystemSettings.mainContent.isNullOrEmpty()) {
                getMeFilesystemSettings.exceptContent.forEach { extension ->
                    filesystemEntities.removeAll { file ->
                        file.extension == extension
                    }
                }
            }

        }

        return filesystemEntities.toList()
    }

    /**
     * @param path - path to directory
     * @param allowBackPath - flag that allow to use previous directories. State manager will be rebuilt if true
     * @return boolean that allow to use path
     * @sample
     *      path is valid: return true and rebuilt state manager if user set true to allowBackPath
     *      path is invalid: return false emmit onError with mistake message
     * */
    private fun usePath(path: String, allowBackPath: Boolean): Boolean {


        if(path == "root") {
            stateManager.apply {
                clear()
                goTo("root")
            }
            return true
        }

        val directory = File(path)

        // Check if path is valid
        if(!directory.exists())
            return false

        // Check that this is a directory
        if(directory.isFile)
            return false

        // Allow to use this path if it is valid and user don't need back path
        if(!allowBackPath)
            return true

        // If user need back path - rebuild back stack in state manager
        stateManager.clear()
        getDeviceStorage().forEach { root ->
            if(root != null && path == root) {
                // Add sd or mounted storage path if it equals to root
                stateManager.goTo(path)
            }else if(root != null && path.contains(root)) { // Find root file directory
                // Add previous directories to state manager
                var previousPath: String = path
                stateManager.goTo(path)
                while(true) {
                    previousPath = downgradePath(previousPath)
                    stateManager.goTo(previousPath)
                    if(root.contains(nameFromPath(previousPath))) break
                }
            }
        }

        // Add root and reverse state manager stack because current state is reversed
        stateManager.apply {
            goTo("root")
            reverse()
        }
        // Allow to use path with back path
        return true
    }

    /**
     * @param path - path to file or directory. Example storage/emulated/0/dir_one
     * @return file name.
     * @sample
     *      input: storage/emulated/0/dir_one
     *      output: dir_one
     * */
    private fun nameFromPath(path: String) = path.apply {
        return drop(lastIndexOf(File.separator) + 1)
    }

    /**
     * @param path - path to file or directory. Example storage/emulated/0/dir_one
     * @return previous directory path.
     * @sample
     *      input: storage/emulated/0/dir_one
     *      output: storage/emulated/0
     * */
    private fun downgradePath(path: String) = path.apply {
        return dropLast(length - lastIndexOf(File.separator))
    }

}