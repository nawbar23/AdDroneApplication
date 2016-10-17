package com.ericsson.addroneapplication.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ericsson.addroneapplication.communication.actions.CommHandlerAction;
import com.ericsson.addroneapplication.connection.StartActivity;
import com.ericsson.addroneapplication.controller.ControlActivity;
import com.ericsson.addroneapplication.model.ConnectionInfo;
import com.ericsson.addroneapplication.uav_manager.UavEvent;
import com.ericsson.addroneapplication.uav_manager.UavManager;
import com.ericsson.addroneapplication.viewmodel.ControlViewModel;

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

    private Handler handler;

    ProgressDialog progressDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(DEBUG_TAG, "onCreate");

        this.uavManager = new UavManager();
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
                uavManager.getCommHandler().connectSocket(connectionInfo);
                break;
        }
    }

    public void attemptDisconnection() {
        state = State.DISCONNECTING;
        uavManager.preformAction(CommHandlerAction.ActionType.DISCONNECT);
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
        uavManager.setControlViewModel(controlViewModel);
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
                state = State.DISCONNECTED;
                startConnectionActivity();
                break;

            case ERROR:
                if (this.state == State.CONNECTING) {
                    this.state = State.DISCONNECTED;
                    progressDialog.dismiss();
                }
                displayToast(event.getMessage());
                break;

            case DEBUG_UPDATED:
            case AUTOPILOT_UPDATED:
            case PING_UPDATED:
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
}
