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

    /**
     * Execute GetMe Lib functional
     * @return list of directories or files from path or root
     * */
    fun execute(): Single<List<FilesystemEntity>> {
        return Single.create {

            // If user use settings - allow it
            if(getMeFilesystemSettings.path != null && getMeFilesystemSettings.path != StateManager.ROOT) {
                val valid = usePath(
                    getMeFilesystemSettings.path,
                    getMeFilesystemSettings.allowBackPath
                )
                // If directory exist and it is not file - open directory
                if(valid) {
                    it.onSuccess(openDirectory(File(getMeFilesystemSettings.path)))
                } else {
                    it.onError(GetMeInvalidPathException())
                }
            }

            // If do not use settings return root directory(-ies)
            it.onSuccess(getDeviceStorage()
                .filterNotNull()
                .map { path -> FilesystemEntity.fromPath(path) }
            )
        }
    }

    /**
     * Update state and open directory
     * @return list of files and directories
     * */
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

    /**
     * Update state and open previous directory
     * @return list of files and directories from previous directory
     * */
    fun openPreviousFilesystemEntity(): Single<List<FilesystemEntity>> {
        return Single.create {
            try {
                // If current directory is not root - open previous directory
                stateManager.goBack()
            } catch (e: Exception) {
                // Throw exception if user press back button when GetMe is closed
                it.onError(GetMeNotFoundException())
            }

            if(!stateManager.hasState()) {
                it.onSuccess(emptyList())
            }else if(stateManager.getLast() == StateManager.ROOT) {
                val filesystemRootEntities = getDeviceStorage()
                    .filterNotNull()
                    .map { path -> FilesystemEntity.fromPath(path) }
                it.onSuccess(filesystemRootEntities)
            }else {
                it.onSuccess(openDirectory(File(stateManager.getLast())))
            }
        }
    }

    /**
     * Prepare result directory. If user use ACTION_SELECT_DIRECTORY and click on okView
     * @return path to selected directory(-ies)
     * */
    fun prepareResultFiles(filesystemEntities: List<FilesystemEntity>): Single<List<File>> {
        return Single.create {
            it.onSuccess(mutableListOf<File>().apply {
                filesystemEntities.forEach { entity ->
                    add(File(entity.path))
                }
            })
        }
    }

    /**
     * Prepare result directory. If user use ACTION_SELECT_DIRECTORY and click on okView
     * @return path to selected directory(-ies)
     * */
    fun prepareResultDirectory(): Single<List<File>> {
        return Single.fromCallable { mutableListOf(File(stateManager.getLast())) }
    }

    /**
     * Save current state manager state to local storage
     * */
    fun saveState() {
        localStorage.saveStack(stateManager.getAllStates())
    }

    /**
     * Restore state manager state (path to directory)
     * @return list of files from restored directory
     * */
    fun restoreState(): Single<List<FilesystemEntity>> {
        return Single.create {
            // Allow back path for restored directory
            usePath(localStorage.getStack().peek(), true)
            // Clear storage to avoid restoring the same state
            localStorage.clearLocalStorage()
            if(stateManager.getLast() == StateManager.ROOT) {
                // Return root directory(-ies) if current state is root
                it.onSuccess(getDeviceStorage()
                    .filterNotNull()
                    .map { path -> FilesystemEntity.fromPath(path) }
                )
            } else {
                // Return list of files from restored directory
                it.onSuccess(openDirectory(
                    File(stateManager.getLast())
                ))
            }
        }
    }

    /**
     * @return all storage available in the device
     * @sample
     *      mount device(isn't removable): /storage/emulated/0
     *      sd card device(removable): /storage/0000-0000/
     * */
    private fun getDeviceStorage() = mutableListOf<String?>().apply {
        // device: /storage/emulated/0
        add(storageHelper.getExternalStoragePath(true))
        // sd card: /storage/0000-0000
        add(storageHelper.getExternalStoragePath(false))
    }

    /**
     * Open directory and sort by extensions if filesystem settings used
     * @param directory - directory that will be opened
     * @return list of files in directory
     * */
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

        // Skip creating back path if root using in the current state
        if(path == StateManager.ROOT) {
            stateManager.apply {
                clear()
                goTo(StateManager.ROOT)
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
            goTo(StateManager.ROOT)
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

    fun retrieveState() = stateManager.getLast()

}