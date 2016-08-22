package com.ericsson.addroneapplication.comunication.data;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.comunication.messages.PingPongMessage;

/**
 * Created by nbar on 2016-08-22.
 */
public class PingPongData implements CommunicationMessageValue {

    int key;

    public PingPongData() {
        key = (int)(Math.random() * 100000);
    }

    public PingPongData(PingPongMessage message) {

    }

    @Override
    public CommunicationMessage getMessage() {
        return new PingPongMessage(this);
    }
}
