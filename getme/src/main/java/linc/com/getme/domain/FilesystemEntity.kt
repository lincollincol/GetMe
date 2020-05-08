package linc.com.getme.domain

import java.io.File

data class FilesystemEntity(
    val path: String = "",
    val title: String = "",
    val extension: String = ""
) {
    companion object {
        fun newInstance(path: String): FilesystemEntity {
            val file = File(path)
            return FilesystemEntity(
                file.path,
                file.name,
                file.extension
            )
        }
    }

}