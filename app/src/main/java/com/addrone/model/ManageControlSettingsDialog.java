package com.addrone.model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.addrone.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ManageControlSettingsDialog extends Dialog {

    @BindView(R.id.txt_cc_maxTime)
    public TextView maxtime;

    @BindView(R.id.txt_cc_uavType)
    public TextView uavType;


    public ManageControlSettingsDialog(Context context) {
        super(context, android.R.style.Theme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calibration_control_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
        ButterKnife.bind(this);
        fillWithData();

    }

    private void fillWithData() {
//        maxtime = rampampam.getTime();
//        uavType = rampampam.getUavType();
    }

    @OnClick(R.id.btn_cc_current)
    public void showAvailableConfigurations() {
        Toast.makeText(getContext(), "Here will be list of available configurations...", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_cc_cancel)
    public void clickButtonCancel() {
        this.dismiss();
    }

}
