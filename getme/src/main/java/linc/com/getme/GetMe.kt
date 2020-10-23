package linc.com.getme

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import linc.com.getme.domain.entities.GetMeFilesystemSettings
import linc.com.getme.domain.utils.StateManager
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.callbacks.CloseFileManagerCallback
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.callbacks.SelectionTrackerCallback
import linc.com.getme.ui.fragments.GetMeFragment
import linc.com.getme.utils.CloseParameterCallback
import linc.com.getme.utils.Constants.Companion.GET_ME_DEFAULT_FILE_LAYOUT
import linc.com.getme.utils.Constants.Companion.GET_ME_DEFAULT_STYLE
import linc.com.getme.utils.Constants.Companion.KEY_FILESYSTEM_SETTINGS
import linc.com.getme.utils.Constants.Companion.KEY_FRAGMENT_STATE
import linc.com.getme.utils.Constants.Companion.KEY_GET_ME_FILE_LAYOUT
import linc.com.getme.utils.Constants.Companion.KEY_GET_ME_STYLE
import linc.com.getme.utils.Constants.Companion.KEY_INTERFACE_SETTINGS
import linc.com.getme.utils.Constants.Companion.TAG_GET_ME
import java.lang.StringBuilder
import java.util.*
import javax.security.auth.callback.Callback

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
    @StyleRes private var style: Int = GET_ME_DEFAULT_STYLE,
    @LayoutRes private var fileLayout: Int = GET_ME_DEFAULT_FILE_LAYOUT
) {

    /**
     * Create GetMe and start it in the fragment manager
     * */
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
                    putInt(KEY_GET_ME_FILE_LAYOUT, fileLayout)
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

    /**
     * Remove GetMe from back stack and clear references
     * */
    fun close(callback: CloseParameterCallback) {
        val getMeFragment = fragmentManager?.findFragmentByTag(TAG_GET_ME)
        if(getMeFragment != null) {
            fragmentManager!!.popBackStack()
        }

        this.fragmentManager = null
        fragmentContainer = null
        getMeFilesystemSettings = null
        getMeInterfaceSettings = null
        closeFileManagerCallback = null
        fileManagerCompleteCallback = null
        selectionTrackerCallback = null
        okView = null
        backView = null

        callback.close();
    }

    /**
     * Handle GetMe instance saving
     * */
    fun onSaveInstanceState(outState: Bundle) {
        fragmentManager?.putFragment(
            outState,
            KEY_FRAGMENT_STATE,
            fragmentManager?.findFragmentByTag(TAG_GET_ME) as Fragment
        )
    }

    /**
     * Handle GetMe instance restoring
     * */
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

    /**
     * Handle back press in GetMe
     * */
    fun onBackPressed() {
        val getMeFragment = fragmentManager?.findFragmentByTag(TAG_GET_ME)
        if(getMeFragment != null) {
            (getMeFragment as GetMeFragment).backPressedInFileManager()
        }
    }

    /**
     * Provoke ok click if user need it without okView etc. Make GetMe more flexible
     * */
    fun performOkClick() {
        val getMeFragment = fragmentManager?.findFragmentByTag(TAG_GET_ME)
        if(getMeFragment != null) {
            (getMeFragment as GetMeFragment).okClicked()
        }
    }

    /**
     * Provoke back click if user need it without backView etc. Make GetMe more flexible
     * */
    fun performBackClick(clearSelection: Boolean) {
        val getMeFragment = fragmentManager?.findFragmentByTag(TAG_GET_ME)
        if(getMeFragment != null) {
            (getMeFragment as GetMeFragment).backClicked(clearSelection)
        }
    }

    /**
     * @return true if current state equals root
     * */
    fun isRoot(): Boolean {
        val getMeFragment = fragmentManager?.findFragmentByTag(TAG_GET_ME)
        if(getMeFragment != null) {
            return (getMeFragment as GetMeFragment)
                .getState() == StateManager.ROOT
        }
        return false
    }

    /**
     * @return current file manager state (path)
     * */
    fun getCurrentPath(): String? {
        val getMeFragment = fragmentManager?.findFragmentByTag(TAG_GET_ME)
        return if(getMeFragment != null) {
            (getMeFragment as GetMeFragment).getState()
        } else null
    }

}