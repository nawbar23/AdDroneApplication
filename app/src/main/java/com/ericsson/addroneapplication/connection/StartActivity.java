package com.ericsson.addroneapplication.connection;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ericsson.addroneapplication.R;
import com.ericsson.addroneapplication.model.ConnectionInfo;
import com.ericsson.addroneapplication.service.AdDroneService;
import com.ericsson.addroneapplication.settings.SettingsActivity;
import com.ericsson.addroneapplication.viewmodel.StartViewModel;

public class StartActivity extends AppCompatActivity implements AddConnectionDialogFragment.AddConnectionDialogListener {

    private static final String DEBUG_TAG = "AdDrone:" + StartActivity.class.getSimpleName();

    private StartViewModel startViewModel;

    private AdDroneService service = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binderService) {
            AdDroneService.LocalBinder binder = (AdDroneService.LocalBinder) binderService;
            service = binder.getService();
            try {
                service.attemptConnection(connectionsListAdapter.getChosenConnection());
            } catch (Exception e) {
                Log.e(DEBUG_TAG, e.getMessage() + " this should never happen here!");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(DEBUG_TAG, "Disconnected !!!");
        }
    };

    private ListView listViewConnections;
    private ConnectionsListAdapter connectionsListAdapter;
    private Button buttonConnect;
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(DEBUG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        startViewModel = new StartViewModel(this);

        // start service
        startService(new Intent(this, AdDroneService.class));

        // set default settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize view
        buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonAdd = (Button) findViewById(R.id.button_add);
        listViewConnections = (ListView) findViewById(R.id.list_connection);

        // fill spinner with options
        connectionsListAdapter = new ConnectionsListAdapter(this, R.layout.connection_list_row, startViewModel.getConnectionInfoMap()) {
            @Override
            public void onEdit(String connectionInfoName) {
                ConnectionInfo connectionInfo = startViewModel.getConnectionInfo(connectionInfoName);
                AddConnectionDialogFragment addDialogFragment = new AddConnectionDialogFragment();
                addDialogFragment.setInitialConnection(connectionInfoName, connectionInfo);
                addDialogFragment.show(getFragmentManager(), "MODIFY_DIALOG");
            }

            @Override
            public void onDelete(String connectionInfoName) {
                startViewModel.removeConnection(connectionInfoName);
                connectionsListAdapter.remove(connectionInfoName);
            }
        };
        connectionsListAdapter.setChosenRowValue(startViewModel.getLastChosenConnectionName());
        listViewConnections.setAdapter(connectionsListAdapter);
        listViewConnections.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectionsListAdapter.onItemClick(parent, view, position, id);
                startViewModel.setLastChosenConnectionName(connectionsListAdapter.getItem(position));
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment addDialogFragment = new AddConnectionDialogFragment();
                addDialogFragment.show(getFragmentManager(), "ADD_DIALOG");
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onConnect(connectionsListAdapter.getChosenConnection());
                } catch (Exception e) {
                    Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.e(DEBUG_TAG, "onDestroy");
        super.onDestroy();
        if (service != null) {
            unbindService(serviceConnection);
        }
    }

    private void onConnect(ConnectionInfo connectionInfo) {
        Log.e(DEBUG_TAG, "onConnect, " + connectionInfo.toString());
        if (service != null) {
            service.attemptConnection(connectionInfo);
        } else {
            bindService(new Intent(StartActivity.this, AdDroneService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
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

        // TODO define rest of menu items: Exit

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddConnection(String name, String ip, int port) {
        startViewModel.addConnection(name, ip, port);
        connectionsListAdapter.add(name);
    }

    @Override
    public void onModifyConnection(String name, String newName, String ip, int port) {
        startViewModel.modifyConnection(name, newName, ip, port);
        connectionsListAdapter.remove(name);
        connectionsListAdapter.add(newName);
    }
}
