package rak.pixellwp.cycling

import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import rak.pixellwp.R


class CyclingPreferenceActivity : PreferenceActivity() {

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


}