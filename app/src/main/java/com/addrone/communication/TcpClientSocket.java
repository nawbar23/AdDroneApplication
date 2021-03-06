package com.addrone.communication;

import android.os.AsyncTask;
import android.util.Log;

import com.addrone.model.ConnectionInfo;
import com.skydive.java.CommInterface;

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

public class TcpClientSocket extends CommInterface {
    private static final String DEBUG_TAG = TcpClientSocket.class.getSimpleName();

    private State state;
    private Socket socket;

    private DataOutputStream outputStream;

    private String ipAddress;
    private int port;

    public TcpClientSocket() {
        this.state = State.DISCONNECTED;
    }

    public State getState() {
        return state;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void connect() {
        state = State.CONNECTING;
        SocketConnection connection = new SocketConnection();
        connection.execute(new ConnectionInfo(ConnectionInfo.Type.TCP_CLIENT, ipAddress, port));
    }

    @Override
    public void disconnect() {
        state = State.DISCONNECTING;
    }

    @Override
    public void send(final byte[] data, final int dataSize) {
        try {
            //Log.e(DEBUG_TAG, "Sending: 0x" + StreamProcessor.byteArrayToHexString(packet));
            outputStream.write(data, 0, dataSize);
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Error while sending: " + e.getMessage());
            e.printStackTrace();
            disconnect();
        }
    }

    private enum State {
        CONNECTED,
        CONNECTING,
        DISCONNECTING,
        DISCONNECTED
    }

    private class SocketConnection extends AsyncTask<ConnectionInfo, Void, Void> {
        private final String DEBUG_TAG = "AdDrone:" + SocketConnection.class.getSimpleName();

        @Override
        protected Void doInBackground(ConnectionInfo... params) {

            ConnectionInfo connectionInfo = params[0];

            Log.d(DEBUG_TAG, "Successfully started thread, " + connectionInfo.toString());

            socket = new Socket();

            try {
                socket.connect(new InetSocketAddress(connectionInfo.getIpAddress(), connectionInfo.getPort()), 3000);

            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while connecting: " + e.getMessage());
                listener.onError(e);
                return null;
            }

            try {
                outputStream = new DataOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while connecting output stream: " + e.getMessage());
                listener.onError(e);
                return null;
            }

            Log.d(DEBUG_TAG, "Connected connected stream");

            DataInputStream inputStream;
            try {
                inputStream = new DataInputStream(socket.getInputStream());

            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while connecting input stream: " + e.getMessage());
                listener.onError(e);
                return null;
            }

            state = State.CONNECTED;
            listener.onConnected();

            try {
                byte buffer[] = new byte[1024];
                while (state != State.DISCONNECTING) {
                    int len = inputStream.available();
                    if (len > 1024) len = 1024;
                    int dataSize = inputStream.read(buffer, 0, len);
                    listener.onDataReceived(buffer, dataSize);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                inputStream.close();

            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while receiving data: " + e.getMessage());
                listener.onError(e);
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            state = State.DISCONNECTED;
            listener.onDisconnected();

            try {
                socket.close();
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }

            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error while closing socket: " + e.getMessage());
            }
        }
    }
}
