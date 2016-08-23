package com.ericsson.addroneapplication.comunication;

import com.ericsson.addroneapplication.comunication.data.PingPongData;
import com.ericsson.addroneapplication.comunication.messages.PingPongMessage;

/**
 * Created by nbar on 2016-08-23.
 * Ping - Pong feature handler.
 * Runs its own thread to send ping and handles pong reception
 */

public class PingPongHandler {
    private final double pingFrequency;
    private TcpSocket socket;

    private PingPongData sentPing;
    private long timestamp;
    private boolean pongReceived;

    public PingPongHandler(TcpSocket socket, double pingFrequency) {
        this.pingFrequency = pingFrequency;
        this.socket = socket;
    }

    public void start() {

    }

    public void stop() {

    }

    public long handlePongReception(PingPongMessage pingPongMessage) throws CommunicationException{
        if (pingPongMessage.getValue().getKey() == sentPing.getKey()) {
            // valid ping measurement, compute ping time
            pongReceived = true;
            return System.currentTimeMillis() - timestamp;
        } else {
            throw new CommunicationException("Pong key does not match to the ping key!");
        }
    }
}
