package linc.com.getme.ui.adapters.selection

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import linc.com.getme.ui.models.FilesystemEntityModel

@Parcelize
internal class SelectionState(
    val selectedItems: MutableList<FilesystemEntityModel>
) : Parcelable