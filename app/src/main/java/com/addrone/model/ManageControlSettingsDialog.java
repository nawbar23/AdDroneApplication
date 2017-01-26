package com.addrone.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.addrone.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ManageControlSettingsDialog extends Dialog {

    @BindView(R.id.txt_cc_maxTime)
    public TextView maxtime;

    @BindView(R.id.txt_cc_uavType)
    public TextView uavType;

    @BindView(R.id.btn_cc_current)
    public Button currentConfiguration;


    public ManageControlSettingsDialog(Context context) {
        super(context, android.R.style.Theme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.control_settings_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
        ButterKnife.bind(this);
    }

    private void getChosenConfiguration(String conf) {
        //TODO: here get chosen configutarion base on string 'conf'
        loadChosenConfiguration();
    }

    private void loadChosenConfiguration() {
        //TODO: load and prepare peculiar data
        fillWithData();
    }

    private void fillWithData() {
        //TODO: set maxTime and uavType to apropriate values
        //        maxtime = rampampam.getMaxTime();
        //        uavType = rampampipe.getUavType();
    }

    @OnClick(R.id.btn_cc_current)
    public void showAvailableConfigurations() {
        createConfigurationPicker();
    }

    @OnClick(R.id.btn_cc_cancel)
    public void clickButtonCancel() {
        this.dismiss();
    }

    @OnClick(R.id.btn_cc_update_exit)
    public void clickButtonUpdate() {
        //TODO: pin method to save data
        this.dismiss();
    }

    @OnClick(R.id.btn_cc_refresh)
    public void clickButtonRefresh() {
        //TODO: pin method to refresh data
        this.dismiss();
    }

    @OnClick(R.id.btn_cc_new)
    public void clickButtonNew() {
        //TODO: pin method to create new configuration
    }


    public void createConfigurationPicker() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext());
        builderSingle.setTitle("Select Configuration: ");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(super.getContext(), android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("first");
        arrayAdapter.add("second");
        arrayAdapter.add("third");
        arrayAdapter.add("fourth");
        arrayAdapter.add("fifth");

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();

                getChosenConfiguration(strName);
                currentConfiguration.setText(strName);
            }
        });
        builderSingle.show();
    }
}