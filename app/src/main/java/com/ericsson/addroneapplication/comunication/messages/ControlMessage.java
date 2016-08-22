package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;
import com.ericsson.addroneapplication.comunication.data.ControlData;

/**
 * Created by nbar on 2016-08-19.
 */
public class ControlMessage extends CommunicationMessage {

    public ControlMessage(ControlData controlData) {

    }

    @Override
    public MessageId getMessageId() {
        return MessageId.CONTROL_MESSAGE;
    }

    @Override
    public byte[] getPreamble() {
        return new byte[]{'$', '$', '$', '$'};
    }

    @Override
    public int getPayloadSize() {
        return 32;
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
