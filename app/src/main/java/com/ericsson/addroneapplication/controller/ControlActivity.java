package com.ericsson.addroneapplication.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ericsson.addroneapplication.R;
import com.ericsson.addroneapplication.model.UpdateUIData;
import com.ericsson.addroneapplication.viewmodel.ControlViewModel;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlActivity extends AppCompatActivity {

    public interface OnControlsChangedListener {

    }

    Fragment mapFragment;
    Fragment cameraFragment;
    ControlViewModel controlViewModel;

    Button buttonChangeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_control);

        controlViewModel = new ControlViewModel(this);

        mapFragment = Fragment.instantiate(this, ControlMapFragment.class.getName());
        cameraFragment = Fragment.instantiate(this, ControlPadFragment.class.getName());

        buttonChangeView = (Button) findViewById(R.id.button_change_view);

        setMapFragment();
    }

    private void setMapFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container, mapFragment)
                .commit();

        buttonChangeView.setText(R.string.camera);
        buttonChangeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCameraFragment();
            }
        });
    }

    private void setCameraFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container, cameraFragment)
                .commit();

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
