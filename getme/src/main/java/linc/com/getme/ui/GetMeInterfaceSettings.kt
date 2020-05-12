package linc.com.getme.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class GetMeInterfaceSettings(
    val selectionType: Int = SELECTION_SINGLE
) : Parcelable {




    companion object {
        const val SELECTION_SINGLE = 0
        const val SELECTION_MULTIPLE = 1
        const val SELECTION_MIXED = 2
    }

}