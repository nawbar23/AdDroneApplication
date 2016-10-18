package com.ericsson.addroneapplication.communication.events;

import com.ericsson.addroneapplication.communication.CommMessage;
import com.ericsson.addroneapplication.communication.data.SignalData;

/**
 * Created by NawBar on 2016-10-12.
 */
public abstract class CommEvent {
    public enum EventType{
        SOCKET_ERROR,
        SOCKET_DISCONNECTED,
        MESSAGE_RECEIVED,
        SIGNAL_PAYLOAD_RECEIVED,
    }

    public abstract EventType getType();

    @Override
    public String toString() {
        return getType().toString();
    }

    public SignalData getSignalData() throws Exception {
        if (getType() == CommEvent.EventType.MESSAGE_RECEIVED
                && ((MessageEvent)this).getMessageType() == CommMessage.MessageType.SIGNAL) {
            return new SignalData(((MessageEvent)this).getMessage());
        } else {
            throw new Exception("Can not get SignalData form event, bad event used!");
        }
    }

    public boolean matchSignalData(SignalData command) throws Exception {
        return getSignalData().equals(command);
    }
}
