package linc.com.getme.ui.adapters.selection

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.selection.MutableSelection
import kotlinx.android.parcel.Parcelize
import linc.com.getme.domain.entities.FilesystemEntity
import linc.com.getme.ui.models.FilesystemEntityModel

@Parcelize
internal class SelectionState(
    val selectedItems: MutableList<FilesystemEntityModel>
) : Parcelable