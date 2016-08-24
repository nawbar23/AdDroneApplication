package com.ericsson.addroneapplication.controller;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ericsson.addroneapplication.R;
import com.ericsson.addroneapplication.model.UpdateUIData;
import com.ericsson.addroneapplication.viewmodel.ControlViewModel;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlActivity extends AppCompatActivity {

    public interface OnControlsChangedListener {

    }

    private Fragment mapFragment;
    private Fragment cameraFragment;
    private ControlViewModel controlViewModel;

    private FrameLayout frameLayout1;
    private FrameLayout frameLayout2;

    private Button buttonChangeView;

    private RelativeLayout.LayoutParams layoutParamsFullscreen;
    private RelativeLayout.LayoutParams layoutParamsHidden;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set default settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // launch activity in fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_control);

        controlViewModel = new ControlViewModel(this);

        mapFragment = Fragment.instantiate(this, ControlMapFragment.class.getName());
        cameraFragment = Fragment.instantiate(this, ControlPadFragment.class.getName());

        frameLayout1 = (FrameLayout) findViewById(R.id.layout_container_1);
        frameLayout2 = (FrameLayout) findViewById(R.id.layout_container_2);
        buttonChangeView = (Button) findViewById(R.id.button_change_view);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container_1, mapFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container_2, cameraFragment)
                .commit();

        layoutParamsFullscreen = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParamsHidden = new RelativeLayout.LayoutParams(0, 0);

        setMapFragment();
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

    private void setCameraFragment() {
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
    protected void onResume() {
        super.onResume();
        controlViewModel.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controlViewModel.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controlViewModel.destroy();
    }

    public void updateUI(UpdateUIData data) {

    }
}
