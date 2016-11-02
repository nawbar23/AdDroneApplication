package com.ericsson.addroneapplication.communication;

import android.os.AsyncTask;
import android.util.Log;

import com.ericsson.addroneapplication.multicopter.CommDispatcher;
import com.ericsson.addroneapplication.multicopter.CommHandler;
import com.ericsson.addroneapplication.multicopter.events.SocketDisconnectedEvent;
import com.ericsson.addroneapplication.multicopter.events.SocketErrorEvent;
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

public class TcpClientSocket {
    private static final String DEBUG_TAG = "AdDrone:" + TcpClientSocket.class.getSimpleName();

    private CommHandler commHandler;
    private CommDispatcher commDispatcher;
    private State state;

    private Socket socket;
    private SocketConnection connection;

    public State getState() {
        return state;
    }

    private DataOutputStream outputStream;

    public TcpClientSocket(CommHandler commHandler, CommDispatcher commDispatcher) {
        this.commHandler = commHandler;
        this.commDispatcher = commDispatcher;
        this.state = State.DISCONNECTED;
    }

    public void connect(ConnectionInfo connectionInfo) {
        state = State.CONNECTING;
        connection = new SocketConnection();
        connection.execute(connectionInfo);
    }

    public void disconnect() {
        state =  State.DISCONNECTED;
    }

    public void send(byte[] packet) {
        try {
            //Log.e(DEBUG_TAG, "Sending: 0x" + StreamProcessor.byteArrayToHexString(packet));
            outputStream.write(packet, 0, packet.length);
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Error while sending: " + e.getMessage());
            disconnect();
        }
    }

    private enum State {
        CONNECTED,
        CONNECTING,
        DISCONNECTING,
        DISCONNECTED
    }

    private enum ConnectionThreadResult {
        CONNECTION_ERROR,
        CONNECTION_INPUT_STREAM_ERROR,
        CONNECTION_OUTPUT_STREAM_ERROR,
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

            try {
                outputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while connecting output stream: " + e.getMessage());
                return ConnectionThreadResult.CONNECTION_OUTPUT_STREAM_ERROR;
            }

            Log.e(DEBUG_TAG, "Connected connected stream");

            DataInputStream inputStream;
            try {
                inputStream = new DataInputStream(socket.getInputStream());
            } catch (IOException e){
                Log.e(DEBUG_TAG, "Error while connecting input stream: " + e.getMessage());
                return ConnectionThreadResult.CONNECTION_INPUT_STREAM_ERROR;
            }

            state = State.CONNECTED;
            commHandler.notifySocketConnected();

            try {
                while(state != State.DISCONNECTED) {
                    byte buffer[] = new byte[1024];
                    int dataSize = inputStream.read(buffer, 0, buffer.length);
                    if (dataSize != -1) {
                        byte[] tempArray = new byte[dataSize];
                        System.arraycopy(buffer, 0, tempArray, 0, dataSize);
                        commDispatcher.proceedReceiving(tempArray);
                    }
                }
                inputStream.close();
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
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while closing socket: " + e.getMessage());
            }
            switch (connectionThreadResult) {
                case CONNECTION_ERROR:
                    Log.e(DEBUG_TAG, "Thread exits with CONNECTION_ERROR");
                    commHandler.handleCommEvent(new SocketErrorEvent("Thread exits with CONNECTION_ERROR"));
                    break;
                case CONNECTION_INPUT_STREAM_ERROR:
                    Log.e(DEBUG_TAG, "Thread exits with CONNECTION_INPUT_STREAM_ERROR");
                    commHandler.handleCommEvent(new SocketErrorEvent("Thread exits with CONNECTION_INPUT_STREAM_ERROR"));
                    break;
                case CONNECTION_OUTPUT_STREAM_ERROR:
                    Log.e(DEBUG_TAG, "Thread exits with CONNECTION_OUTPUT_STREAM_ERROR");
                    commHandler.handleCommEvent(new SocketErrorEvent("Thread exits with CONNECTION_OUTPUT_STREAM_ERROR"));
                    break;
                case RECEIVING_ERROR:
                    Log.e(DEBUG_TAG, "Thread exits with RECEIVING_ERROR");
                    commHandler.handleCommEvent(new SocketErrorEvent("Thread exits with RECEIVING_ERROR"));
                    break;
                case SUCCESS:
                    Log.e(DEBUG_TAG, "Thread exits with SUCCESS");
                    break;
            }
            commHandler.handleCommEvent(new SocketDisconnectedEvent());
            state = State.DISCONNECTED;
        }
    }
}
