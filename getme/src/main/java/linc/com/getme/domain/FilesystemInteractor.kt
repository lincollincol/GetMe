package linc.com.getme.domain

import io.reactivex.rxjava3.core.Single
import linc.com.getme.device.StorageHelper

import java.io.File

class FilesystemInteractor(
    private val storageHelper: StorageHelper,
    private val getMeSettings: GetMeSettings
) {

    fun getRoot(): Single<List<FilesystemEntity>> {
        return Single.create {
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
            openDirectory(File(filesystemEntity.path))
        }
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
}
