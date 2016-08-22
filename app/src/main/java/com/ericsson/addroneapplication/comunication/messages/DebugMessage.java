package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;
import com.ericsson.addroneapplication.comunication.data.DebugData;

/**
 * Created by nbar on 2016-08-22.
 */
public class DebugMessage extends CommunicationMessage {

    public DebugMessage(DebugData debugData) {

    }

    @Override
    public MessageId getMessageId() {
        return MessageId.DEBUG_MESSAGE;
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
    public int getMessageSize() {
        return PREAMBLE_SIZE + getPayloadSize() + CRC_SIZE;
    }

    @Override
    public CommunicationMessageValue getValue() {
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
