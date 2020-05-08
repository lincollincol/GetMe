package linc.com.getme.domain

import io.reactivex.rxjava3.core.Single
import linc.com.getme.device.StorageHelper

import java.io.File

class FilesystemInteractor(
    private val storageHelper: StorageHelper
) {

    fun getRoot(): Single<List<FilesystemEntity>> {
        return Single.create {
            val filesystemEntities = mutableListOf<FilesystemEntity>()

            // storage/emulated/0
            filesystemEntities.add(FilesystemEntity.newInstance(
                storageHelper.getExternalStoragePath(false)!!
            ))

            // sd card: storage/0000-0000
            val sdCard = storageHelper.getExternalStoragePath(true)

            if(sdCard != null) {
                filesystemEntities.add(FilesystemEntity.newInstance(sdCard))
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
        val filesystemEntities = mutableListOf<FilesystemEntity>()

        for (fileEntry in directory.listFiles()) {
            filesystemEntities.add(FilesystemEntity(
                fileEntry.path,
                fileEntry.name,
                fileEntry.extension
            ))
        }

        return filesystemEntities
    }
}
