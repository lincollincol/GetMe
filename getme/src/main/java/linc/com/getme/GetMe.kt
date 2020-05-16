package linc.com.getme

import android.os.Bundle
import android.view.View
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import linc.com.getme.domain.entities.GetMeFilesystemSettings
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.callbacks.CloseFileManagerCallback
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.callbacks.SelectionTrackerCallback
import linc.com.getme.ui.fragments.GetMeFragment
import linc.com.getme.utils.Constants.Companion.GET_ME_DEFAULT_STYLE
import linc.com.getme.utils.Constants.Companion.KEY_FILESYSTEM_SETTINGS
import linc.com.getme.utils.Constants.Companion.KEY_FRAGMENT_STATE
import linc.com.getme.utils.Constants.Companion.KEY_GET_ME_STYLE
import linc.com.getme.utils.Constants.Companion.KEY_INTERFACE_SETTINGS
import linc.com.getme.utils.Constants.Companion.TAG_GET_ME

class GetMe (
    private var fragmentManager: FragmentManager?,
    private var fragmentContainer: Int?,
    private var getMeFilesystemSettings: GetMeFilesystemSettings?,
    private var getMeInterfaceSettings: GetMeInterfaceSettings?,
    private var closeFileManagerCallback: CloseFileManagerCallback?,
    private var fileManagerCompleteCallback: FileManagerCompleteCallback?,
    private var selectionTrackerCallback: SelectionTrackerCallback? = null,
    private var okView: View? = null,
    private var backView: View? = null,
    private var firstClearSelectionAfterBack: Boolean = false,
    @StyleRes private val style: Int = GET_ME_DEFAULT_STYLE
) {

    fun show() {
        if(fragmentManager == null) {
            return
        }
        fragmentManager!!.beginTransaction()
            .replace(
                fragmentContainer!!,
                GetMeFragment.newInstance(Bundle().apply {
                    putParcelable(KEY_FILESYSTEM_SETTINGS, getMeFilesystemSettings)
                    putParcelable(KEY_INTERFACE_SETTINGS, getMeInterfaceSettings!!.apply {
                        actionType = getMeFilesystemSettings!!.actionType
                    })
                    putInt(KEY_GET_ME_STYLE, style)
                }).apply {
                    setCloseFileManagerCallback(closeFileManagerCallback!!)
                    setFileManagerCompleteCallback(fileManagerCompleteCallback!!)
                    if(okView != null) setOkView(okView!!)
                    if(backView != null) setBackView(backView!!, firstClearSelectionAfterBack)
                    if(selectionTrackerCallback != null) setSelectionCallback(selectionTrackerCallback!!)
                } as Fragment,
                TAG_GET_ME
            )
            .addToBackStack(null)
            .commit()
    }

    fun close() {
        val getMeFragment = fragmentManager?.findFragmentByTag(TAG_GET_ME)
        if(getMeFragment != null) {
            fragmentManager!!.beginTransaction()
                .remove(getMeFragment)
                .commit()
        }

        fragmentManager = null
        fragmentContainer = null
        getMeFilesystemSettings = null
        getMeInterfaceSettings = null
        closeFileManagerCallback = null
        fileManagerCompleteCallback = null
        selectionTrackerCallback = null
        okView = null
        backView = null
    }

    fun onSaveInstanceState(outState: Bundle) {
        fragmentManager?.putFragment(
            outState,
            KEY_FRAGMENT_STATE,
            fragmentManager?.findFragmentByTag(TAG_GET_ME) as Fragment
        )
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        if(fragmentManager == null || fragmentManager!!.getFragment(savedInstanceState, KEY_FRAGMENT_STATE) == null) {
            return
        }
        fragmentManager!!.beginTransaction()
            .replace(
                fragmentContainer!!,
                (fragmentManager!!.getFragment(savedInstanceState, KEY_FRAGMENT_STATE) as GetMeFragment).apply {
                    setCloseFileManagerCallback(closeFileManagerCallback!!)
                    setFileManagerCompleteCallback(fileManagerCompleteCallback!!)
                    if(okView != null) setOkView(okView!!)
                    if(backView != null) setBackView(backView!!, firstClearSelectionAfterBack)
                    if(selectionTrackerCallback != null) setSelectionCallback(selectionTrackerCallback!!)
                },
                TAG_GET_ME
            )
            .addToBackStack(null)
            .commit()
    }

    fun onBackPressed() {
        val getMeFragment = fragmentManager?.findFragmentByTag(TAG_GET_ME)
        if(getMeFragment != null) {
            (getMeFragment as GetMeFragment).backPressedInFileManager()
        }
    }

}