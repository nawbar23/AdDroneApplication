package com.ericsson.addroneapplication.comunication.data;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;
import com.ericsson.addroneapplication.comunication.messages.DebugMessage;

/**
 * Created by nbar on 2016-08-22.
 * Container tha stores most important telemetry data from drone:
 *  - euler angles - rotation of drone
 *  - position (lat, lon, alt), altitude is relative to start
 *  - vLoc - speed of drone relatively to ground
 *  - controller state - actual control command used by controller
 *  - flags:
 *    GPS fix | GPS 3D fix | low. bat. volt. | errorHandling | autopilotUsed | solver1 | solver2
 */
public class DebugData implements CommunicationMessageValue {

    private float eulerX, eulerY, eulerZ;

    private float latitude, longintude;
    private float relativeAltitude;

    private float vLoc;

    private short controllerState;
    private byte flags;

    public DebugData() {

    }

    public DebugData(DebugMessage message){

    }

    @Override
    public CommunicationMessage getMessage() {
        return null;
    }
}
