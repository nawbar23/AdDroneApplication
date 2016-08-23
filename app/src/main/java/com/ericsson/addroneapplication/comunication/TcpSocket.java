package com.ericsson.addroneapplication.comunication;

import com.ericsson.addroneapplication.model.ConnectionInfo;

/**
 * Created by nbar on 2016-08-23.
 * Handler for internet connection for one socket.
 * Provides connect, disconnect and send methods and interface for onPacketReceived event
 */

public class TcpSocket {
    private static final String DEBUG_TAG = "AdDrone:" + TcpSocket.class.getSimpleName();

    private TcpSocketListener listener;
    private State state;

    public TcpSocket(TcpSocketListener listener) {
        this.listener = listener;

        this.state = State.DISCONNECTED;
    }

    public void connect(ConnectionInfo connectionInfo) {

    }

    public void disconnect() {

    }

    public void send(byte[] packet) {

    }

    public interface TcpSocketListener {
        void onPacketReceived(byte[] packet);
    }

    public enum State {
        CONNECTED,
        CONNECTING,
        DICONNECTING,
        DISCONNECTED,
        ERROR;
    }
}
