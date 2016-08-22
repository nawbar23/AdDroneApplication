package com.ericsson.addroneapplication.viewmodel;

import android.content.Context;

import com.ericsson.addroneapplication.model.ConnectionInfo;

import java.util.ArrayList;

/**
 * Created by nbar on 2016-08-19.
 * View model for StartActivity
 * Contains list of saved configurations and handles loading them from internal repo
 */
public class StartViewModel implements ViewModel {
    public interface DataListener {
        void onConnectionInformationListUpdated(ArrayList<ConnectionInfo> connectionInformationList);
        void onChosenConnectionInfoUpdated(ConnectionInfo chosenConnectionInfo);
    }

    private Context context;
    private DataListener dataListener;

    private ArrayList<ConnectionInfo> connectionInformationList;
    private ConnectionInfo chosenConnectionInfo;

    public StartViewModel(Context context, DataListener dataListener)
    {
        this.context = context;
        this.dataListener = dataListener;

        setConnectionInformationList(loadConnectionInformationList());
        setChosenConnectionInfo(connectionInformationList.get(0));
    }

    private ArrayList<ConnectionInfo> loadConnectionInformationList()
    {
        ArrayList<ConnectionInfo> connectionInformationList = new ArrayList<>();
        connectionInformationList.add(new ConnectionInfo("111.111.111.1111", 3333, "Test 1"));
        connectionInformationList.add(new ConnectionInfo("111.111.111.1111", 3333, "Test 2"));
        connectionInformationList.add(new ConnectionInfo("111.111.111.1111", 3333, "Test 3"));
        connectionInformationList.add(new ConnectionInfo("111.111.111.1111", 3333, "Test 4"));
        return connectionInformationList;
    }

    public ConnectionInfo getChosenConnectionInfo() {
        return chosenConnectionInfo;
    }

    public void setChosenConnectionInfo(ConnectionInfo chosenConnectionInfo) {
        this.chosenConnectionInfo = chosenConnectionInfo;
        this.dataListener.onChosenConnectionInfoUpdated(this.chosenConnectionInfo);
    }

    public ArrayList<ConnectionInfo> getConnectionInformationList() {
        return connectionInformationList;
    }

    public void setConnectionInformationList(ArrayList<ConnectionInfo> connectionInformationList) {
        this.connectionInformationList = connectionInformationList;
        this.dataListener.onConnectionInformationListUpdated(this.connectionInformationList);
    }

    @Override
    public void destroy() {
        context = null;
        dataListener = null;
    }
}
