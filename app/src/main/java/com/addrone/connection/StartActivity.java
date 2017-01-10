package com.addrone.connection;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

import com.addrone.R;
import com.addrone.model.ConnectionInfo;
import com.addrone.service.AdDroneService;
import com.addrone.settings.SettingsActivity;
import com.addrone.viewmodel.StartViewModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity implements AddConnectionDialogFragment.AddConnectionDialogListener {

    private static final String DEBUG_TAG = "AdDrone:" + StartActivity.class.getSimpleName();

    private StartViewModel startViewModel;

    private AdDroneService service = null;

    private ProgressDialog progressDialog;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binderService) {
            AdDroneService.LocalBinder binder = (AdDroneService.LocalBinder) binderService;
            service = binder.getService();
            try {
                showProgressDialog();
                service.attemptConnection(connectionsListAdapter.getChosenConnection(), progressDialog);
            } catch (Exception e) {
                Log.e(DEBUG_TAG, e.getMessage() + " this should never happen here!");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(DEBUG_TAG, "Disconnected !!!");
        }
    };

    private ConnectionsListAdapter connectionsListAdapter;


    @BindView(R.id.button_connect) private Button buttonConnect;
    @BindView(R.id.button_add) private Button buttonAdd;
    @BindView(R.id.list_connection) private ListView listViewConnections;


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
        listViewConnections = (ListView) findViewById(R.id.list_connection);
        ButterKnife.bind(this);


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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting...");
        progressDialog.setCancelable(false);
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
            showProgressDialog();
            service.attemptConnection(connectionInfo, progressDialog);
        } else {
            bindService(new Intent(StartActivity.this, AdDroneService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void showProgressDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
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


    @OnClick(R.id.button_add)
    public void clickButtonAdd(){
                DialogFragment addDialogFragment = new AddConnectionDialogFragment();
        addDialogFragment.show(getFragmentManager(), "ADD_DIALOG");
    }


    @OnClick(R.id.button_connect)
    public void clickButtonConnect() {
                try {
                    onConnect(connectionsListAdapter.getChosenConnection());
                } catch (Exception e) {
                    Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
}


