package com.ericsson.addroneapplication.communication.events;

/**
 * Created by nawba on 18.10.2016.
 */

public class SocketDisconnectedEvent extends CommEvent {

    @Override
    public EventType getType() {
        return EventType.SOCKET_DISCONNECTED;
    }
}