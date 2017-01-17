package com.addrone.model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.addrone.R;

/**
 * Created by emergency on 2017-01-13.
 */

public abstract class MagnetCalibDialog extends Dialog {

    public enum ButtonCalibId {
        DONE,
        CANCEL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView rocketImage = (ImageView) findViewById(R.id.imageview_animation);
        rocketImage.setBackgroundResource(R.drawable.animation_magnet);
        AnimationDrawable frameAnimation = (AnimationDrawable) rocketImage.getBackground();
        frameAnimation.start();

    }

    public MagnetCalibDialog(Context context) {
        super(context, android.R.style.Theme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.magnetometer_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);

        findViewById(R.id.done_magnet_calib).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonMagnetCalibClick(ButtonCalibId.DONE);
            }
        });

        findViewById(R.id.cancel_magnet_calib).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonMagnetCalibClick(ButtonCalibId.CANCEL);
            }
        });
    }

    public abstract void onButtonMagnetCalibClick(ButtonCalibId buttonCalibId);
}
