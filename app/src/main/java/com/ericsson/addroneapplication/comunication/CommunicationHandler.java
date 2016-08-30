package com.ericsson.addroneapplication.comunication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ericsson.addroneapplication.comunication.data.ControlData;
import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.comunication.messages.PingPongMessage;
import com.ericsson.addroneapplication.model.ConnectionInfo;
import com.ericsson.addroneapplication.viewmodel.ControlViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
    private ControlViewModel controlViewModel;

    private Context context;

    private TcpSocket tcpSocket;
    private StreamProcessor streamProcessor;

    private PingPongHandler pingPongHandler;

    private Timer controlTimer;

    public CommunicationHandler(Context context) {
        this.context = context;

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
        Log.e(DEBUG_TAG, "disconnecting...");
        this.controlTimer.cancel();
        this.pingPongHandler.stop();
        this.tcpSocket.disconnect();
    }

    @Override
    public void onMessageReceived(CommunicationMessage message) {
        if (message.getMessageId() == CommunicationMessage.MessageId.PING_MESSAGE) {
            try {
                long pingDelay = pingPongHandler.handlePongReception((PingPongMessage) message);
                notifyOnPingUpdated(pingDelay);
            } catch (CommunicationException e) {
                notifyOnError("Ping timeout", false);
            }
        } else {
            notifyOnMessageReceived(message);
        }
    }

    @Override
    public void onConnected() {
        notifyOnConnected();
        this.pingPongHandler.start();

        TimerTask controlTimerTask = new TimerTask() {
            @Override
            public void run() {
                // send control message to controller
                ControlData controlData;
                if (controlViewModel == null) {
                    controlData = new ControlData();
                } else {
                    controlData = controlViewModel.getCurrentControlData();
                }
                Log.e(DEBUG_TAG, "Sending ControlData: " + controlData.toString());
                tcpSocket.send(controlData.getMessage().getByteArray());
            }
        };
        // TODO set control frequency from settings
        this.controlTimer = new Timer("control_timer");
        this.controlTimer.scheduleAtFixedRate(controlTimerTask, 1000, 200);
    }

    @Override
    public void onDisconnected() {
        notifyOnDisconnected();
    }

    @Override
    public void onError(String message, boolean critical) {
        if (critical) {
            // close connection
            disconnect();
        }
        notifyOnError(message, critical);
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

    public void notifyOnError(String message, boolean critical) {
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

    public void notifyOnPingUpdated(long pingDelay) {
        Log.e(DEBUG_TAG, "notifyOnPingUpdated, delay: " + String.valueOf(pingDelay) + " ms, network generation: " + getNetworkInfo());
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

    public void setControlViewModel(ControlViewModel controlViewModel) {
        this.controlViewModel = controlViewModel;
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

        void onPingUpdated(long pingDelay);
    }

    @NonNull
    private String getNetworkInfo() {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4G";
                default:
                    return "Unknown";
            }
        }
        else {
            return "Unknown";
        }
    }
}
