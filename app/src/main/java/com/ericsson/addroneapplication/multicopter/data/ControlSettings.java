package com.ericsson.addroneapplication.multicopter.data;

/**
 * Created by ebarnaw on 2016-10-14.
 */
public class ControlSettings implements SignalPayloadData {

    public ControlSettings(final byte[] dataArray) {

    }

    @Override
    public SignalData.Command getDataType() {
        return SignalData.Command.CONTROL_SETTINGS_DATA;
    }
}
