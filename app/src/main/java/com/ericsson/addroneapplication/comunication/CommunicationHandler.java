package com.ericsson.addroneapplication.comunication;

import android.util.Log;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.comunication.messages.PingPongMessage;
import com.ericsson.addroneapplication.model.ConnectionInfo;

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

public class CommunicationHandler implements
        StreamProcessor.StreamProcessorListener,
        TcpSocket.TcpSocketEventListener {
    private static final String DEBUG_TAG = "AdDrone:" + CommunicationHandler.class.getSimpleName();
    private ArrayList<CommunicationListener> listeners;

    private TcpSocket tcpSocket;
    private StreamProcessor streamProcessor;

    private PingPongHandler pingPongHandler;

    public CommunicationHandler() {
        this.listeners = new ArrayList<>();

        this.streamProcessor = new StreamProcessor(getSupportedMessagesMap(), this);
        this.tcpSocket = new TcpSocket(this.streamProcessor, this);

        // TODO get ping frequency from settings
        this.pingPongHandler = new PingPongHandler(tcpSocket, 0.5);
    }

    public void connect(ConnectionInfo connectionInfo) {
        Log.e(DEBUG_TAG, "connecting...");
        this.tcpSocket.connect(connectionInfo);
    }

    public void disconnect() {
        this.tcpSocket.disconnect();
    }

    @Override
    public void onMessageReceived(CommunicationMessage message) {
        if (message.getMessageId() == CommunicationMessage.MessageId.PING_MESSAGE) {
            try {
                long pingDelay = pingPongHandler.handlePongReception((PingPongMessage) message);
                notifyOnPingUpdated(pingDelay);
            } catch (CommunicationException e) {
                notifyOnError("Ping timeout");
            }
        } else {
            notifyOnMessageReceived(message);
        }
    }

    @Override
    public void onConnected() {
        notifyOnConnected();
    }

    @Override
    public void onDisconnected() {
        notifyOnDisconnected();
    }

    @Override
    public void onError(String message) {
        notifyOnError(message);
    }

    public void notifyOnConnected() {
        Log.e(DEBUG_TAG, "notifyOnConnected");
        for (CommunicationListener listener : listeners) {
            listener.onConnected();
        }
    }

    public void notifyOnDisconnected() {
        Log.e(DEBUG_TAG, "notifyOnDisconnected");
        for (CommunicationListener listener : listeners) {
            listener.onDisconnected();
        }
    }

    public void notifyOnError(String message) {
        Log.e(DEBUG_TAG, "notifyOnError");
        for (CommunicationListener listener : listeners) {
            listener.onError(message);
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

    private Map<Byte, CommunicationMessage.MessageId> getSupportedMessagesMap(){
        Map<Byte, CommunicationMessage.MessageId> map = new HashMap<>();
        map.put(CommunicationMessage.getPreambleById(
                CommunicationMessage.MessageId.DEBUG_MESSAGE)[0],
                CommunicationMessage.MessageId.DEBUG_MESSAGE);
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

        void onError(String message);

        void onMessageReceived(CommunicationMessage message);

        void onPingUpdated(double pingDelay);
    }
}
