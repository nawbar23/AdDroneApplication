package com.addrone.multicopter.data;

import android.util.Log;

import com.addrone.multicopter.CommMessage;

import java.nio.ByteBuffer;

/**
 * Created by nbar on 2016-08-22.
 * Container tha stores most important telemetry data from drone:
 * - euler angles - rotation of drone
 * - position (lat, lon, alt), altitude is relative to start
 * - vLoc - speed of drone relatively to ground
 * - controller state - actual control command used by controller
 * - flags:
 * GPS fix | GPS 3D fix | low. bat. volt. | errorHandling | autopilotUsed | solver1 | solver2
 * - battery voltage [volts]
 */
public class DebugData {
    private static final String DEBUG_TAG = "AdDrone:" + DebugData.class.getSimpleName();

    private float roll, pitch, yaw;

    private float latitude, longitude;
    private float relativeAltitude;

    private float vLoc;

    private ControllerState controllerState;
    private Flags flags;

    private byte battery;

    public DebugData() {

    }

    public DebugData(final CommMessage message) {
        ByteBuffer buffer = message.getByteBuffer();
        this.roll = buffer.getFloat();
        this.pitch = buffer.getFloat();
        this.yaw = buffer.getFloat();
        this.latitude = buffer.getFloat();
        this.longitude = buffer.getFloat();
        this.relativeAltitude = buffer.getFloat();
        this.vLoc = buffer.getFloat();
        this.controllerState = ControllerState.getControllerState(buffer.getShort());
        this.flags = new Flags(8, buffer.get());
        this.battery = buffer.get();
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
        return yaw + (float)Math.PI;
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

    public float getVLoc() {
        return vLoc;
    }

    public void setvLoc(float vLoc) {
        this.vLoc = vLoc;
    }

    public ControllerState getControllerState() {
        return controllerState;
    }

    public void setControllerState(ControllerState controllerState) {
        this.controllerState = controllerState;
    }

    public byte getFlags() {
        return (byte)flags.getFlags();
    }

    public void setFlags(byte flags) {
        this.flags = new Flags(8, flags);
    }

    public boolean getFlagState(FlagId id) {
        try {
            return flags.getFlagState(id.getValue());
        } catch (Exception e) {
            Log.e(DEBUG_TAG, e.getMessage());
            return false;
        }
    }

    public void setFLagState(FlagId id, boolean state) {
        try {
            flags.setFlagsState(id.getValue(), state);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, e.getMessage());
        }
    }

    public byte getBattery() {
        return battery;
    }

    public void setBattery(byte battery) {
        this.battery = battery;
    }

    public boolean isStopState() {
        return this.controllerState == ControllerState.STOP;
    }

    @Override
    public String toString() {
        // TODO format this string for better presentation
        String result = "Rotation: roll: " + String.valueOf(roll) + " pitch: " + String.valueOf(pitch) + " yaw: " + String.valueOf(yaw);
        result += (", Position: lat: " + String.valueOf(latitude) + " lon: " + String.valueOf(longitude) + " alt: " + String.valueOf(relativeAltitude));
        result += (", Controller state: " + controllerState.toString() + "(" + String.valueOf(controllerState.getValue()) + ")");
        return result;
    }

    public CommMessage getMessage() {
        return null;
    }

    public enum ControllerState {
        IDLE((short)0),
        // manual control
        MANUAL(ControlData.ControllerCommand.MANUAL.getValue()),
        // auto lading with specified descend rate
        AUTOLANDING(ControlData.ControllerCommand.AUTOLANDING.getValue()),
        // auto lading with specified descend rate and position hold
        AUTOLANDING_AP(ControlData.ControllerCommand.AUTOLANDING_AP.getValue()),
        // auto altitude hold, throttle value is now specifying descend/climb rate
        // th = 0 -> -v, th = 0.5 -> 0, th = 1.0 -> v
        HOLD_ALTITUDE(ControlData.ControllerCommand.HOLD_ALTITUDE.getValue()),
        // auto position hold, (hold altitude enabled)
        HOLD_POSITION(ControlData.ControllerCommand.HOLD_POSITION.getValue()),
        // autonomous back to base, climb 10 meters above start, cruise to base and auto land with AP
        BACK_TO_BASE(ControlData.ControllerCommand.BACK_TO_BASE.getValue()),
        // cruise via specific route and back to base
        VIA_ROUTE(ControlData.ControllerCommand.VIA_ROUTE.getValue()),
        // immediate STOP (even when fling)
        STOP(ControlData.ControllerCommand.STOP.getValue()),

        ERROR_CONNECTION((short)6100);

        private final short value;

        ControllerState(short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }

        static public ControllerState getControllerState(short value) {
            if (value == IDLE.getValue()) return IDLE;
            else if (value == MANUAL.getValue()) return MANUAL;
            else if (value == AUTOLANDING.getValue()) return AUTOLANDING;
            else if (value == AUTOLANDING_AP.getValue()) return AUTOLANDING_AP;
            else if (value == HOLD_ALTITUDE.getValue()) return HOLD_ALTITUDE;
            else if (value == HOLD_POSITION.getValue()) return HOLD_POSITION;
            else if (value == BACK_TO_BASE.getValue()) return BACK_TO_BASE;
            else if (value == VIA_ROUTE.getValue()) return VIA_ROUTE;
            else if (value == STOP.getValue()) return STOP;
            else if (value == ERROR_CONNECTION.getValue()) return ERROR_CONNECTION;
            else return IDLE; // TODO throw some exception
        }

        @Override
        public String toString() {
            if (value == IDLE.getValue()) return "Idle";
            else if (value == MANUAL.getValue()) return "Manual";
            else if (value == AUTOLANDING.getValue()) return "Auto landing";
            else if (value == AUTOLANDING_AP.getValue()) return "Auto landing AP";
            else if (value == HOLD_ALTITUDE.getValue()) return "Hold altitude";
            else if (value == HOLD_POSITION.getValue()) return "Hold position";
            else if (value == BACK_TO_BASE.getValue()) return "Back to base";
            else if (value == VIA_ROUTE.getValue()) return "Via route";
            else if (value == STOP.getValue()) return "Stop";
            else if (value == ERROR_CONNECTION.getValue()) return "Error connection";
            else return "Error type!";
        }
    }

    public enum FlagId {
        GPS_FIX(0),
        GPS_FIX_3D(1),
        LOW_BATTERY_VOLTAGE(2),
        ERROR_HANDLING(3),
        AUTOPILOT_USED(4);

        private final int value;

        FlagId(int value){
            this.value = value;
        }

        int getValue(){
            return value;
        }
    }
}
