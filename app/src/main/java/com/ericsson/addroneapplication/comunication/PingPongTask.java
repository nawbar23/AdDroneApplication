package com.ericsson.addroneapplication.comunication;

import android.util.Log;

import com.ericsson.addroneapplication.comunication.data.PingPongData;
import com.ericsson.addroneapplication.comunication.messages.PingPongMessage;

/**
 * Created by nbar on 2016-08-30.
 */

public class PingPongTask extends CommunicationTask{
    private static final String DEBUG_TAG = "AdDrone:" + AutopilotTask.class.getSimpleName();

    private PingPongData sentPing;
    private long timestamp;

    private State state;

    public PingPongTask(CommunicationHandler communicationHandler, TcpSocket tcpSocket, double frequency) {
        super(communicationHandler, tcpSocket, frequency);
        this.state = State.CONFIRMED;
    }

    public long notifyPongTeceived(PingPongMessage pingPongMessage) throws CommunicationException{
        if (pingPongMessage.getValue().getKey() == sentPing.getKey()) {
            // valid ping measurement, compute ping time
            this.state = State.CONFIRMED;
            return (System.currentTimeMillis() - timestamp) / 2;
        } else {
            throw new CommunicationException("Pong key does not match to the ping key!");
        }
    }

    @Override
    void task() {
        switch (state) {
            case CONFIRMED:
                sentPing = new PingPongData();
                tcpSocket.send(sentPing.getMessage().getByteArray());
                timestamp = System.currentTimeMillis();
                break;

            case WAITING:
                Log.e(DEBUG_TAG, "Ping receiving timeout");
                state = State.CONFIRMED;
                break;
        }
    }

    @Override
    String getTaskName() {
        return "AutopilotTask";
    }

    private enum State {
        WAITING,
        CONFIRMED
    }
}
