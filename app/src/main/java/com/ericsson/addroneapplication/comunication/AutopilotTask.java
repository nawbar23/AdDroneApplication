package com.ericsson.addroneapplication.comunication;

import android.util.Log;

import com.ericsson.addroneapplication.comunication.data.AutopilotData;
import com.ericsson.addroneapplication.comunication.messages.AutopilotMessage;

/**
 * Created by nbar on 2016-08-30.
 */

public class AutopilotTask extends CommunicationTask {
    private static final String DEBUG_TAG = "AdDrone:" + AutopilotTask.class.getSimpleName();

    private AutopilotData sentAutopilotData;

    private State state;
    private double retransmitFrequency;

    public AutopilotTask(CommunicationHandler communicationHandler, TcpSocket tcpSocket, double retransmitFrequency, double syncFrequency) {
        super(communicationHandler, tcpSocket, syncFrequency);
        this.retransmitFrequency = retransmitFrequency;
        this.state = State.CONFIRMED;
    }

    public void sendAutopilotEvent(AutopilotData autopilotData) {
        Log.e(DEBUG_TAG, "Sending autopilotData event, " + autopilotData.toString());
        tcpSocket.send(autopilotData.getMessage().getByteArray());
        sentAutopilotData = autopilotData;
        state = State.WAITING;
        restart(retransmitFrequency);
    }

    public void notifyAutopilotMessageReceived(AutopilotMessage autopilotMessage){
        AutopilotData autopilotData = autopilotMessage.getValue();
        if (autopilotData.equals(sentAutopilotData)) {
            Log.e(DEBUG_TAG, "Autopilot data confirmed");
            state = State.CONFIRMED;
            restart(frequency);
        }
    }

    @Override
    void task() {
        if (sentAutopilotData != null) {
            Log.e(DEBUG_TAG, "Retransmitting autopilot data at state: " + state.toString());
            tcpSocket.send(sentAutopilotData.getMessage().getByteArray());
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
