package com.ericsson.addroneapplication.viewmodel;

import com.ericsson.addroneapplication.model.ConnectionInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nbar on 2016-08-19.
 * View model for StartActivity
 * Contains list of saved configurations and handles loading them from internal repo
 */
public class StartViewModel implements ViewModel {
    private Map<String, ConnectionInfo> connectionInfoMap;

    public StartViewModel() {

        connectionInfoMap = new HashMap<>();
        loadConnectionInformationMap();
    }

    private void loadConnectionInformationMap() {
        connectionInfoMap.put("Connection 1", new ConnectionInfo("111.111.111.111", 3333));
        connectionInfoMap.put("Connection 2", new ConnectionInfo("111.111.111.111", 3333));
        connectionInfoMap.put("Connection 3", new ConnectionInfo("111.111.111.111", 3333));
        connectionInfoMap.put("Connection 4", new ConnectionInfo("111.111.111.111", 3333));
    }

    public Map<String, ConnectionInfo> getConnectionInfoMap() {
        return connectionInfoMap;
    }

    public ArrayList<String> getConnectionInfoNames() {
        return new ArrayList<>(connectionInfoMap.keySet());
    }

    public ConnectionInfo getConnectionInfo(String s) {
        return connectionInfoMap.get(s);
    }

    @Override
    public void destroy() {
    }
}
