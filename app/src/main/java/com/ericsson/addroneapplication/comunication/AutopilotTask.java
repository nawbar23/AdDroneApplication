package com.ericsson.addroneapplication.comunication;

import android.util.Log;

import com.ericsson.addroneapplication.comunication.data.AutopilotData;
import com.ericsson.addroneapplication.comunication.messages.AutopilotMessage;

import static com.ericsson.addroneapplication.comunication.CommunicationHandler.COMM_FREQ_DIVIDER;

/**
 * Created by nbar on 2016-08-30.
 * Task for handling asynchronous autopilot control.
 * When autopilot target is changed autopilot data event is sent to drone and task is waiting for ACK.
 * At WAITING state retransmission frequency is set to retransmitFreq. After reception of valid, popper AutopilotData
 * confirmation task is back to standard syncFreq.
 */

public class AutopilotTask extends CommunicationTask {
    private static final String DEBUG_TAG = "AdDrone:" + AutopilotTask.class.getSimpleName();

    private AutopilotData sentAutopilotData;

    private State state;

    public AutopilotTask(CommunicationHandler communicationHandler, TcpSocket tcpSocket, double frequency) {
        super(communicationHandler, tcpSocket, frequency);
        this.state = State.CONFIRMED;
    }

    public void sendAutopilotEvent(AutopilotData autopilotData) {
        Log.e(DEBUG_TAG, "Sending autopilotData event, " + autopilotData.toString());
        tcpSocket.send(autopilotData.getMessage().getByteArray());
        sentAutopilotData = autopilotData;
        state = State.WAITING;
        restart(frequency);
    }

    public void notifyAutopilotMessageReceived(AutopilotMessage autopilotMessage){
        AutopilotData autopilotData = autopilotMessage.getValue();
        if (autopilotData.equals(sentAutopilotData)) {
            Log.e(DEBUG_TAG, "Autopilot data confirmed");
            state = State.CONFIRMED;
            restart(frequency / COMM_FREQ_DIVIDER);
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
