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

    private TcpSocketDataListener dataListener;
    private TcpSocketEventListener eventListener;
    private State state;

    private Socket socket;
    private SocketConnection connection;

    private DataOutputStream outputStream;

    public TcpSocket(TcpSocketDataListener dataListener, TcpSocketEventListener eventListener) {
        this.dataListener = dataListener;
        this.eventListener = eventListener;

        this.state = State.DISCONNECTED;
    }

    public void connect(ConnectionInfo connectionInfo) {
        this.connection = new SocketConnection();
        connection.execute(connectionInfo);
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

    public interface TcpSocketDataListener {
        void onPacketReceived(byte[] packet);
    }

    public interface TcpSocketEventListener {
        void onConnected();
        void onDisconnected();
        void onError(String message);
    }

    public enum State {
        CONNECTED,
        CONNECTING,
        DISCONNECTING,
        DISCONNECTED,
        ERROR;
    }

    public enum ConnectionThreadResult {
        CONNECTION_ERROR,
        CONNECTION_INPUT_STREAM_ERROR,
        RECEIVING_ERROR,
        SUCCESS
    }

    private class SocketConnection extends AsyncTask<ConnectionInfo, Void, ConnectionThreadResult> {
        private final String DEBUG_TAG = "AdDrone:" + SocketConnection.class.getSimpleName();

        @Override
        protected ConnectionThreadResult doInBackground(ConnectionInfo... params) {

            ConnectionInfo connectionInfo = params[0];

            Log.e(DEBUG_TAG, "Successfully started thread, " + connectionInfo.toString());

            socket = new Socket();

            try {
                socket.connect(new InetSocketAddress(connectionInfo.getIpAddress(), connectionInfo.getPort()), 3000);
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while connecting: " + e.getMessage());
                return ConnectionThreadResult.CONNECTION_ERROR;
            }

            Log.e(DEBUG_TAG, "Connected");

            try {
                outputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while connecting output stream: " + e.getMessage());
                return ConnectionThreadResult.CONNECTION_INPUT_STREAM_ERROR;
            }

            Log.e(DEBUG_TAG, "Connected connected stream");

            eventListener.onConnected();

            try {
                DataInputStream mDataInputStream = new DataInputStream(socket.getInputStream());
                byte[] buffer = new byte[255];
                while(state != State.DISCONNECTING)
                {
                    int receptionSize = mDataInputStream.read(buffer);
                    if(receptionSize > 0) {
                        dataListener.onPacketReceived(buffer);
                    }
                }
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "Error while receiving data: " + e.getMessage());
                return ConnectionThreadResult.RECEIVING_ERROR;
            }
            return ConnectionThreadResult.SUCCESS;
        }

        @Override
        protected void onPostExecute(ConnectionThreadResult connectionThreadResult) {
            super.onPostExecute(connectionThreadResult);
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while closing socket: " + e.getMessage());
            }
            switch (connectionThreadResult) {
                case CONNECTION_ERROR:
                    Log.e(DEBUG_TAG, "Thread exits with CONNECTION_ERROR");
                    eventListener.onError("Thread exits with CONNECTION_ERROR");
                    break;
                case CONNECTION_INPUT_STREAM_ERROR:
                    Log.e(DEBUG_TAG, "Thread exits with CONNECTION_INPUT_STREAM_ERROR");
                    eventListener.onError("Thread exits with CONNECTION_INPUT_STREAM_ERROR");
                    eventListener.onDisconnected();
                    break;
                case RECEIVING_ERROR:
                    Log.e(DEBUG_TAG, "Thread exits with RECEIVING_ERROR");
                    eventListener.onError("Thread exits with RECEIVING_ERROR");
                    eventListener.onDisconnected();
                    break;
                case SUCCESS:
                    Log.e(DEBUG_TAG, "Thread exits with SUCCESS");
                    eventListener.onDisconnected();
                    break;
            }
        }
    }
}
