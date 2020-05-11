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

}