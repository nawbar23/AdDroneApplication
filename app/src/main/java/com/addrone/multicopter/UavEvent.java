package com.addrone.multicopter;

/**
 * Created by ebarnaw on 2016-10-14.
 */
public class UavEvent {
    public enum Type {
        CONNECTED,
        DISCONNECTED,
        ERROR,
        MESSAGE,
        CALIBRATION_NON_STATIC,
        DEBUG_UPDATED,
        AUTOPILOT_UPDATED,
        CALIBRATION_UPDATED,
        CONTROL_UPDATED,
        ROUTE_UPDATED,
        PING_UPDATED,
        FLIGHT_STARTED,
        FLIGHT_ENDED,
    }

    private Type type;
    private String message;

    public UavEvent(Type type) {
        this.type = type;
    }

    public UavEvent(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
