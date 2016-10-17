package com.ericsson.addroneapplication.communication.actions;

import com.ericsson.addroneapplication.communication.events.CommEvent;
import com.ericsson.addroneapplication.communication.CommHandler;

import static com.ericsson.addroneapplication.communication.actions.CommHandlerAction.ActionType.IDLE;

/**
 * Created by NawBar on 2016-10-12.
 */
public class IdleAction extends CommHandlerAction {

    public IdleAction(CommHandler commHandler){
        super(commHandler);
    }

    @Override
    public boolean isActionDone() {
        return true;
    }

    @Override
    public void start() {
        System.out.println("Starting IDLE action - no action");
    }

    @Override
    public void handleEvent(CommEvent event) {
        System.out.println("Idle action drops event");
    }

    @Override
    public ActionType getActionType() {
        return IDLE;
    }
}
