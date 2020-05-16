package linc.com.getme.ui.views

import linc.com.getme.domain.entities.FilesystemEntity
import linc.com.getme.ui.models.FilesystemEntityModel
import java.io.File


internal interface FilesystemView {

    fun showFilesystemEntities(filesystemEntityModels: List<FilesystemEntityModel>)
    fun closeManager(resultFiles: List<File>)

    fun enableSelection(enable: Boolean)
    fun scrollToTop()
}