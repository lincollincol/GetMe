package linc.com.getme.ui.views

import linc.com.getme.domain.models.FilesystemEntity


internal interface FilesystemView {

    fun showFilesystemEntities(filesystemEntities: List<FilesystemEntity>)
    fun closeManager()


    fun enableSelection(enable: Boolean)
}