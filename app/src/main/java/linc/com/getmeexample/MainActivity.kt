package linc.com.getmeexample

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import linc.com.getme.ui.activities.GetMeActivity
import linc.com.getme.ui.fragments.CloseFileManagerCallback
import linc.com.getme.ui.fragments.FilesystemBackListener
import linc.com.getme.ui.fragments.GetMeFragment

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

        // Activity
//        openFileManager.setOnClickListener {
//            startActivity(Intent(this, GetMeActivity::class.java))
//        }

        openFileManager.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    GetMeFragment.newInstance(this@MainActivity, this)
                ).commit()
        }

    }

    override fun onCloseFileManager() {
        super.onBackPressed()
    }

    override fun onBackPressed() {
        filesystemBackListener.subscribe()
    }

}
