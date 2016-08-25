package com.ericsson.addroneapplication.comunication.data;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.comunication.messages.ControlMessage;

import java.nio.ByteBuffer;

/**
 * Created by nbar on 2016-08-19.
 * Main control message, it is send to controller synchronous with frequency of 20Hz
 * Contains basic control parameters:
 * - euler angles (roll, pitch, yaw) - desired rotation of UAV [rad]
 * - throttle - desired power of motors [0 - 1.0]
 * - command - command for specific control type
 * - solver mode - (only in MANUAL command), defines way of euler angles interpretation
 */

public class ControlData implements CommunicationMessageValue {

    private float roll, pitch, yaw;
    private float throttle;
    private ControllerCommand command;
    private SolverMode mode;

    public ControlData() {
        this.roll = 0.0f;
        this.pitch = 0.0f;
        this.yaw = 0.0f;
        this.throttle = 0.0f;
        this.command = ControllerCommand.STOP;
        this.mode = SolverMode.ANGLE_NO_YAW;
    }

    public ControlData(ControlMessage message) {
        ByteBuffer buffer = message.getByteBuffer();
        this.roll = buffer.getFloat();
        this.pitch= buffer.getFloat();
        this.yaw = buffer.getFloat();
        this.command = ControllerCommand.getControllerCommand((short)(buffer.get() & 0xff));
        this.mode = SolverMode.getSolverMode(buffer.get());
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

    public float getThrottle() {
        return throttle;
    }

    public void setThrottle(float throttle) {
        this.throttle = throttle;
    }

    public ControllerCommand getCommand() {
        return command;
    }

    public void setCommand(ControllerCommand command) {
        this.command = command;
    }

    public SolverMode getMode() {
        return mode;
    }

    public void setMode(SolverMode mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        // TODO format this string for better presentation
        String result = "Rotation: roll: " + String.valueOf(roll) + " pitch: " + String.valueOf(pitch) + " yaw: " + String.valueOf(yaw);
        result += (", Controller command: " + command.toString() + " solver mode: " + mode.toString());
        return result;
    }

    @Override
    public CommunicationMessage getMessage() {
        return new ControlMessage(this);
    }

    public enum ControllerCommand {
        // manual control
        MANUAL((short)1000),
        // auto lading with specified descend rate
        AUTOLANDING((short)1100),
        // auto lading with specified descend rate and position hold
        AUTOLANDING_AP((short)1200),
        // auto altitude hold, throttle value is now specifying descend/climb rate
        // th = 0 -> -v, th = 0.5 -> 0, th = 1.0 -> v
        HOLD_ALTITUDE((short)1300),
        // auto position hold, (hold altitude enabled)
        HOLD_POSITION((short)1400),
        // autonomous back to base, climb 10 meters above start, cruise to base and auto land with AP
        BACK_TO_BASE((short)1500),
        // cruise via specific route and back to base
        VIA_ROUTE((short)1600),
        // immediate STOP (even when fling)
        STOP((short)2000),
        // error conditions
        ERROR_CONNECTION((short)6100),
        ERROR_JOYSTICK((short)6200),
        ERROR_EXTERNAL((short)6300);

        private final short value;

        ControllerCommand(short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }

        static public ControllerCommand getControllerCommand(short value) {
            if (value == MANUAL.getValue()) return MANUAL;
            else if (value == AUTOLANDING.getValue()) return AUTOLANDING;
            else if (value == AUTOLANDING_AP.getValue()) return AUTOLANDING_AP;
            else if (value == HOLD_ALTITUDE.getValue()) return HOLD_ALTITUDE;
            else if (value == HOLD_POSITION.getValue()) return HOLD_POSITION;
            else if (value == BACK_TO_BASE.getValue()) return BACK_TO_BASE;
            else if (value == VIA_ROUTE.getValue()) return VIA_ROUTE;
            else if (value == ERROR_CONNECTION.getValue()) return ERROR_CONNECTION;
            else if (value == ERROR_JOYSTICK.getValue()) return ERROR_JOYSTICK;
            else return ERROR_EXTERNAL; // TODO throw some exception
        }
    }

    public enum SolverMode {
        STABILIZATION((byte)0),
        ANGLE_NO_YAW((byte)1),
        ANGLE((byte)2),
        HEADLESS((byte)3);

        private byte value;

        SolverMode(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        static public SolverMode getSolverMode(byte value) {
            if (value == STABILIZATION.getValue()) return STABILIZATION;
            else if (value == ANGLE_NO_YAW.getValue()) return ANGLE_NO_YAW;
            else if (value == ANGLE.getValue()) return ANGLE;
            else if (value == HEADLESS.getValue()) return HEADLESS;
            else return ANGLE_NO_YAW; // TODO throw some exception
        }
    }
}
