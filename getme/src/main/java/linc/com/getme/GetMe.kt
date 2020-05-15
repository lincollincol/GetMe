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
import linc.com.getme.utils.Constants.Companion.KEY_FILESYSTEM_SETTINGS
import linc.com.getme.utils.Constants.Companion.KEY_INTERFACE_SETTINGS

class GetMe <T : CloseFileManagerCallback> (
    private val fragmentManager: FragmentManager,
    private val fragmentContainer: Int,
    private val parentComponent: T,
    private val getMeFilesystemSettings: GetMeFilesystemSettings,
    private val getMeInterfaceSettings: GetMeInterfaceSettings,
    private val closeFileManagerCallback: CloseFileManagerCallback,
    private val fileManagerCompleteCallback: FileManagerCompleteCallback,
    private val selectionTrackerCallback: SelectionTrackerCallback? = null,
    private val okView: View? = null,
    private val backView: View? = null,
    private val firstClearSelectionAfterBack: Boolean = false,
    @StyleRes private val style: Int = -1
) {

    fun show() {

        fragmentManager.beginTransaction()
            .replace(
                fragmentContainer,
                GetMeFragment.newInstance(Bundle().apply {
                    putParcelable(KEY_FILESYSTEM_SETTINGS, getMeFilesystemSettings)
                    putParcelable(KEY_INTERFACE_SETTINGS, getMeInterfaceSettings.apply {
                        actionType = getMeFilesystemSettings.actionType
                    })
                    putInt("STYLE", style)
                }).apply {
                    setParentComponent(parentComponent)
                    setCloseFileManagerCallback(closeFileManagerCallback)
                    setFileManagerCompleteCallback(fileManagerCompleteCallback)
                    if(okView != null) setOkView(okView)
                    if(backView != null) setBackView(backView, firstClearSelectionAfterBack)
                    if(selectionTrackerCallback != null) setSelectionCallback(selectionTrackerCallback)
                }
            )
            .addToBackStack(null)
            .commit()
    }


    fun saveState(outState: Bundle) {
        fragmentManager.putFragment(
            outState,
            "myFragmentName",
            fragmentManager.findFragmentById(fragmentContainer) as Fragment
        )
    }

    fun restoreState(savedInstanceState: Bundle) {
        fragmentManager.beginTransaction()
            .replace(
                fragmentContainer,
                fragmentManager.getFragment(savedInstanceState, "myFragmentName") as Fragment
            )
            .addToBackStack(null)
            .commit()

    }

}