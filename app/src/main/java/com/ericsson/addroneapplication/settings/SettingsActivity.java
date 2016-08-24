package com.ericsson.addroneapplication.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Kamil on 8/22/2016.
 */
public class SettingsActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "AdDrone:" + SettingsActivity.class.getSimpleName();

    public static final String KEY_PREF_MSG_RETRANS_RATE = "pref_msg_retrans_rate";
    public static final String KEY_PREF_MSG_RETRANS_NUM = "pref_msg_retrans_num";
    public static final String KEY_PREF_UI_REFRESH_RATE = "pref_ui_refresh_rate";

    private SettingsFragment settingsFragment;

    public static int getIntFromPreferences(Context context, String key, int defaultValue) {
        int value = defaultValue;

        try {
            value = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            Log.w(DEBUG_TAG, e);
        }

        return value;
    }

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
