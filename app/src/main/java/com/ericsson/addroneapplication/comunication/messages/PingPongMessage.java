package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;
import com.ericsson.addroneapplication.comunication.data.PingPongData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by nbar on 2016-08-22.
 */

public class PingPongMessage extends CommunicationMessage {

    public PingPongMessage(byte[] byteArray) {
        super(byteArray);
    }

    public PingPongMessage(PingPongData pingPongData) {
        this.payload = new byte[getPayloadSize()];
        ByteBuffer buffer = ByteBuffer.allocate(getPayloadSize());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(pingPongData.getKey());
        System.arraycopy(buffer.array(), 0, this.payload, 0, getPayloadSize());
        // compute and set CRC for message
        setCrc();
    }

    @Override
    public MessageId getMessageId() {
        return MessageId.PING_MESSAGE;
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
