package linc.com.getme.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class FilesystemEntity(
    val path: String = "",
    val title: String = "",
    val extension: String = "",
    val isDirectory: Boolean = false
) : Parcelable {

    companion object {
        fun fromPath(path: String): FilesystemEntity {
            val file = File(path)
            return fromFile(file)
        }

        fun fromFile(file: File) = FilesystemEntity(
            file.path,
            file.name,
            file.extension,
            file.isDirectory
        )
    }

}