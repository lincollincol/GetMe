package linc.com.getme.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import jp.wasabeef.recyclerview.adapters.AnimationAdapter
import linc.com.getme.R
import linc.com.getme.data.preferences.LocalPreferences
import linc.com.getme.device.StorageHelperImpl
import linc.com.getme.domain.FilesystemInteractorImpl
import linc.com.getme.domain.utils.StateManager
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.adapters.FilesystemEntitiesAdapter
import linc.com.getme.ui.adapters.selection.FilesystemEntityKeyProvider
import linc.com.getme.ui.adapters.selection.FilesystemEntityLookup
import linc.com.getme.ui.adapters.selection.SelectionState
import linc.com.getme.ui.callbacks.CloseFileManagerCallback
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.callbacks.SelectionTrackerCallback
import linc.com.getme.ui.custom.OverScrollBehavior
import linc.com.getme.ui.models.FilesystemEntityModel
import linc.com.getme.ui.presenters.FilesystemPresenter
import linc.com.getme.ui.presenters.FilesystemPresenterImpl
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
import linc.com.getme.utils.RecyclerViewAnimationProvider
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
    private lateinit var containerEmptyDirectoryGetMe: LinearLayout
    private var animationAdapter: AnimationAdapter? = null
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
            presenter = FilesystemPresenterImpl(
                FilesystemInteractorImpl(
                    StorageHelperImpl(activity!!.applicationContext),
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
        presenter?.prepare()
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
        containerEmptyDirectoryGetMe = view.findViewById(R.id.containerEmptyDirectoryGetMe)

        presenter?.bind(this)

        filesystemEntities = view.findViewById<RecyclerView>(R.id.filesystemEntitiesGetMe).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = animationAdapter ?: filesystemEntitiesAdapter
            setHasFixedSize(true)
        }

        presenter?.getFilesystemEntities()

    }

    /**
     * Update adapter items with directories or files entities
     * */
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

    /**
     * Show icon and title about empty directory
     * */
    override fun showEmptySign(visibility: Int) {
        containerEmptyDirectoryGetMe.visibility = visibility
    }

    /**
     * Close callback. Call method in the external app to implement back pressed logic
     * */
    override fun closeManager(resultFiles: List<File>) {
        if(!resultFiles.isNullOrEmpty())
            fileManagerCompleteCallback.onFilesSelected(resultFiles)
        closeFileManagerCallback.onCloseFileManager()
    }

    /**
     * Initialize SelectionTracker is external app need it
     * */
    override fun enableSelection(enable: Boolean, maxSize: Int) {
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
            )
            .apply {
                if(maxSize == GetMeInterfaceSettings.SELECTION_SIZE_DEFAULT) {
                    withSelectionPredicate(SelectionPredicates.createSelectAnything())
                } else {
                    withSelectionPredicate(object : SelectionTracker.SelectionPredicate<FilesystemEntityModel>() {
                        override fun canSelectMultiple() = true

                        override fun canSetStateForKey(
                            key: FilesystemEntityModel,
                            nextState: Boolean
                        ): Boolean {
                            if(nextState && selectionTracker!!.selection.size() >= 10) {
                                return false
                            }
                            return true
                        }

                        override fun canSetStateAtPosition(
                            position: Int,
                            nextState: Boolean
                        ) = true

                    })
                }
            }
            .build()

        selectionTrackerCallback.onSelectionTrackerCreated(selectionTracker!!)
        filesystemEntitiesAdapter.setSelectionTracker(selectionTracker!!)
    }

    /**
     * Move adapter position to top
     * */
    override fun scrollToTop() {
        filesystemEntities?.scrollToPosition(RECYCLER_VIEW_TOP)
    }

    /**
     * Initialize adapter and apply animation if it is selected from external app
     * */
    override fun initFilesystemEntitiesAdapter(adapterAnimation: Int, firstOnly: Boolean) {
        filesystemEntitiesAdapter = FilesystemEntitiesAdapter().apply {
            setFilesystemEntityClickListener(this@GetMeFragment)
            val layout = arguments!!.getInt(Constants.KEY_GET_ME_FILE_LAYOUT)
            // Set adapter item layout
            setLayout(
                when(layout) {
                    GET_ME_DEFAULT_FILE_LAYOUT -> R.layout.item_filesystem_entity_get_me
                    else -> layout
                }
            )
        }

        animationAdapter = RecyclerViewAnimationProvider.adapterAnimationFromConst(
            adapterAnimation,
            firstOnly,
            filesystemEntitiesAdapter
        )
    }

    /**
     * Set layout over scroll behavior if external app use enableOverScroll = true
     * */
    override fun enableOverScroll(enable: Boolean) {
        if (!enable) return
        val overScrollParams =
            filesystemEntities?.layoutParams as CoordinatorLayout.LayoutParams
        overScrollParams.behavior = OverScrollBehavior()
        filesystemEntities?.requestLayout()
    }

    /**
     * Handle item (directory/file) click
     * */
    override fun onClick(filesystemEntityModel: FilesystemEntityModel) {
        presenter?.handleFilesystemEntityAction(filesystemEntityModel)
        selectionTracker?.clearSelection()
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
        okView.setOnClickListener { okClicked() }
    }

    /**
     * Set external view that will handle click events to open previous directory
     * @param backView - View go to previous directory after click
     * @param firstClearSelection - in case when it's true - clear selection if external app use selectionTracker
     * */
    fun setBackView(backView: View, firstClearSelection: Boolean) {
        backView.setOnClickListener { backClicked(firstClearSelection) }
    }

    /**
     * Internal implementation
     * */

    /**
     * Execute ok click functional
     * */
    internal fun okClicked() {
        presenter?.prepareResultFiles(
            selectionTracker?.selection?.toList() ?: emptyList()
        )
        selectionTracker?.clearSelection()
    }

    /**
     * Execute back click functional
     * */
    internal fun backClicked(clearSelection: Boolean) {
        if(selectionTracker?.hasSelection() == true) {
            selectionTracker?.clearSelection()
            if(clearSelection) return
        }
        presenter?.openPreviousFilesystemEntity()
    }

    /**
     * Execute back pressed functional
     * */
    internal fun backPressedInFileManager() {
        if(selectionTracker?.hasSelection() == true) {
            selectionTracker?.clearSelection()
            return
        }
        presenter?.openPreviousFilesystemEntity()
    }

    /**
     * Return current file manager state
     * */
    internal fun getState() = presenter?.retrieveState()

}