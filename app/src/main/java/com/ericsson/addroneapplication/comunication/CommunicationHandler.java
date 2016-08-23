package com.ericsson.addroneapplication.comunication;

import android.util.Log;
import android.util.SparseArray;

import com.ericsson.addroneapplication.comunication.data.PingPongData;
import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.comunication.messages.PingPongMessage;
import com.ericsson.addroneapplication.model.ConnectionInfo;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

public class CommunicationHandler implements StreamProcessor.StreamProcessorListener {
    private static final String DEBUG_TAG = "AdDrone:" + CommunicationHandler.class.getSimpleName();
    private ArrayList<CommunicationListener> listeners;

    private TcpSocket tcpSocket;
    private StreamProcessor streamProcessor;

    private PingPongHandler pingPongHandler;

    public CommunicationHandler() {
        this.listeners = new ArrayList<>();

        this.streamProcessor = new StreamProcessor(getSuppertetMessagesMap(), this);
        this.tcpSocket = new TcpSocket(this.streamProcessor);

        // TODO get ping frequency from settings
        this.pingPongHandler = new PingPongHandler(tcpSocket, 0.5);
    }

    public void connect(ConnectionInfo connectionInfo) {
        Log.e(DEBUG_TAG, "connecting...");

        // TODO implement connection algorithm

        notifyOnConnected();
    }

    public void disconnect() {

    }

    @Override
    public void onMessageReceived(CommunicationMessage message) {
        if (message.getMessageId() == CommunicationMessage.MessageId.PING_MESSAGE) {
            try {
                long pingDelay = pingPongHandler.handlePongReception((PingPongMessage) message);
                notifyOnPingUpdated(pingDelay);
            } catch (CommunicationException e) {
                notifyOnTimeout(TimeoutId.PING_TIMEOUT);
            }
        } else {
            notifyOnMessageReceived(message);
        }
    }

    public void notifyOnConnected() {
        Log.e(DEBUG_TAG, "notifyOnConnected");
        for (CommunicationListener listener : listeners) {
            listener.onConnected();
        }
    }

    public void notifyOnDisconnected() {
        Log.e(DEBUG_TAG, "notifyOnConnected");
        for (CommunicationListener listener : listeners) {
            listener.onDisconnected();
        }
    }

    public void notifyOnTimeout(TimeoutId timeoutId) {
        Log.e(DEBUG_TAG, "notifyOnConnect");
        for (CommunicationListener listener : listeners) {
            listener.onTimeout(timeoutId);
        }
    }

    public void notifyOnMessageReceived(CommunicationMessage message) {
        Log.e(DEBUG_TAG, "notifyOnMessageReceived");
        for (CommunicationListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }

    public void notifyOnPingUpdated(double pingDelay) {
        Log.e(DEBUG_TAG, "notifyOnPingUpdated");
        for (CommunicationListener listener : listeners) {
            listener.onPingUpdated(pingDelay);
        }
    }

    private Map<Byte, CommunicationMessage.MessageId> getSuppertetMessagesMap(){
        Map<Byte, CommunicationMessage.MessageId> map = new HashMap<>();
        map.put(CommunicationMessage.getPreambleById(
                CommunicationMessage.MessageId.CONTROL_MESSAGE)[0],
                CommunicationMessage.MessageId.CONTROL_MESSAGE);
        map.put(CommunicationMessage.getPreambleById(
                CommunicationMessage.MessageId.DEBUG_MESSAGE)[0],
                CommunicationMessage.MessageId.CONTROL_MESSAGE);
        map.put(CommunicationMessage.getPreambleById(
                CommunicationMessage.MessageId.PING_MESSAGE)[0],
                CommunicationMessage.MessageId.PING_MESSAGE);
        map.put(CommunicationMessage.getPreambleById(
                CommunicationMessage.MessageId.AUTOPILOT_MESSAGE)[0],
                CommunicationMessage.MessageId.AUTOPILOT_MESSAGE);
        return map;
    }

    public void registerListener(CommunicationListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(CommunicationListener listener) {
        listeners.remove(listener);
    }

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
}
