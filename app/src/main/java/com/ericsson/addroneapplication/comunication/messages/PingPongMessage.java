package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;
import com.ericsson.addroneapplication.comunication.data.PingPongData;

/**
 * Created by nbar on 2016-08-22.
 */
public class PingPongMessage extends CommunicationMessage {

    public PingPongMessage(PingPongData pingPongData){

    }

    @Override
    MessageId getMessageId() {
        return MessageId.PING_MESSAGE;
    }

    @Override
    byte[] getPreamble() {
        return new byte[]{'%', '%', '%', '%'};
    }

    @Override
    int getMessageSize() {
        return PREAMBLE_SIZE + getPayloadSize() + CRC_SIZE;
    }

    @Override
    int getPayloadSize() {
        return 4;
    }

    @Override
    CommunicationMessageValue getValue() {
        return new PingPongData(this);
    }

    @Override
    String toByteString() {
        return null;
    }

    @Override
    String toHexString() {
        return null;
    }
}
