package com.addrone.controller;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.addrone.R;
import com.addrone.service.AdDroneService;
import com.addrone.viewmodel.ControlViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.multicopter.java.data.AutopilotData;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlActivity extends AppCompatActivity {

    private Fragment mapFragment;
    private Fragment cameraFragment;
    private ControlViewModel controlViewModel;

    private FrameLayout frameLayout1;
    private FrameLayout frameLayout2;

    private HudView hudView;
    private Timer hudViewUpdateTimer;
    private TimerTask hudViewTimerUpdateTask = new TimerTask() {
        @Override
        public void run() {
            if (controlViewModel != null) {
                ControlActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hudView.updateUiDataPack(controlViewModel.getCurrentUiDataPack());
                    }
                });
            }
        }
    };

    private Button buttonChangeView;
    private Button buttonAction;

    private ControlPadView controlPadView;
    private ControlThrottleView controlThrottleView;

    private RelativeLayout.LayoutParams layoutParamsFullscreen;
    private RelativeLayout.LayoutParams layoutParamsHidden;

    private AdDroneService service = null;
    private long tmp;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
            AdDroneService.LocalBinder binder = (AdDroneService.LocalBinder) serviceBinder;
            service = binder.getService();
            service.setControlViewModel(controlViewModel);
            service.registerListener(controlViewModel);
            controlViewModel.setUavManager(service.getUavManager());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set default settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // launch activity in fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_control);

        controlViewModel = new ControlViewModel(this);

        // Bind to service
        bindService(new Intent(this, AdDroneService.class), serviceConnection, 0);

        // Setup GUI
        frameLayout1 = (FrameLayout) findViewById(R.id.layout_container_1);
        frameLayout2 = (FrameLayout) findViewById(R.id.layout_container_2);
        hudView = (HudView) findViewById(R.id.view_hud);
        buttonChangeView = (Button) findViewById(R.id.button_change_view);
        buttonAction = (Button) findViewById(R.id.button_action);
        controlPadView = (ControlPadView) findViewById(R.id.joystick);
        controlThrottleView = (ControlThrottleView) findViewById(R.id.throttle);

        controlPadView.setVisibility(View.INVISIBLE);
        controlThrottleView.setVisibility(View.INVISIBLE);

        // Register GUI listeners
        controlPadView.setOnControlPadChangedListener(controlViewModel);
        controlThrottleView.setOnControlThrottlePadChangedListener(controlViewModel);

        // Setup fragments
        mapFragment = Fragment.instantiate(this, ControlMapFragment.class.getName());
        cameraFragment = Fragment.instantiate(this, ControlPadFragment.class.getName());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container_1, mapFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container_2, cameraFragment)
                .commit();

        layoutParamsFullscreen = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParamsHidden = new RelativeLayout.LayoutParams(0, 0);

        setMapFragment();
        notifyFlightEnded();

        hudViewUpdateTimer = new Timer();
        hudViewUpdateTimer.scheduleAtFixedRate(hudViewTimerUpdateTask, 1000, 80);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setMapFragment() {
        frameLayout1.setLayoutParams(layoutParamsFullscreen);
        frameLayout2.setLayoutParams(layoutParamsHidden);

        buttonChangeView.setText(R.string.camera);
        buttonChangeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCameraFragment();
            }
        });
    }

    public void notifyFlightStarted() {
        buttonAction.setText(R.string.end_fly);
        controlPadView.setVisibility(View.VISIBLE);
        controlThrottleView.setVisibility(View.VISIBLE);

        buttonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlViewModel.onEndFlightClick();
            }
        });
    }

    public void notifyFlightEnded() {
        buttonAction.setText(R.string.action);
        controlPadView.setVisibility(View.INVISIBLE);
        controlThrottleView.setVisibility(View.INVISIBLE);

        buttonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlViewModel.onActionClick();
            }
        });
    }

    public void setAutopilotData(LatLng point) {
        AutopilotData autopilotData = new AutopilotData();
        autopilotData.setLatitude(point.latitude);
        autopilotData.setLongitude(point.longitude);
        autopilotData.setRelativeAltitude(10.0f);
        autopilotData.setFlags(0);
        Log.e("AUTO_PILOT_DATA_UPDATE", "Autopilot event: " + autopilotData.toString());
        service.getUavManager().notifyAutopilotEvent(autopilotData);
    }

    private void setCameraFragment() {
        //TODO only temporary event for autopilot
        AutopilotData autopilotData = new AutopilotData();
        autopilotData.setLatitude(50.00 + (Math.random() - 0.5) / 100);
        autopilotData.setLongitude(20.00 + (Math.random() - 0.5) / 100);
        autopilotData.setRelativeAltitude(10.0f);
        autopilotData.setFlags(0);
        Log.e("DDD", "Autopilot event: " + autopilotData.toString());
        service.getUavManager().notifyAutopilotEvent(autopilotData);

        frameLayout1.setLayoutParams(layoutParamsHidden);
        frameLayout2.setLayoutParams(layoutParamsFullscreen);

        buttonChangeView.setText(R.string.map);
        buttonChangeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMapFragment();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        controlViewModel.pause();
        Log.d("ControlAcivity","onPause sie wywolalo");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controlViewModel.destroy();
        if (service != null) {
            service.unregisterListener(controlViewModel);
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        hudViewUpdateTimer.purge();
        Log.d("ControlAcivity","onRestrat sie wywolalo");

    }

    @Override
    protected void onStop() {
        super.onStop();
        controlViewModel.stop();
        tmp = System.currentTimeMillis();
        Log.d("ControlAcivity", "onStop sie wywolalo");
        hudViewTimerUpdateTask.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        controlViewModel.start();


        Log.d("ControlAcivity", "onStart sie wywolalo");
    }

    @Override
    protected void onResume() {
        super.onResume();
        controlViewModel.resume();
        }


    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        getDelegate().onSaveInstanceState(savedInstanceState);

    }

}
