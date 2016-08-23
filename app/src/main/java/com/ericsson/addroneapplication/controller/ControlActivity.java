package com.ericsson.addroneapplication.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

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
    ControlViewModel controlViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        controlViewModel = new ControlViewModel(this);

        mapFragment = Fragment.instantiate(this, ControlMapFragment.class.getName());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container, mapFragment)
                .commit();
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
