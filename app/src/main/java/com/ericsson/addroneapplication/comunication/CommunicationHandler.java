package com.ericsson.addroneapplication.comunication;

import android.util.Log;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.model.ConnectionInfo;

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

    public interface CommunicationLisener {
        void onConnected();
        void onDisconnected();
        void onTimeout(TimeoutId timeoutId);
        void onMessageReceived(CommunicationMessage message);
        void onPingUpdated(double pingDelay);
    }

    public CommunicationHandler() {
    }

    public void connect(ConnectionInfo connectionInfo) {
        Log.e(DEBUG_TAG, "connecting...");
    }

    public void disconnect(){

    }
}
