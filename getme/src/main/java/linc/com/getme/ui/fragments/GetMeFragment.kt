package linc.com.getme.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.MutableSelection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import linc.com.getme.ui.callbacks.FileManagerBackListener
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.callbacks.SelectionTrackerCallback
import linc.com.getme.ui.models.FilesystemEntityModel
import linc.com.getme.ui.presenters.FilesystemPresenter
import linc.com.getme.ui.views.FilesystemView
import linc.com.getme.utils.Constants.Companion.KEY_FILESYSTEM_SETTINGS
import linc.com.getme.utils.Constants.Companion.KEY_INTERFACE_SETTINGS
import java.io.File


internal class GetMeFragment : Fragment(),
    FilesystemView,
    FileManagerBackListener,
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
    private lateinit var filesystemEntities: RecyclerView

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
                arguments?.getParcelable(KEY_INTERFACE_SETTINGS)!!
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
        outState.putParcelable("RV", filesystemEntities.layoutManager!!.onSaveInstanceState())
        outState.putParcelable("SEL", SelectionState(selectionTracker?.selection!!.toMutableList()))

        outState.putInt("KEY_STATE", 123)
        presenter?.saveCurrentState()
        super.onSaveInstanceState(outState)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState != null && savedInstanceState.getInt("KEY_STATE") == 123) {
            presenter?.restoreState()
        }

        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable("RV")!!
            selectionState = savedInstanceState.getParcelable("SEL")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val style = if(arguments?.getInt("STYLE") != -1) {
            arguments!!.getInt("STYLE")
        } else R.style.GetMeDefaultTheme

        val contextThemeWrapper: Context =
            ContextThemeWrapper(activity, style)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_get_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filesystemEntitiesAdapter = FilesystemEntitiesAdapter().apply {
            setFilesystemEntityClickListener(this@GetMeFragment)
        }

        filesystemEntities = view.findViewById<RecyclerView>(R.id.filesystemEntities).apply {
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
            filesystemEntities.layoutManager!!.onRestoreInstanceState(recyclerViewState)
        }
        if(selectionState != null) {
            selectionTracker?.setItemsSelected(selectionState!!.selectedItems, true)
            selectionState = null
        }
    }

    override fun closeManager(resultFiles: List<File>) {
        if(!resultFiles.isNullOrEmpty())
            fileManagerCompleteCallback.onFilesSelected(resultFiles)
        fragmentManager?.popBackStack()
        closeFileManagerCallback.onCloseFileManager()
//        fragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    override fun enableSelection(enable: Boolean) {
        if (!enable) return

        if(selectionTracker != null) {
            return
        }

        filesystemEntityKeyProvider = FilesystemEntityKeyProvider()
        selectionTracker = SelectionTracker.Builder(
                "SELECTION_IDDD",
                filesystemEntities,
                filesystemEntityKeyProvider!!,
                FilesystemEntityLookup(filesystemEntities),
                StorageStrategy.createParcelableStorage(FilesystemEntityModel::class.java)
            ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        selectionTrackerCallback.onSelectionTrackerCreated(selectionTracker!!)
        filesystemEntitiesAdapter.setSelectionTracker(selectionTracker!!)
    }

    override fun scrollToTop() {
        filesystemEntities.scrollToPosition(0)
    }

    override fun onClick(filesystemEntityModel: FilesystemEntityModel) {
        presenter?.handleFilesystemEntityAction(filesystemEntityModel)
    }

    override fun backPressedInFileManager() {
        if(selectionTracker?.hasSelection() == true) {
            selectionTracker?.clearSelection()
            return
        }
        presenter?.openPreviousFilesystemEntity()
    }

    /**
     * External callbacks
     */

    fun <T : CloseFileManagerCallback> setParentComponent(parentComponent: T) {
        parentComponent.fileManagerBackListener = this
    }

    fun setCloseFileManagerCallback(closeFileManagerCallback: CloseFileManagerCallback) {
        this.closeFileManagerCallback = closeFileManagerCallback
    }

    fun setFileManagerCompleteCallback(fileManagerCompleteCallback: FileManagerCompleteCallback) {
        this.fileManagerCompleteCallback = fileManagerCompleteCallback
    }

    fun setSelectionCallback(selectionTrackerCallback: SelectionTrackerCallback) {
        this.selectionTrackerCallback = selectionTrackerCallback
    }

    fun setOkView(okView: View) {
        okView.setOnClickListener {
            presenter?.prepareResultFiles(
                selectionTracker?.selection?.toList() ?: emptyList()
            )
            selectionTracker?.clearSelection()
        }
    }

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