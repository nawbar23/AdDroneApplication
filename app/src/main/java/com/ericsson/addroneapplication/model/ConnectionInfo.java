package com.ericsson.addroneapplication.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by nbar on 2016-08-19.
 * Container for connection information
 * Stores IP, port and name of drone server
 */
public class ConnectionInfo {
    private String ipAddress;
    private int port;

    public ConnectionInfo(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public ConnectionInfo(JSONObject object) throws JSONException {
        this.ipAddress = object.getString("ipAddress");
        this.port = object.getInt("port");
    }

    @Override
    public String toString() {
        return ipAddress + ":" + String.valueOf(port);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public JSONObject serialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ipAddress", ipAddress);
        jsonObject.put("port", port);
        return jsonObject;
    }
}
