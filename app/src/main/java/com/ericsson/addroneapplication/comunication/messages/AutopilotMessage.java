package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.AutopilotData;
import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by nbar on 2016-08-22.
 */
public class AutopilotMessage extends CommunicationMessage {

    public AutopilotMessage(byte[] byteArray) {
        super(byteArray);
    }

    public AutopilotMessage(AutopilotData autopilotData) {
        this.payload = new byte[getPayloadSize()];
        ByteBuffer buffer = ByteBuffer.allocate(getPayloadSize());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putDouble(autopilotData.getLatitude());
        buffer.putDouble(autopilotData.getLongitude());
        buffer.putFloat(autopilotData.getRelativeAltitude());
        buffer.putInt(autopilotData.getFlags());
        System.arraycopy(buffer.array(), 0, this.payload, 0, getPayloadSize());

        // compute and set CRC for message
        setCrc();
    }

    @Override
    public MessageId getMessageId() {
        return MessageId.AUTOPILOT_MESSAGE;
    }

    @Override
    public byte[] getPreamble() {
        return new byte[]{'#', '#', '#', '#'};
    }

    @Override
    public int getPayloadSize() {
        return getPayloadSizeById(getMessageId());
    }

    @Override
    public AutopilotData getValue() {
        return new AutopilotData(this);
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
