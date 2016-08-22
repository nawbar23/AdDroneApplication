package com.ericsson.addroneapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ericsson.addroneapplication.comunication.CommunicationHandler;
import com.ericsson.addroneapplication.model.ConnectionInfo;

/**
 * Created by nbar on 2016-08-19.
 * Service that contains CommunicationHandler
 * Can be stopped only when state is DISCONNECTED
 */
public class AdDroneService extends Service {
    private static final String DEBUG_TAG = "AdDrone:" + AdDroneService.class.getSimpleName();

    private enum State {
        DISABLED,
        CONNECTED,
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
        if (isConnected()) {
            Log.e(DEBUG_TAG, "Service connected in state CONNECTED");
            startControlActivity();
        } else {
            communicationHandler.connect(connectionInfo);
        }
    }

    private void startControlActivity() {

    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isConnected()
    {
        return state == State.CONNECTED;
    }

    public class LocalBinder extends Binder {
        public AdDroneService getService() {
            return AdDroneService.this;
        }
    }
}
