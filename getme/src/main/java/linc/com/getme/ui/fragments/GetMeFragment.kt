package linc.com.getme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import linc.com.getme.R
import linc.com.getme.device.StorageHelper
import linc.com.getme.domain.FilesystemEntity
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.ui.adapters.FilesystemEntitiesAdapter
import linc.com.getme.ui.presenters.FilesystemPresenter
import linc.com.getme.ui.views.FilesystemView
import linc.com.getme.utils.StateManager

class GetMeFragment : Fragment(),
    FilesystemView,
    FilesystemBackListener,
    FilesystemEntitiesAdapter.FilesystemEntityClickListener {

    private lateinit var filesystemEntitiesAdapter: FilesystemEntitiesAdapter
    private lateinit var closeFileManagerCallback: CloseFileManagerCallback
    private var presenter: FilesystemPresenter? = null

    companion object {

        fun <T : CloseFileManagerCallback> newInstance(
            closeFileManagerCallback: CloseFileManagerCallback,
            parentComponent: T
        ) = GetMeFragment().apply {
            this.closeFileManagerCallback = closeFileManagerCallback
            parentComponent.filesystemBackListener = this
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(presenter == null) {
            presenter = FilesystemPresenter(
                FilesystemInteractor(StorageHelper(activity!!.applicationContext)),
                StateManager()
            )
        }

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

        val filesystemEntities = view.findViewById<RecyclerView>(R.id.filesystemEntities).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = filesystemEntitiesAdapter
            setHasFixedSize(true)
        }
    }

    override fun showFilesystemEntities(filesystemEntities: List<FilesystemEntity>) {
        filesystemEntitiesAdapter.updateFilesystemEntities(filesystemEntities)
    }

    override fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun closeManager() {
        closeFileManagerCallback.onCloseFileManager()
    }

    override fun onClick(filesystemEntity: FilesystemEntity) {
        presenter?.openFilesystemEntity(filesystemEntity)
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

    override fun subscribe() {
        presenter?.openPreviousFilesystemEntity()
    }

}