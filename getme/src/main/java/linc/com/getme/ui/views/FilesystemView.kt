package linc.com.getme.ui.views

import linc.com.getme.domain.FilesystemEntity


internal interface FilesystemView {
    fun showFilesystemEntities(filesystemEntities: List<FilesystemEntity>)
    fun closeManager()
}