package com.addrone.viewmodel;

import android.app.ProgressDialog;
import android.util.Log;

import com.addrone.controller.ControlActivity;
import com.addrone.controller.ControlPadFragment;
import com.addrone.controller.ControlPadView;
import com.addrone.controller.ControlThrottleView;
import com.addrone.model.ActionDialog;
import com.addrone.model.CalibrationInfoDialog;
import com.addrone.model.MagnetCalibDialog;
import com.addrone.model.ManageControlSettingsDialog;
import com.addrone.model.UIDataPack;
import com.addrone.settings.SettingsActivity;
import com.skydive.java.UavEvent;
import com.skydive.java.UavManager;
import com.skydive.java.data.AutopilotData;
import com.skydive.java.data.CalibrationSettings;
import com.skydive.java.data.ControlData;
import com.skydive.java.data.ControlSettings;
import com.skydive.java.data.DebugData;

import java.sql.Timestamp;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlViewModel implements ViewModel,
        ControlPadView.OnControlPadChangedListener,
        ControlThrottleView.OnControlThrottlePadChanged,
        UavManager.UavManagerListener {

    private static final String TAG = "ControlViewModel";
    private ControlActivity activity;
    private long delay;
    private ControlData controlData = new ControlData();
    private Lock controlDataLock = new ReentrantLock();

    private DebugData debugData = new DebugData();
    private AutopilotData autopilotData = new AutopilotData();
    private long ping = 0;
    private Lock uiDataLock = new ReentrantLock();

    private UavManager uavManager;

    private ProgressDialog dialog;

    private long lastUpdate;

    public ControlViewModel(ControlActivity activity) {
        this.activity = activity;

        delay = 1000 / SettingsActivity.getIntFromPreferences(activity.getApplicationContext(), SettingsActivity.KEY_PREF_UI_REFRESH_RATE, 2);

        resume();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setTimeStamp() {
        this.lastUpdate = new Timestamp(System.currentTimeMillis()).getTime();
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
    public void stop() {

    }

    @Override
    public void onControlPadChanged(float x, float y) {
        //Log.v("CONTROLS_UPDATE", "Received pad update: " + x + " " + y);
        controlDataLock.lock();
        controlData.setRoll(x);
        controlData.setPitch(y);
        setTimeStamp();
        controlDataLock.unlock();
    }

    @Override
    public void onControlThrottlePadChanged(float x, float y) {
        //Log.v("CONTROLS_UPDATE", "Received throttle update: " + x + " " + y);
        controlDataLock.lock();
        // TODO temporary trim yaw control for only big changes
        if (Math.abs(x) > 0.95) {
            controlData.setYaw(x);
        } else {
            controlData.setYaw(0.0f);
        }
        controlData.setThrottle(y);
        setTimeStamp();
        controlDataLock.unlock();
    }

    private void resetControlData() {
        controlData.setRoll(0.0f);
        controlData.setPitch(0.0f);
        controlData.setYaw(0.0f);
        controlData.setThrottle(0.0f);
        controlData.setCommand(ControlData.ControllerCommand.MANUAL);
        controlData.setMode(ControlData.SolverMode.ANGLE_NO_YAW);
    }

    public void onActionClick() {
        final ActionDialog dialog = new ActionDialog(activity) {
            @Override
            public void onButtonClick(ButtonId buttonId) {
                new Thread(new ActionMenu(buttonId)).start();
                dismiss();
            }
        };
        dialog.show();
    }

    public void onEndFlightClick() {
        try {
            uavManager.endFlightLoop();
        } catch (Exception e) {
            Log.e(TAG, "Error while stopping flight loop, message: " + e.getMessage());
        }
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
    public void handleUavEvent(final UavEvent event, final UavManager uavManager) {
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
                        resetControlData();
                        activity.notifyFlightStarted();
                        setTimeStamp();
                        break;
                    case FLIGHT_ENDED:
                        activity.notifyFlightEnded();
                        break;
                    case MAGNETOMETER_CALIBRATION_STARTED:
                        startMagnetCalibDialog();
                        break;
                    case CONTROL_UPDATED:
                        startDownloadControlSettingsDialog(uavManager.getControlSettings());
                        break;
                    case ACCEL_CALIB_DONE:
                        killProgressDialog();
                        break;
                    case ERROR:
                        killProgressDialog();
                        break;
                }
            }
        });

    }

    private void startDownloadControlSettingsDialog(final ControlSettings controlSettings) {
        if (!activity.isFinishing()) {
            Log.d(TAG, "showControlSettingsDialog");
            ManageControlSettingsDialog dialog = new ManageControlSettingsDialog(activity, controlSettings, uavManager);
            dialog.show();
        }
    }


    private void showCalibration(final CalibrationSettings calibrationSettings) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "showCalibrationMethod");
                CalibrationInfoDialog dialog = new CalibrationInfoDialog(activity);
                dialog.fillWithParams(calibrationSettings);
                dialog.show();
            }
        });
    }

    private void startMagnetCalibDialog() {
        if (!activity.isFinishing()) {
            final MagnetCalibDialog dialog = new MagnetCalibDialog(activity) {
                @Override
                public void onButtonMagnetCalibClick(ButtonCalibId buttonCalibId) {
                    new Thread(new MagnetCalibMenu(buttonCalibId)).start();
                    dismiss();
                }
            };
            dialog.show();
        }
    }

    private void startProgressDialog(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new ProgressDialog(activity);
                dialog.setMessage(message);
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }

    private void killProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private class ActionMenu implements Runnable {

        private ActionDialog.ButtonId buttonId;

        ActionMenu(ActionDialog.ButtonId buttonId) {
            this.buttonId = buttonId;
        }

        @Override
        public void run() {
            switch (buttonId) {
                case FLY:
                    uavManager.startFlightLoop();
                    break;
                case CALIB_ACCEL:
                    startProgressDialog("Calibrating...");
                    uavManager.startAccelerometerCalibration();
                    break;
                case CALIB_MAGNET:
                    uavManager.startMagnetometerCalibration();
                    break;
                case DISCONNECT:
                    uavManager.disconnectApplicationLoop();
                    break;
                case CHANGE_VIEW:
                    float rotation = ((ControlPadFragment) activity.getCameraFragment()).getImageView().getRotation();
                    ((ControlPadFragment) activity.getCameraFragment()).getImageView().setRotation(rotation + 180);
                    break;
                case VIEW_CALIB:
                    showCalibration(uavManager.getCalibrationSettings());
                    break;
                case MANAGE_CONTROL_SETTINGS:
                    uavManager.downloadControlSettings();
                    break;
            }
        }
    }

    private class MagnetCalibMenu implements Runnable {

        private MagnetCalibDialog.ButtonCalibId buttonCalibId;

        MagnetCalibMenu(MagnetCalibDialog.ButtonCalibId buttonCalibId) {
            this.buttonCalibId = buttonCalibId;
        }

        @Override
        public void run() {
            switch (buttonCalibId) {
                case DONE:
                    try {
                        uavManager.doneMagnetometerCalibration();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CANCEL:
                    try {
                        uavManager.cancelMagnetometerCalibration();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
