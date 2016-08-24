package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;
import com.ericsson.addroneapplication.comunication.data.PingPongData;

/**
 * Created by nbar on 2016-08-22.
 */

public class PingPongMessage extends CommunicationMessage {

    public PingPongMessage(byte[] byteArray) {
        super(byteArray);
    }

    public PingPongMessage(PingPongData pingPongData) {

    }

    @Override
    public MessageId getMessageId() {
        return MessageId.PING_MESSAGE;
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
    public PingPongData getValue() {
        return new PingPongData(this);
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
