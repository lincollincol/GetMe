package linc.com.getme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import linc.com.getme.domain.models.GetMeFilesystemSettings
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.callbacks.CloseFileManagerCallback
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.fragments.GetMeFragment
import linc.com.getme.utils.Constants.Companion.KEY_FILESYSTEM_SETTINGS
import linc.com.getme.utils.Constants.Companion.KEY_INTERFACE_SETTINGS

class GetMe <T : CloseFileManagerCallback> (
    private val fragmentManager: FragmentManager,
    private val fragmentContainer: Int,
    private val getMeFilesystemSettings: GetMeFilesystemSettings,
    private val getMeInterfaceSettings: GetMeInterfaceSettings,
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
                    putParcelable(KEY_FILESYSTEM_SETTINGS, getMeFilesystemSettings)
                    putParcelable(KEY_INTERFACE_SETTINGS, getMeInterfaceSettings)
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