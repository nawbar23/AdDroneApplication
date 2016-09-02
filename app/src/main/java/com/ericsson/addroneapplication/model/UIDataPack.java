package com.ericsson.addroneapplication.model;

import com.ericsson.addroneapplication.comunication.data.AutopilotData;
import com.ericsson.addroneapplication.comunication.data.DebugData;

/**
 * Created by Kamil on 8/25/2016.
 */
public class UIDataPack {

    public long ping;

    public float lat, lng;

    public double targetLat, targetLon;

    public float pitch, roll, yaw;

    public float altitude, velocity;

    public float battery;

    public UIDataPack() {

    }

    public UIDataPack(DebugData debugData, AutopilotData autopilotData, long ping) {
        this.ping = ping;

        this.lat = debugData.getLatitude();
        this.lng = debugData.getLongitude();

        this.targetLat = autopilotData.getLatitude();
        this.targetLon = autopilotData.getLongitude();

        this.pitch = debugData.getPitch();
        this.roll = debugData.getRoll();
        this.yaw = debugData.getYaw();

        this.altitude = debugData.getRelativeAltitude();
        this.velocity = debugData.getVLoc();

        // TODO: compute battery percentage
        this.battery = (float)debugData.getBattery() / 255;
    }
}
