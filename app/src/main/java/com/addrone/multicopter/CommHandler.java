package com.addrone.multicopter;

import android.util.Log;

import com.addrone.multicopter.actions.AppLoopAction;
import com.addrone.multicopter.actions.CalibrateAccelAction;
import com.addrone.multicopter.actions.CommHandlerAction;
import com.addrone.multicopter.actions.ConnectAction;
import com.addrone.multicopter.actions.DisconnectAction;
import com.addrone.multicopter.actions.FlightLoopAction;
import com.addrone.multicopter.actions.IdleAction;
import com.addrone.multicopter.data.SignalData;
import com.addrone.multicopter.events.CommEvent;
import com.addrone.multicopter.events.MessageEvent;
import com.addrone.model.ConnectionInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NawBar on 2016-10-12.
 */
public class CommHandler implements CommInterface.CommInterfaceListener {

    private CommHandlerAction commHandlerAction;
    private CommInterface commInterface;
    private CommDispatcher dispatcher;

    private UavManager uavManager;

    private List<CommTask> runningTasks;

    public CommHandler(UavManager uavManager, CommInterface commInterface){
        this.commHandlerAction = new IdleAction(this);
        this.commInterface = commInterface;
        this.commInterface.setListener(this);
        this.dispatcher = new CommDispatcher(this);

        this.uavManager = uavManager;

        this.runningTasks = new ArrayList<>();
    }

    public void connectSocket(ConnectionInfo connectionInfo) {
        System.out.println("CommHandler: connectSocket");
        commInterface.connect(connectionInfo);
    }

    public void disconnectSocket() {
        System.out.println("CommHandler: disconnectSocket");
        stopAllTasks();
        commInterface.disconnect();
    }

    public void endFlightLoop() {
        ((FlightLoopAction)commHandlerAction).breakLoop();
    }

    public void preformAction(CommHandlerAction.ActionType actionType) throws Exception {
        if (commHandlerAction.isActionDone()){
            commHandlerAction = actionFactory(actionType);
            commHandlerAction.start();
        } else {
            throw new Exception("CommHandler: Previous action not ready at state: " + commHandlerAction.getActionName() + ", aborting...");
        }
    }

    public void handleCommEvent(CommEvent event){
        System.out.println("CommHandler: Event " + event.toString() + " received at action " + commHandlerAction.toString());

        switch (event.getType()) {
            case MESSAGE_RECEIVED:
                if (((MessageEvent)event).getMessageType() == CommMessage.MessageType.SIGNAL) {
                    SignalData signalData = new SignalData(((MessageEvent)event).getMessage());
                    if (signalData.getCommand() == SignalData.Command.PING_VALUE) {
                        uavManager.setCommDelay(handlePongReception(signalData));
                        return;
                    }
                }
                break;
        }

        try {
            commHandlerAction.handleEvent(event);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public UavManager getUavManager() {
        return uavManager;
    }

    public CommHandlerAction.ActionType getCommActionType(){
        return commHandlerAction.getActionType();
    }

    public void send(CommMessage message) {
        System.out.println("CommHandler: Sending message: " + message.toString());
        commInterface.send(message.getByteArray());
    }

    public void notifyActionDone() {
        System.out.println("CommHandler: notifyActionDone");
        try {
            preformAction(CommHandlerAction.ActionType.APPLICATION_LOOP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CommHandlerAction actionFactory(CommHandlerAction.ActionType actionType) throws Exception {
        switch (actionType){
            case IDLE:
                return new IdleAction(this);
            case CONNECT:
                return new ConnectAction(this);
            case DISCONNECT:
                return new DisconnectAction(this);
            case APPLICATION_LOOP:
                return new AppLoopAction(this);
            case FLIGHT_LOOP:
                return new FlightLoopAction(this);
            case CALIBRATE_ACCELEROMETER:
                return new CalibrateAccelAction(this);

            default:
                throw new Exception("CommHandler: Unsupported action type");
        }
    }

    @Override
    public void onDataReceived(byte[] data) {
        dispatcher.proceedReceiving(data);
    }

    @Override
    public void onError(IOException e) {
        System.out.println("CommHandler: onError: " + e.getMessage());
        uavManager.notifyUavEvent(new UavEvent(UavEvent.Type.ERROR, e.getMessage()));
        uavManager.notifyUavEvent(new UavEvent(UavEvent.Type.DISCONNECTED));
    }

    @Override
    public void onDisconnected() {
        System.out.println("CommHandler: onDisconnected");
        commHandlerAction = new IdleAction(this);
    }

    @Override
    public void onConnected() {
        System.out.println("CommHandler: onConnected");
        try {
            preformAction(CommHandlerAction.ActionType.CONNECT);
        } catch (Exception e) {
            e.printStackTrace();
            uavManager.notifyUavEvent(new UavEvent(UavEvent.Type.ERROR, e.getMessage()));
        }
    }

    private SignalData sentPing;
    private long timestamp;

    private PingTaskState state = PingTaskState.CONFIRMED;

    private CommTask pingTask = new CommTask(this, 0.5) {

        @Override
        protected String getTaskName() {
            return "ping_task";
        }

        @Override
        protected void task() {
            System.out.println("CommHandler: Pinging...");
            switch (state) {
                case CONFIRMED:
                    sentPing = new SignalData(SignalData.Command.PING_VALUE, (int) (Math.random() * 1000000000));
                    send(sentPing.getMessage());
                    timestamp = System.currentTimeMillis();
                    break;

                case WAITING:
                    Log.e(getTaskName(), "CommHandler: Ping receiving timeout");
                    state = PingTaskState.CONFIRMED;
                    break;
            }
        }
    };

    private long handlePongReception(final SignalData pingPongMessage) {
        if (pingPongMessage.getParameterValue() == sentPing.getParameterValue()) {
            // valid ping measurement, compute ping time
            state = PingTaskState.CONFIRMED;
            return (System.currentTimeMillis() - timestamp) / 2;
        } else {
            Log.e(pingTask.getTaskName(), "CommHandler: Pong key does not match to the ping key!");
            return 0;
        }
    }

    private enum PingTaskState {
        WAITING,
        CONFIRMED
    }

    public CommTask getPingTask() {
        return pingTask;
    }

    public void startCommTask(CommTask task) {
        task.start();
        runningTasks.add(task);
    }

    public void stopCommTask(CommTask task) {
        task.stop();
        runningTasks.remove(task);
    }

    private void stopAllTasks() {
        System.out.println("CommHandler: stopAllTasks");
        for (CommTask task : runningTasks) {
            stopCommTask(task);
        }
    }
}
