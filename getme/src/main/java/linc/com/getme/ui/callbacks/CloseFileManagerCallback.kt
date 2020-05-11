package linc.com.getme.ui.callbacks

interface CloseFileManagerCallback {
    var fileManagerBackListener: FileManagerBackListener
    fun onCloseFileManager()
}