package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.DebugData;

/**
 * Created by nbar on 2016-08-22.
 */
public class DebugMessage extends CommunicationMessage {

    public DebugMessage(byte[] byteArray) {
        super(byteArray);
    }

    public DebugMessage(DebugData debugData) {

    }

    @Override
    public MessageId getMessageId() {
        return MessageId.DEBUG_MESSAGE;
    }

    @Override
    public DebugData getValue() {
        return new DebugData(this);
    }

    @Override
    public String toByteString() {
        return null;
    }

    @Override
    public String toHexString() {
        return null;
    }
}
