package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.AutopilotData;
import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;

/**
 * Created by nbar on 2016-08-22.
 */
public class AutopilotMessage extends CommunicationMessage {

    public AutopilotMessage(AutopilotData autopilotData) {

    }

    @Override
    MessageId getMessageId() {
        return MessageId.AUTOPILOT_MESSAGE;
    }

    @Override
    byte[] getPreamble() {
        return new byte[]{'#', '#', '#', '#'};
    }

    @Override
    int getPayloadSize() {
        return 24;
    }

    @Override
    CommunicationMessageValue getValue() {
        return new AutopilotData(this);
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
