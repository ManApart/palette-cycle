package rak.pixellwp

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast

class CirclePreferenceActivity : PreferenceActivity() {
    private val NUMBER_OF_CIRCLES = "numberOfCircles"
    private val TOUCH_ENABLED = "touch"

    class MyPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            PreferenceManager.setDefaultValues(activity, R.xml.circle_prefs, false)
            addPreferencesFromResource(R.xml.circle_prefs)
        }
    }

    private var numberCheckListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener({ preference: SharedPreferences, newValue: Any ->
        if (NUMBER_OF_CIRCLES == newValue) {
            val num = preference.getString(NUMBER_OF_CIRCLES, "")
            if (num.isEmpty() || !num.matches(Regex("\\d*"))) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show()
                preference.edit().putInt(NUMBER_OF_CIRCLES, 1).apply()
            }
        }
        Log.d(Log.DEBUG.toString(), "circles value is : ${preference.getString(NUMBER_OF_CIRCLES, "")}")
        Log.d(Log.DEBUG.toString(), "touch value is : ${preference.getBoolean(TOUCH_ENABLED, false)}")
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, MyPreferenceFragment()).commit()
        PreferenceManager.getDefaultSharedPreferences(applicationContext).registerOnSharedPreferenceChangeListener(numberCheckListener)
    }

}