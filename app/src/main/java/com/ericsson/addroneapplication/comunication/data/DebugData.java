package com.ericsson.addroneapplication.comunication.data;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.comunication.messages.DebugMessage;

/**
 * Created by nbar on 2016-08-22.
 * Container tha stores most important telemetry data from drone:
 * - euler angles - rotation of drone
 * - position (lat, lon, alt), altitude is relative to start
 * - vLoc - speed of drone relatively to ground
 * - controller state - actual control command used by controller
 * - flags:
 * GPS fix | GPS 3D fix | low. bat. volt. | errorHandling | autopilotUsed | solver1 | solver2
 */
public class DebugData implements CommunicationMessageValue {

    private float roll, pitch, yaw;

    private float latitude, longitude;
    private float relativeAltitude;

    private float vLoc;

    private short controllerState;
    private byte flags;

    public DebugData() {

    }

    public DebugData(DebugMessage message) {

    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getRelativeAltitude() {
        return relativeAltitude;
    }

    public void setRelativeAltitude(float relativeAltitude) {
        this.relativeAltitude = relativeAltitude;
    }

    public float getvLoc() {
        return vLoc;
    }

    public void setvLoc(float vLoc) {
        this.vLoc = vLoc;
    }

    public short getControllerState() {
        return controllerState;
    }

    public void setControllerState(short controllerState) {
        this.controllerState = controllerState;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    @Override
    public CommunicationMessage getMessage() {
        return null;
    }
}
