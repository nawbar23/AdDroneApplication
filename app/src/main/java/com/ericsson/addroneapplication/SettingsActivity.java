package com.ericsson.addroneapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Kamil on 8/22/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_MSG_RETRANS_RATE = "pref_msg_retrans_rate";
    public static final String KEY_PREF_MSG_RETRANS_NUM = "pref_msg_retrans_num";
    public static final String KEY_PREF_ENABLE_LITTLE_ENDIAN = "pref_enable_little_endian";

    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsFragment = new SettingsFragment();

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(settingsFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(settingsFragment);
    }
}
