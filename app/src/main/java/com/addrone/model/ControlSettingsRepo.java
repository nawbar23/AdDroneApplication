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
            jsonObject.put("AutoLandingDescendRate", String.valueOf(boardControlSettings.getAutoLandingDescendRate()));
            jsonObject.put("MaxAutoLandingTime", String.valueOf(boardControlSettings.getMaxAutoLandingTime()));
            jsonObject.put("MaxRollPitchControlValue", String.valueOf(boardControlSettings.getMaxRollPitchControlValue()));
            jsonObject.put("MaxYawControlValue", String.valueOf(boardControlSettings.getMaxYawControlValue()));
            jsonObject.put("PidRollRateX", String.valueOf(boardControlSettings.getPidRollRate()[0]));
            jsonObject.put("PidRollRateY", String.valueOf(boardControlSettings.getPidRollRate()[1]));
            jsonObject.put("PidRollRateZ", String.valueOf(boardControlSettings.getPidRollRate()[2]));
            jsonObject.put("PidPitchRateX", String.valueOf(boardControlSettings.getPidPitchRate()[0]));
            jsonObject.put("PidPitchRateY", String.valueOf(boardControlSettings.getPidPitchRate()[1]));
            jsonObject.put("PidPitchRateZ", String.valueOf(boardControlSettings.getPidPitchRate()[2]));
            jsonObject.put("PidYawRateX", String.valueOf(boardControlSettings.getPidYawRate()[0]));
            jsonObject.put("PidYawRateY", String.valueOf(boardControlSettings.getPidYawRate()[1]));
            jsonObject.put("PidYawRateZ", String.valueOf(boardControlSettings.getPidYawRate()[2]));
            jsonObject.put("RollProp", String.valueOf(boardControlSettings.getRollProp()));
            jsonObject.put("PitchProp", String.valueOf(boardControlSettings.getPitchProp()));
            jsonObject.put("YawProp", String.valueOf(boardControlSettings.getYawProp()));
            jsonObject.put("AltPositionProp", String.valueOf(boardControlSettings.getAltPositionProp()));
            jsonObject.put("AltVelocityProp", String.valueOf(boardControlSettings.getAltVelocityProp()));
            jsonObject.put("PidThrottleAccelX", String.valueOf(boardControlSettings.getPidThrottleAccel()[0]));
            jsonObject.put("PidThrottleAccelY", String.valueOf(boardControlSettings.getPidThrottleAccel()[1]));
            jsonObject.put("PidThrottleAccelZ", String.valueOf(boardControlSettings.getPidThrottleAccel()[2]));
            jsonObject.put("ThrottleAltRateProp", String.valueOf(boardControlSettings.getThrottleAltRateProp()));
            jsonObject.put("MaxAutoAngle", String.valueOf(boardControlSettings.getMaxAutoAngle()));
            jsonObject.put("MaxAutoVelocity", String.valueOf(boardControlSettings.getMaxAutoVelocity()));
            jsonObject.put("AutoPositionProp", String.valueOf(boardControlSettings.getAutoPositionProp()));
            jsonObject.put("AutoVelocityProp", String.valueOf(boardControlSettings.getAutoVelocityProp()));
            jsonObject.put("PidAutoAccelX", String.valueOf(boardControlSettings.getPidAutoAccel()[0]));
            jsonObject.put("PidAutoAccelY", String.valueOf(boardControlSettings.getPidAutoAccel()[1]));
            jsonObject.put("PidAutoAccelZ", String.valueOf(boardControlSettings.getPidAutoAccel()[2]));
            jsonObject.put("StickPositionRateProp", String.valueOf(boardControlSettings.getStickPositionRateProp()));
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