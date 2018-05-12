package com.addrone.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nbar on 2016-08-19.
 * Container for connection information
 * Stores IP, port and name of drone server
 */
public class ConnectionInfo {
    public enum Type {
        USB, TCP_CLIENT,
    }

    private Type type;
    private String ipAddress;
    private int port;


    public ConnectionInfo(Type type, String ipAddress, int port) {
        this.type = type;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public ConnectionInfo(JSONObject object) throws JSONException {
        Log.e("TAG", object.toString());
        this.type = object.getString("type").equalsIgnoreCase(Type.TCP_CLIENT.toString()) ?
                Type.TCP_CLIENT : Type.USB;
        this.ipAddress = object.getString("ipAddress");
        this.port = object.getInt("port");
    }

    @Override
    public String toString() {
        switch (type) {
            case USB: return "USB";
            default:
                return ipAddress + ":" + String.valueOf(port);
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
        jsonObject.put("type", type.toString());
        jsonObject.put("ipAddress", ipAddress);
        jsonObject.put("port", port);
        Log.e("TAG", jsonObject.toString());
        return jsonObject;
    }
}
