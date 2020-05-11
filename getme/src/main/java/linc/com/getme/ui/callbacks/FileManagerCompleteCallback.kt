package linc.com.getme.ui.callbacks

import java.io.File

interface FileManagerCompleteCallback {
    fun onFilesSelected(selectedFiles: List<File>)
}