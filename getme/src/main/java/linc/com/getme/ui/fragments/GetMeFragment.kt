package linc.com.getme.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import linc.com.getme.R
import linc.com.getme.data.preferences.LocalPreferences
import linc.com.getme.device.StorageHelper
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.domain.utils.StateManager
import linc.com.getme.ui.adapters.FilesystemEntitiesAdapter
import linc.com.getme.ui.adapters.selection.FilesystemEntityKeyProvider
import linc.com.getme.ui.adapters.selection.FilesystemEntityLookup
import linc.com.getme.ui.adapters.selection.SelectionState
import linc.com.getme.ui.callbacks.CloseFileManagerCallback
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.callbacks.SelectionTrackerCallback
import linc.com.getme.ui.models.FilesystemEntityModel
import linc.com.getme.ui.presenters.FilesystemPresenter
import linc.com.getme.ui.views.FilesystemView
import linc.com.getme.utils.Constants
import linc.com.getme.utils.Constants.Companion.GET_ME_DEFAULT_FILE_LAYOUT
import linc.com.getme.utils.Constants.Companion.GET_ME_DEFAULT_STYLE
import linc.com.getme.utils.Constants.Companion.ID_SELECTION
import linc.com.getme.utils.Constants.Companion.KEY_FILESYSTEM_SETTINGS
import linc.com.getme.utils.Constants.Companion.KEY_GET_ME_STYLE
import linc.com.getme.utils.Constants.Companion.KEY_INTERFACE_SETTINGS
import linc.com.getme.utils.Constants.Companion.KEY_RECYCLER_VIEW_STATE
import linc.com.getme.utils.Constants.Companion.KEY_SELECTION_STATE
import linc.com.getme.utils.Constants.Companion.RECYCLER_VIEW_TOP
import java.io.File


internal class GetMeFragment : Fragment(),
    FilesystemView,
    FilesystemEntitiesAdapter.FilesystemEntityClickListener {

    // Callbacks
    private lateinit var closeFileManagerCallback: CloseFileManagerCallback
    private lateinit var fileManagerCompleteCallback: FileManagerCompleteCallback
    private lateinit var selectionTrackerCallback: SelectionTrackerCallback

    // Mvp
    private var presenter: FilesystemPresenter? = null

    // Ui
    private lateinit var filesystemEntitiesAdapter: FilesystemEntitiesAdapter
    private var filesystemEntityKeyProvider: FilesystemEntityKeyProvider? = null
    private var selectionTracker: SelectionTracker<FilesystemEntityModel>? = null
    private var filesystemEntities: RecyclerView? = null

    // States
    private var recyclerViewState: Parcelable? = null
    private var selectionState: SelectionState? = null

    companion object {
        fun newInstance(data: Bundle) = GetMeFragment().apply {
            arguments = data
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(presenter == null) {
            presenter = FilesystemPresenter(
                FilesystemInteractor(
                    StorageHelper(activity!!.applicationContext),
                    StateManager(),
                    arguments?.getParcelable(KEY_FILESYSTEM_SETTINGS)!!,
                    LocalPreferences(activity!!.applicationContext)
                ),
                arguments?.getParcelable(KEY_INTERFACE_SETTINGS)!!,
                CompositeDisposable()
            )
        }

    }

    override fun onResume() {
        super.onResume()
        presenter?.bind(this)
    }

    override fun onStop() {
        super.onStop()
        presenter?.unbind()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save recycler view state
        outState.putParcelable(KEY_RECYCLER_VIEW_STATE, filesystemEntities?.layoutManager?.onSaveInstanceState())
        // Save selection state
        outState.putParcelable(
            KEY_SELECTION_STATE,
            SelectionState(
            selectionTracker?.selection?.toMutableList()
                ?: emptyList<FilesystemEntityModel>().toMutableList()
            )
        )
        // Save current directory state
        presenter?.saveCurrentState()
        super.onSaveInstanceState(outState)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            // Restore directory state
            presenter?.restoreState()
            recyclerViewState = savedInstanceState.getParcelable(KEY_RECYCLER_VIEW_STATE)!!
            selectionState = savedInstanceState.getParcelable(KEY_SELECTION_STATE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set GetMe style
        val style = if(arguments?.getInt(KEY_GET_ME_STYLE) != GET_ME_DEFAULT_STYLE) {
            arguments!!.getInt(KEY_GET_ME_STYLE)
        } else R.style.GetMeDefaultTheme
        // Inflate view
        val contextThemeWrapper: Context = ContextThemeWrapper(activity, style)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_get_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filesystemEntitiesAdapter = FilesystemEntitiesAdapter().apply {
            setFilesystemEntityClickListener(this@GetMeFragment)
            val layout = arguments!!.getInt(Constants.KEY_GET_ME_FILE_LAYOUT)
            setLayout(
                if(layout == GET_ME_DEFAULT_FILE_LAYOUT)
                    R.layout.item_filesystem_entity_get_me
                else layout
            )
        }

        filesystemEntities = view.findViewById<RecyclerView>(R.id.filesystemEntitiesGetMe).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = filesystemEntitiesAdapter
            setHasFixedSize(true)
        }

        presenter?.getFilesystemEntities()

    }

    override fun showFilesystemEntities(filesystemEntityModels: List<FilesystemEntityModel>) {
        filesystemEntitiesAdapter.updateFilesystemEntities(filesystemEntityModels)
        filesystemEntityKeyProvider?.setFilesystemEntities(filesystemEntityModels)
        if(recyclerViewState != null) {
            filesystemEntities?.layoutManager!!.onRestoreInstanceState(recyclerViewState)
        }
        if(selectionState != null) {
            selectionTracker?.setItemsSelected(selectionState!!.selectedItems, true)
            selectionState = null
        }
    }

    override fun closeManager(resultFiles: List<File>) {
        if(!resultFiles.isNullOrEmpty())
            fileManagerCompleteCallback.onFilesSelected(resultFiles)
        closeFileManagerCallback.onCloseFileManager()
    }

    override fun enableSelection(enable: Boolean) {
        if (!enable) return

        if(selectionTracker != null) {
            return
        }

        filesystemEntityKeyProvider = FilesystemEntityKeyProvider()
        selectionTracker = SelectionTracker.Builder(
                ID_SELECTION,
                filesystemEntities!!,
                filesystemEntityKeyProvider!!,
                FilesystemEntityLookup(filesystemEntities!!),
                StorageStrategy.createParcelableStorage(FilesystemEntityModel::class.java)
            ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        selectionTrackerCallback.onSelectionTrackerCreated(selectionTracker!!)
        filesystemEntitiesAdapter.setSelectionTracker(selectionTracker!!)
    }

    override fun scrollToTop() {
        filesystemEntities?.scrollToPosition(RECYCLER_VIEW_TOP)
    }

    override fun onClick(filesystemEntityModel: FilesystemEntityModel) {
        presenter?.handleFilesystemEntityAction(filesystemEntityModel)
    }

    internal fun backPressedInFileManager() {
        if(selectionTracker?.hasSelection() == true) {
            selectionTracker?.clearSelection()
            return
        }
        presenter?.openPreviousFilesystemEntity()
    }

    /**
     * External callbacks
     */

    /**
     * Call onCloseFileManager() when user press back button from root directory
     * @param closeFileManagerCallback - implementation from external
     * */
    fun setCloseFileManagerCallback(closeFileManagerCallback: CloseFileManagerCallback) {
        this.closeFileManagerCallback = closeFileManagerCallback
    }

    /**
     * Call onFilesSelected(files) when user click okView with selected files
     * @param fileManagerCompleteCallback - implementation from external
     * */
    fun setFileManagerCompleteCallback(fileManagerCompleteCallback: FileManagerCompleteCallback) {
        this.fileManagerCompleteCallback = fileManagerCompleteCallback
    }

    /**
     * Call onSelectionTrackerCreated(selectionTracker) to make library more flexible with selections.
     * External app can clear or select items with ActionBar etc.
     * @param selectionTrackerCallback - implementation from external
     * */
    fun setSelectionCallback(selectionTrackerCallback: SelectionTrackerCallback) {
        this.selectionTrackerCallback = selectionTrackerCallback
    }

    /**
     * Set external view that will handle click events to return result
     * @param okView - View that will return result in onFilesSelected(files) after click
     * */
    fun setOkView(okView: View) {
        okView.setOnClickListener {
            presenter?.prepareResultFiles(
                selectionTracker?.selection?.toList() ?: emptyList()
            )
            selectionTracker?.clearSelection()
        }
    }

    /**
     * Set external view that will handle click events to open previous directory
     * @param backView - View go to previous directory after click
     * @param firstClearSelection - in case when it's true - clear selection if external app use selectionTracker
     * */
    fun setBackView(backView: View, firstClearSelection: Boolean) {
        backView.setOnClickListener {
            if(selectionTracker?.hasSelection() == true) {
                selectionTracker?.clearSelection()
                if(firstClearSelection) return@setOnClickListener
            }
            presenter?.openPreviousFilesystemEntity()
        }
    }

}