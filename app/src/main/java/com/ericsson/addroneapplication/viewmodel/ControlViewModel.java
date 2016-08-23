package com.ericsson.addroneapplication.viewmodel;

import android.preference.PreferenceManager;

import com.ericsson.addroneapplication.controller.ControlActivity;
import com.ericsson.addroneapplication.model.UpdateUIData;
import com.ericsson.addroneapplication.settings.SettingsActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlViewModel implements ViewModel, ControlActivity.OnControlsChangedListener {

    ControlActivity activity;
    Timer updateTimer;
    long delay;

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            UpdateUIData data = new UpdateUIData();

            activity.updateUI(data);
        }
    };

    public ControlViewModel(ControlActivity activity) {
        this.activity = activity;

        updateTimer = new Timer();
        delay = 1000 / PreferenceManager.getDefaultSharedPreferences(activity).getInt(SettingsActivity.KEY_PREF_UI_REFRESH_RATE, 2);

        resume();
    }

    public void resume() {
        updateTimer.scheduleAtFixedRate(updateTask, 1000, delay);
    }

    public void pause() {
        updateTimer.cancel();
    }

    @Override
    public void destroy() {
        updateTimer.cancel();
    }
}
