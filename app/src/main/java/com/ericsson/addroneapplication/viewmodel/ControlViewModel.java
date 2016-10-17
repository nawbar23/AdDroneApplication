package com.ericsson.addroneapplication.viewmodel;

import android.util.Log;

import com.ericsson.addroneapplication.communication.data.AutopilotData;
import com.ericsson.addroneapplication.communication.data.ControlData;
import com.ericsson.addroneapplication.communication.data.DebugData;
import com.ericsson.addroneapplication.controller.ControlActivity;
import com.ericsson.addroneapplication.controller.ControlPadView;
import com.ericsson.addroneapplication.controller.ControlThrottleView;
import com.ericsson.addroneapplication.controller.IirLowpassFilter;
import com.ericsson.addroneapplication.model.UIDataPack;
import com.ericsson.addroneapplication.settings.SettingsActivity;
import com.ericsson.addroneapplication.uav_manager.UavEvent;
import com.ericsson.addroneapplication.uav_manager.UavManager;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlViewModel implements ViewModel, ControlPadView.OnControlPadChangedListener, ControlThrottleView.setOnControlThrottlePadChangedListener, UavManager.UavManagerListener {

    ControlActivity activity;
    long delay;

    private ControlData controlData = new ControlData();
    private Lock controlDataLock = new ReentrantLock();

    private DebugData debugData = new DebugData();
    private AutopilotData autopilotData = new AutopilotData();
    private long ping = 0;
    private Lock uiDataLock = new ReentrantLock();

    IirLowpassFilter rollFilter, pitchFilter, yawFilter, throttleFilter;

    public ControlViewModel(ControlActivity activity) {
        this.activity = activity;

        delay = 1000 / SettingsActivity.getIntFromPreferences(activity.getApplicationContext(), SettingsActivity.KEY_PREF_UI_REFRESH_RATE, 2);

        rollFilter = new IirLowpassFilter(0);
        pitchFilter = new IirLowpassFilter(0);
        yawFilter = new IirLowpassFilter(0);
        throttleFilter = new IirLowpassFilter(0);

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

        // TODO temporary trim yaw control for only big changes
        if (Math.abs(x) > 0.95) {
            controlData.setYaw(x);
        } else {
            controlData.setYaw(0.0f);
        }
        controlData.setThrottle(y);

        controlDataLock.unlock();
    }

    public ControlData getCurrentControlData() {
        controlDataLock.lock();
        ControlData currentControlData = new ControlData(controlData);
        controlDataLock.unlock();

        // controller inputs filtering
        // TODO test this feature
        ControlData filteredControlData = new ControlData(currentControlData);
        filteredControlData.setRoll((float)rollFilter.update(currentControlData.getRoll()));
        filteredControlData.setPitch((float)pitchFilter.update(currentControlData.getPitch()));
        filteredControlData.setYaw((float)yawFilter.update(currentControlData.getYaw()));
        filteredControlData.setThrottle((float)throttleFilter.update(currentControlData.getThrottle()));

        return filteredControlData;
    }

    public UIDataPack getCurrentUiDataPack() {
        uiDataLock.lock();
        UIDataPack uiDataPack = new UIDataPack(debugData, autopilotData, ping);
        uiDataLock.unlock();

        return uiDataPack;
    }

    @Override
    public void handleUavEvent(UavEvent event, UavManager uavManager) {
        uiDataLock.lock();

        switch (event.getType()) {
            case DEBUG_UPDATED:
                debugData = uavManager.getDebugData();
                break;
            case AUTOPILOT_UPDATED:
                autopilotData = uavManager.getAutopilotData();
                break;
            case PING_UPDATED:
                ping = uavManager.getCommDelay();
                break;
        }

        uiDataLock.unlock();
    }
}
