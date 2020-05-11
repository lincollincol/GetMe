package linc.com.getme.ui.dialogs

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import linc.com.getme.R
import linc.com.getme.device.StorageHelper
import linc.com.getme.domain.FilesystemEntity
import linc.com.getme.domain.FilesystemInteractor
import linc.com.getme.domain.GetMeSettings
import linc.com.getme.ui.adapters.FilesystemEntitiesAdapter
import linc.com.getme.ui.presenters.FilesystemPresenter
import linc.com.getme.ui.views.FilesystemView
import linc.com.getme.utils.StateManager


@Deprecated(message = "This function will be available in the future")
class GetMeBottomSheetDialog : BottomSheetDialogFragment(),
    FilesystemView,
    FilesystemEntitiesAdapter.FilesystemEntityClickListener {

    private var presenter: FilesystemPresenter? = null
    private lateinit var filesystemEntitiesAdapter: FilesystemEntitiesAdapter

    companion object {
        @Deprecated(message = "This function will be available in the future", level = DeprecationLevel.HIDDEN)
        fun newInstance() = GetMeBottomSheetDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(presenter == null) {
//            presenter = FilesystemPresenter(
//                FilesystemInteractor(StorageHelper(activity!!.applicationContext), GetMeSettings(
//                    GetMeSettings.ACTION_SELECT_FILE)
//                ),
//                StateManager()
//            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_bottom_sheet_get_me, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (requireDialog() as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        filesystemEntitiesAdapter = FilesystemEntitiesAdapter().apply {
            setFilesystemEntityClickListener(this@GetMeBottomSheetDialog)
        }

        view.findViewById<RecyclerView>(R.id.filesystemEntities).apply {
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
        dismiss()
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

}