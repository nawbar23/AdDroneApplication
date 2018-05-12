package com.addrone.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.addrone.model.ConnectionInfo;

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

    public void setLastChosenConnectionName(String name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_chosen_connection", name);
        editor.apply();
    }
    public String getLastChosenConnectionName() {
         return preferences.getString("last_chosen_connection", null);
    }

    public void addConnection(String name, ConnectionInfo connectionInfo) {
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

    public void removeConnection(String connectionInfoName) {
        connectionInfoMap.remove(connectionInfoName);

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("name").equalsIgnoreCase(connectionInfoName)) {
                    jsonArray.remove(i);
                    break;
                }
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("saved_connections", jsonArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void modifyConnection(String name, String newName, ConnectionInfo connectionInfo) {
        removeConnection(name);
        addConnection(newName, connectionInfo);
    }

    public Map<String, ConnectionInfo> getConnectionInfoMap() {
        return connectionInfoMap;
    }

    public ArrayList<String> getConnectionInfoNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<String, ConnectionInfo> entry : connectionInfoMap.entrySet()) {
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

    @Override
    public void stop() {
    }
}
