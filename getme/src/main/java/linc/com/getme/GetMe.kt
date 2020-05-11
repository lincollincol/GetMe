package linc.com.getme

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.LogPrinter
import android.view.View
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import linc.com.getme.domain.GetMeSettings
import linc.com.getme.ui.activities.GetMeActivity
import linc.com.getme.ui.callbacks.CloseFileManagerCallback
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.fragments.GetMeFragment
import linc.com.getme.utils.Constants.Companion.KEY_SETTINGS
import kotlin.concurrent.fixedRateTimer

class GetMe <T : CloseFileManagerCallback> (
    private val fragmentManager: FragmentManager,
    private val fragmentContainer: Int,
    private val getMeSettings: GetMeSettings,
    private val closeFileManagerCallback: CloseFileManagerCallback,
    private val fileManagerCompleteCallback: FileManagerCompleteCallback,
    private val parentComponent: T,
    private val okView: View? = null
) {

    fun show() {
        fragmentManager.beginTransaction()
            .replace(
                fragmentContainer,
                GetMeFragment.newInstance(Bundle().apply {
                    putParcelable(KEY_SETTINGS, getMeSettings)
                }).apply {
                    setCloseFileManagerCallback(closeFileManagerCallback)
                    setFileManagerCompleteCallback(fileManagerCompleteCallback)
                    setParentComponent(parentComponent)
                    if(okView != null) setOkView(okView)
                }
            )
            .commit()
    }

    /*fun show() {
        when(type) {
            ACTIVITY -> {
                startActivityForResult(
                    activity!!,
                    Intent(activity.applicationContext, GetMeActivity::class.java),
                    GET_ME_CODE,
                    null
                )
            }
            FRAGMENT -> {
                fragmentManager?.beginTransaction()
                    ?.replace(fragmentContainer, GetMeFragment.newInstance())
                    ?.commit()
            }
            DIALOG -> {}
        }
    }

    data class Builder(
        private var activity: Activity? = null,
        private var fragmentManager: FragmentManager? = null,
        private var fragmentContainer: Int? = null,
        private var type: Int? = null
    ) {
        fun activity(activity: Activity) = apply {
            this.activity = activity
        }

        fun fragmentManager(fragmentManager: FragmentManager?) = apply {
            this.fragmentManager = fragmentManager
        }

        fun fragmentContainer(fragmentContainer: Int?) = apply {
            this.fragmentContainer = fragmentContainer
        }

        fun type(type: Int) = apply {
            this.type = type
        }

        fun build() = GetMe(activity, fragmentManager, fragmentContainer, type)
    }

    companion object {
        const val ACTIVITY = 0
        const val FRAGMENT = 1
        const val DIALOG = 3

        // Activity result code
        const val GET_ME_CODE = 10102
    }*/

}