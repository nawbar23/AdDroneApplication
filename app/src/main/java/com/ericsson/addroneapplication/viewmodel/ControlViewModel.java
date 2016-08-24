package com.ericsson.addroneapplication.viewmodel;

import com.ericsson.addroneapplication.controller.ControlActivity;
import com.ericsson.addroneapplication.model.UpdateUIData;
import com.ericsson.addroneapplication.settings.SettingsActivity;

import java.util.TimerTask;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlViewModel implements ViewModel, ControlActivity.OnControlsChangedListener {

    ControlActivity activity;
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

        delay = 1000 / SettingsActivity.getIntFromPreferences(activity.getApplicationContext(), SettingsActivity.KEY_PREF_UI_REFRESH_RATE, 2);

        resume();
    }

    public void resume() {

    }

    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
