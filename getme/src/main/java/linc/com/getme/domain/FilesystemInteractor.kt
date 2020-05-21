package linc.com.getme.domain

import io.reactivex.rxjava3.core.Single
import linc.com.getme.domain.entities.FilesystemEntity
import java.io.File

internal interface FilesystemInteractor {

    fun execute(): Single<List<FilesystemEntity>>
    fun openFilesystemEntity(filesystemEntity: FilesystemEntity): Single<List<FilesystemEntity>>
    fun openPreviousFilesystemEntity(): Single<List<FilesystemEntity>>

    fun prepareResultFiles(filesystemEntities: List<FilesystemEntity>): Single<List<File>>
    fun prepareResultDirectory(): Single<List<File>>

    fun saveState()
    fun restoreState(): Single<List<FilesystemEntity>>

    fun retrieveState(): String
}