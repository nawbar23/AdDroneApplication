package com.ericsson.addroneapplication.comunication;

import android.util.Log;

import com.ericsson.addroneapplication.comunication.data.ControlData;
import com.ericsson.addroneapplication.viewmodel.ControlViewModel;

/**
 * Created by nbar on 2016-08-30.
 */

public class ControlTask extends CommunicationTask {
    private static final String DEBUG_TAG = "AdDrone:" + ControlTask.class.getSimpleName();

    private ControlViewModel controlViewModel = null;

    public ControlTask(CommunicationHandler communicationHandler, TcpSocket tcpSocket, double frequency) {
        super(communicationHandler, tcpSocket, frequency);
    }

    public void setControlViewModel(ControlViewModel controlViewModel) {
        this.controlViewModel = controlViewModel;
    }

    @Override
    void task() {
        // send control message to controller
        ControlData controlData;
        if (controlViewModel == null) {
            controlData = new ControlData();
        } else {
            controlData = controlViewModel.getCurrentControlData();
        }
        Log.e(DEBUG_TAG, "Sending ControlData: " + controlData.toString());
        tcpSocket.send(controlData.getMessage().getByteArray());
    }

    @Override
    String getTaskName() {
        return "ControlTask";
    }
}
