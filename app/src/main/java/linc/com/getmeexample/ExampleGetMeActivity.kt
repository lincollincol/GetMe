package linc.com.getmeexample

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_example_get_me.*
import kotlinx.android.synthetic.main.activity_main.*
import linc.com.getme.GetMe
import linc.com.getme.domain.entities.GetMeFilesystemSettings
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.callbacks.CloseFileManagerCallback
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.callbacks.SelectionTrackerCallback
import linc.com.getme.ui.models.FilesystemEntityModel
import linc.com.getmeexample.fragments.SelectionActionMode
import java.io.File

class ExampleGetMeActivity : AppCompatActivity(),
    CloseFileManagerCallback,
    FileManagerCompleteCallback,
    SelectionTrackerCallback,
    SelectionActionMode.MenuItemClickListener {

    private lateinit var getMe: GetMe
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_get_me)

        getMe = GetMe(
            supportFragmentManager,
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

        if(savedInstanceState == null) {
            getMe.show()
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
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
            // TODO implement back click logic here after calling getMe.close()
            finish()
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
                        actionMode = startSupportActionMode(
                            SelectionActionMode(selectionTracker, this@ExampleGetMeActivity))
                    actionMode?.title = "Selected ${selectionTracker.selection.size()}"
                }else {
                    actionMode?.finish()
                    actionMode = null
                }
            }
        })
    }

    override fun onMenuItemClicked(item: MenuItem?) {
        getMe.provokeOkClick()
    }
}
