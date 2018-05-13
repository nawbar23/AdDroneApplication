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
    private static final String DEBUG_TAG = ConnectionInfo.class.getSimpleName();

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
        Log.e(DEBUG_TAG, jsonObject.toString());
        return jsonObject;
    }

    public static ConnectionInfo parse(JSONObject object) throws JSONException {
        Log.e(DEBUG_TAG, "Parsing: " + object.toString());
        ConnectionInfo.Type type = Type.valueOf(object.getString("type"));
        if (type == Type.TCP_CLIENT) {
            String ipAddress = object.getString("ipAddress");
            int port = object.getInt("port");
            return new ConnectionInfo(type, ipAddress, port);
        } else {
            return new ConnectionInfo(type, null, -1);
        }
    }
}
