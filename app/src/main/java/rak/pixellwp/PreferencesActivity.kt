package rak.pixellwp

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.util.Log
import android.widget.Toast

class PreferencesActivity : PreferenceActivity() {
    var numberCheckListener: Preference.OnPreferenceChangeListener = Preference.OnPreferenceChangeListener({
        preference: Preference, newValue: Any ->

        if (newValue?.toString().isNotEmpty() && newValue?.toString().matches(Regex("\\d*"))){
            Log.d(Log.DEBUG.toString(), "grabbed new preference: " + newValue)
            return@OnPreferenceChangeListener true
        }

        Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show()
        return@OnPreferenceChangeListener false
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.prefs)

        val circlePreference = getPreferenceScreen().findPreference("numberOfCircles")
        circlePreference.setOnPreferenceChangeListener(numberCheckListener)
        Log.d(Log.DEBUG.toString(), "finished preference create")
    }

}