package com.ericsson.addroneapplication.communication;

import com.ericsson.addroneapplication.communication.actions.*;
import com.ericsson.addroneapplication.communication.data.SignalData;
import com.ericsson.addroneapplication.communication.events.CommEvent;
import com.ericsson.addroneapplication.communication.events.MessageEvent;
import com.ericsson.addroneapplication.communication.events.SocketErrorEvent;
import com.ericsson.addroneapplication.model.ConnectionInfo;
import com.ericsson.addroneapplication.uav_manager.UavEvent;
import com.ericsson.addroneapplication.uav_manager.UavManager;

/**
 * Created by NawBar on 2016-10-12.
 */
public class CommHandler {

    private CommHandlerAction commHandlerAction;

    private TcpSocket socket;

    private CommDispatcher commDispatcher;
    private UavManager uavManager;

    public CommHandler(UavManager uavManager){
        this.commHandlerAction = new IdleAction(this);

        this.commDispatcher = new CommDispatcher(this);
        this.uavManager = uavManager;

        this.socket = new TcpSocket(this, commDispatcher);
    }

    public void connectSocket(ConnectionInfo connectionInfo) {
        socket.connect(connectionInfo);
    }

    public void disconnectSocket() {
        socket.disconnect();
    }

    public void preformAction(CommHandlerAction.ActionType actionType) throws Exception {
        if (commHandlerAction.isActionDone()){
            commHandlerAction = actionFactory(actionType);
            commHandlerAction.start();
        } else {
            throw new Exception("Previous action not ready, aborting...");
        }
    }

    public void handleCommEvent(CommEvent event){
        System.out.println("Event " + event.toString() + " received at action " + commHandlerAction.toString());

        switch (event.getType()) {
            case MESSAGE_RECEIVED:
                if (((MessageEvent)event).getMessageType() == CommMessage.MessageType.SIGNAL) {
                    SignalData signalData = new SignalData(((MessageEvent)event).getMessage());
                    if (signalData.getCommand() == SignalData.Command.PING_VALUE) {
                        handlePongReception(signalData);
                        return;
                    }
                }
                break;

            case SOCKET_ERROR:
                uavManager.notifyUavEvent(new UavEvent(UavEvent.Type.ERROR, ((SocketErrorEvent)event).getMessage()));
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

    public void send(CommMessage message) {
        System.out.println("Sending message: " + message.toString());
        socket.send(message.getByteArray());
    }

    public void notifySocketConnected() {
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
                throw new Exception("Unsupported action type");
        }
    }

    public CommTask getPingTask() {
        return pingTask;
    }

    private CommTask pingTask = new CommTask(this, 0.5) {
        @Override
        protected String getTaskName() {
            return "ping_task";
        }

        @Override
        protected void task() {
            System.out.println("Pinging...");
        }
    };

    private void handlePongReception(final SignalData signalData) {
        System.out.println("Pong received...");
    }
}
