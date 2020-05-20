package linc.com.getme.ui.views

import linc.com.getme.ui.models.FilesystemEntityModel
import java.io.File

internal interface FilesystemView {

    fun showFilesystemEntities(filesystemEntityModels: List<FilesystemEntityModel>)
    fun showEmptySign(visibility: Int)
    fun closeManager(resultFiles: List<File>)
    fun scrollToTop()
    fun enableSelection(enable: Boolean, maxSize: Int)
    fun initFilesystemEntitiesAdapter(adapterAnimation: Int, firstOnly: Boolean)

}