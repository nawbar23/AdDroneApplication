package com.ericsson.addroneapplication;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ericsson.addroneapplication.model.ConnectionInfo;
import com.ericsson.addroneapplication.service.AdDroneService;
import com.ericsson.addroneapplication.viewmodel.StartViewModel;

public class StartActivity extends AppCompatActivity implements AddConnectionDialogFragment.AddConnectionDialogListener {

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
            service.attemptConnection(getSelectedConnection());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(DEBUG_TAG, "Disconnected !!!");
        }
    };

    private Spinner spinnerConnection;
    private Button buttonConnect;
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(DEBUG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // find views
        spinnerConnection = (Spinner) findViewById(R.id.spinner_connection);
        buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonAdd = (Button) findViewById(R.id.button_add);

        startViewModel = new StartViewModel(this);

        // start service
        startService(new Intent(this, AdDroneService.class));

        // fill spinner with options
        updateSpinner();

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConnect(getSelectedConnection());
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment addDialogFragment = new AddConnectionDialogFragment();
                addDialogFragment.show(getFragmentManager(), "ADD_DIALOG");
            }
        });
    }

    private void onConnect(ConnectionInfo connectionInfo) {
        Log.e(DEBUG_TAG, "onConnect, " + connectionInfo.toString());
        if (service != null) {
            service.attemptConnection(connectionInfo);
        } else {
            bindService(new Intent(StartActivity.this, AdDroneService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    private void updateSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, startViewModel.getConnectionInfoNames());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConnection.setAdapter(adapter);
    }

    private ConnectionInfo getSelectedConnection() {
        return startViewModel.getConnectionInfo(spinnerConnection.getSelectedItem().toString());
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

    @Override
    public void onAddConnection(String name, String ip, int port) {
        Log.i(DEBUG_TAG, "Adding new connection " + name + ": " + ip + ":" + port);
        startViewModel.addConnection(name, ip, port);
        updateSpinner();
    }
}
