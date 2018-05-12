package com.addrone.communication;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.skydive.java.CommInterface;
import com.skydive.java.CommMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class UsbOtgPort extends CommInterface implements Runnable {
    private static final String DEBUG_TAG = TcpClientSocket.class.getSimpleName();

    private UsbManager manager;

    private UsbDeviceConnection connection;

    private UsbInterface usbCdcInterface;

    private UsbEndpoint usbCdcWrite;
    private UsbEndpoint usbCdcRead;

    private Thread readThread = null;
    private volatile boolean readThreadRunning = true;

    public UsbOtgPort(Context context) {
        this.manager = (UsbManager)context.getSystemService(Context.USB_SERVICE);;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void connect() {
        Log.i(DEBUG_TAG, "connect");
        if (manager != null) {
            HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
            for (UsbDevice dev : deviceList.values())
            {
                if (dev.getManufacturerName() != null &&
                        dev.getManufacturerName().contains("STMicroelectronics"))
                {
                    Log.i(DEBUG_TAG, "device found: " + dev.toString());
                    try {
                        open(dev);
                        listener.onConnected();
                    } catch (IOException e) {
                        listener.onError(e);
                    }
                    return;
                }
            }
            listener.onError(new IOException("STM device not found"));
        } else {
            listener.onError(new IOException("UsbManager is null"));
        }
    }

    @Override
    public void disconnect() {
        Log.e(DEBUG_TAG, "disconnect");
        readThreadRunning = false;
        if (connection == null) {
            return;
        }
        connection.releaseInterface(usbCdcInterface);
        connection.close();
        connection = null;
        listener.onDisconnected();
    }

    @Override
    public void send(byte[] data) {
        //Log.e(DEBUG_TAG, "Sending: 0x" + CommMessage.byteArrayToHexString(data));
        connection.bulkTransfer(usbCdcWrite, data, data.length, 200);
    }

    private void open(UsbDevice usbDevice) throws IOException
    {
        usbCdcWrite = null;
        usbCdcRead = null;

        for(int i = 0; i < usbDevice.getInterfaceCount(); i++) {
            // communications device class (CDC) type device
            if(usbDevice.getInterface(i).getInterfaceClass() == UsbConstants.USB_CLASS_CDC_DATA) {
                usbCdcInterface = usbDevice.getInterface(i);

                // find the endpoints
                for(int j = 0; j < usbCdcInterface.getEndpointCount(); j++) {
                    if(usbCdcInterface.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if(usbCdcInterface.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT) {
                            // from host to device
                            usbCdcWrite = usbCdcInterface.getEndpoint(j);
                        }
                        if(usbCdcInterface.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN) {
                            // from device to host
                            usbCdcRead = usbCdcInterface.getEndpoint(j);
                        }
                    }
                }
            }
        }
        if (usbCdcRead == null || usbCdcWrite == null) {
            throw new IOException("Could not find IO endpoints");
        }

        connection = manager.openDevice(usbDevice);
        if (connection == null) {
            throw new IOException("Could not open connection");
        }

        if (!connection.claimInterface(usbCdcInterface, true)) {
            throw new IOException("Could not claim interface");
        }

        connection.controlTransfer(0x21, 0x22, 0x1, 0, null, 0, 0);

        int baudRate = 500000;
        byte stopBitsByte = 1;
        byte parityBitesByte = 0;
        byte dataBits = 8;
        byte[] msg = {
                (byte) (baudRate & 0xff),
                (byte) ((baudRate >> 8) & 0xff),
                (byte) ((baudRate >> 16) & 0xff),
                (byte) ((baudRate >> 24) & 0xff),
                stopBitsByte,
                parityBitesByte,
                dataBits
        };

        connection.controlTransfer(UsbConstants.USB_TYPE_CLASS | 0x01, 0x20, 0, 0, msg, msg.length, 5000);

        readThreadRunning = true;
        readThread = new Thread(this);
        readThread.start();
    }

    @Override
    public void run()
    {
        Log.e(DEBUG_TAG, "Starting receiving thread");

        ByteBuffer buffer = ByteBuffer.allocate(64);
        UsbRequest request = new UsbRequest();
        request.initialize(connection, usbCdcRead);

        while(readThreadRunning)
        {
            // queue a request on the interrupt endpoint
            request.queue(buffer, buffer.capacity());

            // wait for status event
            if(connection.requestWait() == request)
            {
                //Log.e(DEBUG_TAG,"Read: " + CommMessage.byteArrayToHexString(buffer.array()));
                listener.onDataReceived(buffer.array(), buffer.position());
            }
            else
            {
                Log.e(DEBUG_TAG, "Was not able to read from USB device, ending listening thread");
                readThreadRunning = false;
                break;
            }
        }
    }
}
