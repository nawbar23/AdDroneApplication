package com.addrone.model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;

import com.addrone.R;
import com.addrone.connection.BindView;
import com.multicopter.java.data.CalibrationSettings;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalibrationInfoDialog extends Dialog {

    private CalibrationSettings cs;

    @BindView(R.id.btnClose)
    private Button closeButton;

    public CalibrationInfoDialog(Context context) {
        super(context, android.R.style.Theme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calibration_view_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);

        ButterKnife.bind(this);
    }


    public void setCalibrationSettings(CalibrationSettings cs) {
        this.cs = cs;
    }

    @OnClick(R.id.btnClose)
    public void clickButtonClose() {
        this.dismiss();
    }
}
