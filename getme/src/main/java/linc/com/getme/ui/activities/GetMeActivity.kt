package linc.com.getme.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import linc.com.getme.R
import linc.com.getme.device.StorageHelper
import linc.com.getme.domain.FilesystemEntity
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.domain.GetMeSettings
import linc.com.getme.ui.adapters.FilesystemEntitiesAdapter
import linc.com.getme.ui.presenters.FilesystemPresenter
import linc.com.getme.ui.views.FilesystemView
import linc.com.getme.utils.StateManager


class GetMeActivity :
    AppCompatActivity(),
    FilesystemView,
    FilesystemEntitiesAdapter.FilesystemEntityClickListener {

    private lateinit var filesystemEntitiesAdapter: FilesystemEntitiesAdapter
    private var presenter: FilesystemPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_me)

        if(presenter == null) {
//            presenter = FilesystemPresenter(
//                FilesystemInteractor(StorageHelper(this), GetMeSettings(GetMeSettings.ACTION_SELECT_FILE)),
//                StateManager()
//            )
        }

        filesystemEntitiesAdapter = FilesystemEntitiesAdapter().apply {
            setFilesystemEntityClickListener(this@GetMeActivity)
        }

        val filesystemEntities = findViewById<RecyclerView>(R.id.filesystemEntities).apply {
            layoutManager = LinearLayoutManager(this@GetMeActivity)
            adapter = filesystemEntitiesAdapter
            setHasFixedSize(true)
        }

    }

    override fun showFilesystemEntities(filesystemEntities: List<FilesystemEntity>) {
        filesystemEntitiesAdapter.updateFilesystemEntities(filesystemEntities)
    }

//    override fun showError(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }

    override fun closeManager() {
        finish()
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

    override fun onBackPressed() {
        presenter?.openPreviousFilesystemEntity()
    }

}
