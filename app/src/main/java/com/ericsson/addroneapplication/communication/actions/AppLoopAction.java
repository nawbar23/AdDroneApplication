package com.ericsson.addroneapplication.communication.actions;

import com.ericsson.addroneapplication.communication.CommHandler;
import com.ericsson.addroneapplication.communication.CommMessage;
import com.ericsson.addroneapplication.communication.data.DebugData;
import com.ericsson.addroneapplication.communication.data.SignalData;
import com.ericsson.addroneapplication.communication.events.CommEvent;
import com.ericsson.addroneapplication.communication.events.MessageEvent;
import com.ericsson.addroneapplication.uav_manager.UavEvent;

/**
 * Created by ebarnaw on 2016-10-14.
 */
public class AppLoopAction extends CommHandlerAction {

    public AppLoopAction(CommHandler commHandler){
        super(commHandler);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.APPLICATION_LOOP;
    }

    @Override
    public boolean isActionDone() {
        return true;
    }

    @Override
    public void start() {
        System.out.println("Starting app loop handling mode");
        commHandler.getPingTask().start();
    }

    @Override
    public void handleEvent(CommEvent event) throws Exception {
        if (event.getType() == CommEvent.EventType.MESSAGE_RECEIVED) {
            final MessageEvent messageEvent = ((MessageEvent)event);

            if (messageEvent.getMessageType() == CommMessage.MessageType.CONTROL) {
                // debug data received
                commHandler.getUavManager().setDebugData(new DebugData(messageEvent.getMessage()));

            } else if (messageEvent.getMessageType() == CommMessage.MessageType.SIGNAL) {
                if (messageEvent.matchSignalData(new SignalData(SignalData.Command.APP_LOOP, SignalData.Parameter.BREAK_ACK))) {
                    // connection broken by board
                    commHandler.getUavManager().notifyUavEvent(new UavEvent(UavEvent.Type.DISCONNECTED, "By board"));
                }
            }
        }
    }
}
