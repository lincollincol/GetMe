package linc.com.getme.ui.dialogs

import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import linc.com.getme.R
import linc.com.getme.domain.models.FilesystemEntity
import linc.com.getme.ui.adapters.FilesystemEntitiesAdapter
import linc.com.getme.ui.presenters.FilesystemPresenter
import linc.com.getme.ui.views.FilesystemView


@Deprecated(message = "This function will be available in the future")
class GetMeDialog : DialogFragment(),
    FilesystemView,
    FilesystemEntitiesAdapter.FilesystemEntityClickListener {

    private var presenter: FilesystemPresenter? = null
    private lateinit var filesystemEntitiesAdapter: FilesystemEntitiesAdapter

    companion object {
        @Deprecated(message = "This function will be available in the future", level = DeprecationLevel.HIDDEN)
        fun newInstance() = GetMeDialog()
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
    ): View? = inflater.inflate(R.layout.dialog_get_me, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        filesystemEntitiesAdapter = FilesystemEntitiesAdapter().apply {
            setFilesystemEntityClickListener(this@GetMeDialog)
        }

        view.findViewById<RecyclerView>(R.id.filesystemEntities).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = filesystemEntitiesAdapter
            setHasFixedSize(true)
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(super.onCreateDialog(savedInstanceState).context) {
            override fun onBackPressed() {
                presenter?.openPreviousFilesystemEntity()

            }
        }
    }

    override fun showFilesystemEntities(filesystemEntities: List<FilesystemEntity>) {
        filesystemEntitiesAdapter.updateFilesystemEntities(filesystemEntities)
    }

    override fun closeManager() {
        dismiss()
    }

    override fun enableSelection(enable: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onClick(filesystemEntity: FilesystemEntity) {
        presenter?.openFilesystemEntity(filesystemEntity)
    }

    override fun onResume() {
        super.onResume()

        val window: Window? = dialog!!.window
        val size = Point()

        val display: Display = window!!.windowManager!!.defaultDisplay
        display.getSize(size)

        val width: Int = size.x
        val height: Int = size.y

        window.setBackgroundDrawable(ContextCompat.getDrawable(
            window.context,
            R.drawable.background_dialog_rounded_corners
        ))

        window.setLayout((width * 0.8).toInt(), (height * 0.6).toInt())
        window.setGravity(Gravity.CENTER)

        presenter?.bind(this)
        presenter?.getFilesystemEntities()

    }

    override fun onStop() {
        super.onStop()
        presenter?.unbind()
    }

}