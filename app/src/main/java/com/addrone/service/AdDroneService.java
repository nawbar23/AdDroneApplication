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
import com.addrone.connection.StartActivity;
import com.addrone.controller.ControlActivity;
import com.multicopter.java.UavEvent;
import com.multicopter.java.UavManager;
import com.multicopter.java.actions.CommHandlerAction;
import com.addrone.viewmodel.ControlViewModel;
import com.addrone.model.ConnectionInfo;
import com.multicopter.java.data.ControlData;

/**
 * Created by nbar on 2016-08-19.
 * Service that contains CommunicationHandler
 * Can be stopped only when state is DISCONNECTED
 */

public class AdDroneService extends Service implements UavManager.UavManagerListener {
    private static final String DEBUG_TAG = "AdDrone:" + AdDroneService.class.getSimpleName();

    private final IBinder mBinder = new LocalBinder();
    private State state;

    // main communication handler for UAV
    private UavManager uavManager;
    private ControlForwarder controlForwarder;

    private Handler handler;

    ProgressDialog progressDialog;

    public static ConnectionInfo actualConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(DEBUG_TAG, "onCreate");

        Context context = getApplicationContext();

        float defaultPing = Float.parseFloat(getResources().getString(R.string.pref_key__freq_default));
        SharedPreferences sharedPrefPing = context.getSharedPreferences(
                getString(R.string.pref_key_ping), Context.MODE_PRIVATE);
        float ping = sharedPrefPing.getFloat(getString(R.string.pref_key_ping), defaultPing);


        float defaultFreq = Float.parseFloat(getResources().getString(R.string.pref_key__freq_default));
        SharedPreferences sharedPrefFreq = context.getSharedPreferences(
                getString(R.string.pref_key_freq), Context.MODE_PRIVATE);
        float freq = sharedPrefFreq.getFloat(getString(R.string.pref_key_freq), defaultFreq);

        this.uavManager = new UavManager(new TcpClientSocket(), ping, freq);
        this.uavManager.registerListener(this);

        this.state = State.DISABLED;

        this.handler = new Handler();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        Log.e(DEBUG_TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(DEBUG_TAG, "onDestroy");
        uavManager.unregisterListener(this);
    }

    public UavManager getUavManager() {
        return uavManager;
    }

    public void attemptConnection(ConnectionInfo connectionInfo, ProgressDialog dialog) {
        switch (state) {
            case CONNECTED:
                Log.e(DEBUG_TAG, "attemptConnection at CONNECTED, starting activity");
                progressDialog.dismiss();
                startControlActivity();
                break;

            case CONNECTING:
                Log.e(DEBUG_TAG, "attemptConnection at CONNECTING, skipping");
                break;

            default:
                Log.e(DEBUG_TAG, "attemptConnection at default, connecting...");
                state = State.CONNECTING;
                progressDialog = dialog;
                actualConnection = connectionInfo;
                uavManager.getCommHandler().connectSocket(connectionInfo.getIpAddress(), connectionInfo.getPort());
                break;
        }
    }

    public void onDisconnectPush() {
        if (uavManager.getCommHandler().getCommActionType() == CommHandlerAction.ActionType.FLIGHT_LOOP) {
            uavManager.endFlightLoop();
        } else {
            state = State.DISCONNECTING;
            uavManager.disconnectApplicationLoop();
        }
    }

    public void onFlightPush() {
        if (uavManager.getCommHandler().getCommActionType() == CommHandlerAction.ActionType.APPLICATION_LOOP) {
            uavManager.startAccelerometerCalibration();
            //uavManager.startFlightLoop();
        } else if (uavManager.getCommHandler().getCommActionType() == CommHandlerAction.ActionType.FLIGHT_LOOP) {
            //uavManager.endFlightLoop();
        }
    }

    private void startControlActivity() {
        Intent intent = new Intent(getApplicationContext(), ControlActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void startConnectionActivity() {
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
                Log.e(DEBUG_TAG, "Disconnected event received at state: " + state.toString());
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
            return controlViewModel.getCurrentControlData();
        }
    }
}
