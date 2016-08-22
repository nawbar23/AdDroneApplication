package com.ericsson.addroneapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ericsson.addroneapplication.comunication.CommunicationHandler;
import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.model.ConnectionInfo;

/**
 * Created by nbar on 2016-08-19.
 * Service that contains CommunicationHandler
 * Can be stopped only when state is DISCONNECTED
 */
public class AdDroneService extends Service implements CommunicationHandler.CommunicationLisener {
    private static final String DEBUG_TAG = "AdDrone:" + AdDroneService.class.getSimpleName();

    private enum State {
        DISABLED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED,
    }

    private final IBinder mBinder = new LocalBinder();

    private State state;

    // main communication handler for internet connection
    private CommunicationHandler communicationHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(DEBUG_TAG, "onCreate");

        this.communicationHandler = new CommunicationHandler();

        this.state = State.DISABLED;
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
    }

    public void attemptConnection(ConnectionInfo connectionInfo)
    {
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

    private void startControlActivity() {

    }

    public State getState() {
        return state;
    }

    public class LocalBinder extends Binder {
        public AdDroneService getService() {
            return AdDroneService.this;
        }
    }

    @Override
    public void onPingUpdated(double pingDelay) {

    }

    @Override
    public void onMessageReceived(CommunicationMessage message) {

    }

    @Override
    public void onTimeout(CommunicationHandler.TimeoutId timeoutId) {

    }

    @Override
    public void onDisconnected() {
        this.state = State.DISCONNECTED;
    }

    @Override
    public void onConnected() {
        this.state = State.CONNECTED;
        startControlActivity();
    }
}
