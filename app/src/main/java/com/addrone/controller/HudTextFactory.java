package com.addrone.controller;

import android.content.Context;

import com.addrone.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kamil on 8/26/2016.
 */
public class HudTextFactory {

    private String latency;

    private String autopilot;
    private String available;
    private String disabled;

    private String battery;

    private String latitude;
    private String longitude;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private DecimalFormat coordinatesFormat = new DecimalFormat(".######");
    private DecimalFormat decimalFormat = new DecimalFormat(".#");

    public HudTextFactory(Context context) {
        latency = context.getString(R.string.latency);

        autopilot = context.getString(R.string.autopilot);
        available = context.getString(R.string.available);
        disabled = context.getString(R.string.disabled);

        battery = context.getString(R.string.battery);

        latitude = context.getString(R.string.latitude);
        longitude = context.getString(R.string.longitude);
    }

    public String getLatencyString(long ping) {
        return latency + ": " + ping + "ms";
    }

    public String getAutopilotString(boolean isAutopilotAvailable) {
        return autopilot + " " + (isAutopilotAvailable ? available : disabled);
    }

    public String getBatteryStateString(float percentage, int minutes) {
        return battery + ": " + Math.round(percentage * 100) + "% (" + minutes + "m)";
    }

    public String getDateString() {
        return dateFormat.format(new Date());
    }

    public String getPositionString(float lat, float lng) {
        char ns = lat < 0 ? 'N' : 'S';
        char we = lng < 0 ? 'W' : 'E';

        return latitude + ": " + coordinatesFormat.format(Math.abs(lat)) + " " + ns + " " + longitude + ": " + coordinatesFormat.format(Math.abs(lng)) + " " + we;
    }

    public String getYawText(float yaw) {
        return decimalFormat.format(yaw / Math.PI * 180) + "°";
    }

    public String getAltText(float altitude) {
        return decimalFormat.format(altitude) + "m";
    }

    public String getVelText(float velocity) {
        return decimalFormat.format(velocity) + "m/s";
    }
}
