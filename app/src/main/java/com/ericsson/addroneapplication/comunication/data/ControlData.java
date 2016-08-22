package com.ericsson.addroneapplication.comunication.data;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.comunication.messages.ControlMessage;

/**
 * Created by nbar on 2016-08-19.
 * Main control message, it is send to controller synchronous with frequency of 20Hz
 * Contains basic control parameters:
 *  - euler angles (roll, pitch, yaw) - desired rotation of UAV [rad]
 *  - throttle - desired power of motors [0 - 1.0]
 *  - command - command for specific control type
 *  - solver mode - (only in MANUAL command), defines way of euler angles interpretation
 */
public class ControlData implements CommunicationMessageValue {

    public enum ControllerCommand {
        // manual control
        MANUAL(1000),
        // auto lading with specified descend rate
        AUTOLANDING(1100),
        // auto lading with specified descend rate and position hold
        AUTOLANDING_AP(1200),
        // auto altitude hold, throttle value is now specifying descend/climb rate
        // th = 0 -> -v, th = 0.5 -> 0, th = 1.0 -> v
        HOLD_ALTITUDE(1300),
        // auto position hold, (hold altitude enabled)
        HOLD_POSITION(1400),
        // autonomous back to base, climb 10 meters above start, cruise to base and auto land with AP
        BACK_TO_BASE(1500),
        // cruise via specific route and back to base
        VIA_ROUTE(1600),
        // immediate STOP (even when fling)
        STOP(2000),
        // error conditions
        ERROR_CONNECTION(6100),
        ERROR_JOYSTICK(6200),
        ERROR_EXTERNAL(6300);

        private int value;
        ControllerCommand(int value){
            this.value = value;
        }
    }

    public enum SolverMode {
        STABLILIZATION,
        ANGLE_NO_YAW,
        ANGLE,
        HEADLESS;
    }

    private float eulerX, eulerY, eulerZ;
    private float throttle;

    short command;
    char mode;

    public ControlData() {
        this.eulerX = 0.0f;
        this.eulerY = 0.0f;
        this.eulerZ = 0.0f;
        this.throttle = 0.0f;
    }

    public ControlData(ControlMessage message){

    }

    @Override
    public CommunicationMessage getMessage() {
        return new ControlMessage(this);
    }
}
