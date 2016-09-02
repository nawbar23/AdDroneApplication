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

    private ControlData.SolverMode defaultSolverMode;

    private double freqDivider;

    public ControlTask(CommunicationHandler communicationHandler, TcpSocket tcpSocket, double frequency, double divider) {
        super(communicationHandler, tcpSocket, frequency);
        this.defaultSolverMode = ControlData.SolverMode.ANGLE_NO_YAW;
        this.freqDivider = divider;
    }

    public void setControlViewModel(ControlViewModel controlViewModel) {
        this.controlViewModel = controlViewModel;
    }

    public void setDefaultSolverMode(ControlData.SolverMode solver) {
        this.defaultSolverMode = solver;
    }

    @Override
    void task() {
        // send control message to controller
        ControlData controlData;
        if (controlViewModel == null) {
            controlData = new ControlData();
            controlData.setMode(defaultSolverMode);
        } else {
            controlData = controlViewModel.getCurrentControlData();
            controlData.setMode(defaultSolverMode);
        }
        Log.e(DEBUG_TAG, "Sending ControlData: " + controlData.toString());
        tcpSocket.send(controlData.getMessage().getByteArray());
    }

    @Override
    String getTaskName() {
        return "ControlTask";
    }
}
