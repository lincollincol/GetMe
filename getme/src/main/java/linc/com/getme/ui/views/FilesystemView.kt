package linc.com.getme.ui.views

import linc.com.getme.domain.FilesystemEntity


interface FilesystemView {
    fun showFilesystemEntities(filesystemEntities: List<FilesystemEntity>)
    fun showError(message: String)
    fun closeManager()
}