package linc.com.getme.ui.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilesystemEntityModel (
    val path: String = "",
    val title: String = "",
    val extension: String = "",
    val lastModified: String = "",
    val size: String = "",
    val parentDirectory: String = "",
    val isDirectory: Boolean = false
) : Parcelable