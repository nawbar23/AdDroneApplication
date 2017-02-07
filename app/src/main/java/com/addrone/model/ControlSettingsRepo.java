package com.addrone.model;

import android.util.Log;

import com.multicopter.java.data.ControlSettings;

import org.json.JSONException;
import org.json.JSONObject;

public class ControlSettingsRepo {

    JSONObject jsonObject = new JSONObject();

    //temporary mock ControlSettings
    public ControlSettings boardControlSettings;

    public void setControlSettings(ControlSettings controlSettings) {
        this.boardControlSettings = controlSettings;
    }

    public JSONObject toJSON() throws JSONException {
        try {
            jsonObject.put("UavType", boardControlSettings.getUavType());
            jsonObject.put("InitialSolverMode", boardControlSettings.getInitialSolverMode());
            jsonObject.put("ManualThrottleMode", boardControlSettings.getManualThrottleMode());
            jsonObject.put("AutoLandingDescendRate", boardControlSettings.getAutoLandingDescendRate());
            jsonObject.put("MaxAutoLandingTime", boardControlSettings.getMaxAutoLandingTime());
            jsonObject.put("MaxRollPitchControlValue", boardControlSettings.getMaxRollPitchControlValue());
            jsonObject.put("MaxYawControlValue", boardControlSettings.getMaxYawControlValue());
            jsonObject.put("PidRollRateX", boardControlSettings.getPidRollRate()[0]);
            jsonObject.put("PidRollRateY", boardControlSettings.getPidRollRate()[1]);
            jsonObject.put("PidRollRateZ", boardControlSettings.getPidRollRate()[2]);
            jsonObject.put("PidPitchRateX", boardControlSettings.getPidPitchRate()[0]);
            jsonObject.put("PidPitchRateY", boardControlSettings.getPidPitchRate()[1]);
            jsonObject.put("PidPitchRateZ", boardControlSettings.getPidPitchRate()[2]);
            jsonObject.put("PidYawRateX", boardControlSettings.getPidYawRate()[0]);
            jsonObject.put("PidYawRateY", boardControlSettings.getPidYawRate()[1]);
            jsonObject.put("PidYawRateZ", boardControlSettings.getPidYawRate()[2]);
            jsonObject.put("RollProp", boardControlSettings.getRollProp());
            jsonObject.put("PitchProp", boardControlSettings.getPitchProp());
            jsonObject.put("YawProp", boardControlSettings.getYawProp());
            jsonObject.put("AltPositionProp", boardControlSettings.getAltPositionProp());
            jsonObject.put("AltVelocityProp", boardControlSettings.getAltVelocityProp());
            jsonObject.put("PidThrottleAccelX", boardControlSettings.getPidThrottleAccel()[0]);
            jsonObject.put("PidThrottleAccelY", boardControlSettings.getPidThrottleAccel()[1]);
            jsonObject.put("PidThrottleAccelZ", boardControlSettings.getPidThrottleAccel()[2]);
            jsonObject.put("ThrottleAltRateProp", boardControlSettings.getThrottleAltRateProp());
            jsonObject.put("MaxAutoAngle", boardControlSettings.getMaxAutoAngle());
            jsonObject.put("MaxAutoVelocity", boardControlSettings.getMaxAutoVelocity());
            jsonObject.put("AutoPositionProp", boardControlSettings.getAutoPositionProp());
            jsonObject.put("AutoVelocityProp", boardControlSettings.getAutoVelocityProp());
            jsonObject.put("PidAutoAccelX", boardControlSettings.getPidAutoAccel()[0]);
            jsonObject.put("PidAutoAccelY", boardControlSettings.getPidAutoAccel()[1]);
            jsonObject.put("PidAutoAccelZ", boardControlSettings.getPidAutoAccel()[2]);
            jsonObject.put("StickPositionRateProp", boardControlSettings.getStickPositionRateProp());
            jsonObject.put("StickMovementMode", boardControlSettings.getStickMovementMode());
            jsonObject.put("BatteryType", boardControlSettings.getBatteryType());
            jsonObject.put("ErrorHandlingAction", boardControlSettings.getErrorHandlingAction());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(this.getClass().toString(), "Error while creating JSON:" + e.getMessage());
            throw e;
        }
    }
}