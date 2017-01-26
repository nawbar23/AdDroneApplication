package com.addrone.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.addrone.R;
import com.addrone.controller.ControlActivity;
import com.addrone.service.AdDroneService;


/**
 * Created by Kamil on 8/22/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public final static String PREFERENCES_KEY = "Key to Settings values";
    public final static String PREF_KEY_PERIOD = "Period value for ControlActivity";
    public final static String PREF_KEY_CONTROL_FREQ = "Sending control data frequency";
    public final static String PREF_KEY_PING_FREQ = "Update delay time frequency";
    public final static long DEFAULT_PERIOD = 80;
    public final static float DEFAULT_CONTROL_FREQ = 20f;
    public final static float DEFAULT_PING_FREQ = 0.5f;

    private AdDroneService service = null;

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
            AdDroneService.LocalBinder binder = (AdDroneService.LocalBinder) serviceBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference connectionPref = findPreference(key);
        if (!sharedPreferences.getString(key, "").isEmpty()) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            if (key.equals(SettingsActivity.KEY_PREF_UI_REFRESH_RATE)) {
                connectionPref.setSummary(sharedPreferences.getString(key, ""));
                editor.putLong(PREF_KEY_PERIOD, Long.valueOf(sharedPreferences.getString(key, "")));
                editor.apply();

            } else if (key.equals(SettingsActivity.KEY_PREF_CON_CON_FREQ)) {
                connectionPref.setSummary(sharedPreferences.getString(key, ""));
                editor.putFloat(PREF_KEY_CONTROL_FREQ, Float.valueOf(sharedPreferences.getString(key, "")));
                editor.apply();

                service.getUavManager().getCommHandler().getControlTask().setFrequency(Double.valueOf(sharedPreferences.getString(key, "")));

            } else if (key.equals(SettingsActivity.KEY_PREF_CON_PING_FREQ)) {
                connectionPref.setSummary(sharedPreferences.getString(key, ""));
                editor.putFloat(PREF_KEY_PING_FREQ, Float.valueOf(sharedPreferences.getString(key, "")));
                editor.apply();

                service.getUavManager().getCommHandler().getPingTask().setFrequency(Double.valueOf(sharedPreferences.getString(key, "")));
            }
        }
    }
}
