package com.ericsson.addroneapplication.uav_manager;

import com.ericsson.addroneapplication.communication.CommHandler;
import com.ericsson.addroneapplication.communication.actions.CommHandlerAction;
import com.ericsson.addroneapplication.communication.data.*;
import com.ericsson.addroneapplication.viewmodel.ControlViewModel;

import java.util.ArrayList;

/**
 * Created by ebarnaw on 2016-10-14.
 * Peruse of this class is to reflect data state of controller board.
 * Additionally this class manages frequent tasks and high level control.
 */
public class UavManager {

    private ArrayList<UavManagerListener> listeners;

    // data received form board
    private DebugData debugData;
    private AutopilotData boardAutopilotData;

    // settings received form board
    private CalibrationSettings calibrationSettings;
    private ControlSettings controlSettings;
    private RouteContainer routeContainer;

    // actual communication delay
    private long commDelay;

    // data to be send to board
    private ControlData controlData;
    private AutopilotData autopilotData;

    // main communication handler
    private CommHandler commHandler;

    private ControlViewModel controlViewModel;

    public UavManager() {
        this.listeners = new ArrayList<>();
        this.commDelay = 0;

        this.commHandler = new CommHandler(this);
    }

    public CommHandler getCommHandler() {
        return commHandler;
    }

    public void disconnectApplicationLoop() {
        preformAction(CommHandlerAction.ActionType.DISCONNECT);
    }

    public void startFlightLoop() {
        if (commHandler.getCommActionType() == CommHandlerAction.ActionType.APPLICATION_LOOP) {
            preformAction(CommHandlerAction.ActionType.FLIGHT_LOOP);
        }
    }

    public void endFlightLoop() {
        if (commHandler.getCommActionType() == CommHandlerAction.ActionType.FLIGHT_LOOP) {
            commHandler.endFlightLoop();
        }
    }

    public void startAccelerometerCalibration() {
        if (commHandler.getCommActionType() == CommHandlerAction.ActionType.APPLICATION_LOOP) {
            preformAction(CommHandlerAction.ActionType.CALIBRATE_ACCELEROMETER);
        }
    }

    private void preformAction(CommHandlerAction.ActionType actionType) {
        try {
            commHandler.preformAction(actionType);
        } catch (Exception e) {
            System.out.println("UavManager exception: " + e.toString());
            notifyUavEvent(new UavEvent(UavEvent.Type.ERROR, e.getMessage()));
        }
    }

    public void notifyUavEvent(UavEvent event) {
        try {
            updateCommState(event);
        }
        catch (Exception e) {
            System.out.println("UavManager update comm state error: " + e.getMessage());
        }
        for (UavManagerListener listener : listeners) {
            listener.handleUavEvent(event, this);
        }
    }

    private void updateCommState(UavEvent event) throws Exception {
        switch (event.getType()) {
            case CONNECTED:
                commHandler.preformAction(CommHandlerAction.ActionType.APPLICATION_LOOP);
                break;

            case DISCONNECTED:
                commHandler.disconnectSocket();
                break;
        }
    }

    public void notifyAutopilotEvent(AutopilotData autopilotData) {
        // TODO implement this feature
    }

    public void registerListener(UavManagerListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(UavManagerListener listener) {
        listeners.remove(listener);
    }

    public void setControlViewModel(ControlViewModel controlViewModel) {
        this.controlViewModel = controlViewModel;
    }

    public ControlViewModel getControlViewModel() {
        return controlViewModel;
    }

    public interface UavManagerListener {
        void handleUavEvent(UavEvent event, UavManager uavManager);
    }

    public DebugData getDebugData() {
        return debugData;
    }

    public void setDebugData(DebugData debugData) {
        this.debugData = debugData;
        notifyUavEvent(new UavEvent(UavEvent.Type.DEBUG_UPDATED));
    }

    public AutopilotData getBoardAutopilotData() {
        return boardAutopilotData;
    }

    public void setBoardAutopilotData(AutopilotData boardAutopilotData) {
        this.boardAutopilotData = boardAutopilotData;
        notifyUavEvent(new UavEvent(UavEvent.Type.AUTOPILOT_UPDATED));
    }

    public CalibrationSettings getCalibrationSettings() {
        return calibrationSettings;
    }

    public void setCalibrationSettings(CalibrationSettings calibrationSettings) {
        this.calibrationSettings = calibrationSettings;
        notifyUavEvent(new UavEvent(UavEvent.Type.CALIBRATION_UPDATED));
    }

    public ControlSettings getControlSettings() {
        return controlSettings;
    }

    public void setControlSettings(ControlSettings controlSettings) {
        this.controlSettings = controlSettings;
        notifyUavEvent(new UavEvent(UavEvent.Type.CONTROL_UPDATED));
    }

    public RouteContainer getRouteContainer() {
        return routeContainer;
    }

    public void setRouteContainer(RouteContainer routeContainer) {
        this.routeContainer = routeContainer;
        notifyUavEvent(new UavEvent(UavEvent.Type.ROUTE_UPDATED));
    }

    public long getCommDelay() {
        return commDelay;
    }

    public void setCommDelay(long commDelay) {
        System.out.println("Ping delay updated: " + String.valueOf(commDelay) + " ms");
        this.commDelay = commDelay;
        notifyUavEvent(new UavEvent(UavEvent.Type.PING_UPDATED));
    }

    public ControlData getControlData() {
        return controlData;
    }

    public void setControlData(ControlData controlData) {
        this.controlData = controlData;
    }

    public AutopilotData getAutopilotData() {
        return autopilotData;
    }

    public void setAutopilotData(AutopilotData autopilotData) {
        this.autopilotData = autopilotData;
    }

    public void setCommHandler(CommHandler commHandler) {
        this.commHandler = commHandler;
    }
}
