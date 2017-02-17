package com.addrone.model;

import com.multicopter.java.data.ControlSettings;

import org.json.JSONException;
import org.json.JSONObject;

public class ControlSettingsRepo {

    private ControlSettings controlSettings;
    private JSONObject jsonObject = new JSONObject();

    public void setControlSettings(ControlSettings controlSettings) {
        this.controlSettings = controlSettings;
    }

    public JSONObject controlSettingsToJSON() throws JSONException {
        jsonObject.put("UavType", controlSettings.getUavType());
        jsonObject.put("InitialSolverMode", controlSettings.getInitialSolverMode());
        jsonObject.put("ManualThrottleMode", controlSettings.getManualThrottleMode());
        jsonObject.put("AutoLandingDescendRate", String.valueOf(controlSettings.getAutoLandingDescendRate()));
        jsonObject.put("MaxAutoLandingTime", String.valueOf(controlSettings.getMaxAutoLandingTime()));
        jsonObject.put("MaxRollPitchControlValue", String.valueOf(controlSettings.getMaxRollPitchControlValue()));
        jsonObject.put("MaxYawControlValue", String.valueOf(controlSettings.getMaxYawControlValue()));
        jsonObject.put("PidRollRateX", String.valueOf(controlSettings.getPidRollRate()[0]));
        jsonObject.put("PidRollRateY", String.valueOf(controlSettings.getPidRollRate()[1]));
        jsonObject.put("PidRollRateZ", String.valueOf(controlSettings.getPidRollRate()[2]));
        jsonObject.put("PidPitchRateX", String.valueOf(controlSettings.getPidPitchRate()[0]));
        jsonObject.put("PidPitchRateY", String.valueOf(controlSettings.getPidPitchRate()[1]));
        jsonObject.put("PidPitchRateZ", String.valueOf(controlSettings.getPidPitchRate()[2]));
        jsonObject.put("PidYawRateX", String.valueOf(controlSettings.getPidYawRate()[0]));
        jsonObject.put("PidYawRateY", String.valueOf(controlSettings.getPidYawRate()[1]));
        jsonObject.put("PidYawRateZ", String.valueOf(controlSettings.getPidYawRate()[2]));
        jsonObject.put("RollProp", String.valueOf(controlSettings.getRollProp()));
        jsonObject.put("PitchProp", String.valueOf(controlSettings.getPitchProp()));
        jsonObject.put("YawProp", String.valueOf(controlSettings.getYawProp()));
        jsonObject.put("AltPositionProp", String.valueOf(controlSettings.getAltPositionProp()));
        jsonObject.put("AltVelocityProp", String.valueOf(controlSettings.getAltVelocityProp()));
        jsonObject.put("PidThrottleAccelX", String.valueOf(controlSettings.getPidThrottleAccel()[0]));
        jsonObject.put("PidThrottleAccelY", String.valueOf(controlSettings.getPidThrottleAccel()[1]));
        jsonObject.put("PidThrottleAccelZ", String.valueOf(controlSettings.getPidThrottleAccel()[2]));
        jsonObject.put("ThrottleAltRateProp", String.valueOf(controlSettings.getThrottleAltRateProp()));
        jsonObject.put("MaxAutoAngle", String.valueOf(controlSettings.getMaxAutoAngle()));
        jsonObject.put("MaxAutoVelocity", String.valueOf(controlSettings.getMaxAutoVelocity()));
        jsonObject.put("AutoPositionProp", String.valueOf(controlSettings.getAutoPositionProp()));
        jsonObject.put("AutoVelocityProp", String.valueOf(controlSettings.getAutoVelocityProp()));
        jsonObject.put("PidAutoAccelX", String.valueOf(controlSettings.getPidAutoAccel()[0]));
        jsonObject.put("PidAutoAccelY", String.valueOf(controlSettings.getPidAutoAccel()[1]));
        jsonObject.put("PidAutoAccelZ", String.valueOf(controlSettings.getPidAutoAccel()[2]));
        jsonObject.put("StickPositionRateProp", String.valueOf(controlSettings.getStickPositionRateProp()));
        jsonObject.put("StickMovementMode", controlSettings.getStickMovementMode());
        jsonObject.put("BatteryType", controlSettings.getBatteryType());
        jsonObject.put("ErrorHandlingAction", controlSettings.getErrorHandlingAction());

        return jsonObject;
    }
}