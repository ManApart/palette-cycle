package rak.pixellwp.cycling.preferences

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import rak.pixellwp.R
import rak.pixellwp.cycling.LoggingManager


class CyclingPreferenceActivity : PreferenceActivity() {
    private val logTag = "Prefs"
    private val loggingManager = LoggingManager(this)
    private val PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1

    class MyPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            PreferenceManager.setDefaultValues(activity, R.xml.cycling_prefs, false)
            addPreferencesFromResource(R.xml.cycling_prefs)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, MyPreferenceFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        when (requestCode) {
            PERMISSIONS_WRITE_EXTERNAL_STORAGE -> {
                reportBug(View(this))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun reportBug(view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_WRITE_EXTERNAL_STORAGE)
            }
        } else {
            loggingManager.writeLogsToExternal()
            Toast.makeText(this, "Saved logs to Android/data/rak.pixellwp/files", Toast.LENGTH_LONG).show()
        }
    }

}