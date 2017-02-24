package com.addrone.model;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ManageControlSettingsDialog extends Dialog {

    private static final int TEXT_VIEW_ARRAY_SIZE = 36;
    private final String BORD_CONF_NAME;
    private final ControlSettingsRepo controlSettingsRepo = new ControlSettingsRepo();
    private final File DIR = new File(getContext().getFilesDir().getPath() + File.separator + "controlSettings");
    private final UavManager uavManager;
    private final ControlSettings controlSettingsObject;
    private final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(super.getContext(), android.R.layout.select_dialog_singlechoice);
    private final InputFilterMinMax filter = new InputFilterMinMax("0", String.valueOf(Double.POSITIVE_INFINITY)) {
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
    @BindView(R.id.btn_cc_configurations_list)
    public Button currentConfiguration;
    @BindView(R.id.btn_cc_delete)
    public Button delete;

    public ManageControlSettingsDialog(Context context, ControlSettings controlSettingsFromBoard, UavManager uavManager) {
        super(context, android.R.style.Theme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.control_settings_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
        ButterKnife.bind(this);
        BORD_CONF_NAME = context.getString(R.string.board_configuration);
        DIR.mkdirs();

        this.uavManager = uavManager;
        this.controlSettingsObject = controlSettingsFromBoard;
        controlSettingsRepo.setControlSettings(controlSettingsObject);

        prepareSettingsViews();

        saveCurrentConfigAsJSON(BORD_CONF_NAME);
        loadChosenConfiguration(BORD_CONF_NAME);

        checkIfTheSame();
    }

    private void prepareSettingsViews() {
        TextView[] textViewArray = putSettingsTextViewsToArray();
        makeParticularFieldsEditable(textViewArray);
        setFiltersOnSettingsInput();
    }

    private void setFiltersOnSettingsInput() {
        //TODO: somehow inform user about possible range
        //TODO: like makeEditable function
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


    private TextView[] putSettingsTextViewsToArray() {
        TextView[] textViewArray = new TextView[TEXT_VIEW_ARRAY_SIZE];
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

        return textViewArray;
    }

    private void makeParticularFieldsEditable(TextView[] textViewArray) {
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

    private void loadChosenConfiguration(String configurationName) {
        fillDialogWithControlSettingsData(configurationName);
    }


    private void fillDialogWithControlSettingsData(String name) {

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(DIR.getPath() + File.separator + name));
            JSONObject jsonObject = (JSONObject) obj;

            uav_type.setText(String.valueOf(ControlSettings.UavType.getUavType(
                    Integer.parseInt(String.valueOf(jsonObject.get("UavType"))))));
            initial_solver_mode.setText(String.valueOf(ControlData.SolverMode.getSolverMode(
                    (byte) Integer.parseInt(String.valueOf(jsonObject.get("InitialSolverMode"))))));
            manual_throttle_mode.setText(String.valueOf(ControlSettings.ThrottleMode.getThrottleMode(
                    Integer.parseInt(String.valueOf(jsonObject.get("ManualThrottleMode"))))));
            auto_landing_descend_rate.setText(String.valueOf(jsonObject.get("AutoLandingDescendRate")));
            max_auto_landing_time.setText(String.valueOf(jsonObject.get("MaxAutoLandingTime")));
            max_roll_pitch_control_value.setText(String.valueOf(jsonObject.get("MaxRollPitchControlValue")));
            max_yaw_control_value.setText(String.valueOf(jsonObject.get("MaxYawControlValue")));
            pid_roll_rateX.setText(String.valueOf(jsonObject.get("PidRollRateX")));
            pid_roll_rateY.setText(String.valueOf(jsonObject.get("PidRollRateY")));
            pid_roll_rateZ.setText(String.valueOf(jsonObject.get("PidRollRateZ")));
            pid_pitch_rateX.setText(String.valueOf(jsonObject.get("PidPitchRateX")));
            pid_pitch_rateY.setText(String.valueOf(jsonObject.get("PidPitchRateY")));
            pid_pitch_rateZ.setText(String.valueOf(jsonObject.get("PidPitchRateZ")));
            pid_yaw_rateX.setText(String.valueOf(jsonObject.get("PidYawRateX")));
            pid_yaw_rateY.setText(String.valueOf(jsonObject.get("PidYawRateY")));
            pid_yaw_rateZ.setText(String.valueOf(jsonObject.get("PidYawRateZ")));
            pid_roll_prop.setText(String.valueOf(jsonObject.get("RollProp")));
            pid_pitch_prop.setText(String.valueOf(jsonObject.get("PitchProp")));
            pid_yaw_prop.setText(String.valueOf(jsonObject.get("YawProp")));
            alt_position_prop.setText(String.valueOf(jsonObject.get("AltPositionProp")));
            alt_velocity_prop.setText(String.valueOf(jsonObject.get("AltVelocityProp")));
            pid_throttle_accelX.setText(String.valueOf(jsonObject.get("PidThrottleAccelX")));
            pid_throttle_accelY.setText(String.valueOf(jsonObject.get("PidThrottleAccelY")));
            pid_throttle_accelZ.setText(String.valueOf(jsonObject.get("PidThrottleAccelZ")));
            throttle_alt_rate_prop.setText(String.valueOf(jsonObject.get("ThrottleAltRateProp")));
            max_auto_angle.setText(String.valueOf(jsonObject.get("MaxAutoAngle")));
            max_auto_velocity.setText(String.valueOf(jsonObject.get("MaxAutoVelocity")));
            auto_position_prop.setText(String.valueOf(jsonObject.get("AutoPositionProp")));
            auto_velocity_prop.setText(String.valueOf(jsonObject.get("AutoVelocityProp")));
            pid_auto_accelX.setText(String.valueOf(jsonObject.get("PidAutoAccelX")));
            pid_auto_accelY.setText(String.valueOf(jsonObject.get("PidAutoAccelY")));
            pid_auto_accelZ.setText(String.valueOf(jsonObject.get("PidAutoAccelZ")));
            stick_position_rate_prop.setText(String.valueOf(jsonObject.get("StickPositionRateProp")));
            stick_movement_mode.setText(String.valueOf(ControlSettings.StickMovementMode
                    .getStickMovementMode(Integer.parseInt(String.valueOf(jsonObject.get("StickMovementMode"))))));
            battery_type.setText(String.valueOf(ControlSettings.BatteryType.getBatteryType(
                    Integer.parseInt(String.valueOf(jsonObject.get("BatteryType"))))));
            error_handling_action.setText(String.valueOf(ControlData.ControllerCommand.getControllerCommand(
                    (short) Integer.parseInt(String.valueOf(jsonObject.get("ErrorHandlingAction"))))));

            currentConfiguration.setText(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_cc_configurations_list)
    public void showAvailableConfigurations() {
        createConfigurationPicker();
    }

    @OnClick(R.id.btn_cc_cancel)
    public void clickButtonCancel() {
        this.dismiss();
    }

    @OnClick(R.id.btn_cc_upload)
    public void clickButtonUpload() {
        updateControlSettingsObject();
        new Thread(new Runnable() {
            @Override
            public void run() {
                uavManager.uploadControlSettings(controlSettingsObject);
            }
        }).start();

        this.dismiss();
    }

    private void updateControlSettingsObject() {
        controlSettingsObject.setUavType(ControlSettings.UavType.valueOf(uav_type.getText().toString()).getValue());
        controlSettingsObject.setInitialSolverMode(ControlData.SolverMode.valueOf(initial_solver_mode.getText().toString()).getValue());
        controlSettingsObject.setManualThrottleMode(ControlSettings.ThrottleMode.valueOf(manual_throttle_mode.getText().toString()).getValue());
        controlSettingsObject.setAutoLandingDescendRate(Float.parseFloat(String.valueOf(auto_landing_descend_rate.getText())));
        controlSettingsObject.setMaxAutoLandingTime(Float.parseFloat(String.valueOf(max_auto_landing_time.getText())));
        controlSettingsObject.setMaxRollPitchControlValue(Float.parseFloat(String.valueOf(max_roll_pitch_control_value.getText())));
        controlSettingsObject.setMaxYawControlValue(Float.parseFloat(String.valueOf(max_yaw_control_value.getText())));
        controlSettingsObject.setPidRollRate(new float[]{Float.parseFloat(String.valueOf(pid_roll_rateX.getText())),
                Float.parseFloat(String.valueOf(pid_roll_rateY.getText())), Float.parseFloat(String.valueOf(pid_roll_rateZ.getText()))});
        controlSettingsObject.setPidPitchRate(new float[]{Float.parseFloat(String.valueOf(pid_pitch_rateX.getText())),
                Float.parseFloat(String.valueOf(pid_pitch_rateY.getText())), Float.parseFloat(String.valueOf(pid_pitch_rateZ.getText()))});
        controlSettingsObject.setPidYawRate(new float[]{Float.parseFloat(String.valueOf(pid_yaw_rateX.getText())),
                Float.parseFloat(String.valueOf(pid_yaw_rateY.getText())), Float.parseFloat(String.valueOf(pid_yaw_rateZ.getText()))});
        controlSettingsObject.setRollProp(Float.parseFloat(String.valueOf(pid_roll_prop.getText())));
        controlSettingsObject.setPitchProp(Float.parseFloat(String.valueOf(pid_pitch_prop.getText())));
        controlSettingsObject.setYawProp(Float.parseFloat(String.valueOf(pid_yaw_prop.getText())));
        controlSettingsObject.setAltPositionProp(Float.parseFloat(String.valueOf(alt_position_prop.getText())));
        controlSettingsObject.setAltVelocityProp(Float.parseFloat(String.valueOf(alt_velocity_prop.getText())));
        controlSettingsObject.setPidThrottleAccel(new float[]{Float.parseFloat(String.valueOf(pid_throttle_accelX.getText())),
                Float.parseFloat(String.valueOf(pid_throttle_accelY.getText())), Float.parseFloat(String.valueOf(pid_throttle_accelZ.getText()))});
        controlSettingsObject.setThrottleAltRateProp(Float.parseFloat(String.valueOf(throttle_alt_rate_prop.getText())));
        controlSettingsObject.setMaxAutoAngle(Float.parseFloat(String.valueOf(max_auto_angle.getText())));
        controlSettingsObject.setMaxAutoVelocity(Float.parseFloat(String.valueOf(max_auto_velocity.getText())));
        controlSettingsObject.setAutoPositionProp(Float.parseFloat(String.valueOf(auto_position_prop.getText())));
        controlSettingsObject.setAutoVelocityProp(Float.parseFloat(String.valueOf(auto_velocity_prop.getText())));
        controlSettingsObject.setPidAutoAccel(new float[]{Float.parseFloat(String.valueOf(pid_auto_accelX.getText())),
                Float.parseFloat(String.valueOf(pid_auto_accelY.getText())), Float.parseFloat(String.valueOf(pid_auto_accelZ.getText()))});
        controlSettingsObject.setStickPositionRateProp(Float.parseFloat(String.valueOf(stick_position_rate_prop.getText())));
        controlSettingsObject.setStickMovementMode(ControlSettings.StickMovementMode.valueOf(stick_movement_mode.getText().toString()).getValue());
        controlSettingsObject.setBatteryType(ControlSettings.BatteryType.valueOf(battery_type.getText().toString()).getValue());
        controlSettingsObject.setErrorHandlingAction(ControlData.ControllerCommand.valueOf(error_handling_action.getText().toString()).getValue());
        controlSettingsObject.setCrc();
    }

    @OnClick(R.id.btn_cc_update)
    public void clickButtonUpdate() {
        String name = currentConfiguration.getText().toString();
        if (name.equals(getContext().getString(R.string.board_configuration))) {
            Toast.makeText(getContext(), "Can't update board configuration!", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        builderInner.setMessage(name);
        builderInner.setTitle("Are you sure you want to update a file: ");
        builderInner.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = currentConfiguration.getText().toString();

                updateControlSettingsObject();
                deleteConfiguration(name);
                saveCurrentConfigAsJSON(name);
            }
        });
        builderInner.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderInner.show();

        this.dismiss();
    }

    @OnClick(R.id.btn_cc_delete)
    public void clickButtonDelete() {
        String name = currentConfiguration.getText().toString();
        if (name.equals(BORD_CONF_NAME)) {
            Toast.makeText(getContext(), "Can't delete board configuration!", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        deleteDialogBuilder.setMessage(name);
        deleteDialogBuilder.setTitle("Are you sure you want to delete configuration: ");
        deleteDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int clickedItem) {
                String name = currentConfiguration.getText().toString();
                if (deleteConfiguration(name)) {
                    loadChosenConfiguration(BORD_CONF_NAME);
                    Toast.makeText(getContext(), "You deleted a file: " + name, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Can't delete file!", Toast.LENGTH_SHORT).show();
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

    private boolean deleteConfiguration(String confName) {
        File file = new File(DIR.getPath(), confName);
        return file.delete();
    }

    @OnClick(R.id.btn_cc_new)
    public void clickButtonNew() {
        final AlertDialog.Builder nameInputDialogBuilder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        nameInputDialogBuilder.setMessage("Enter a unique name for the repository.");
        final EditText input = new EditText(getContext());
        input.setSingleLine();
        nameInputDialogBuilder.setView(input);
        nameInputDialogBuilder.setCancelable(true);
        nameInputDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName = input.getText().toString();
                if (isNameAlreadyUsed(newName)) {
                    Toast.makeText(getContext(), "Name is already used! Please enter a unique name.", Toast.LENGTH_LONG).show();
                    return;
                }

                updateControlSettingsObject();
                saveCurrentConfigAsJSON(newName);
                loadChosenConfiguration(newName);
            }
        });

        nameInputDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog alert = nameInputDialogBuilder.create();
        alert.show();
    }

    private boolean isNameAlreadyUsed(String name) {
        File[] files = new File[0];
        if (DIR.listFiles() != null) {
            files = DIR.listFiles();
        }
        arrayAdapter.clear();
        for (File file : files) {
            arrayAdapter.add(file.getName());
        }
        for (int i = 0; i < DIR.listFiles().length; i++) {
            if (name.equals(arrayAdapter.getItem(i))) {
                return true;
            }
        }
        return false;
    }


    private void saveCurrentConfigAsJSON(String fileName) {
        try {
            JSONObject jsonToSave = controlSettingsRepo.controlSettingsToJSON();
            try {
                deleteConfiguration(fileName);
                File fil = new File(DIR.getPath(), fileName);
                if (fil.createNewFile()) {
                    FileWriter fileWriter = new FileWriter(fil);
                    fileWriter.write(jsonToSave.toString());
                    fileWriter.flush();
                    fileWriter.close();
                    Log.d(this.getClass().toString(), "File saved:" + DIR.getPath()
                            + " name: " + fileName);
                    Toast.makeText(getContext(), "File saved.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "File not saved!", Toast.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Fail while saving file:" + e.getMessage()
                        + " path: " + DIR.getPath() + " name: " + fileName);
                e.printStackTrace();
                Toast.makeText(getContext(), "Fail while saving file.", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error while creating JSON File", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.e(this.getClass().toString(), "Error while creating JSON File");
        }
    }

    private void addFilesToArrayAdapter() {
        File[] files = new File[0];
        if (DIR.listFiles() != null) {
            files = DIR.listFiles();
        }
        arrayAdapter.clear();
        try {
            //arrayAdapter.add(saveConfigurationToJSONFile().getName());
            for (File file : files) {
//                if (!file.getName().equals(getContext().getString(R.string.current_configuration))) {
                arrayAdapter.add(file.getName());
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "No start configuration.", Toast.LENGTH_SHORT).show();

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
                battery_type.setText(batteryType);
            }
        });
        builderSingle.show();
    }

    @OnClick(R.id.error_handling_action)
    public void errorHandlingActionChoice() {
        arrayAdapter.clear();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext(), R.style.DarkAlertDialog);
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
                error_handling_action.setText(errorHandlingAction);
            }
        });
        builderSingle.show();
    }

    private void createConfigurationPicker() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext(), R.style.DarkAlertDialog);
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
            public void onClick(DialogInterface dialog, int clickedItem) {
                String itemName = arrayAdapter.getItem(clickedItem);
                loadChosenConfiguration(itemName);
                Toast.makeText(getContext(), "You selected a file: " + itemName, Toast.LENGTH_SHORT).show();
            }
        });
        builderSingle.show();
    }

    private void checkIfTheSame() {
        String text = comparingJSONs();
        if (text != null) {
            currentConfiguration.setText(text);
        }
    }

    private String comparingJSONs() {
        String name;
        addFilesToArrayAdapter();
        byte[] currentConfigByte = toArrayByte(new File(DIR.getPath() + File.separator + BORD_CONF_NAME));

        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            name = arrayAdapter.getItem(i);
            if (name != null && !name.equals(BORD_CONF_NAME)) {
                byte[] memoryConfig = toArrayByte(new File(DIR.getPath() + File.separator + name));
                if (Arrays.equals(currentConfigByte, memoryConfig)) {
                    return name;
                }
            }
        }
        return null;
    }


    private byte[] toArrayByte(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private class InputFilterMinMax implements InputFilter {
        private float min, max;

        private InputFilterMinMax(String min, String max) {
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
                nfe.printStackTrace();
            }
            return "";
        }

        private boolean isInRange(float a, float b, float c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}

