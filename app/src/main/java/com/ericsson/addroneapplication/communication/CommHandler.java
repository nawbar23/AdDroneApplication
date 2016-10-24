package com.ericsson.addroneapplication.communication;

import android.util.Log;

import com.ericsson.addroneapplication.communication.actions.*;
import com.ericsson.addroneapplication.communication.data.SignalData;
import com.ericsson.addroneapplication.communication.events.CommEvent;
import com.ericsson.addroneapplication.communication.events.MessageEvent;
import com.ericsson.addroneapplication.communication.events.SocketErrorEvent;
import com.ericsson.addroneapplication.controller.StreamConnection;
import com.ericsson.addroneapplication.model.ConnectionInfo;
import com.ericsson.addroneapplication.uav_manager.UavEvent;
import com.ericsson.addroneapplication.uav_manager.UavManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NawBar on 2016-10-12.
 */
public class CommHandler {

    private CommHandlerAction commHandlerAction;
    private TcpSocket socket;
    private UavManager uavManager;
    private List<CommTask> runningTasks;

    public CommHandler(UavManager uavManager){

        this.commHandlerAction = new IdleAction(this);
        this.socket = new TcpSocket(this, new CommDispatcher(this));
        this.uavManager = uavManager;
        this.runningTasks = new ArrayList<>();
    }

    public void connectSocket(ConnectionInfo connectionInfo) {
        System.out.println("CommHandler: connectSocket");
        socket.connect(connectionInfo);
    }

    public void disconnectSocket() {
        System.out.println("CommHandler: disconnectSocket");
        stopAllTasks();
        socket.disconnect();
    }

    public void disconnectFlightLoop() {
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

            case SOCKET_ERROR:
                uavManager.notifyUavEvent(new UavEvent(UavEvent.Type.ERROR, ((SocketErrorEvent)event).getMessage()));
                uavManager.notifyUavEvent(new UavEvent(UavEvent.Type.DISCONNECTED));
                return;

            case SOCKET_DISCONNECTED:
                commHandlerAction = new IdleAction(this);
                return;
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
        socket.send(message.getByteArray());
    }

    public void notifySocketConnected() {
        System.out.println("CommHandler: notifySocketConnected");
        try {
            preformAction(CommHandlerAction.ActionType.CONNECT);
        } catch (Exception e) {
            e.printStackTrace();
            uavManager.notifyUavEvent(new UavEvent(UavEvent.Type.ERROR, e.getMessage()));
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

            default:
                throw new Exception("CommHandler: Unsupported action type");
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
