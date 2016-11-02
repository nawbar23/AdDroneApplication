package com.ericsson.addroneapplication.multicopter;

import com.ericsson.addroneapplication.model.ConnectionInfo;

/**
 * Created by ebarnaw on 2016-11-02.
 */

public abstract class CommInterface {

    private CommDispatcher commDispatcher;

    public void setCommDispatcher(CommDispatcher commDispatcher) {
        this.commDispatcher = commDispatcher;
    }

    public abstract void connect(ConnectionInfo connectionInfo);

    public abstract void disconnect();

    public abstract void send(final byte[] data);

    protected void onDataReceived(final byte[] data) {
        commDispatcher.proceedReceiving(data);
    }
}
