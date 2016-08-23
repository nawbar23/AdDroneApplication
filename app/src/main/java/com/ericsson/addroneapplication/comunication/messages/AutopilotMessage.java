package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.AutopilotData;
import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;

/**
 * Created by nbar on 2016-08-22.
 */
public class AutopilotMessage extends CommunicationMessage {

    public AutopilotMessage(byte[] byteArray) {

    }

    public AutopilotMessage(AutopilotData autopilotData) {

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
    public CommunicationMessageValue getValue() {
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
