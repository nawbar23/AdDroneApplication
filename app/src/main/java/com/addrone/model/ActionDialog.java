package com.addrone.model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.addrone.R;

/**
 * Created by nawba on 24.10.2016.
 */

public abstract class ActionDialog extends Dialog {

    public enum ButtonId {
        FLY,
        CALIB_ACCEL,
        CALIB_MAGNET,
        DISCONNECT,
        VIEW_CALIB,
        VIEW_CONTROL,
        CHANGE_VIEW;
    }

    public ActionDialog(Context context) {
        super(context, android.R.style.Theme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.action_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);

        findViewById(R.id.btnFly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(ButtonId.FLY);
            }
        });
        findViewById(R.id.btnCalibAccel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(ButtonId.CALIB_ACCEL);
            }
        });
        findViewById(R.id.btnCalibMagnet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(ButtonId.CALIB_MAGNET);
            }
        });
        findViewById(R.id.btnDisconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(ButtonId.DISCONNECT);
            }
        });
        findViewById(R.id.btnViewCalib).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(ButtonId.VIEW_CALIB);
            }
        });
        findViewById(R.id.btnViewControl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(ButtonId.VIEW_CONTROL);
            }
        });

        findViewById(R.id.btnChangeView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(ButtonId.CHANGE_VIEW);
            }
        });
    }

    public abstract void onButtonClick(ButtonId buttonId);
}
