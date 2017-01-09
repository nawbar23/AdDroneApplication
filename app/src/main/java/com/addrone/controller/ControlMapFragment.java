package com.addrone.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.addrone.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;

/**
 * Created by Kamil on 8/23/2016.
 */
public class ControlMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    MapView mapView;
    private GoogleMap googleMap;
    private Marker marker;
    private Marker autopilotDestinationMarker;
    private final LatLng WADOWICKA_6 = new LatLng(50.034, 19.940);
    private final int ZOOM_VAL = 15;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());
        mapView.getMapAsync(this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        CameraPosition cameraPosition = new CameraPosition.Builder().target(WADOWICKA_6).zoom(ZOOM_VAL).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.setOnMapLongClickListener(this);

        autopilotDestinationMarker = googleMap.addMarker(new MarkerOptions()
                        .position(WADOWICKA_6)
                        .title(getString(R.string.autopilot_destination))
                        .icon(BitmapDescriptorFactory.defaultMarker(HUE_AZURE))
                        .visible(false)
        );
    }

    public void onMapLongClick(LatLng point) {
        Toast.makeText(getActivity(), "Autopilot destination has been set to: " + "\npoint lat: " + point.latitude + "\nlong: " + point.longitude,
                Toast.LENGTH_SHORT).show();
        ((ControlActivity) getActivity()).setAutopilotData(point);

        autopilotDestinationMarker.setPosition(point);
        if (!autopilotDestinationMarker.isVisible())
            autopilotDestinationMarker.setVisible(true);
    }

    public void updatePosition(LatLng latLng) {
        if (marker == null) {
            marker = googleMap.addMarker(new MarkerOptions().position(latLng));
        } else {
            marker.setPosition(latLng);
        }
    }
}
