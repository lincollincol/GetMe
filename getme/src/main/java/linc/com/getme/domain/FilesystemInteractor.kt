package linc.com.getme.domain

import io.reactivex.rxjava3.core.Single
import linc.com.getme.device.StorageHelper
import linc.com.getme.utils.StateManager

import java.io.File

internal class FilesystemInteractor(
    private val storageHelper: StorageHelper,
    private val stateManager: StateManager,
    private val getMeSettings: GetMeSettings
) {

    fun execute(): Single<List<FilesystemEntity>> {
        return Single.create {

            if(getMeSettings.path != null) {
                val valid = usePath(getMeSettings.path, getMeSettings.allowBackPath)
                if(valid) {
                    it.onSuccess(openDirectory(
                        File(getMeSettings.path)
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
        return Single.fromCallable {
            stateManager.goTo(filesystemEntity.path)
            openDirectory(File(filesystemEntity.path))
        }
    }

    fun openPreviousFilesystemEntity(): Single<List<FilesystemEntity>> {
        return Single.create {
            stateManager.goBack()
            if(!stateManager.hasState() ) {
                it.onSuccess(emptyList())
            }else if(stateManager.getLast() == "root") {
                val filesystemRootEntities = getDeviceStorage()
                    .filterNotNull()
                    .map {
                        path -> FilesystemEntity.fromPath(path)
                    }
                it.onSuccess(filesystemRootEntities)
            }else {
                it.onSuccess(
                    openDirectory(File(stateManager.getLast()))
                )
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

    private fun openDirectory(directory: File): List<FilesystemEntity> {
        val filesystemEntities = hashSetOf<FilesystemEntity>()

        for (fileEntry in directory.listFiles()) {
            if(getMeSettings.actionType == GetMeSettings.ACTION_SELECT_DIRECTORY && fileEntry.isDirectory) {
                // Add only directories
                filesystemEntities.add(FilesystemEntity.fromFile(fileEntry))
                continue
                // Skip elements if action = select directory
            } else if(getMeSettings.actionType == GetMeSettings.ACTION_SELECT_DIRECTORY && !fileEntry.isDirectory)
                continue

            // If action = select files - check main content or except extensions
            // ! WARNING ! We can use only one function: EXCEPT or MAIN content.
            // In case if user use both - filter main content and ignore except
            //              |                       |
            //              V                       V

            // If user set main content extensions - save directories and all files with this extensions
            if(!getMeSettings.mainContent.isNullOrEmpty()) {
                getMeSettings.mainContent.forEach { extension ->
                    if(extension == fileEntry.extension || fileEntry.isDirectory)
                        filesystemEntities.add(FilesystemEntity.fromFile(fileEntry))
                }
                continue
            }

            // Add file if user don't filter it by extension or directory action
            filesystemEntities.add(FilesystemEntity.fromFile(fileEntry))

            // If user set except content extensions - remove all files with this extension
            if(!getMeSettings.exceptContent.isNullOrEmpty() && getMeSettings.mainContent.isNullOrEmpty()) {
                getMeSettings.exceptContent.forEach { extension ->
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
            // Find root file directory
            if(root != null && path.contains(root)) {
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