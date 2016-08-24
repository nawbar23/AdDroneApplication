package com.ericsson.addroneapplication.comunication.data;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.comunication.messages.PingPongMessage;

import java.nio.ByteBuffer;

/**
 * Created by nbar on 2016-08-22.
 */
public class PingPongData implements CommunicationMessageValue {

    int key;

    public PingPongData() {
        key = (int) (Math.random() * 1000000000);
    }

    public PingPongData(PingPongMessage message) {
        ByteBuffer buffer = message.getByteBuffer();
        key = buffer.getInt();
    }

    public int getKey() {
        return key;
    }

    @Override
    public CommunicationMessage getMessage() {
        return new PingPongMessage(this);
    }
}
