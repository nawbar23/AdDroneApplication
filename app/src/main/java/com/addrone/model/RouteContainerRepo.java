package com.addrone.model;

import android.util.Log;

import com.multicopter.java.data.RouteContainer;

import org.json.JSONException;
import org.json.JSONObject;

public class RouteContainerRepo {

    JSONObject jsonObject = new JSONObject();

    //temporary
    private RouteContainer routeContainer = new RouteContainer();
    private RouteContainer.Waypoint waypoint;

    public JSONObject toJSON() {
        try {
            jsonObject.put("WaypointTime", routeContainer.getWaypointTime());
            jsonObject.put("BaseTime", routeContainer.getBaseTime());
            jsonObject.put("Route", routeContainer.getRoute());
            jsonObject.put("RouteSize", routeContainer.getRouteSize());
            jsonObject.put("CrcValue", routeContainer.getCrcValue());
            jsonObject.put("Latitude", waypoint.getLatitude());
            jsonObject.put("Longitude", waypoint.getLongitude());
            jsonObject.put("AbsoluteAltitude", waypoint.getAbsoluteAltitude());
            jsonObject.put("RelativeAltitude", waypoint.getRelativeAltitude());
            jsonObject.put("Velocity", waypoint.getVelocity());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(RouteContainerRepo.class.toString(), "Error while creating JSON!");
            e.printStackTrace();
            return null;
        }
    }

}
