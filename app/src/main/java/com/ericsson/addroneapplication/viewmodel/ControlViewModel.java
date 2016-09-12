package com.ericsson.addroneapplication.viewmodel;

import android.util.Log;

import com.ericsson.addroneapplication.comunication.CommunicationHandler;
import com.ericsson.addroneapplication.comunication.data.AutopilotData;
import com.ericsson.addroneapplication.comunication.data.ControlData;
import com.ericsson.addroneapplication.comunication.data.DebugData;
import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.controller.ControlActivity;
import com.ericsson.addroneapplication.controller.ControlPadView;
import com.ericsson.addroneapplication.controller.ControlThrottleView;
import com.ericsson.addroneapplication.controller.IirLowpassFilter;
import com.ericsson.addroneapplication.model.UIDataPack;
import com.ericsson.addroneapplication.settings.SettingsActivity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlViewModel implements ViewModel, ControlPadView.OnControlPadChangedListener, ControlThrottleView.setOnControlThrottlePadChangedListener, CommunicationHandler.CommunicationListener {

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

    // COMMUNICATION LISTENER METHODS

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onMessageReceived(CommunicationMessage message) {
        uiDataLock.lock();

        switch (message.getMessageId()) {
            case DEBUG_MESSAGE:
                debugData = (DebugData) message.getValue();
                break;
            case AUTOPILOT_MESSAGE:
                autopilotData = (AutopilotData) message.getValue();
                break;
        }

        uiDataLock.unlock();
    }

    @Override
    public void onPingUpdated(long pingDelay) {
        uiDataLock.lock();
        ping = pingDelay;
        uiDataLock.unlock();
    }
}
