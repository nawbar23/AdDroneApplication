package com.ericsson.addroneapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import com.ericsson.addroneapplication.model.ConnectionInfo;
import com.ericsson.addroneapplication.service.AdDroneService;
import com.ericsson.addroneapplication.viewmodel.StartViewModel;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity implements StartViewModel.DataListener {

    private static final String DEBUG_TAG = "AdDrone:" + StartActivity.class.getSimpleName();

    StartViewModel startViewModel;

    // TODO TextView - for actually chosen ConnectionInfo
    // TODO ListAdapter - for list of stored ConnectionInfoList

    private AdDroneService service = null;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binderService) {
            AdDroneService.LocalBinder binder = (AdDroneService.LocalBinder) binderService;
            service = binder.getService();
            service.attemptConnection(startViewModel.getChosenConnectionInfo());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(DEBUG_TAG, "Disconnected !!!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(DEBUG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startViewModel = new StartViewModel(this, this);

        // start service
        startService(new Intent(this, AdDroneService.class));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onConnect(startViewModel.getChosenConnectionInfo());
                }
            });
        }
    }

    private void onConnect(ConnectionInfo connectionInfo) {
        Log.e(DEBUG_TAG, "onConnect, " + connectionInfo.toString());
        if (service != null) {
            service.attemptConnection(connectionInfo);
        } else {
            bindService(new Intent(StartActivity.this, AdDroneService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onChosenConnectionInfoUpdated(ConnectionInfo chosenConnectionInfo) {
        // TODO update view
    }

    @Override
    public void onConnectionInformationListUpdated(ArrayList<ConnectionInfo> connectionInformationList) {
        // TODO update view
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO define rest of menu items: Exit, Add, Remove

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
