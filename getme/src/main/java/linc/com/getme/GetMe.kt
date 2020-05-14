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

    private var getMeFragment: GetMeFragment? = null

    fun show() {
//        restoreFragmentInstance()

        getMeFragment = GetMeFragment.newInstance(Bundle().apply {
            putParcelable(KEY_FILESYSTEM_SETTINGS, getMeFilesystemSettings)
            putParcelable(KEY_INTERFACE_SETTINGS, getMeInterfaceSettings.apply {
                actionType = getMeFilesystemSettings.actionType
            })
            putInt("STYLE", style)
        })

        println("SHOW =========== is null ${getMeFragment == null}")

        fragmentManager.beginTransaction()
            .replace(
                fragmentContainer,
                getMeFragment.apply {
                    this?.setParentComponent(parentComponent)
                    this?.setCloseFileManagerCallback(closeFileManagerCallback)
                    this?.setFileManagerCompleteCallback(fileManagerCompleteCallback)
                    if(okView != null) this?.setOkView(okView)
                    if(backView != null) this?.setBackView(backView, firstClearSelectionAfterBack)
                    if(selectionTrackerCallback != null) this?.setSelectionCallback(selectionTrackerCallback)
                } as Fragment,
                "GET_ME"
            )
            .addToBackStack(null)
            .commit()

        /*

        ?: GetMeFragment.newInstance(Bundle().apply {
                    putParcelable(KEY_FILESYSTEM_SETTINGS, getMeFilesystemSettings)
                    putParcelable(KEY_INTERFACE_SETTINGS, getMeInterfaceSettings.apply {
                        actionType = getMeFilesystemSettings.actionType
                    })
                })
         */
    }


    fun onSaveInstanceState(outState: Bundle) {
        println("GET_ME save state =========== is null ${getMeFragment == null}")
//        fragmentManager.putFragment(outState, "myFragmentName", getMeFragment as Fragment)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        getMeFragment = fragmentManager.getFragment(savedInstanceState, "myFragmentName") as GetMeFragment

        val oldFragment = fragmentManager.findFragmentByTag("GET_ME")

        if(oldFragment != null) {
            fragmentManager.beginTransaction().remove(oldFragment).commit()
            println("REMOVE_OLD")
        }

        getMeFragment = GetMeFragment.newInstance(Bundle().apply {
            putParcelable(KEY_FILESYSTEM_SETTINGS, getMeFilesystemSettings)
            putParcelable(KEY_INTERFACE_SETTINGS, getMeInterfaceSettings.apply {
                actionType = getMeFilesystemSettings.actionType
            })
            putInt("STYLE", style)
        })

        fragmentManager.beginTransaction()
            .replace(
                fragmentContainer,
                getMeFragment.apply {
                    this?.setParentComponent(parentComponent)
                    this?.setCloseFileManagerCallback(closeFileManagerCallback)
                    this?.setFileManagerCompleteCallback(fileManagerCompleteCallback)
                    if(okView != null) this?.setOkView(okView)
                    if(backView != null) this?.setBackView(backView, firstClearSelectionAfterBack)
                    if(selectionTrackerCallback != null) this?.setSelectionCallback(selectionTrackerCallback)
                } as Fragment,
                "GET_ME"
            )
            .addToBackStack(null)
            .commit()

    }

    /*fun restoreFragmentInstance() {
        if(instanceState != null) {
            getMeFragment = fragmentManager.getFragment(instanceState, "myFragmentName") as GetMeFragment

            println("GET_ME RESTORE state =========== is null ${getMeFragment == null}")
        } else {
            println("SKIP_RESTORE")

        }
    }*/

}