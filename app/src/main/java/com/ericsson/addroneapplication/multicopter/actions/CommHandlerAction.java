package com.ericsson.addroneapplication.multicopter.actions;

import com.ericsson.addroneapplication.multicopter.events.CommEvent;
import com.ericsson.addroneapplication.multicopter.CommHandler;

/**
 * Created by NawBar on 2016-10-12.
 */
public abstract class CommHandlerAction {

    protected CommHandler commHandler;

    public enum ActionType {
        IDLE,
        CONNECT,
        DISCONNECT,
        APPLICATION_LOOP,
        FLIGHT_LOOP,
        CALIBRATE_ACCELEROMETER,
    }

    CommHandlerAction(CommHandler commHandler){
        this.commHandler = commHandler;
    }

    public abstract boolean isActionDone();

    public abstract void start();

    public abstract void handleEvent(CommEvent event) throws Exception;

    public abstract ActionType getActionType();

    public String getActionName(){
        return getActionType().toString();
    }

    @Override
    public String toString() {
        return getActionName();
    }
}