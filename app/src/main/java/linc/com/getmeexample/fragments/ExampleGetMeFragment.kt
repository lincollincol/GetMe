package linc.com.getmeexample.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import kotlinx.android.synthetic.main.fragment_default_get_me.*
import linc.com.getme.GetMe
import linc.com.getme.domain.entities.GetMeFilesystemSettings
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.callbacks.CloseFileManagerCallback
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.callbacks.SelectionTrackerCallback
import linc.com.getme.ui.models.FilesystemEntityModel

import linc.com.getmeexample.R
import java.io.File

class ExampleGetMeFragment : Fragment(),
    CloseFileManagerCallback,
    FileManagerCompleteCallback,
    SelectionTrackerCallback,
    FileManagerFragment,
    SelectionActionMode.MenuItemClickListener {

    private lateinit var getMe: GetMe
    private var actionMode: ActionMode? = null

    companion object {
        fun newInstance(type: Int) = ExampleGetMeFragment().apply {
            arguments = Bundle().apply {
                putInt("TYPE", type)
            }
        }

        const val DEFAULT_GETME = 0
        const val CUSTOM_ITEM_LAYOUT_GETME = 1
        const val CUSTOM_STYLE_GETME = 2
        const val MAIN_CONTENT_GETME = 3
        const val EXCEPT_CONTENT_GETME = 4
        const val ONLY_DIRECTORIES_GETME = 5
        const val SINGLE_SELECTION_GETME = 6
        const val FROM_PATH_GETME = 7
        const val MAX_SELECTION_SIZE_GETME = 8
        const val ADAPTER_ANIMATION_GETME = 9
        const val OVERSCROLL_GETME = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_default_get_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments!!.getInt("TYPE")

        getMe = when(type) {
            DEFAULT_GETME -> getDefaultGetMe()
            CUSTOM_ITEM_LAYOUT_GETME -> getCustomItemLayoutGetMe()
            CUSTOM_STYLE_GETME -> getCustomStyleGetMe()
            MAIN_CONTENT_GETME -> getMainContentGetMe()
            EXCEPT_CONTENT_GETME -> getExceptContentGetMe()
            ONLY_DIRECTORIES_GETME -> getOnlyDirectoriesGetMe()
            SINGLE_SELECTION_GETME -> getSingleSelectionGetMe()
            FROM_PATH_GETME -> getFromPathGetMe()
            MAX_SELECTION_SIZE_GETME -> getMaximumSelectionSizeGetMe()
            ADAPTER_ANIMATION_GETME -> getAdapterAnimationGetMe()
            OVERSCROLL_GETME -> getOverScrollGetMe()
            else -> getDefaultGetMe()
        }

        if(savedInstanceState == null) {
            getMe.show()
        }

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState != null)
            getMe.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        getMe.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        getMe.onBackPressed()
    }

    override fun onCloseFileManager() {
        // Remove GetMe from fragment manager
        getMe.close {
            // TODO Implement back click logic here after calling getMe.close()
            fragmentManager?.popBackStack()
        }
    }

    override fun onFilesSelected(selectedFiles: List<File>) {
        println(selectedFiles)
    }

    override fun onSelectionTrackerCreated(selectionTracker: SelectionTracker<FilesystemEntityModel>) {
        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<FilesystemEntityModel>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if(selectionTracker.hasSelection()) {
                    if(actionMode == null)
                        actionMode = (activity as AppCompatActivity)
                            .startSupportActionMode(SelectionActionMode(selectionTracker, this@ExampleGetMeFragment))
                    actionMode?.title = "Selected ${selectionTracker.selection.size()}"
                }else {
                    actionMode?.finish()
                    actionMode = null
                }
            }
        })
    }

    override fun onMenuItemClicked(item: MenuItem?) {
        getMe.performOkClick()
    }

    /**
     *
     * GetMe settings types
     *
     * */

    private fun getDefaultGetMe(): GetMe {
        return GetMe(
            childFragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(GetMeFilesystemSettings.ACTION_SELECT_FILE),
            GetMeInterfaceSettings(GetMeInterfaceSettings.SELECTION_MIXED),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true
        )
    }

    private fun getCustomItemLayoutGetMe(): GetMe {
        return GetMe(
            fragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(GetMeFilesystemSettings.ACTION_SELECT_FILE),
            GetMeInterfaceSettings(GetMeInterfaceSettings.SELECTION_MIXED),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true,
            fileLayout = R.layout.item_get_me_custom
        )
    }

    private fun getCustomStyleGetMe(): GetMe {
        return GetMe(
            fragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(GetMeFilesystemSettings.ACTION_SELECT_FILE),
            GetMeInterfaceSettings(GetMeInterfaceSettings.SELECTION_MIXED),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true,
            style = R.style.GetMeCustomTheme
        )
    }

    private fun getMainContentGetMe(): GetMe {
        return GetMe(
            fragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(
                actionType = GetMeFilesystemSettings.ACTION_SELECT_FILE,
                mainContent = mutableListOf("mp3", "pdf", "png")
            ),
            GetMeInterfaceSettings(GetMeInterfaceSettings.SELECTION_MIXED),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true
        )
    }

    private fun getExceptContentGetMe(): GetMe {
        return GetMe(
            fragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(
                actionType = GetMeFilesystemSettings.ACTION_SELECT_FILE,
                exceptContent = mutableListOf("mp3", "pdf", "png")
            ),
            GetMeInterfaceSettings(GetMeInterfaceSettings.SELECTION_MIXED),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true
        )
    }

    private fun getOnlyDirectoriesGetMe(): GetMe {
        return GetMe(
            fragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(GetMeFilesystemSettings.ACTION_SELECT_DIRECTORY),
            GetMeInterfaceSettings(GetMeInterfaceSettings.SELECTION_MULTIPLE),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true
        )
    }

    private fun getSingleSelectionGetMe(): GetMe {
        return GetMe(
            fragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(GetMeFilesystemSettings.ACTION_SELECT_FILE),
            GetMeInterfaceSettings(GetMeInterfaceSettings.SELECTION_SINGLE),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true
        )
    }

    private fun getFromPathGetMe(): GetMe {
        return GetMe(
            fragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(
                actionType = GetMeFilesystemSettings.ACTION_SELECT_FILE,
                path = "/storage/emulated/0/Download",
                allowBackPath = true
            ),
            GetMeInterfaceSettings(GetMeInterfaceSettings.SELECTION_SINGLE),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true
        )
    }

    private fun getMaximumSelectionSizeGetMe(): GetMe {
        return GetMe(
            childFragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(GetMeFilesystemSettings.ACTION_SELECT_FILE),
            GetMeInterfaceSettings(
                selectionType = GetMeInterfaceSettings.SELECTION_MIXED,
                selectionMaxSize = 10
            ),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true
        )
    }

    private fun getAdapterAnimationGetMe(): GetMe {
        return GetMe(
            childFragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(GetMeFilesystemSettings.ACTION_SELECT_FILE),
            GetMeInterfaceSettings(
                selectionType = GetMeInterfaceSettings.SELECTION_MIXED,
                adapterAnimation = GetMeInterfaceSettings.ANIMATION_ADAPTER_FADE_IN,
                animationFirstOnly = false
            ),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true
        )
    }

    private fun getOverScrollGetMe(): GetMe {
        return GetMe(
            childFragmentManager,
            R.id.getMeContainer,
            GetMeFilesystemSettings(GetMeFilesystemSettings.ACTION_SELECT_FILE),
            GetMeInterfaceSettings(
                selectionType = GetMeInterfaceSettings.SELECTION_MIXED,
                enableOverScroll = true
            ),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = buttonGet,
            backView = buttonBack,
            firstClearSelectionAfterBack = true
        )
    }

}
