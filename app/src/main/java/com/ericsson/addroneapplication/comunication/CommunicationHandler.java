package com.ericsson.addroneapplication.comunication;

import android.util.Log;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.model.ConnectionInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by nbar on 2016-08-19.
 * Class used by AdDroneService
 * Handles all internet communication tasks:
 * - sending and receiving data with specific frequency
 * - triggering communication timeout events
 * - retransmitting timeout/bad packets
 * - handling ping/pong feature
 * - dismissing communication messages which are not valid (wrong crc, etc.)
 * This class also stores all internet communication objects (sockets, etc.)
 */
public class CommunicationHandler {
    private static final String DEBUG_TAG = "AdDrone:" + CommunicationHandler.class.getSimpleName();

    public enum TimeoutId {
        CONNECTION_TIMEOUT,
        DEBUG_DATA_TIMEOUT,
        PING_TIMEOUT,
        AUTOPILOT_DATA_TIMEOUT,
    }

    public interface CommunicationListener {
        void onConnected();
        void onDisconnected();
        void onTimeout(TimeoutId timeoutId);
        void onMessageReceived(CommunicationMessage message);
        void onPingUpdated(double pingDelay);
    }

    ArrayList<CommunicationListener> listeners;

    public CommunicationHandler() {
        this.listeners = new ArrayList<>();
    }

    public void connect(ConnectionInfo connectionInfo) {
        Log.e(DEBUG_TAG, "connecting...");

        // TODO implement connection algorithm

        notifyOnConnected();
    }

    public void disconnect(){

    }

    public void notifyOnConnected(){
        Log.e(DEBUG_TAG, "notifyOnConnected");
        for (CommunicationListener listener : listeners) {
            listener.onConnected();
        }
    }

    public void notifyOnDisconnected(){
        Log.e(DEBUG_TAG, "notifyOnConnected");
        for (CommunicationListener listener : listeners) {
            listener.onDisconnected();
        }
    }

    public void notifyOnConnect(TimeoutId timeoutId){
        Log.e(DEBUG_TAG, "notifyOnConnect");
        for (CommunicationListener listener : listeners) {
            listener.onTimeout(timeoutId);
        }
    }

    public void notifyOnMessageReceived(CommunicationMessage message){
        Log.e(DEBUG_TAG, "notifyOnMessageReceived");
        for (CommunicationListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }

    public void notifyOnPingUpdated(double pingDelay){
        Log.e(DEBUG_TAG, "notifyOnPingUpdated");
        for (CommunicationListener listener : listeners) {
            listener.onPingUpdated(pingDelay);
        }
    }

    public void registerListener(CommunicationListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(CommunicationListener listener) {
        listeners.remove(listener);
    }
}
