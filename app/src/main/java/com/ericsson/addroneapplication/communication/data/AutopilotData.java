package com.ericsson.addroneapplication.communication.data;

import com.ericsson.addroneapplication.communication.CommMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by nbar on 2016-08-22.
 * Autopilot data used to control drone in autopilot mode.
 * This data is sent to drone when target is changed and send back from drone as ACK.
 * Data:
 * - position (lat, lon, alt)
 * - flags:
 * ..............
 */
public class AutopilotData {

    private double latitude, longitude;
    private float relativeAltitude;

    private int flags;

    public AutopilotData() {

    }

    public AutopilotData(CommMessage message) {
        ByteBuffer buffer = message.getByteBuffer();
        this.latitude = buffer.getDouble();
        this.longitude = buffer.getDouble();
        this.relativeAltitude = buffer.getFloat();
        this.flags = buffer.getInt();
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
    public String toString() {
        String result = ("Target: lat: " + String.valueOf(latitude) + " lon: " + String.valueOf(longitude) + " alt: " + String.valueOf(relativeAltitude));
        result += (", flags: " + String.valueOf(flags) + ")");
        return result;
    }

    public CommMessage getMessage() {
        byte[] payload = new byte[CommMessage.getPayloadSizeByType(CommMessage.MessageType.AUTOPILOT)];
        ByteBuffer buffer = ByteBuffer.allocate(payload.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putDouble(getLatitude());
        buffer.putDouble(getLongitude());
        buffer.putFloat(getRelativeAltitude());
        buffer.putInt(getFlags());
        return new CommMessage(CommMessage.MessageType.AUTOPILOT, payload);
    }

    public boolean isEqual(AutopilotData autopilotData) {
        return this.latitude == autopilotData.latitude
                && this.longitude == autopilotData.longitude
                && this.relativeAltitude == autopilotData.relativeAltitude
                && this.flags == autopilotData.flags;
    }
}
