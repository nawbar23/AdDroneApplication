package com.ericsson.addroneapplication.controller;

import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by nbar on 2016-09-05.
 */

public class StreamConnection extends Thread {

    private final static int MAX_BUFFER = 2500000;

    public interface OnNewFrameListener {
        void setNewFrame(byte[] array, int length);
    }

    private OnNewFrameListener listener;
    private boolean connected = false;

    public StreamConnection(OnNewFrameListener onNewFrameListener) {
        listener = onNewFrameListener;
    }

    @Override
    public void run() {

        Socket clientSocket = null;
        try {
            clientSocket = new Socket("192.168.25.1", 25505);
            Log.i("DDD", "connected");
            connected = true;
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

            byte buffer[] = new byte[4];
            byte imageBuffer[] = new byte[MAX_BUFFER];

            while(connected) {
                int dataSize = dataInputStream.read(buffer, 0, buffer.length);

                if (dataSize != -1) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, 4);
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

                    int imageLength = byteBuffer.getInt();

                    if(imageLength > MAX_BUFFER) {
                        dataInputStream.skipBytes(imageLength);
                    } else {

                        int readed = 0;
                        while (readed < imageLength) {
                            readed += dataInputStream.read(imageBuffer, readed, imageLength - readed);
                        }

                        listener.setNewFrame(imageBuffer, imageLength);
                    }

                    for (int i = 0; i < 4; i++) {
                        buffer[i] = 0;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(clientSocket != null)
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static String byteToHexString(char b) {
        String ret = "";
        int intVal = b & 0xff;
        if (intVal < 0x10) ret += "0";
        ret += Integer.toHexString(intVal);
        return ret;
    }

    public void disconnect() {
        connected = false;
    }
}