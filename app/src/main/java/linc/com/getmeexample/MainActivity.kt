package linc.com.getmeexample

import android.Manifest
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.SelectionTracker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import linc.com.getme.GetMe
import linc.com.getme.domain.entities.GetMeFilesystemSettings
import linc.com.getme.ui.GetMeInterfaceSettings
import linc.com.getme.ui.callbacks.CloseFileManagerCallback
import linc.com.getme.ui.callbacks.FileManagerBackListener
import linc.com.getme.ui.callbacks.FileManagerCompleteCallback
import linc.com.getme.ui.callbacks.SelectionTrackerCallback
import linc.com.getme.ui.models.FilesystemEntityModel
import java.io.File

class MainActivity : AppCompatActivity(),
    CloseFileManagerCallback,
    FileManagerCompleteCallback,
    SelectionTrackerCallback {

    override lateinit var fileManagerBackListener: FileManagerBackListener

    lateinit var getMe: GetMe<CloseFileManagerCallback>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {}
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {}
            }).check()

        getMe = GetMe(
            supportFragmentManager,
            R.id.fragmentContainer,
            this,
            GetMeFilesystemSettings(
                actionType = GetMeFilesystemSettings.ACTION_SELECT_FILE
//                mainContent = mutableListOf("pdf", "mp3"),
//                path = "/storage/emulated/0/viber/media",
//                allowBackPath = true
            ),
            GetMeInterfaceSettings(GetMeInterfaceSettings.SELECTION_MIXED),
            closeFileManagerCallback = this,
            fileManagerCompleteCallback = this,
            selectionTrackerCallback = this,
            okView = getFiles,
            backView = back,
            firstClearSelectionAfterBack = true
//            style = R.style.GetMeCustomTheme
        )


        open.setOnClickListener {
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

    override fun onCloseFileManager() {
        // Remove GetMe from fragment manager
        getMe.close()
        // todo handle back pressed
    }

    override fun onBackPressed() {
        fileManagerBackListener.backPressedInFileManager()
    }

    override fun onFilesSelected(selectedFiles: List<File>) {
        println(selectedFiles)
    }

    override fun onSelectionTrackerCreated(selectionTracker: SelectionTracker<FilesystemEntityModel>) {
        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<FilesystemEntityModel>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                // todo selection
            }
        })
    }

}
