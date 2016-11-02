package com.ericsson.addroneapplication.multicopter.actions;

import com.ericsson.addroneapplication.multicopter.events.CommEvent;
import com.ericsson.addroneapplication.multicopter.CommHandler;

import static com.ericsson.addroneapplication.multicopter.actions.CommHandlerAction.ActionType.IDLE;

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
