package com.ericsson.addroneapplication.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.ericsson.addroneapplication.R;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlActivity extends AppCompatActivity {

    Fragment mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        mapFragment = Fragment.instantiate(this, ControlMapFragment.class.getName());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container, mapFragment)
                .commit();
    }
}
