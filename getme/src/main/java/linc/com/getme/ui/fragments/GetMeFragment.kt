package linc.com.getme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import linc.com.getme.R
import linc.com.getme.device.StorageHelper
import linc.com.getme.domain.models.FilesystemEntity
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.ui.adapters.FilesystemEntitiesAdapter
import linc.com.getme.ui.adapters.selection.FilesystemEntityKeyProvider
import linc.com.getme.ui.adapters.selection.FilesystemEntityLookup
import linc.com.getme.ui.callbacks.CloseFileManagerCallback
import linc.com.getme.ui.callbacks.FileManagerBackListener
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.presenters.FilesystemPresenter
import linc.com.getme.ui.views.FilesystemView
import linc.com.getme.utils.Constants.Companion.KEY_FILESYSTEM_SETTINGS
import linc.com.getme.domain.utils.StateManager
import linc.com.getme.ui.callbacks.SelectionTrackerCallback
import linc.com.getme.utils.Constants
import linc.com.getme.utils.Constants.Companion.KEY_INTERFACE_SETTINGS

internal class GetMeFragment : Fragment(),
    FilesystemView,
    FileManagerBackListener,
    FilesystemEntitiesAdapter.FilesystemEntityClickListener {


    private lateinit var closeFileManagerCallback: CloseFileManagerCallback
    private lateinit var fileManagerCompleteCallback: FileManagerCompleteCallback
    private lateinit var selectionTrackerCallback: SelectionTrackerCallback

    private lateinit var filesystemEntitiesAdapter: FilesystemEntitiesAdapter
    private lateinit var filesystemEntityKeyProvider: FilesystemEntityKeyProvider
    private var presenter: FilesystemPresenter? = null

    private lateinit var filesystemEntities: RecyclerView

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
                    arguments?.getParcelable(KEY_FILESYSTEM_SETTINGS)!!
                ),
                arguments?.getParcelable(KEY_INTERFACE_SETTINGS)!!
            )
        }

    }

    override fun onResume() {
        super.onResume()
        presenter?.bind(this)
        presenter?.getFilesystemEntities()
    }

    override fun onStop() {
        super.onStop()
        presenter?.unbind()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_get_me, container, false)

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

    }

    override fun showFilesystemEntities(filesystemEntities: List<FilesystemEntity>) {
        filesystemEntitiesAdapter.updateFilesystemEntities(filesystemEntities)
        filesystemEntityKeyProvider.setFilesystemEntities(filesystemEntities)
    }

    override fun closeManager() {
        closeFileManagerCallback.onCloseFileManager()
    }

    override fun enableSelection(enable: Boolean) {
        filesystemEntityKeyProvider = FilesystemEntityKeyProvider()

        val selectionTracker = SelectionTracker.Builder(
                "SELECTION_IDDD",
                filesystemEntities,
                filesystemEntityKeyProvider,
                FilesystemEntityLookup(filesystemEntities),
                StorageStrategy.createParcelableStorage(FilesystemEntity::class.java)
            ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

//        selectionTrackerCallback.onSelectionTrackerCreated(selectionTracker)

        /*selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<FilesystemEntity>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
            }
        })*/
    }

    override fun onClick(filesystemEntity: FilesystemEntity) {
        presenter?.openFilesystemEntity(filesystemEntity)
    }

    override fun backPressedInFileManager() {
        presenter?.openPreviousFilesystemEntity()
    }

    /**
     * External callbacks
     */

    fun setCloseFileManagerCallback(closeFileManagerCallback: CloseFileManagerCallback) {
        this.closeFileManagerCallback = closeFileManagerCallback
    }

    fun setFileManagerCompleteCallback(fileManagerCompleteCallback: FileManagerCompleteCallback) {
        this.fileManagerCompleteCallback = fileManagerCompleteCallback
    }

    fun <T : CloseFileManagerCallback> setParentComponent(parentComponent: T) {
        parentComponent.fileManagerBackListener = this
    }

    fun setOkView(okView: View) {
        okView.setOnClickListener {
            fileManagerCompleteCallback.onFilesSelected(emptyList())
        }
    }

}