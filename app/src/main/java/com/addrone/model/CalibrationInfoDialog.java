package com.addrone.model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.addrone.R;
import com.addrone.connection.BindView;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalibrationInfoDialog extends Dialog {

    private TextView textView;

    @BindView(R.id.btnClose)
    private Button closeButton;

    public CalibrationInfoDialog(Context context) {
        super(context, android.R.style.Theme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calibration_view_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);

        textView = (TextView) this.findViewById(R.id.calibration_text);
        ButterKnife.bind(this);
    }

    public void setTextView(String text) {
        textView.setText(text);
    }

    @OnClick(R.id.btnClose)
    public void clickButtonClose() {
        this.dismiss();
    }
}
