package com.addrone.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.addrone.R;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ManageControlSettingsDialog extends Dialog {

    String name;
    ControlSettingsRepo controlSettingsRepo = new ControlSettingsRepo();
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(super.getContext(), android.R.layout.select_dialog_singlechoice);
    org.json.JSONObject jsonObject = new org.json.JSONObject();

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

    File directory = new File(getContext().getFilesDir().getPath()+File.separator +"controlSettings");

    public ManageControlSettingsDialog(Context context) {
        super(context, android.R.style.Theme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.control_settings_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
        ButterKnife.bind(this);
        makeEditable();
    }

    public void makeEditable(){

        TextView[] textViewArray;
        textViewArray = new TextView[36];
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

        for (int i = 0; i<= 35; i++) {
            textViewArray[i].setClickable(true);
            textViewArray[i].setFocusableInTouchMode(true);
            textViewArray[i].setInputType(InputType.TYPE_CLASS_TEXT);
            textViewArray[i].requestFocus();
            textViewArray[i].setImeOptions(EditorInfo.IME_ACTION_DONE);
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
            JSONObject jsonObject = (JSONObject)obj;

            String uavType = String.valueOf(jsonObject.get("UavType"));
            uav_type.setText(uavType);

            String initialSolverMode = String.valueOf(jsonObject.get("InitialSolverMode"));
            initial_solver_mode.setText(initialSolverMode);

            String manualThrottleMode = String.valueOf(jsonObject.get("ManualThrottleMode"));
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

            String stickMovementMode = String.valueOf(jsonObject.get("StickMovementMode"));
            stick_movement_mode.setText(stickMovementMode);

            String batteryType = String.valueOf(jsonObject.get("BatteryType"));
            battery_type.setText(batteryType);

            String errorHandlingAction = String.valueOf(jsonObject.get("ErrorHandlingAction"));
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
        }

    @OnClick(R.id.btn_cc_update)
    public void clickButtonUpdate() {

        if (name == null)
        {
            Toast toast = Toast.makeText(getContext(), "First you should add new configuration!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
        builderInner.setMessage(name);
        builderInner.setTitle("Are you sure you want to update a file: ");
        builderInner.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateJSON();
                File fileB = new File(directory.getPath(),name);
                FileWriter file = null;
                try {
                    fileB.createNewFile();
                    file = new FileWriter(fileB);
                    file.write(jsonObject.toString());
                    file.flush();
                    file.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

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
    public void clickButtonDelete(){
        if (name == null)
        {
            Toast toast = Toast.makeText(getContext(), "First you should choose a configuration!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
        builderInner.setMessage(name);
        builderInner.setTitle("Are you sure you want to delete a file: ");
        builderInner.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File fileB = new File(directory.getPath(),name);
                    fileB.delete();
                    AlertDialog.Builder builderInner2 = new AlertDialog.Builder(getContext());
                    builderInner2.setMessage(name);
                    builderInner2.setTitle("You deleted a file: ");
                    builderInner2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderInner2.show();
                }
            });
        builderInner.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        builderInner.show();

        //TODO add a method which will show data uploaded during click on manage control settings,
    }

    @OnClick(R.id.btn_cc_new)
    public void clickButtonNew() {
        //TODO: pin method to create new configuration
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setMessage("Enter a unique name for the repository.");
        final EditText input = new EditText(getContext());
        builder.setView(input);
        builder.setCancelable(true);
        name = input.getText().toString();
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                controlSettingsRepo.toJSON();
                directory.mkdirs();
                File[] files = directory.listFiles();
                for (i = 0; i < files.length; i++)
                {
                    arrayAdapter.add(files[i].getName());
                }
                name = input.getText().toString();

                for (i = 0; i < directory.listFiles().length; i++) {
                    if (name.equals(arrayAdapter.getItem(i))) {
                        Toast toast = Toast.makeText(getContext(), "Name is already used! Please enter a unique name.", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                }
                    File fileB = new File(directory.getPath(),name);
                    FileWriter file = null;
                    try {
                        fileB.createNewFile();
                        file = new FileWriter(fileB);
                        file.write(controlSettingsRepo.jsonObject.toString());
                        file.flush();
                        file.close();
                        Toast toast = Toast.makeText(getContext(),"File saved as " + name + " in " + directory.getPath(),Toast.LENGTH_LONG);
                        toast.show();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }


    public void createConfigurationPicker() {
        arrayAdapter.clear();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(super.getContext());
        builderSingle.setTitle("Select Configuration: ");

        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            arrayAdapter.add(files[i].getName());
        }

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

    public void updateJSON(){
        try{
            jsonObject.put("UavType", uav_type.getText());
            jsonObject.put("InitialSolverMode", initial_solver_mode.getText());
            jsonObject.put("ManualThrottleMode", manual_throttle_mode.getText());
            jsonObject.put("AutoLandingDescendRate", auto_landing_descend_rate.getText());
            jsonObject.put("MaxAutoLandingTime", max_auto_landing_time.getText());
            jsonObject.put("MaxRollPitchControlValue", max_roll_pitch_control_value.getText());
            jsonObject.put("MaxYawControlValue",max_yaw_control_value.getText());
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
            jsonObject.put("StickMovementMode", stick_movement_mode.getText());
            jsonObject.put("BatteryType", battery_type.getText());
            jsonObject.put("ErrorHandlingAction", error_handling_action.getText());
        }
        catch (Exception e){
            System.out.println("Error while updating a JSON file!");
        }
    }
}