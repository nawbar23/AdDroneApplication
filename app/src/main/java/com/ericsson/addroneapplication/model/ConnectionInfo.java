package com.ericsson.addroneapplication.model;

/**
 * Created by nbar on 2016-08-19.
 * Container for connection information
 * Stores IP, port and name of drone server
 */
public class ConnectionInfo {
    private String ipAddress;
    private int port;

    public ConnectionInfo(String ipAddress, int port)
    {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public String toString() {
        return "ConnectionInfo: " + ipAddress + ":" + String.valueOf(port);
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
}
