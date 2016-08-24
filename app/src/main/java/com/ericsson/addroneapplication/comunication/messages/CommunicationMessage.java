package com.ericsson.addroneapplication.comunication.messages;

import android.util.Log;

import com.ericsson.addroneapplication.comunication.StreamProcessor;
import com.ericsson.addroneapplication.comunication.data.CommunicationMessageValue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by nbar on 2016-08-19.
 * Base class for every communication message
 * Contains its preamble, provides interfaces for all usage methods
 * and handles CRC validation
 */
public abstract class CommunicationMessage {
    private static final int PREAMBLE_SIZE = 4;
    private static final int CRC_SIZE = 2;
    protected byte[] payload;

    protected short crc;

    public CommunicationMessage() {}

    public CommunicationMessage(byte[] byteArray){
        this.payload = new byte[getPayloadSize()];
        System.arraycopy(byteArray, 4, this.payload, 0, getPayloadSize());
        ByteBuffer buffer = ByteBuffer.wrap(byteArray, 4 + getPayloadSize(), 2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        this.crc = ((short) (buffer.get() & 0xff));
    }

    public abstract MessageId getMessageId();

    public abstract byte[] getPreamble();

    public abstract int getPayloadSize();

    public int getMessageSize() {
        return PREAMBLE_SIZE + getPayloadSize() + CRC_SIZE;
    }

    public abstract CommunicationMessageValue getValue();

    public abstract String toByteString();

    public abstract String toHexString();

    public void setCrc() {
        this.crc = computeCrc();
    }

    public byte[] getByteArray() {
        byte[] message = new byte[getMessageSize()];
        System.arraycopy(getPreamble(), 0, message, 0, 4);
        System.arraycopy(payload, 0, message, 4, getPayloadSize());
        message[getMessageSize() - 2] = (byte) (crc & 0xff);
        message[getMessageSize() - 1] = (byte) ((crc >> 8) & 0xff);
        return message;
    }

    public boolean isValid() {
        return crc == computeCrc();
    }

    private short computeCrc() {
        int crcShort = 0;
        for (int i = 0; i < getPayloadSize(); i++) {
            crcShort = ((crcShort  >>> 8) | (crcShort  << 8) )& 0xffff;
            crcShort ^= (payload[i] & 0xff);
            crcShort ^= ((crcShort & 0xff) >> 4);
            crcShort ^= (crcShort << 12) & 0xffff;
            crcShort ^= ((crcShort & 0xFF) << 5) & 0xffff;
        }
        crcShort &= 0xffff;
        return (short)crcShort;
    }

    public static byte[] getPreambleById(MessageId id) {
        switch (id) {
            case CONTROL_MESSAGE:
                return new byte[]{'$', '$', '$', '$'};
            case DEBUG_MESSAGE:
                return new byte[]{'$', '$', '$', '$'};
            case PING_MESSAGE:
                return new byte[]{'%', '%', '%', '%'};
            case AUTOPILOT_MESSAGE:
                return new byte[]{'#', '#', '#', '#'};
            default:
                // TODO throw some error
                return new byte[]{'a', 'a', 'a', 'a'};
        }
    }

    public static int getPayloadSizeById(MessageId id){
        switch (id) {
            case CONTROL_MESSAGE:
                return 32;
            case DEBUG_MESSAGE:
                return 32;
            case PING_MESSAGE:
                return 4;
            case AUTOPILOT_MESSAGE:
                return 24;
            default:
                // TODO throw some error
                return -1;
        }
    }

    public static CommunicationMessage inputMessageFactory(MessageId id, byte[] data){
        switch (id) {
            case DEBUG_MESSAGE:
                return new DebugMessage(data);
            case PING_MESSAGE:
                return new PingPongMessage(data);
            case AUTOPILOT_MESSAGE:
                return new AutopilotMessage(data);
            default:
                // TODO throw some error
                return new ControlMessage(data);
        }
    }

    public enum MessageId {
        CONTROL_MESSAGE,
        DEBUG_MESSAGE,
        PING_MESSAGE,
        AUTOPILOT_MESSAGE,
    }
}
