package com.ericsson.addroneapplication.communication.events;

/**
 * Created by nawba on 17.10.2016.
 */

public class SocketErrorEvent extends CommEvent {

    private String message;

    public SocketErrorEvent(String message) {
        this.message = message;
    }

    @Override
    public EventType getType() {
        return EventType.SOCKET_ERROR;
    }

    public String getMessage() {
        return message;
    }
}

