package com.ericsson.addroneapplication.comunication;

import android.os.AsyncTask;
import android.util.Log;

import com.ericsson.addroneapplication.model.ConnectionInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by nbar on 2016-08-23.
 * Handler for internet connection for one socket.
 * Provides connect, disconnect and send methods and interface for onPacketReceived event
 */

public class TcpSocket {
    private static final String DEBUG_TAG = "AdDrone:" + TcpSocket.class.getSimpleName();

    private TcpSocketListener listener;
    private State state;

    private Socket socket;
    private SocketConnection connection;

    private DataOutputStream outputStream;

    public TcpSocket(TcpSocketListener listener) {
        this.listener = listener;

        this.state = State.DISCONNECTED;
    }

    public void connect(ConnectionInfo connectionInfo) {
        this.socket = new Socket();

        try {
            this.socket.connect(new InetSocketAddress(connectionInfo.getIpAddress(), connectionInfo.getPort()), 3000);
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Error while connecting: " + e.getMessage());
        }

        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Error while connection output stream: " + e.getMessage());
        }

        Log.e(DEBUG_TAG, "Successfully connected to: " + connectionInfo.toString() + " starting receiving task...");

        connection.execute((Void)null);

    }

    public void disconnect() {

    }

    public void send(byte[] packet) {
        try {
            outputStream.write(packet, 0, packet.length);
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Error while sending: " + e.getMessage());
        }
    }

    public interface TcpSocketListener {
        void onPacketReceived(byte[] packet);
    }

    public enum State {
        CONNECTED,
        CONNECTING,
        DISCONNECTING,
        DISCONNECTED,
        ERROR;
    }

    public enum ConnectionThreadResult {
        NULL_SOCKET,
        RECEIVING_ERROR,
        SUCCESS;
    }

    private class SocketConnection extends AsyncTask<Void, Void, ConnectionThreadResult> {
        private final String DEBUG_TAG = "AdDrone:" + SocketConnection.class.getSimpleName();

        @Override
        protected ConnectionThreadResult doInBackground(Void... params) {
            try {
                DataInputStream mDataInputStream = new DataInputStream(socket.getInputStream());
                byte[] buffer = new byte[255];
                while(state != State.DISCONNECTING)
                {
                    int receptionSize = mDataInputStream.read(buffer);
                    if(receptionSize > 0) {
                        listener.onPacketReceived(buffer);
                    }
                }
            } catch (Exception e) {
                if(state != State.DISCONNECTING) {
                    return ConnectionThreadResult.RECEIVING_ERROR;
                }
            }
            return ConnectionThreadResult.SUCCESS;
        }

        @Override
        protected void onPostExecute(ConnectionThreadResult connectionThreadResult) {
            super.onPostExecute(connectionThreadResult);
            switch (connectionThreadResult) {
                case NULL_SOCKET:
                    Log.e(DEBUG_TAG, "Thread exits with NULL_SOCKET");
                case RECEIVING_ERROR:
                    Log.e(DEBUG_TAG, "Thread exits with RECEIVING_ERROR");
                case SUCCESS:
                    Log.e(DEBUG_TAG, "Thread exits with SUCCESS");
            }
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while closing socket: " + e.getMessage());
            }
        }
    }
}
