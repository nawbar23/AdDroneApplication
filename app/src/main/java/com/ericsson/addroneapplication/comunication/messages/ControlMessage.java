package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;
import com.ericsson.addroneapplication.comunication.data.ControlData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by nbar on 2016-08-19.
 */
public class ControlMessage extends CommunicationMessage {

    public ControlMessage(byte[] byteArray) {
        super(byteArray);
    }

    public ControlMessage(ControlData controlData) {
        this.payload = new byte[getPayloadSize()];
        ByteBuffer buffer = ByteBuffer.allocate(getPayloadSize());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putFloat(controlData.getRoll());
        buffer.putFloat(controlData.getPitch());
        buffer.putFloat(controlData.getYaw());
        buffer.putFloat(controlData.getThrottle());
        buffer.putShort(controlData.getCommand().getValue());
        buffer.put(controlData.getMode().getValue());
        System.arraycopy(buffer.array(), 0, this.payload, 0, getPayloadSize());
        // compute and set CRC for message
        setCrc();
    }

    @Override
    public MessageId getMessageId() {
        return MessageId.CONTROL_MESSAGE;
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
