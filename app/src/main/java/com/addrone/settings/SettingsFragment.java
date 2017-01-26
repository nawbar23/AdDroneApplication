package com.addrone.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.addrone.R;
import com.addrone.controller.ControlActivity;


/**
 * Created by Kamil on 8/22/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public final static String PREFERENCES_KEY="Key to Settings values";
    public final static String PREF_KEY_PERIOD = "Period value for ControlActivity";
    public final static String PREF_KEY_PING= "Ping for uavManager";
    public final static long DEFAULT_PERIOD=80;
    public final static Float DEFAULT_PING=5f;

    static long period;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference connectionPref = findPreference(key);
        SharedPreferences sharedPref=getActivity().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPref.edit();

        if (key.equals(SettingsActivity.KEY_PREF_UI_REFRESH_RATE)) {
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            editor.putLong(PREF_KEY_PERIOD,Long.valueOf(sharedPreferences.getString(key, "")));
            editor.commit();

        }
    }
}
