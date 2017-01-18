package com.addrone.settings;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.addrone.R;
import com.addrone.service.AdDroneService;


/**
 * Created by Kamil on 8/22/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

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

        if (key.equals(SettingsActivity.KEY_PREF_UI_REFRESH_RATE)) {
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            service.getUavManager().getCommHandler().getPingTask().setFrequency(Double.valueOf(sharedPreferences.getString(key, "")));
        }
    }
}
