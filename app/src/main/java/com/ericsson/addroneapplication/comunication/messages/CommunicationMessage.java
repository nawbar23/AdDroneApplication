package com.ericsson.addroneapplication.comunication.messages;

import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;

/**
 * Created by nbar on 2016-08-19.
 * Base class for every communication message
 * Contains its preamble, provides interfaces for all usage methods
 * and handles CRC validation
 */
public abstract class CommunicationMessage {
    protected static final int PREAMBLE_SIZE = 4;
    protected static final int CRC_SIZE = 2;
    private byte[] payload;
    private short crc;

    abstract MessageId getMessageId();

    abstract byte[] getPreamble();

    abstract int getPayloadSize();

    public int getMessageSize() {
        return PREAMBLE_SIZE + getPayloadSize() + CRC_SIZE;
    }

    abstract CommunicationMessageValue getValue();

    abstract String toByteString();

    abstract String toHexString();

    public boolean isValid() {
        return crc == computeCrc();
    }

    public short computeCrc() {
        // TODO implement this algorithm
        return 0;
    }

    public enum MessageId {
        CONTROL_MESSAGE,
        DEBUG_MESSAGE,
        PING_MESSAGE,
        AUTOPILOT_MESSAGE,
    }
}
