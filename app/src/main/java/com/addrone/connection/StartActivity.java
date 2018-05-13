package com.addrone.connection;

import android.app.ActivityManager;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;
import android.widget.Toast;

import com.addrone.R;
import com.addrone.controller.ControlActivity;
import com.addrone.model.ConnectionInfo;
import com.addrone.service.AdDroneService;
import com.addrone.settings.SettingsActivity;
import com.addrone.viewmodel.StartViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity implements AddConnectionDialogFragment.AddConnectionDialogListener {

    private static final String DEBUG_TAG = "AdDrone:" + StartActivity.class.getSimpleName();

    private StartViewModel startViewModel;

    private AdDroneService service = null;
    private ProgressDialog progressDialog;
    private IntentReceiver mIntentReceiver;

    private ConnectionsListAdapter connectionsListAdapter;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binderService) {
            AdDroneService.LocalBinder binder = (AdDroneService.LocalBinder) binderService;
            service = binder.getService();
            try {
                if (service.getState() == AdDroneService.State.CONNECTED) {
                    Intent intent = new Intent(StartActivity.this , ControlActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e(DEBUG_TAG, e.getMessage() + " this should never happen here!");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(DEBUG_TAG, "Disconnected !!!");
        }
    };

    @BindView(R.id.list_connection)
    public ListView listViewConnections;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        startViewModel = new StartViewModel(this);

        // start service
        Log.d(DEBUG_TAG, "AdDroneService running: " + String.valueOf(isAdDroneServiceRunning(AdDroneService.class)));
        if (!isAdDroneServiceRunning(AdDroneService.class)) {
            startService(new Intent(this, AdDroneService.class));
        }

        bindService(new Intent(StartActivity.this, AdDroneService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        // set default settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.activity_start);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize view
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

    private boolean isAdDroneServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIntentReceiver = new IntentReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AdDroneService.START_ACTIVITY);
        registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mIntentReceiver);
    }

    @Override
    protected void onDestroy() {
        Log.d(DEBUG_TAG, "onDestroy");
        super.onDestroy();
        if (service != null) {
            unbindService(serviceConnection);
        }
    }

    private void onConnect(ConnectionInfo connectionInfo) {
        Log.d(DEBUG_TAG, "onConnect, " + connectionInfo.toString());
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
    public void onAddConnection(String name, ConnectionInfo connectionInfo) {
        Log.i(DEBUG_TAG, "onAddConnection" + connectionInfo.toString());
        startViewModel.addConnection(name, connectionInfo);
        connectionsListAdapter.add(name);
        connectionsListAdapter.setChosenRowValue(name);
    }

    @Override
    public void onModifyConnection(String name, String newName, ConnectionInfo connectionInfo) {
        Log.i(DEBUG_TAG, "onModifyConnection" + connectionInfo.toString());
        startViewModel.modifyConnection(name, newName, connectionInfo);
        connectionsListAdapter.remove(name);
        connectionsListAdapter.add(newName);
    }


    @OnClick(R.id.button_add)
    public void clickButtonAdd() {
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

    public class IntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), ControlActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
    }
}


