package com.ericsson.addroneapplication.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ericsson.addroneapplication.model.ConnectionInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nbar on 2016-08-19.
 * View model for StartActivity
 * Contains list of saved configurations and handles loading them from internal repo
 */
public class StartViewModel implements ViewModel {

    private SharedPreferences preferences;
    private Map<String, ConnectionInfo> connectionInfoMap;
    private JSONArray jsonArray;

    public static String connectionNameToId(String name) {
        return name.substring(0, name.lastIndexOf('(') - 1);
    }

    public StartViewModel(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        connectionInfoMap = new HashMap<>();

        try {
            jsonArray = new JSONArray(preferences.getString("saved_connections", "[]"));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.getString("name");
                ConnectionInfo connectionInfo = new ConnectionInfo(jsonObject.getJSONObject("connectionInfo"));

                connectionInfoMap.put(name, connectionInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addConnection(String name, String ip, int port) {
        ConnectionInfo connectionInfo = new ConnectionInfo(ip, port);
        connectionInfoMap.put(name, connectionInfo);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("connectionInfo", connectionInfo.serialize());

            jsonArray.put(jsonObject);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("saved_connections", jsonArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getConnectionInfoNames() {
        ArrayList<String> names = new ArrayList<>();
        for(Map.Entry<String, ConnectionInfo> entry: connectionInfoMap.entrySet()) {
            names.add(entry.getKey() + " (" + entry.getValue().toString() + ")");
        }
        return names;
    }

    public ConnectionInfo getConnectionInfo(String s) {
        return connectionInfoMap.get(s);
    }

    @Override
    public void destroy() {
    }
}
