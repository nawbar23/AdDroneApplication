package com.ericsson.addroneapplication.comunication.data;

import com.ericsson.addroneapplication.comunication.messages.AutopilotMessage;
import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;

/**
 * Created by nbar on 2016-08-22.
 * Autopilot data used to control drone in autopilot mode.
 * This data is sent to drone when target is changed and send back from drone as ACK.
 * Data:
 * - position (lat, lon, alt)
 * - flags:
 * ..............
 */
public class AutopilotData implements CommunicationMessageValue {

    private double latitude, longitude;
    private float relativeAltitude;

    private int flags;

    public AutopilotData() {

    }

    public AutopilotData(AutopilotMessage message) {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRelativeAltitude() {
        return relativeAltitude;
    }

    public void setRelativeAltitude(float relativeAltitude) {
        this.relativeAltitude = relativeAltitude;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    public CommunicationMessage getMessage() {
        return new AutopilotMessage(this);
    }
}
