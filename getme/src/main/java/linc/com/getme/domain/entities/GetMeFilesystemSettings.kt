package linc.com.getme.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class GetMeFilesystemSettings(
    internal val actionType: Int,
    internal val mainContent: MutableList<String>? = null,
    internal val exceptContent: MutableList<String>? = null,
    internal val path: String? = null,
    internal val allowBackPath: Boolean = false
) : Parcelable {

    /*fun useContent(extensions: List<String>) {
        if(mainContent == null) {
            mainContent = mutableListOf()
        }
        mainContent?.clear()
        mainContent?.addAll(extensions)
    }

    fun exceptContent(extensions: List<String>) {
        if(exceptContent == null) {
            exceptContent = mutableListOf()
        }
        exceptContent?.clear()
        exceptContent?.addAll(extensions)
    }*/

    companion object {
        const val ACTION_SELECT_DIRECTORY = 0
        const val ACTION_SELECT_FILE = 1
    }

}