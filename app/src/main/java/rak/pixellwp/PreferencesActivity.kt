package rak.pixellwp

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast

class PreferencesActivity : PreferenceActivity() {
    val NUMBER_OF_CIRCLES = "numberOfCircles"
    val TOUCH_ENABLED = "touch"


    private var numberCheckListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener({ preference: SharedPreferences, newValue: Any ->
        val num = preference.getString(NUMBER_OF_CIRCLES, "")
        val touch = preference.getBoolean(TOUCH_ENABLED, false)
        Log.d(Log.DEBUG.toString(), "evaluate new preference: $newValue")

        if (NUMBER_OF_CIRCLES == newValue) {
            if (num.isNotEmpty() && num.matches(Regex("\\d*"))) {
                Log.d(Log.DEBUG.toString(), "evaluate new preference: $num circles and touch is $touch")
            } else {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show()
                preference.edit().putInt(NUMBER_OF_CIRCLES, 1)
            }
        }

        Log.d(Log.DEBUG.toString(), "circles value is : ${preference.getString(NUMBER_OF_CIRCLES, "")}")
        Log.d(Log.DEBUG.toString(), "touch value is : ${preference.getBoolean(TOUCH_ENABLED, false)}")
    })

    class MyPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            PreferenceManager.setDefaultValues(activity, R.xml.prefs, false)
            addPreferencesFromResource(R.xml.prefs)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragment = MyPreferenceFragment()
        fragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit()


        val circlePreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        circlePreference.registerOnSharedPreferenceChangeListener(numberCheckListener)
    }

}