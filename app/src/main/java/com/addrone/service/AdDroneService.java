package com.addrone.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.addrone.R;
import com.addrone.communication.TcpClientSocket;
import com.addrone.model.ConnectionInfo;
import com.addrone.settings.SettingsFragment;
import com.addrone.viewmodel.ControlViewModel;
import com.multicopter.java.UavEvent;
import com.multicopter.java.UavManager;
import com.multicopter.java.data.ControlData;

/**
 * Created by nbar on 2016-08-19.
 * Service that contains CommunicationHandler
 * Can be stopped only when state is DISCONNECTED
 */

public class AdDroneService extends Service implements UavManager.UavManagerListener {
    public final static String START_ACTIVITY = "START_CONTROL_ACTIVITY";
    public final static String CONTROL_ACTIVITY = "START_START_ACTIVITY";
    private static final String DEBUG_TAG = "AdDrone:" + AdDroneService.class.getSimpleName();
    public static ConnectionInfo actualConnection;
    private final IBinder mBinder = new LocalBinder();
    ProgressDialog progressDialog;
    private State state;
    // main communication handler for UAV
    private UavManager uavManager;
    private ControlForwarder controlForwarder;
    private Handler handler;
    private long errorJoystickTime;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(DEBUG_TAG, "onCreate");

        Context context = getApplicationContext();

        SharedPreferences sharedPref = context.getSharedPreferences(
                SettingsFragment.PREFERENCES_KEY, Context.MODE_PRIVATE);
        double controlFreq = sharedPref.getFloat(SettingsFragment.PREF_KEY_CONTROL_FREQ, SettingsFragment.DEFAULT_CONTROL_FREQ);

        double pingFreq = sharedPref.getFloat(SettingsFragment.PREF_KEY_PING_FREQ, SettingsFragment.DEFAULT_PING_FREQ);

        errorJoystickTime=(long)(sharedPref.getFloat(SettingsFragment.PREF_ERROR_JOYSTICK_TIME,SettingsFragment.DEFAULT_ERROR_JOYSTICK_TIME)*1000);

        this.uavManager = new UavManager(new TcpClientSocket(), controlFreq, pingFreq);
        this.uavManager.registerListener(this);

        this.state = State.DISABLED;

        this.handler = new Handler();
    }

    public void setErrorJoystickTime(long errorJoystickTime) {
        this.errorJoystickTime = errorJoystickTime;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        Log.d(DEBUG_TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "onDestroy");
        uavManager.unregisterListener(this);
    }

    public UavManager getUavManager() {
        return uavManager;
    }

    public void attemptConnection(ConnectionInfo connectionInfo, ProgressDialog dialog) {
        switch (state) {
            case CONNECTED:
                Log.d(DEBUG_TAG, "attemptConnection at CONNECTED, starting activity");
                progressDialog.dismiss();
                startControlActivity();
                break;

            case CONNECTING:
                Log.d(DEBUG_TAG, "attemptConnection at CONNECTING, skipping");
                break;

            default:
                Log.d(DEBUG_TAG, "attemptConnection at default, connecting...");
                state = State.CONNECTING;
                progressDialog = dialog;
                actualConnection = connectionInfo;
                uavManager.getCommHandler().connectSocket(connectionInfo.getIpAddress(), connectionInfo.getPort());
                break;
        }
    }

    private void startControlActivity() {
        Intent intent = new Intent();
        intent.setAction(START_ACTIVITY);
        sendBroadcast(intent);
    }

    private void startConnectionActivity() {
        Intent intent = new Intent();
        intent.setAction(CONTROL_ACTIVITY);
        sendBroadcast(intent);
    }

    public void registerListener(UavManager.UavManagerListener listener) {
        uavManager.registerListener(listener);
    }

    public void unregisterListener(UavManager.UavManagerListener listener) {
        uavManager.registerListener(listener);
    }

    public void setControlViewModel(ControlViewModel controlViewModel) {
        controlForwarder = new ControlForwarder(controlViewModel);
        uavManager.setControlDataSource(controlForwarder);
    }

    public State getState() {
        return state;
    }

    @Override
    public void handleUavEvent(UavEvent event, UavManager uavManager) {
        switch (event.getType()) {
            case CONNECTED:
                state = State.CONNECTED;
                progressDialog.dismiss();
                startControlActivity();
                break;

            case DISCONNECTED:
                Log.d(DEBUG_TAG, "Disconnected event received at state: " + state.toString());
                if (state == State.CONNECTED || state == State.DISCONNECTING) {
                    startConnectionActivity();
                }
                state = State.DISCONNECTED;
                break;

            case ERROR:
                Log.e(DEBUG_TAG, "Error event: " + event.getMessage() + " at state: " + state.toString());
                if (state == State.CONNECTING) {
                    progressDialog.dismiss();
                } else if (state == State.CONNECTED) {
                    state = State.DISCONNECTING;
                }
                displayToast(event.getMessage());
                break;

            case MESSAGE:
                displayToast(event.getMessage());
                break;

            case FLIGHT_STARTED:
                displayToast("Flight started");
                break;

            case FLIGHT_ENDED:
                displayToast("Flight ended: " + event.getMessage());
                break;
        }
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void displayToast(final String message) {
        if (message != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private enum State {
        DISABLED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED,
    }

    public class LocalBinder extends Binder {
        public AdDroneService getService() {
            return AdDroneService.this;
        }
    }

    public class ControlForwarder implements UavManager.ControlDataSource {
        ControlViewModel controlViewModel;

        public ControlForwarder(ControlViewModel controlViewModel) {
            this.controlViewModel = controlViewModel;
        }

        @Override
        public ControlData getControlData() {

            if (controlViewModel.getCurrentControlData().getCommand().getValue() == ControlData.ControllerCommand.MANUAL.getValue()) {
                if (System.currentTimeMillis() - controlViewModel.getLastUpdate() > errorJoystickTime) {
                    ControlData controlData = new ControlData();
                    ControlData.ControllerCommand command = ControlData.ControllerCommand.ERROR_JOYSTICK;
                    controlData.setCommand(command);
                    return controlData;
                } else
                    return controlViewModel.getCurrentControlData();
            } else
                return controlViewModel.getCurrentControlData();
        }
    }
}
