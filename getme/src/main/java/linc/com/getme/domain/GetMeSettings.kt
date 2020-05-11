package linc.com.getme.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class GetMeSettings(
    val actionType: Int,
    var mainContent: MutableList<String>? = null,
    var exceptContent: MutableList<String>? = null
) : Parcelable {

    fun useContent(extensions: List<String>) {
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
    }

    companion object {
        const val ACTION_SELECT_DIRECTORY = 0
        const val ACTION_SELECT_FILE = 1
    }

}