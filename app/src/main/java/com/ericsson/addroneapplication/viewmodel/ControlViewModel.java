package com.ericsson.addroneapplication.viewmodel;

import android.util.Log;

import com.ericsson.addroneapplication.comunication.data.ControlData;
import com.ericsson.addroneapplication.controller.ControlActivity;
import com.ericsson.addroneapplication.controller.ControlPadView;
import com.ericsson.addroneapplication.controller.ControlThrottleView;
import com.ericsson.addroneapplication.settings.SettingsActivity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlViewModel implements ViewModel, ControlPadView.OnControlPadChangedListener, ControlThrottleView.OnControlTrottlePadChangedListener {

    ControlActivity activity;
    long delay;

    private ControlData controlData = new ControlData();
    private Lock controlDataLock = new ReentrantLock();

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

    @Override
    public void onControlPadChanged(float x, float y) {
        Log.v("CONTROLS_UPDATE", "Received pad update: "  + x + " " + y);

        controlDataLock.lock();

        controlData.setRoll(x);
        controlData.setPitch(y);

        controlDataLock.unlock();
    }

    @Override
    public void onControlThrottlePadChangedListener(float x, float y) {
        Log.v("CONTROLS_UPDATE", "Received throttle update: "  + x + " " + y);

        controlDataLock.lock();

        controlData.setYaw(x);
        controlData.setThrottle(y);

        controlDataLock.unlock();
    }

    public ControlData getCurrentControlData() {
        controlDataLock.lock();
        ControlData currentControlData = new ControlData(controlData);
        controlDataLock.unlock();

        return currentControlData;
    }
}
