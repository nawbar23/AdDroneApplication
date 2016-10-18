package com.ericsson.addroneapplication.communication.actions;

import com.ericsson.addroneapplication.communication.CommHandler;
import com.ericsson.addroneapplication.communication.CommTask;
import com.ericsson.addroneapplication.communication.data.ControlData;
import com.ericsson.addroneapplication.communication.data.DebugData;
import com.ericsson.addroneapplication.communication.data.SignalData;
import com.ericsson.addroneapplication.communication.events.CommEvent;
import com.ericsson.addroneapplication.communication.events.MessageEvent;
import com.ericsson.addroneapplication.uav_manager.UavEvent;

/**
 * Created by ebarnaw on 2016-10-14.
 */
public class FlightLoopAction extends CommHandlerAction {

    public enum FlightLoopState {
        IDLE,
        INITIAL_COMMAND,
        FLING_STARTED,
        FLING,
    }

    private FlightLoopState state;

    private boolean flightLoopDone;

    public FlightLoopAction(CommHandler commHandler) {
        super(commHandler);
        state = FlightLoopState.IDLE;
        flightLoopDone = false;
    }

    @Override
    public boolean isActionDone() {
        return flightLoopDone;
    }

    @Override
    public void start() {
        System.out.println("Starting flight loop");
        commHandler.getPingTask().stop();
        flightLoopDone = false;
        state = FlightLoopState.INITIAL_COMMAND;
        commHandler.send(new SignalData(SignalData.Command.FLIGHT_LOOP, SignalData.Parameter.START).getMessage());
    }

    @Override
    public void handleEvent(CommEvent event) throws Exception {
        FlightLoopState actualState = state;
        switch (state) {
            case INITIAL_COMMAND:
                if (event.getType() == CommEvent.EventType.MESSAGE_RECEIVED) {
                    switch (((MessageEvent)event).getMessageType()) {
                        case CONTROL:
                            System.out.println("DebugData received when waiting for ACK on initial flight loop command");
                            commHandler.getUavManager().setDebugData(new DebugData(((MessageEvent) event).getMessage()));
                            break;

                        case SIGNAL:
                            if (event.matchSignalData(new SignalData(SignalData.Command.FLIGHT_LOOP, SignalData.Parameter.ACK))) {
                                System.out.println("Flight loop initial command successful");
                                state = FlightLoopState.FLING;
                                commHandler.getPingTask().start();
                                controlTask.start();
                                commHandler.getUavManager().notifyUavEvent(new UavEvent(UavEvent.Type.FLIGHT_STARTED));

                            } else if (event.matchSignalData(new SignalData(SignalData.Command.FLIGHT_LOOP, SignalData.Parameter.NOT_ALLOWED))) {
                                System.out.println("Flight loop not allowed!");
                                state = FlightLoopState.IDLE;
                                flightLoopDone = true;
                                commHandler.preformAction(ActionType.APPLICATION_LOOP);
                                commHandler.getUavManager().notifyUavEvent(new UavEvent(UavEvent.Type.MESSAGE, "Flight loop not allowed!"));

                            } else {
                                System.out.println("Unexpected event received !!!");
                            }
                            break;
                    }
                }
                break;

            case FLING:
                if (event.getType() == CommEvent.EventType.MESSAGE_RECEIVED) {
                    switch (((MessageEvent)event).getMessageType()) {
                        case CONTROL:
                            commHandler.getUavManager().setDebugData(new DebugData(((MessageEvent)event).getMessage()));
                            break;

                        case SIGNAL:
                            handleSignalWhileFlying(new SignalData(((MessageEvent)event).getMessage()));
                            break;
                    }
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

    private void handleSignalWhileFlying(final SignalData command) {
        if (command.getCommand() == SignalData.Command.FLIGHT_LOOP) {

            controlTask.stop();
            state = FlightLoopState.IDLE;
            flightLoopDone = true;

            if (command.getParameter() == SignalData.Parameter.BREAK) {
                commHandler.getUavManager().notifyUavEvent(new UavEvent(UavEvent.Type.FLIGHT_ENDED, "By user"));
            } else {
                commHandler.getUavManager().notifyUavEvent(new UavEvent(UavEvent.Type.FLIGHT_ENDED, "By board"));
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.FLIGHT_LOOP;
    }

    private CommTask controlTask = new CommTask(commHandler, 20) {
        @Override
        protected String getTaskName() {
            return "control_task";
        }

        @Override
        protected void task() {
            System.out.println("Controlling...");
            ControlData controlData = commHandler.getUavManager().getControlViewModel().getCurrentControlData();
        }
    };
}