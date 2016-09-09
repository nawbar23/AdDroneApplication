package com.ericsson.addroneapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ericsson.addroneapplication.connection.StartActivity;
import com.ericsson.addroneapplication.comunication.CommunicationHandler;
import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.controller.ControlActivity;
import com.ericsson.addroneapplication.model.ConnectionInfo;
import com.ericsson.addroneapplication.viewmodel.ControlViewModel;

/**
 * Created by nbar on 2016-08-19.
 * Service that contains CommunicationHandler
 * Can be stopped only when state is DISCONNECTED
 */

public class AdDroneService extends Service implements CommunicationHandler.CommunicationListener {
    private static final String DEBUG_TAG = "AdDrone:" + AdDroneService.class.getSimpleName();

    private final IBinder mBinder = new LocalBinder();
    private State state;

    // main communication handler for internet connection
    private CommunicationHandler communicationHandler;

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(DEBUG_TAG, "onCreate");

        this.communicationHandler = new CommunicationHandler(getApplicationContext());
        this.communicationHandler.registerListener(this);

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

        this.communicationHandler.unregisterListener(this);
    }

    public CommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    public void attemptConnection(ConnectionInfo connectionInfo) {
        switch (state) {
            case CONNECTED:
                Log.e(DEBUG_TAG, "attemptConnection at CONNECTED, starting activity");
                startControlActivity();
                break;

            case CONNECTING:
                Log.e(DEBUG_TAG, "attemptConnection at CONNECTING, skipping");
                break;

            default:
                Log.e(DEBUG_TAG, "attemptConnection at default, connecting...");
                communicationHandler.connect(connectionInfo);
                break;
        }
    }

    public void attemptDisconnection() {
        this.state = State.DISCONNECTING;
        this.communicationHandler.disconnect();
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

    public void registerListener(CommunicationHandler.CommunicationListener listener) {
        communicationHandler.registerListener(listener);
    }

    public void unregisterListener(CommunicationHandler.CommunicationListener listener) {
        communicationHandler.registerListener(listener);
    }

    public void setControlViewModel(ControlViewModel controlViewModel) {
        communicationHandler.setControlViewModel(controlViewModel);
    }

    public State getState() {
        return state;
    }

    @Override
    public void onPingUpdated(long pingDelay) {

    }

    @Override
    public void onMessageReceived(CommunicationMessage message) {
        Log.e(DEBUG_TAG, message.getValue().toString());
    }

    @Override
    public void onError(String message) {
        if (this.state == State.CONNECTING) {
            this.state = State.DISCONNECTED;
        }
        displayToast(message);
    }

    @Override
    public void onDisconnected() {
        this.state = State.DISCONNECTED;
        startConnectionActivity();
    }

    @Override
    public void onConnected() {
        this.state = State.CONNECTED;
        startControlActivity();
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void displayToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
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
