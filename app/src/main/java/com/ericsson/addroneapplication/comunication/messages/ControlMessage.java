package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;
import com.ericsson.addroneapplication.comunication.data.ControlData;

/**
 * Created by nbar on 2016-08-19.
 */
public class ControlMessage extends CommunicationMessage {

    public ControlMessage(byte[] byteArray) {
        super(byteArray);
    }

    public ControlMessage(ControlData controlData) {

    }

    @Override
    public MessageId getMessageId() {
        return MessageId.CONTROL_MESSAGE;
    }

    @Override
    public byte[] getPreamble() {
        return getPreambleById(getMessageId());
    }

    @Override
    public int getPayloadSize() {
        return getPayloadSizeById(getMessageId());
    }

    @Override
    public CommunicationMessageValue getValue() {
        return new ControlData(this);
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
