package com.ericsson.addroneapplication.multicopter.actions;

import com.ericsson.addroneapplication.multicopter.data.CalibrationSettings;
import com.ericsson.addroneapplication.multicopter.data.SignalData;
import com.ericsson.addroneapplication.multicopter.events.CommEvent;
import com.ericsson.addroneapplication.multicopter.CommHandler;
import com.ericsson.addroneapplication.multicopter.events.SignalPayloadEvent;
import com.ericsson.addroneapplication.uav_manager.UavEvent;

import static com.ericsson.addroneapplication.multicopter.actions.CommHandlerAction.ActionType.CONNECT;

/**
 * Created by ebarnaw on 2016-10-13.
 */
public class ConnectAction extends CommHandlerAction {

    public enum ConnectState {
        IDLE,
        INITIAL_COMMAND,
        WAITING_FOR_CALIBRATION,
        WAITING_FOR_CALIBRATION_DATA,
        FINAL_COMMAND,
    }

    private ConnectState state;

    private boolean connectionProcedureDone;

    public ConnectAction(CommHandler commHandler){
        super(commHandler);
        state = ConnectState.IDLE;
        connectionProcedureDone = false;
    }

    @Override
    public boolean isActionDone() {
        return connectionProcedureDone;
    }

    @Override
    public void start() {
        System.out.println("Starting connection procedure");
        connectionProcedureDone = false;
        state = ConnectState.INITIAL_COMMAND;
        commHandler.send(new SignalData(SignalData.Command.START_CMD, SignalData.Parameter.START).getMessage());
    }

    @Override
    public void handleEvent(CommEvent event) throws Exception {
        ConnectState actualState = state;
        switch (state){
            case INITIAL_COMMAND:
                if (event.matchSignalData(
                        new SignalData(SignalData.Command.START_CMD, SignalData.Parameter.ACK))){
                    state = ConnectState.WAITING_FOR_CALIBRATION;
                    System.out.println("Initial command received successfully");
                } else {
                    System.out.println("Unexpected event received at state " + state.toString());
                }
                break;

            case WAITING_FOR_CALIBRATION:
                if (event.matchSignalData(
                        new SignalData(SignalData.Command.CALIBRATION_SETTINGS, SignalData.Parameter.READY))) {
                    state = ConnectState.WAITING_FOR_CALIBRATION_DATA;
                    System.out.println("Calibration done successfully, data ready");
                } else  if (event.matchSignalData(
                        new SignalData(SignalData.Command.CALIBRATION_SETTINGS, SignalData.Parameter.NON_STATIC))){
                    System.out.println("Calibration non static");
                    commHandler.getUavManager().notifyUavEvent(new UavEvent(UavEvent.Type.CALIBRATION_NON_STATIC));
                } else {
                    System.out.println("Unexpected event received at state " + state.toString());
                }
                break;

            case WAITING_FOR_CALIBRATION_DATA:
                if (event.getType() == CommEvent.EventType.SIGNAL_PAYLOAD_RECEIVED
                        && ((SignalPayloadEvent)event).getDataType() == SignalData.Command.CALIBRATION_SETTINGS_DATA) {
                    SignalPayloadEvent signalEvent = (SignalPayloadEvent)event;

                    state = ConnectState.FINAL_COMMAND;
                    System.out.println("Calibration settings received after adHoc calibration");
                    commHandler.send(new SignalData(SignalData.Command.CALIBRATION_SETTINGS, SignalData.Parameter.ACK).getMessage());
                    commHandler.getUavManager().setCalibrationSettings(((CalibrationSettings)signalEvent.getData()));

                    // send final start command
                    commHandler.send(new SignalData(SignalData.Command.APP_LOOP, SignalData.Parameter.START).getMessage());
                } else {
                    System.out.println("Unexpected event received at state " + state.toString());
                }
                break;

            case FINAL_COMMAND:
                if (event.matchSignalData(
                        new SignalData(SignalData.Command.APP_LOOP, SignalData.Parameter.ACK))) {
                    connectionProcedureDone = true;
                    state = ConnectState.IDLE;
                    System.out.println("Final command received successfully, connection procedure done");
                    commHandler.getUavManager().notifyUavEvent(new UavEvent(UavEvent.Type.CONNECTED));
                } else {
                    System.out.println("Unexpected event received at state " + state.toString());
                }
                break;

            default:
                throw new Exception("Event: " + event.toString() + " received at unknown state");
        }
        if (actualState != state) {
            System.out.println("HandleEvent done, transition: " + actualState.toString() + " -> " + state.toString());
        } else {
            System.out.println("HandleEvent done, no state change");
        }
    }

    @Override
    public ActionType getActionType() {
        return CONNECT;
    }
}