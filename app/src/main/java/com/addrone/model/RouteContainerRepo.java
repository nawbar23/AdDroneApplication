package com.addrone.model;

import android.os.Environment;

import com.multicopter.java.data.RouteContainer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by eagaspy on 2017-01-26.
 */

public class RouteContainerRepo {
    RouteContainer routeContainer;
    String jsonArrayString;
    RouteContainer.Waypoint waypoint;
    String sdCard = Environment.getExternalStorageDirectory().getPath() + "/";
    String name = "plik.json";
    String filename = sdCard + name;

    public String toJSON(){

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try{
//            jsonObject.put("WaypointTime",routeContainer.getWaypointTime());
//            jsonObject.put("BaseTime",routeContainer.getBaseTime());
//            jsonObject.put("Route",routeContainer.getRoute());
//            jsonObject.put("RouteSize",routeContainer.getRouteSize());
//            jsonObject.put("CrcValue",routeContainer.getCrcValue());
//            jsonObject.put("Latitude",waypoint.getLatitude());
//            jsonObject.put("Longitude",waypoint.getLongitude());
//            jsonObject.put("AbsoluteAltitude",waypoint.getAbsoluteAltitude());
//            jsonObject.put("RelativeAltitude",waypoint.getRelativeAltitude());
//            jsonObject.put("Velocity",waypoint.getVelocity());

            jsonObject.put("123","456");

            jsonArray.put(jsonObject);
            System.out.println(jsonObject);
            jsonArrayString = jsonArray.toString();
            System.out.println("Metoda: " + jsonArrayString);
            return jsonArrayString;
        }
        catch (JSONException e) {
            System.out.println("Error while putting JSON to array!");
            e.printStackTrace();
            return "";
        }
    }

    public void saveRouteContainer(){
        toJSON();

        File fileB = new File(filename);
        FileWriter file = null;
        try {
            fileB.createNewFile();
            file = new FileWriter(fileB);
            file.write(jsonArrayString);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRouteContainer() {
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),filename); //path to your file assuming its in root of SD card
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String content = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            try {
                StringBuilder stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();

                while (line != null) {
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
                content = stringBuilder.toString();
            } finally {
                bufferedReader.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
        try {
            JSONArray jsonArray = new JSONArray(content);
            int length = jsonArray.length();

            for (int i = 0; i < length; i++) { //itterate throu json array
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                jsonObject.toString();
                System.out.println("Metoda load control settings: obiekty json: " + jsonObject);
                //here you can access individual parts of you json object by getString()...getBoolean().... etc...
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
