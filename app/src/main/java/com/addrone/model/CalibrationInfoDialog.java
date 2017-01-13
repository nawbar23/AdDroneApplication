package com.addrone.model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import com.addrone.R;
import com.multicopter.java.data.CalibrationSettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalibrationInfoDialog extends Dialog {

    @BindView(R.id.acc0)
    public TextView acc0;
    @BindView(R.id.acc1)
    public TextView acc1;
    @BindView(R.id.acc2)
    public TextView acc2;
    @BindView(R.id.acc3)
    public TextView acc3;
    @BindView(R.id.acc4)
    public TextView acc4;
    @BindView(R.id.acc5)
    public TextView acc5;
    @BindView(R.id.acc6)
    public TextView acc6;
    @BindView(R.id.acc7)
    public TextView acc7;
    @BindView(R.id.acc8)
    public TextView acc8;

    public CalibrationInfoDialog(Context context) {
        super(context, android.R.style.Theme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calibration_view_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnClose)
    public void clickButtonClose() {
        this.dismiss();
    }

    public void fillWithParams(CalibrationSettings cs) {
        fillAcceleration(cs.getAccelCalib());
        fillGyro(cs.getGyroOffset());
    }

    private void fillGyro(float[] gyroOffset) {

    }

    public void fillAcceleration(float[] accelCalib) {
        acc0.setText(String.format("%.4f", accelCalib[0]));
        acc1.setText(String.format("%.4f", accelCalib[1]));
        acc2.setText(String.format("%.4f", accelCalib[2]));
        acc3.setText(String.format("%.4f", accelCalib[3]));
        acc4.setText(String.format("%.4f", accelCalib[4]));
        acc5.setText(String.format("%.4f", accelCalib[5]));
        acc6.setText(String.format("%.4f", accelCalib[6]));
        acc7.setText(String.format("%.4f", accelCalib[7]));
        acc8.setText(String.format("%.4f", accelCalib[8]));
    }
}
