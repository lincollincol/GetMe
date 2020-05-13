package linc.com.getme.ui.mappers

import linc.com.getme.domain.entities.FilesystemEntity
import linc.com.getme.ui.models.FilesystemEntityModel
import linc.com.getme.utils.DateFormatUtil
import linc.com.getme.utils.SizeUtil
import java.io.File

internal class FilesystemEntityMapper {

    companion object {
        fun toFilesystemModelsList(filesystemEntities: List<FilesystemEntity>) =
            mutableListOf<FilesystemEntityModel>().apply {
                filesystemEntities.forEach {
                        entity -> add(toFilesystemModel(entity))
                }
            }

        fun toFilesystemEntitiesList(filesystemEntityModel: List<FilesystemEntityModel>) =
            mutableListOf<FilesystemEntity>().apply {
                filesystemEntityModel.forEach {
                        model -> add(toFilesystemEntity(model))
                }
            }

        fun toFilesystemEntity(filesystemEntityModel: FilesystemEntityModel) = FilesystemEntity(
            filesystemEntityModel.path,
            filesystemEntityModel.title,
            filesystemEntityModel.extension,
            isDirectory = filesystemEntityModel.isDirectory
        )

        private fun toFilesystemModel(filesystemEntity: FilesystemEntity) = FilesystemEntityModel(
            filesystemEntity.path,
            filesystemEntity.title,
            filesystemEntity.extension,
            DateFormatUtil.formatFromLong(filesystemEntity.lastModified),
            SizeUtil.format(filesystemEntity.size),
            filesystemEntity.path.apply {
                dropLast(length - lastIndexOf(File.separator))
            },
            filesystemEntity.isDirectory
        )
    }

}