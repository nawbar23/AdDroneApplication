package com.addrone.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.addrone.R;
import com.multicopter.java.UavManager;
import com.multicopter.java.data.ControlData;
import com.multicopter.java.data.ControlSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ManageControlSettingsDialog extends Dialog {

    private static final int TEXT_VIEW_ARRAY_SIZE = 36;
    private String name;
    private final ControlSettingsRepo controlSettingsRepo = new ControlSettingsRepo();
    private ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(super.getContext(), android.R.layout.select_dialog_singlechoice);
    private org.json.JSONObject jsonObject = new org.json.JSONObject();
    UavManager uavManager;
    ControlSettings controlSettingsUpload;

    InputFilterMinMax filter = new InputFilterMinMax("0", String.valueOf(Double.POSITIVE_INFINITY)) {
    };

    @BindView(R.id.uav_type)
    public TextView uav_type;

    @BindView(R.id.initial_solver_mode)
    public TextView initial_solver_mode;

    @BindView(R.id.manual_throttle_mode)
    public TextView manual_throttle_mode;

    @BindView(R.id.auto_landing_descend_rate)
    public TextView auto_landing_descend_rate;

    @BindView(R.id.max_auto_landing_time)
    public TextView max_auto_landing_time;

    @BindView(R.id.max_roll_pitch_control_value)
    public TextView max_roll_pitch_control_value;

    @BindView(R.id.max_yaw_control_value)
    public TextView max_yaw_control_value;

    @BindView(R.id.pid_roll_rateX)
    public TextView pid_roll_rateX;

    @BindView(R.id.pid_roll_rateY)
    public TextView pid_roll_rateY;

    @BindView(R.id.pid_roll_rateZ)
    public TextView pid_roll_rateZ;

    @BindView(R.id.pid_pitch_rateX)
    public TextView pid_pitch_rateX;

    @BindView(R.id.pid_pitch_rateY)
    public TextView pid_pitch_rateY;

    @BindView(R.id.pid_pitch_rateZ)
    public TextView pid_pitch_rateZ;

    @BindView(R.id.pid_yaw_rateX)
    public TextView pid_yaw_rateX;

    @BindView(R.id.pid_yaw_rateY)
    public TextView pid_yaw_rateY;

    @BindView(R.id.pid_yaw_rateZ)
    public TextView pid_yaw_rateZ;

    @BindView(R.id.pid_roll_prop)
    public TextView pid_roll_prop;

    @BindView(R.id.pid_pitch_prop)
    public TextView pid_pitch_prop;

    @BindView(R.id.pid_yaw_prop)
    public TextView pid_yaw_prop;

    @BindView(R.id.alt_position_prop)
    public TextView alt_position_prop;

    @BindView(R.id.alt_velocity_prop)
    public TextView alt_velocity_prop;

    @BindView(R.id.pid_throttle_accelX)
    public TextView pid_throttle_accelX;

    @BindView(R.id.pid_throttle_accelY)
    public TextView pid_throttle_accelY;

    @BindView(R.id.pid_throttle_accelZ)
    public TextView pid_throttle_accelZ;

    @BindView(R.id.throttle_alt_rate_prop)
    public TextView throttle_alt_rate_prop;

    @BindView(R.id.max_auto_angle)
    public TextView max_auto_angle;

    @BindView(R.id.max_auto_velocity)
    public TextView max_auto_velocity;

    @BindView(R.id.auto_position_prop)
    public TextView auto_position_prop;

    @BindView(R.id.auto_velocity_prop)
    public TextView auto_velocity_prop;

    @BindView(R.id.pid_auto_accelX)
    public TextView pid_auto_accelX;

    @BindView(R.id.pid_auto_accelY)
    public TextView pid_auto_accelY;

    @BindView(R.id.pid_auto_accelZ)
    public TextView pid_auto_accelZ;

    @BindView(R.id.stick_position_rate_prop)
    public TextView stick_position_rate_prop;

    @BindView(R.id.stick_movement_mode)
    public TextView stick_movement_mode;

    @BindView(R.id.battery_type)
    public TextView battery_type;

    @BindView(R.id.error_handling_action)
    public TextView error_handling_action;

    @BindView(R.id.btn_cc_current)
    public Button currentConfiguration;

    @BindView(R.id.btn_cc_delete)
    public Button delete;

    private final File directory = new File(getContext().getFilesDir().getPath() + File.separator + "controlSettings");

    public ManageControlSettingsDialog(Context context, ControlSettings controlSettings, UavManager uavManager) {
        super(context, android.R.style.Theme_Dialog);
        controlSettingsRepo.setControlSettings(controlSettings);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.control_settings_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.uavManager=uavManager;
//        this.controlSettingsUpload=controlSettings;
        setCancelable(true);
        ButterKnife.bind(this);
        makeEditable();
        checkIfInRange();
        currentConfigurationChoice();
        getChosenConfiguration("current configuration");
        currentConfiguration.setText("current configuration");
        checkIfTheSame();
    }

    private void checkIfInRange() {
        auto_landing_descend_rate.setFilters(new InputFilter[]{filter});
        max_auto_landing_time.setFilters(new InputFilter[]{filter});
        max_roll_pitch_control_value.setFilters(new InputFilter[]{new InputFilterMinMax("0", "0.8727")});
        max_yaw_control_value.setFilters(new InputFilter[]{new InputFilterMinMax("0", "3.4907")});
        pid_roll_rateX.setFilters(new InputFilter[]{filter});
        pid_roll_rateY.setFilters(new InputFilter[]{filter});
        pid_roll_rateZ.setFilters(new InputFilter[]{filter});
        pid_pitch_rateX.setFilters(new InputFilter[]{filter});
        pid_pitch_rateY.setFilters(new InputFilter[]{filter});
        pid_pitch_rateZ.setFilters(new InputFilter[]{filter});
        pid_yaw_rateX.setFilters(new InputFilter[]{filter});
        pid_yaw_rateY.setFilters(new InputFilter[]{filter});
        pid_yaw_rateZ.setFilters(new InputFilter[]{filter});
        pid_roll_prop.setFilters(new InputFilter[]{filter});
        pid_pitch_prop.setFilters(new InputFilter[]{filter});
        pid_yaw_prop.setFilters(new InputFilter[]{filter});
        max_auto_angle.setFilters(new InputFilter[]{new InputFilterMinMax("0", "0.5236")});
        max_auto_velocity.setFilters(new InputFilter[]{new InputFilterMinMax("0", "10")});
        alt_position_prop.setFilters(new InputFilter[]{filter});
        alt_velocity_prop.setFilters(new InputFilter[]{filter});
        auto_position_prop.setFilters(new InputFilter[]{filter});
        auto_velocity_prop.setFilters(new InputFilter[]{filter});
        pid_throttle_accelX.setFilters(new InputFilter[]{filter});
        pid_throttle_accelY.setFilters(new InputFilter[]{filter});
        pid_throttle_accelZ.setFilters(new InputFilter[]{filter});
        pid_auto_accelX.setFilters(new InputFilter[]{filter});
        pid_auto_accelY.setFilters(new InputFilter[]{filter});
        pid_auto_accelZ.setFilters(new InputFilter[]{filter});
        stick_position_rate_prop.setFilters(new InputFilter[]{new InputFilterMinMax("0", "10")});
    }


    private void makeEditable() {

        TextView[] textViewArray;
        textViewArray = new TextView[TEXT_VIEW_ARRAY_SIZE];
        textViewArray[0] = uav_type;
        textViewArray[1] = initial_solver_mode;
        textViewArray[2] = manual_throttle_mode;
        textViewArray[3] = auto_landing_descend_rate;
        textViewArray[4] = max_auto_landing_time;
        textViewArray[5] = max_roll_pitch_control_value;
        textViewArray[6] = max_yaw_control_value;
        textViewArray[7] = pid_roll_rateX;
        textViewArray[8] = pid_roll_rateY;
        textViewArray[9] = pid_roll_rateZ;
        textViewArray[10] = pid_pitch_rateX;
        textViewArray[11] = pid_pitch_rateY;
        textViewArray[12] = pid_pitch_rateZ;
        textViewArray[13] = pid_yaw_rateX;
        textViewArray[14] = pid_yaw_rateY;
        textViewArray[15] = pid_yaw_rateZ;
        textViewArray[16] = pid_roll_prop;
        textViewArray[17] = pid_pitch_prop;
        textViewArray[18] = pid_yaw_prop;
        textViewArray[19] = alt_position_prop;
        textViewArray[20] = alt_velocity_prop;
        textViewArray[21] = pid_throttle_accelX;
        textViewArray[22] = pid_throttle_accelY;
        textViewArray[23] = pid_throttle_accelZ;
        textViewArray[24] = throttle_alt_rate_prop;
        textViewArray[25] = max_auto_angle;
        textViewArray[26] = max_auto_velocity;
        textViewArray[27] = auto_position_prop;
        textViewArray[28] = auto_velocity_prop;
        textViewArray[29] = pid_auto_accelX;
        textViewArray[30] = pid_auto_accelY;
        textViewArray[31] = pid_auto_accelZ;
        textViewArray[32] = stick_position_rate_prop;
        textViewArray[33] = stick_movement_mode;
        textViewArray[34] = battery_type;
        textViewArray[35] = error_handling_action;

        for (int i = 0; i < TEXT_VIEW_ARRAY_SIZE; i++) {
            if (textViewArray[i] == uav_type) {
                continue;
            }
            if (textViewArray[i] == initial_solver_mode) {
                continue;
            }
            if (textViewArray[i] == manual_throttle_mode) {
                continue;
            }
            if (textViewArray[i] == stick_movement_mode) {
                continue;
            }
            if (textViewArray[i] == battery_type) {
                continue;
            }
            if (textViewArray[i] == error_handling_action) {
                continue;
            } else {
                textViewArray[i].setClickable(true);
                textViewArray[i].setFocusableInTouchMode(true);
                textViewArray[i].setInputType(InputType.TYPE_CLASS_TEXT);
                textViewArray[i].requestFocus();
                textViewArray[i].setImeOptions(EditorInfo.IME_ACTION_DONE);
            }

            textViewArray[i].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (textView.getText().toString().trim().length() == 0) {
                        Toast.makeText(getContext(), "Please enter a value!", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
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

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(directory.getPath() + File.separator + name));
            JSONObject jsonObject = (JSONObject) obj;

            String uavType = String.valueOf(ControlSettings.UavType.getUavType(Integer.parseInt(String.valueOf(jsonObject.get("UavType")))));
            uav_type.setText(uavType);

            String initialSolverMode = String.valueOf(ControlData.SolverMode.getSolverMode((byte) Integer.parseInt(String.valueOf(jsonObject.get("InitialSolverMode")))));
            initial_solver_mode.setText(initialSolverMode);

            String manualThrottleMode = String.valueOf(ControlSettings.ThrottleMode.getThrottleMode(Integer.parseInt(String.valueOf(jsonObject.get("ManualThrottleMode")))));
            manual_throttle_mode.setText(manualThrottleMode);

            String autoLandingDescendRate = String.valueOf(jsonObject.get("AutoLandingDescendRate"));
            auto_landing_descend_rate.setText(autoLandingDescendRate);

            String maxAutoLandingTime = String.valueOf(jsonObject.get("MaxAutoLandingTime"));
            max_auto_landing_time.setText(maxAutoLandingTime);

            String maxRollPitchControlValue = String.valueOf(jsonObject.get("MaxRollPitchControlValue"));
            max_roll_pitch_control_value.setText(maxRollPitchControlValue);

            String maxYawControlValue = String.valueOf(jsonObject.get("MaxYawControlValue"));
            max_yaw_control_value.setText(maxYawControlValue);

            String pidRollRateX = String.valueOf(jsonObject.get("PidRollRateX"));
            pid_roll_rateX.setText(pidRollRateX);

            String pidRollRateY = String.valueOf(jsonObject.get("PidRollRateY"));
            pid_roll_rateY.setText(pidRollRateY);

            String pidRollRateZ = String.valueOf(jsonObject.get("PidRollRateZ"));
            pid_roll_rateZ.setText(pidRollRateZ);

            String pidPitchRateX = String.valueOf(jsonObject.get("PidPitchRateX"));
            pid_pitch_rateX.setText(pidPitchRateX);

            String pidPitchRateY = String.valueOf(jsonObject.get("PidPitchRateY"));
            pid_pitch_rateY.setText(pidPitchRateY);

            String pidPitchRateZ = String.valueOf(jsonObject.get("PidPitchRateZ"));
            pid_pitch_rateZ.setText(pidPitchRateZ);

            String pidYawRateX = String.valueOf(jsonObject.get("PidYawRateX"));
            pid_yaw_rateX.setText(pidYawRateX);

            String pidYawRateY = String.valueOf(jsonObject.get("PidYawRateY"));
            pid_yaw_rateY.setText(pidYawRateY);

            String pidYawRateZ = String.valueOf(jsonObject.get("PidYawRateZ"));
            pid_yaw_rateZ.setText(pidYawRateZ);

            String rollProp = String.valueOf(jsonObject.get("RollProp"));
            pid_roll_prop.setText(rollProp);

            String pitchProp = String.valueOf(jsonObject.get("PitchProp"));
            pid_pitch_prop.setText(pitchProp);

            String yawProp = String.valueOf(jsonObject.get("YawProp"));
            pid_yaw_prop.setText(yawProp);

            String altPositionProp = String.valueOf(jsonObject.get("AltPositionProp"));
            alt_position_prop.setText(altPositionProp);

            String altVelocityProp = String.valueOf(jsonObject.get("AltVelocityProp"));
            alt_velocity_prop.setText(altVelocityProp);

            String pidThrottleAccelX = String.valueOf(jsonObject.get("PidThrottleAccelX"));
            pid_throttle_accelX.setText(pidThrottleAccelX);

            String pidThrottleAccelY = String.valueOf(jsonObject.get("PidThrottleAccelY"));
            pid_throttle_accelY.setText(pidThrottleAccelY);

            String pidThrottleAccelZ = String.valueOf(jsonObject.get("PidThrottleAccelZ"));
            pid_throttle_accelZ.setText(pidThrottleAccelZ);

            String throttleAltRateProp = String.valueOf(jsonObject.get("ThrottleAltRateProp"));
            throttle_alt_rate_prop.setText(throttleAltRateProp);

            String maxAutoAngle = String.valueOf(jsonObject.get("MaxAutoAngle"));
            max_auto_angle.setText(maxAutoAngle);

            String maxAutoVelocity = String.valueOf(jsonObject.get("MaxAutoVelocity"));
            max_auto_velocity.setText(maxAutoVelocity);

            String autoPositionProp = String.valueOf(jsonObject.get("AutoPositionProp"));
            auto_position_prop.setText(autoPositionProp);

            String autoVelocityProp = String.valueOf(jsonObject.get("AutoVelocityProp"));
            auto_velocity_prop.setText(autoVelocityProp);

            String pidAutoAccelX = String.valueOf(jsonObject.get("PidAutoAccelX"));
            pid_auto_accelX.setText(pidAutoAccelX);

            String pidAutoAccelY = String.valueOf(jsonObject.get("PidAutoAccelY"));
            pid_auto_accelY.setText(pidAutoAccelY);

            String pidAutoAccelZ = String.valueOf(jsonObject.get("PidAutoAccelZ"));
            pid_auto_accelZ.setText(pidAutoAccelZ);

            String stickPositionRateProp = String.valueOf(jsonObject.get("StickPositionRateProp"));
            stick_position_rate_prop.setText(stickPositionRateProp);

            String stickMovementMode = String.valueOf(ControlSettings.StickMovementMode.getStickMovementMode(Integer.parseInt(String.valueOf(jsonObject.get("StickMovementMode")))));
            stick_movement_mode.setText(stickMovementMode);

            String batteryType = String.valueOf(ControlSettings.BatteryType.getBatteryType(Integer.parseInt(String.valueOf(jsonObject.get("BatteryType")))));
            battery_type.setText(batteryType);

            String errorHandlingAction = String.valueOf(ControlData.ControllerCommand.getControllerCommand((short) Integer.parseInt(String.valueOf(jsonObject.get("ErrorHandlingAction")))));
            error_handling_action.setText(errorHandlingAction);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_cc_current)
    public void showAvailableConfigurations() {
        createConfigurationPicker();
    }

    @OnClick(R.id.btn_cc_cancel)
    public void clickButtonCancel() {
        this.dismiss();
    }

    @OnClick(R.id.btn_cc_upload)
    public void clickButtonUpload() {
        //TODO: pin method to send message to drone
        controlSettingsUpload.setMaxAutoAngle(0.4f);
        controlSettingsUpload.setCrc();
        new Thread(new Runnable() {
            @Override
            public void run() {
                uavManager.uploadControlSettings(controlSettingsUpload);
            }
        }).start();
    }

    @OnClick(R.id.btn_cc_update)
    public void clickButtonUpdate() {

        if (name == null) {
            Toast.makeText(getContext(), "First you should add new configuration!", Toast.LENGTH_LONG).show();
            return;
        }
        if (name.equals("current configuration")) {
            Toast.makeText(getContext(), "Can't update current configuration!", Toast.LENGTH_LONG).show();
            return;
        }
        if (currentConfiguration.getText().toString().contains("\n(current)")) {
            Toast.makeText(getContext(), "Can't update current configuration!", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
        builderInner.setMessage(name);
        builderInner.setTitle("Are you sure you want to update a file: ");
        builderInner.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (updateJSON()) {
                    try {
                        File file = new File(directory.getPath(), name);
                        file.createNewFile();
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(jsonObject.toString());
                        fileWriter.flush();
                        fileWriter.close();

                        AlertDialog.Builder builderInner2 = new AlertDialog.Builder(getContext());
                        builderInner2.setMessage(name);
                        builderInner2.setTitle("You updated a file: ");
                        builderInner2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner2.show();
                    } catch (IOException e) {
                        Log.e(this.getClass().toString(), "Error while updating file:" + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Fail updating file!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        builderInner.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderInner.show();
    }

    @OnClick(R.id.btn_cc_delete)
    public void clickButtonDelete() {
        if (name == null) {
            Toast.makeText(getContext(), "First you should choose a configuration!", Toast.LENGTH_LONG).show();
            return;
        }

        if (currentConfiguration.getText().toString().contains("\n(current)")) {
            Toast.makeText(getContext(), "Can't delete current configuration!", Toast.LENGTH_LONG).show();
            return;
        }

        if (name.equals("current configuration")) {
            Toast.makeText(getContext(), "Can't delete current configuration!", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(getContext());
        deleteDialogBuilder.setMessage(name);
        deleteDialogBuilder.setTitle("Are you sure you want to delete a file: ");
        deleteDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(directory.getPath(), name);
                if (file.delete()) {
                    AlertDialog.Builder innerDialogBuilder = new AlertDialog.Builder(getContext());
                    innerDialogBuilder.setMessage(name);
                    innerDialogBuilder.setTitle("You deleted a file: ");
                    innerDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            currentConfigurationChoice();
                            getChosenConfiguration("current configuration");
                            currentConfiguration.setText("current configuration");
                        }
                    });
                    innerDialogBuilder.show();
                } else {
                    Toast.makeText(getContext(), "Can't delete file!", Toast.LENGTH_LONG).show();
                }
            }
        });
        deleteDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        deleteDialogBuilder.show();
    }

    @OnClick(R.id.btn_cc_new)
    public void clickButtonNew() {
        //TODO: pin method to create new configuration
        final android.support.v7.app.AlertDialog.Builder nameInputDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
        nameInputDialogBuilder.setMessage("Enter a unique name for the repository.");
        final EditText input = new EditText(getContext());
        nameInputDialogBuilder.setView(input);
        nameInputDialogBuilder.setCancelable(true);
        name = input.getText().toString();
        nameInputDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateJSON();
                //TODO after adding download option change "controlSettingsRepo.toJSON()' to 'updateJSON'
                name = input.getText().toString();

                if (isNameAlreadyUsed()) {
                    Toast.makeText(getContext(), "Name is already used! Please enter a unique name.", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    File file = new File(directory.getPath(), name);
                    if (!file.createNewFile()) {
                        return;
                    }
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(jsonObject.toString());
                    fileWriter.flush();
                    fileWriter.close();
                    Toast.makeText(getContext(), "File saved as " + name + " in " + directory.getPath(), Toast.LENGTH_LONG).show();
                    getChosenConfiguration(name);
                    currentConfiguration.setText(name);

                } catch (IOException e) {
                    Log.e(this.getClass().toString(), "Fail while saving file:" + e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Fail while saving file.", Toast.LENGTH_LONG).show();
                }
            }
        });

        nameInputDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        android.support.v7.app.AlertDialog alert = nameInputDialogBuilder.create();
        alert.show();
    }

    private boolean isNameAlreadyUsed() {
        File[] files = new File[0];
        if (directory.listFiles() != null) {
            files = directory.listFiles();
        }
        arrayAdapter.clear();
        for (File file : files) {
            arrayAdapter.add(file.getName());
        }
        for (int i = 0; i < directory.listFiles().length; i++) {
            if (name.equals(arrayAdapter.getItem(i))) {
                return true;
            }
        }
        return false;
    }

    private File currentConfigurationChoice() {
        try {
            controlSettingsRepo.toJSON();
            name = "current configuration";
            try {
                File fileCurrentConfiguration = new File(directory.getPath(), name);
                fileCurrentConfiguration.createNewFile();
                FileWriter fileWriter = new FileWriter(fileCurrentConfiguration);
                fileWriter.write(controlSettingsRepo.jsonObject.toString());
                fileWriter.flush();
                fileWriter.close();
                return fileCurrentConfiguration;
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Fail while saving file:" + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getContext(), "Fail while saving file.", Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error while creating JSON File", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    private void addFilesToArrayAdapter() {
        File[] files = new File[0];
        if (directory.listFiles() != null) {
            files = directory.listFiles();
        }
        arrayAdapter.clear();
        arrayAdapter.add(currentConfigurationChoice().getName());
        for (File file : files) {
            if (file.getName().equals("current configuration")) {
                continue;
            } else {
                arrayAdapter.add(file.getName());
            }
        }
    }

    @OnClick(R.id.uav_type)
    public void uavTypeChoice() {
        arrayAdapter.clear();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext());
        builderSingle.setTitle("Select Configuration: ");

        arrayAdapter.add(String.valueOf(ControlSettings.UavType.getUavType(1000)));
        arrayAdapter.add(String.valueOf(ControlSettings.UavType.getUavType(2000)));
        arrayAdapter.add(String.valueOf(ControlSettings.UavType.getUavType(2500)));
        arrayAdapter.add(String.valueOf(ControlSettings.UavType.getUavType(3000)));
        arrayAdapter.add(String.valueOf(ControlSettings.UavType.getUavType(3500)));
        arrayAdapter.add(String.valueOf(ControlSettings.UavType.getUavType(4000)));
        arrayAdapter.add(String.valueOf(ControlSettings.UavType.getUavType(4500)));


        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String uavType = arrayAdapter.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                builderInner.setMessage(uavType);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();

                uav_type.setText(uavType);
            }
        });
        builderSingle.show();
    }

    @OnClick(R.id.initial_solver_mode)
    public void initialSolverModeChoice() {
        arrayAdapter.clear();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext());
        builderSingle.setTitle("Select Configuration: ");

        arrayAdapter.add(String.valueOf(ControlData.SolverMode.getSolverMode((byte) 0)));
        arrayAdapter.add(String.valueOf(ControlData.SolverMode.getSolverMode((byte) 1)));
        arrayAdapter.add(String.valueOf(ControlData.SolverMode.getSolverMode((byte) 3)));

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String initialSolverMode = arrayAdapter.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                builderInner.setMessage(initialSolverMode);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();

                initial_solver_mode.setText(initialSolverMode);
            }
        });
        builderSingle.show();
    }

    @OnClick(R.id.manual_throttle_mode)
    public void manualThrottleModeChoice() {
        arrayAdapter.clear();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext());
        builderSingle.setTitle("Select Configuration: ");

        arrayAdapter.add(String.valueOf(ControlSettings.ThrottleMode.getThrottleMode(10)));
        arrayAdapter.add(String.valueOf(ControlSettings.ThrottleMode.getThrottleMode(20)));

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String manualThrottleMode = arrayAdapter.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                builderInner.setMessage(manualThrottleMode);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();

                manual_throttle_mode.setText(manualThrottleMode);
            }
        });
        builderSingle.show();
    }

    @OnClick(R.id.stick_movement_mode)
    public void stickMovementModeChoice() {
        arrayAdapter.clear();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext());
        builderSingle.setTitle("Select Configuration: ");

        arrayAdapter.add(String.valueOf(ControlSettings.StickMovementMode.getStickMovementMode(0)));
        arrayAdapter.add(String.valueOf(ControlSettings.StickMovementMode.getStickMovementMode(1)));
        arrayAdapter.add(String.valueOf(ControlSettings.StickMovementMode.getStickMovementMode(2)));

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String stickMovementMode = arrayAdapter.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                builderInner.setMessage(stickMovementMode);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();

                stick_movement_mode.setText(stickMovementMode);
            }
        });
        builderSingle.show();
    }

    @OnClick(R.id.battery_type)
    public void batteryTypeChoice() {
        arrayAdapter.clear();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext());
        builderSingle.setTitle("Select Configuration: ");

        arrayAdapter.add(String.valueOf(ControlSettings.BatteryType.getBatteryType(0)));
        arrayAdapter.add(String.valueOf(ControlSettings.BatteryType.getBatteryType(2)));
        arrayAdapter.add(String.valueOf(ControlSettings.BatteryType.getBatteryType(3)));
        arrayAdapter.add(String.valueOf(ControlSettings.BatteryType.getBatteryType(4)));
        arrayAdapter.add(String.valueOf(ControlSettings.BatteryType.getBatteryType(5)));
        arrayAdapter.add(String.valueOf(ControlSettings.BatteryType.getBatteryType(6)));

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String batteryType = arrayAdapter.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                builderInner.setMessage(batteryType);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();

                battery_type.setText(batteryType);
            }
        });
        builderSingle.show();
    }

    @OnClick(R.id.error_handling_action)
    public void errorHandlingActionChoice() {
        arrayAdapter.clear();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext());
        builderSingle.setTitle("Select Configuration: ");

        arrayAdapter.add(String.valueOf(ControlData.ControllerCommand.getControllerCommand((short) 1100)));
        arrayAdapter.add(String.valueOf(ControlData.ControllerCommand.getControllerCommand((short) 1200)));
        arrayAdapter.add(String.valueOf(ControlData.ControllerCommand.getControllerCommand((short) 1500)));

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String errorHandlingAction = arrayAdapter.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                builderInner.setMessage(errorHandlingAction);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();

                error_handling_action.setText(errorHandlingAction);
            }
        });
        builderSingle.show();
    }

    private void createConfigurationPicker() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext());
        builderSingle.setTitle("Select Configuration: ");

        addFilesToArrayAdapter();

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = arrayAdapter.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                builderInner.setMessage(name);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();

                getChosenConfiguration(name);
                currentConfiguration.setText(name);
            }
        });
        builderSingle.show();
    }

    private void checkIfTheSame() {
        if (comparingJSONs()) {
            currentConfiguration.setText(name + "\n(current)");
        }
    }
    private boolean comparingJSONs() {

        boolean result=false;
        addFilesToArrayAdapter();
        byte[] currentConfigByte = toArrayByte(new File(directory.getPath() + File.separator + "current configuration"));

        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            name = arrayAdapter.getItem(i).toString();
            if (!name.equals("current configuration")) {
                byte[] memoryConfig = toArrayByte(new File(directory.getPath() + File.separator + name));
                if (Arrays.equals(currentConfigByte, memoryConfig)) {
                    result=true;
                    break;
                }
            }
        }
        System.out.println("comparingJSONs "+result);
        return result;
    }


    private byte[] toArrayByte(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
    private boolean updateJSON() {
        try {
            jsonObject.put("UavType", ControlSettings.UavType.valueOf(uav_type.getText().toString()).getValue());
            jsonObject.put("InitialSolverMode", ControlData.SolverMode.valueOf(initial_solver_mode.getText().toString()).getValue());
            jsonObject.put("ManualThrottleMode", ControlSettings.ThrottleMode.valueOf(manual_throttle_mode.getText().toString()).getValue());
            jsonObject.put("AutoLandingDescendRate", auto_landing_descend_rate.getText());
            jsonObject.put("MaxAutoLandingTime", max_auto_landing_time.getText());
            jsonObject.put("MaxRollPitchControlValue", max_roll_pitch_control_value.getText());
            jsonObject.put("MaxYawControlValue", max_yaw_control_value.getText());
            jsonObject.put("PidRollRateX", pid_roll_rateX.getText());
            jsonObject.put("PidRollRateY", pid_roll_rateY.getText());
            jsonObject.put("PidRollRateZ", pid_roll_rateZ.getText());
            jsonObject.put("PidPitchRateX", pid_pitch_rateX.getText());
            jsonObject.put("PidPitchRateY", pid_pitch_rateY.getText());
            jsonObject.put("PidPitchRateZ", pid_pitch_rateZ.getText());
            jsonObject.put("PidYawRateX", pid_yaw_rateX.getText());
            jsonObject.put("PidYawRateY", pid_yaw_rateY.getText());
            jsonObject.put("PidYawRateZ", pid_yaw_rateZ.getText());
            jsonObject.put("RollProp", pid_roll_prop.getText());
            jsonObject.put("PitchProp", pid_pitch_prop.getText());
            jsonObject.put("YawProp", pid_yaw_prop.getText());
            jsonObject.put("AltPositionProp", alt_position_prop.getText());
            jsonObject.put("AltVelocityProp", alt_velocity_prop.getText());
            jsonObject.put("PidThrottleAccelX", pid_throttle_accelX.getText());
            jsonObject.put("PidThrottleAccelY", pid_throttle_accelY.getText());
            jsonObject.put("PidThrottleAccelZ", pid_throttle_accelZ.getText());
            jsonObject.put("ThrottleAltRateProp", throttle_alt_rate_prop.getText());
            jsonObject.put("MaxAutoAngle", max_auto_angle.getText());
            jsonObject.put("MaxAutoVelocity", max_auto_velocity.getText());
            jsonObject.put("AutoPositionProp", auto_position_prop.getText());
            jsonObject.put("AutoVelocityProp", auto_velocity_prop.getText());
            jsonObject.put("PidAutoAccelX", pid_auto_accelX.getText());
            jsonObject.put("PidAutoAccelY", pid_auto_accelY.getText());
            jsonObject.put("PidAutoAccelZ", pid_auto_accelZ.getText());
            jsonObject.put("StickPositionRateProp", stick_position_rate_prop.getText());
            jsonObject.put("StickMovementMode", ControlSettings.StickMovementMode.valueOf(stick_movement_mode.getText().toString()).getValue());
            jsonObject.put("BatteryType", ControlSettings.BatteryType.valueOf(battery_type.getText().toString()).getValue());
            jsonObject.put("ErrorHandlingAction", ControlData.ControllerCommand.valueOf(error_handling_action.getText().toString()).getValue());

            return true;
        } catch (JSONException e) {
            Log.e(this.getClass().toString(), "Error while updating a JSON file:" + e.getMessage());
            return false;
        }
    }

    class InputFilterMinMax implements InputFilter {

        private float min, max;


        public InputFilterMinMax(String min, String max) {
            this.min = Float.parseFloat(min);
            this.max = Float.parseFloat(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                float input = Float.parseFloat(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(float a, float b, float c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}

