package linc.com.getme.ui.fragments

interface CloseFileManagerCallback {
    var filesystemBackListener: FilesystemBackListener
    fun onCloseFileManager()
}