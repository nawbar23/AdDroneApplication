package com.ericsson.addroneapplication.comunication;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ericsson.addroneapplication.comunication.data.PingPongData;
import com.ericsson.addroneapplication.comunication.messages.PingPongMessage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nbar on 2016-08-23.
 * Ping - Pong feature handler.
 * Runs its own thread to send ping and handles pong reception
 */

public class PingPongHandler {
    private static final String DEBUG_TAG = "AdDrone:" + PingPongHandler.class.getSimpleName();

    private final double pingFrequency;
    private TcpSocket socket;

    private PingPongData sentPing;
    private long timestamp;
    private boolean pongReceived;

    private Timer timer;

    public PingPongHandler(TcpSocket socket, double pingFrequency) {
        this.pingFrequency = pingFrequency;
        this.socket = socket;
    }

    public void start() {
        Log.e(DEBUG_TAG, "Starting ping-pong task");
        this.pongReceived = true;
        this.timer = new Timer("ping_pong_timer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (pongReceived) {
                    sentPing = new PingPongData();
                    socket.send(sentPing.getMessage().getByteArray());
                    timestamp = System.currentTimeMillis();
                } else {
                    Log.e(DEBUG_TAG, "Ping receiving timeout");
                    pongReceived = true;
                }
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 1000, (long)((1.0 / this.pingFrequency) * 1000));
    }

    public void stop() {
        Log.e(DEBUG_TAG, "Stopping ping-pong task");
        this.timer.cancel();
    }

    public long handlePongReception(PingPongMessage pingPongMessage) throws CommunicationException{
        if (pingPongMessage.getValue().getKey() == sentPing.getKey()) {
            // valid ping measurement, compute ping time
            pongReceived = true;
            return (System.currentTimeMillis() - timestamp) / 2;
        } else {
            throw new CommunicationException("Pong key does not match to the ping key!");
        }
    }
}
