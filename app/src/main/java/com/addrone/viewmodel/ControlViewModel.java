package com.addrone.viewmodel;

import android.util.Log;

import com.addrone.controller.ControlPadView;
import com.addrone.model.UIDataPack;
import com.multicopter.java.UavEvent;
import com.multicopter.java.UavManager;
import com.addrone.settings.SettingsActivity;
import com.multicopter.java.data.AutopilotData;
import com.multicopter.java.data.ControlData;
import com.multicopter.java.data.DebugData;
import com.addrone.controller.ControlActivity;
import com.addrone.controller.ControlThrottleView;
import com.addrone.model.ActionDialog;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlViewModel implements ViewModel, ControlPadView.OnControlPadChangedListener, ControlThrottleView.setOnControlThrottlePadChangedListener, UavManager.UavManagerListener {

    private ControlActivity activity;
    private long delay;
    private static final String TAG="ControlViewModel";

    private ControlData controlData = new ControlData();
    private Lock controlDataLock = new ReentrantLock();

    private DebugData debugData = new DebugData();
    private AutopilotData autopilotData = new AutopilotData();
    private long ping = 0;
    private Lock uiDataLock = new ReentrantLock();

    private UavManager uavManager;

    private ActionDialog.ButtonId buttonIdd;

    public ControlViewModel(ControlActivity activity) {
        this.activity = activity;

        delay = 1000 / SettingsActivity.getIntFromPreferences(activity.getApplicationContext(), SettingsActivity.KEY_PREF_UI_REFRESH_RATE, 2);

        resume();
    }

    public void resume() {

    }

    public void pause() {

    }

    public void setUavManager(UavManager uavManager) {
        this.uavManager = uavManager;
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

    public void onActionClick() {
        //uavManager.disconnectApplicationLoop();
        final ActionDialog dialog = new ActionDialog(activity) {
            @Override
            public void onButtonClick(ButtonId buttonId) {

                buttonIdd=buttonId;
                new Thread(new ActionMenu()).start();
                dismiss();
            }
        };
        dialog.show();
    }

    public void onEndFlightClick() {
        uavManager.endFlightLoop();
    }

    public ControlData getCurrentControlData() {
        controlDataLock.lock();
        ControlData currentControlData = new ControlData(controlData);
        controlDataLock.unlock();

        return currentControlData;
    }

    public UIDataPack getCurrentUiDataPack() {
        uiDataLock.lock();
        UIDataPack uiDataPack = new UIDataPack(debugData, autopilotData, ping);
        uiDataLock.unlock();

        return uiDataPack;
    }

    @Override
    public void handleUavEvent(final UavEvent event, UavManager uavManager) {
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

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (event.getType()) {
                    case FLIGHT_STARTED:
                        activity.notifyFlightStarted();
                        break;

                    case FLIGHT_ENDED:
                        activity.notifyFlightEnded();
                        break;
                }
            }
        });

    }


    private class ActionMenu implements Runnable {
        @Override
        public void run() {
            switch (buttonIdd) {
                case FLY:
                    uavManager.startFlightLoop();
                    break;

                case CALIB_ACCEL:
                    uavManager.startAccelerometerCalibration();
                    break;

                case DISCONNECT:
                    uavManager.disconnectApplicationLoop();
                    break;
            }
        }
    }
}
