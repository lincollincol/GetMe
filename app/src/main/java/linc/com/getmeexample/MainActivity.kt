package linc.com.getmeexample

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import linc.com.getme.GetMe
import linc.com.getmeexample.fragments.ExampleGetMeFragment
import linc.com.getmeexample.fragments.FileManagerFragment
import linc.com.getmeexample.fragments.StartFragment

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

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, StartFragment())
                .commit()
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, supportFragmentManager.getFragment(savedInstanceState, "FRA")!!)
            .commit()
    }

   override fun onSaveInstanceState(outState: Bundle) {
        supportFragmentManager.putFragment(
            outState,
            "FRA",
            supportFragmentManager.findFragmentById(R.id.fragmentContainer)!!
        )
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if(fragment != null && fragment is FileManagerFragment)
            fragment.onBackPressed()
    }
}