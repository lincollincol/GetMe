package linc.com.getmeexample

import android.Manifest
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
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

        GetMe(
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
        ).show()

    }

    override fun onCloseFileManager() {
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

/** Dialog fragment*/
/*class MainActivity : AppCompatActivity() {

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

        openFileManager.setOnClickListener {
            GetMeDialog.newInstance()
                .show(supportFragmentManager, "GET_ME")
        }
    }

}*/

/** Fragment */
/*
class MainActivity : AppCompatActivity(), CloseFileManagerCallback {

    override lateinit var filesystemBackListener: FilesystemBackListener

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

        // Fragment
        openFileManager.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    GetMeFragment.newInstance(this, this)
                ).commit()
        }
    }

    override fun onCloseFileManager() {
        super.onBackPressed()
    }

    override fun onBackPressed() {
        filesystemBackListener.backPressedInFileManager()
    }

}*/

/** Activity */
/*
class MainActivity : AppCompatActivity() {

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

        // Activity
        openFileManager.setOnClickListener {
            startActivity(Intent(this, GetMeActivity::class.java))
        }

    }
}*/
