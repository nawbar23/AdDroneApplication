package com.ericsson.addroneapplication.communication.events;

import com.ericsson.addroneapplication.communication.CommMessage;

/**
 * Created by NawBar on 2016-10-12.
 */
public class MessageEvent extends CommEvent {

    private CommMessage message;

    public MessageEvent(CommMessage message) {
        this.message = message;
    }

    @Override
    public EventType getType() {
        return EventType.MESSAGE_RECEIVED;
    }

    public CommMessage getMessage() {
        return message;
    }

    public CommMessage.MessageType getMessageType() {
        return message.getType();
    }
}
