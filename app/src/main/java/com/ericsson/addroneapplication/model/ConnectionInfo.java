package com.ericsson.addroneapplication.model;

/**
 * Created by nbar on 2016-08-19.
 * Container for connection information
 * Stores IP, port and name of drone server
 */
public class ConnectionInfo {
    private String ipAddress;
    private int port;
    private String name;

    public ConnectionInfo(String ipAddress, int port, String name)
    {
        this.ipAddress = ipAddress;
        this.port = port;
        this.name = name;
    }

    @Override
    public String toString() {
        return "ConnectionInfo: " + ipAddress + ":" + String.valueOf(port) + ", user name: \"" + name + "\"";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
