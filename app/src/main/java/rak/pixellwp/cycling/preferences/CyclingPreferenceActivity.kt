package rak.pixellwp.cycling.preferences

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.preference.*
import rak.pixellwp.R
import rak.pixellwp.cycling.*
import rak.pixellwp.cycling.jsonLoading.ImageLoader
import rak.pixellwp.cycling.wallpaperService.ImageType
import rak.pixellwp.cycling.wallpaperService.toImageType


class CyclingPreferenceActivity : FragmentActivity() {
    private val loggingManager = LoggingManager(this)
    private val PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1

    class MyPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            PreferenceManager.setDefaultValues(requireContext(), R.xml.cycling_prefs, false)
            addPreferencesFromResource(R.xml.cycling_prefs)
            val imageType = findPreference<DropDownPreference>(IMAGE_TYPE)?.value.toImageType()
            setOptionsVisibility(imageType)
            PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(preferenceListener)
        }

        override fun onDestroy() {
            PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(preferenceListener)
            super.onDestroy()
        }

        private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { preference: SharedPreferences, newValue: Any ->
            if (newValue == IMAGE_TYPE){
                val prefImageType = preference.getString(IMAGE_TYPE, TIMELINE_IMAGE).toImageType()
                setOptionsVisibility(prefImageType)
            }
        }

        private fun setOptionsVisibility(imageType: ImageType) {
            val timelineList = findPreference<DropDownPreference>(TIMELINE_IMAGE)
            val collectionList = findPreference<DropDownPreference>(IMAGE_COLLECTION)
            val panOverride = findPreference<SwitchPreference>(ADJUST_MODE)

            timelineList?.isVisible = false
            collectionList?.isVisible = false
            panOverride?.isVisible = false

            when (imageType) {
                ImageType.TIMELINE -> {
                    timelineList?.isVisible = true
                    panOverride?.isVisible = true
                }
                ImageType.COLLECTION -> {
                    collectionList?.isVisible = true
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, MyPreferenceFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_WRITE_EXTERNAL_STORAGE -> {
                reportBug(View(this))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun reportBug(view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_WRITE_EXTERNAL_STORAGE)
            }
        } else {
            loggingManager.writeLogsToExternal()
            Toast.makeText(this, "Saved logs to Android/data/rak.pixellwp/files", Toast.LENGTH_LONG).show()
        }
    }

    fun preload(view: View) {
        val imageLoader = ImageLoader(this)
        imageLoader.preloadImages()
    }

}