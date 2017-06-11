package com.addrone.model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import com.addrone.R;
import com.skydive.java.data.CalibrationSettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalibrationInfoDialog extends Dialog {

    private static final float KELVIN_DIFF = 273;

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

    @BindView(R.id.gyroX)
    public TextView gyroX;
    @BindView(R.id.gyroY)
    public TextView gyroY;
    @BindView(R.id.gyroZ)
    public TextView gyroZ;

    @BindView(R.id.soft0)
    public TextView soft0;
    @BindView(R.id.soft1)
    public TextView soft1;
    @BindView(R.id.soft2)
    public TextView soft2;
    @BindView(R.id.soft3)
    public TextView soft3;
    @BindView(R.id.soft4)
    public TextView soft4;
    @BindView(R.id.soft5)
    public TextView soft5;
    @BindView(R.id.soft6)
    public TextView soft6;
    @BindView(R.id.soft7)
    public TextView soft7;
    @BindView(R.id.soft8)
    public TextView soft8;

    @BindView(R.id.hardX)
    public TextView hardX;
    @BindView(R.id.hardY)
    public TextView hardY;
    @BindView(R.id.hardZ)
    public TextView hardZ;

    @BindView(R.id.altimeter)
    public TextView altimeter;

    @BindView(R.id.temperature)
    public TextView temperature;

    @BindView(R.id.board_type)
    public TextView boardType;

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
        fillSoftMagnet(cs.getMagnetSoft());
        fillHardMagnet(cs.getMagnetHard());
        fillAltimeter(cs.getAltimeterSetting());
        fillTemperature(cs.getTemperatureSetting());
        fillBoardType(cs.getBoardType().toString());
    }

    private void fillBoardType(String boardType) {
        this.boardType.setText(boardType);
    }

    private void fillTemperature(float temperatureSetting) {
        String txt = String.format("%.0f", temperatureSetting - KELVIN_DIFF) + " °C";
        temperature.setText(txt);
    }

    private void fillAltimeter(float altimeterSetting) {
        String txt = String.format("%.0f", altimeterSetting) + " hPa";
        altimeter.setText(txt);
    }

    private void fillHardMagnet(float[] magnetHard) {
        hardX.setText(String.format("%.4f", magnetHard[0]));
        hardY.setText(String.format("%.4f", magnetHard[1]));
        hardZ.setText(String.format("%.4f", magnetHard[2]));
    }

    private void fillSoftMagnet(float[] magnetSoft) {
        soft0.setText(String.format("%.4f", magnetSoft[0]));
        soft1.setText(String.format("%.4f", magnetSoft[1]));
        soft2.setText(String.format("%.4f", magnetSoft[2]));
        soft3.setText(String.format("%.4f", magnetSoft[3]));
        soft4.setText(String.format("%.4f", magnetSoft[4]));
        soft5.setText(String.format("%.4f", magnetSoft[5]));
        soft6.setText(String.format("%.4f", magnetSoft[6]));
        soft7.setText(String.format("%.4f", magnetSoft[7]));
        soft8.setText(String.format("%.4f", magnetSoft[8]));
    }

    private void fillGyro(float[] gyroOffset) {
        gyroX.setText(String.format("%.3f", gyroOffset[0]));
        gyroY.setText(String.format("%.3f", gyroOffset[1]));
        gyroZ.setText(String.format("%.3f", gyroOffset[2]));
    }

    private void fillAcceleration(float[] accCalibration) {
        acc0.setText(String.format("%.4f", accCalibration[0]));
        acc1.setText(String.format("%.4f", accCalibration[1]));
        acc2.setText(String.format("%.4f", accCalibration[2]));
        acc3.setText(String.format("%.4f", accCalibration[3]));
        acc4.setText(String.format("%.4f", accCalibration[4]));
        acc5.setText(String.format("%.4f", accCalibration[5]));
        acc6.setText(String.format("%.4f", accCalibration[6]));
        acc7.setText(String.format("%.4f", accCalibration[7]));
        acc8.setText(String.format("%.4f", accCalibration[8]));
    }
}
