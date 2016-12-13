package com.addrone.multicopter.actions;

import com.addrone.multicopter.CommHandler;
import com.addrone.multicopter.data.SignalData;
import com.addrone.multicopter.events.CommEvent;
import com.addrone.multicopter.UavEvent;

/**
 * Created by nawba on 17.10.2016.
 */
public class DisconnectAction extends CommHandlerAction  {

    private boolean disconnectionProcedureDone;

    public DisconnectAction(CommHandler commHandler){
        super(commHandler);
        disconnectionProcedureDone = false;
    }

    @Override
    public boolean isActionDone() {
        return disconnectionProcedureDone;
    }

    @Override
    public void start() {
        System.out.println("Starting disconnection procedure");
        commHandler.stopCommTask(commHandler.getPingTask());
        commHandler.send(new SignalData(SignalData.Command.APP_LOOP, SignalData.Parameter.BREAK).getMessage());
    }

    @Override
    public void handleEvent(CommEvent event) throws Exception {
        if (event.matchSignalData(new SignalData(SignalData.Command.APP_LOOP, SignalData.Parameter.BREAK_ACK))) {
            // app loop disconnected
            disconnectionProcedureDone = true;
            commHandler.getUavManager().notifyUavEvent(new UavEvent(UavEvent.Type.DISCONNECTED));
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.DISCONNECT;
    }
}
