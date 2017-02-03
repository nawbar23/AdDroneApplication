package com.addrone.model;

import android.util.Log;

import com.multicopter.java.data.ControlSettings;

import org.json.JSONException;
import org.json.JSONObject;

public class ControlSettingsRepo {

    JSONObject jsonObject = new JSONObject();

    //temporary mock ControlSettings
    private ControlSettings controlSettings = new ControlSettings();

    public JSONObject toJSON() throws JSONException {
        try {
            jsonObject.put("UavType", controlSettings.getUavType());
            jsonObject.put("InitialSolverMode", controlSettings.getInitialSolverMode());
            jsonObject.put("ManualThrottleMode", controlSettings.getManualThrottleMode());
            jsonObject.put("AutoLandingDescendRate", controlSettings.getAutoLandingDescendRate());
            jsonObject.put("MaxAutoLandingTime", controlSettings.getMaxAutoLandingTime());
            jsonObject.put("MaxRollPitchControlValue", controlSettings.getMaxRollPitchControlValue());
            jsonObject.put("MaxYawControlValue", controlSettings.getMaxYawControlValue());
            jsonObject.put("PidRollRateX", controlSettings.getPidRollRate()[0]);
            jsonObject.put("PidRollRateY", controlSettings.getPidRollRate()[1]);
            jsonObject.put("PidRollRateZ", controlSettings.getPidRollRate()[2]);
            jsonObject.put("PidPitchRateX", controlSettings.getPidPitchRate()[0]);
            jsonObject.put("PidPitchRateY", controlSettings.getPidPitchRate()[1]);
            jsonObject.put("PidPitchRateZ", controlSettings.getPidPitchRate()[2]);
            jsonObject.put("PidYawRateX", controlSettings.getPidYawRate()[0]);
            jsonObject.put("PidYawRateY", controlSettings.getPidYawRate()[1]);
            jsonObject.put("PidYawRateZ", controlSettings.getPidYawRate()[2]);
            jsonObject.put("RollProp", controlSettings.getRollProp());
            jsonObject.put("PitchProp", controlSettings.getPitchProp());
            jsonObject.put("YawProp", controlSettings.getYawProp());
            jsonObject.put("AltPositionProp", controlSettings.getAltPositionProp());
            jsonObject.put("AltVelocityProp", controlSettings.getAltVelocityProp());
            jsonObject.put("PidThrottleAccelX", controlSettings.getPidThrottleAccel()[0]);
            jsonObject.put("PidThrottleAccelY", controlSettings.getPidThrottleAccel()[1]);
            jsonObject.put("PidThrottleAccelZ", controlSettings.getPidThrottleAccel()[2]);
            jsonObject.put("ThrottleAltRateProp", controlSettings.getThrottleAltRateProp());
            jsonObject.put("MaxAutoAngle", controlSettings.getMaxAutoAngle());
            jsonObject.put("MaxAutoVelocity", controlSettings.getMaxAutoVelocity());
            jsonObject.put("AutoPositionProp", controlSettings.getAutoPositionProp());
            jsonObject.put("AutoVelocityProp", controlSettings.getAutoVelocityProp());
            jsonObject.put("PidAutoAccelX", controlSettings.getPidAutoAccel()[0]);
            jsonObject.put("PidAutoAccelY", controlSettings.getPidAutoAccel()[1]);
            jsonObject.put("PidAutoAccelZ", controlSettings.getPidAutoAccel()[2]);
            jsonObject.put("StickPositionRateProp", controlSettings.getStickPositionRateProp());
            jsonObject.put("StickMovementMode", controlSettings.getStickMovementMode());
            jsonObject.put("BatteryType", controlSettings.getBatteryType());
            jsonObject.put("ErrorHandlingAction", controlSettings.getErrorHandlingAction());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(this.getClass().toString(), "Error while creating JSON:" + e.getMessage());
            throw e;
        }
    }
}