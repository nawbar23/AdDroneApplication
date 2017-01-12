package com.addrone.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.addrone.R;


/**
 * Created by Kamil on 8/22/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference connectionPref = findPreference(key);

        if (key.equals(SettingsActivity.KEY_PREF_UI_REFRESH_RATE)) {
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
