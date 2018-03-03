package rak.pixellwp.cycling

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import rak.pixellwp.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.channels.FileChannel


class CyclingPreferenceActivity : PreferenceActivity() {
    private val logTag = "Prefs"
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
                Log.d(logTag, "Write permissions granted")
                writeLogsToExternal()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun reportBug(view: View) {
        Log.d(logTag, "report bug")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(logTag, "permissions not granted")
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_WRITE_EXTERNAL_STORAGE)
        } else {
            Log.d(logTag, "have permissions")
            writeLogsToExternal()
            Toast.makeText(this, "Saved Logs.", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeLogsToExternal() {
        val logDirectory = baseContext.getDir("logs", Context.MODE_PRIVATE)
        Log.d(logTag, "found ${logDirectory.listFiles().size} log files")

        for (log in logDirectory.listFiles ()){
            val fileOut = File(getExternalFilesDir(null), log.name)
            var inChannel: FileChannel? = null
            var outChannel: FileChannel? = null

            try {
                inChannel = FileInputStream(log).channel
                outChannel = FileOutputStream(fileOut).channel
                inChannel!!.transferTo(0, inChannel!!.size(), outChannel)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                if (inChannel != null) inChannel!!.close()
                if (outChannel != null) outChannel!!.close()
            }
            Log.d(logTag, "wrote file: ${fileOut.absolutePath}")
        }
        Log.d(logTag, "finished writing files")

//        val dstPath = "Environment.getExternalStorageDirectory() + File.separator + "myApp" + File.separator"
//        val dst = File(dstPath)
//        if (!dst.exists()) {
//            if (!dst.mkdir()) {
//            }
//        }
    }


}