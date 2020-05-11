package linc.com.getme.domain

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import linc.com.getme.device.StorageHelper
import linc.com.getme.utils.StateManager

import java.io.File

internal class FilesystemInteractor(
    private val storageHelper: StorageHelper,
    private val stateManager: StateManager,
    private val getMeSettings: GetMeSettings
) {

    fun getRoot(): Single<List<FilesystemEntity>> {
        return Single.create {

            // todo replace with get roots
            // todo replace with get roots
            // todo replace with get roots

            val filesystemEntities = mutableListOf<FilesystemEntity>()

            // storage/emulated/0
            filesystemEntities.add(FilesystemEntity.fromPath(
                storageHelper.getExternalStoragePath(false)!!
            ))

            // sd card: storage/0000-0000
            val sdCard = storageHelper.getExternalStoragePath(true)

            if(sdCard != null) {
                filesystemEntities.add(FilesystemEntity.fromPath(sdCard))
            }

            it.onSuccess(filesystemEntities)
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
                val filesystemRootEntities = getDeviceStorages().filter {
                    path -> path != null
                }.map {
                    path -> FilesystemEntity.fromPath(path!!)
                }
                it.onSuccess(filesystemRootEntities)
            }else {
                it.onSuccess(
                    openDirectory(File(stateManager.getLast()))
                )
            }
        }
    }

    private fun getDeviceStorages() = mutableListOf<String?>().apply {
        add(storageHelper.getExternalStoragePath(false))
        add(storageHelper.getExternalStoragePath(true))
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
                getMeSettings.mainContent!!.forEach { extension ->
                    if(extension == fileEntry.extension || fileEntry.isDirectory)
                        filesystemEntities.add(FilesystemEntity.fromFile(fileEntry))
                }
                continue
            }

            // Add file if user don't filter it by extension or directory action
            filesystemEntities.add(FilesystemEntity.fromFile(fileEntry))

            // If user set except content extensions - remove all files with this extension
            if(!getMeSettings.exceptContent.isNullOrEmpty() && getMeSettings.mainContent.isNullOrEmpty()) {
                getMeSettings.exceptContent!!.forEach { extension ->
                    filesystemEntities.removeAll { file ->
                        file.extension == extension
                    }
                }
            }

        }

        return filesystemEntities.toList()
    }

    private fun usePath(path: String, allowBackPath: Boolean): Single<List<String>> {
        return Single.create {
            val previousDirectories = mutableListOf<String>()
            val file = File(path)

            if(!file.exists())
                it.onError(Exception("Invalid path"))

            if(!allowBackPath)
                it.onSuccess(mutableListOf(path))

            val possibleRoots = mutableListOf<String?>().apply {
                add(storageHelper.getExternalStoragePath(false))
                add(storageHelper.getExternalStoragePath(true))
            }.forEach { root ->
                println(root)
                if(root != null && path.contains(root)) {
                    while(true) {
                        val previousPath = downgradePath(path)
                        previousDirectories.add(previousPath)
                        if(nameFromPath(previousPath) == root) break
                    }
                }

            }

            println(previousDirectories)
        }
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