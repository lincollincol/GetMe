package linc.com.getme.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class GetMeFilesystemSettings(
    internal val actionType: Int,
    internal val mainContent: MutableList<String>? = null,
    internal val exceptContent: MutableList<String>? = null,
    internal val path: String? = null,
    internal var allowBackPath: Boolean = false
) : Parcelable {

    companion object {
        const val ACTION_SELECT_DIRECTORY = 0
        const val ACTION_SELECT_FILE = 1
    }

}