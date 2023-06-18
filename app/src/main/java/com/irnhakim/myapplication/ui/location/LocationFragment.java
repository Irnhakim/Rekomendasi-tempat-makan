package com.irnhakim.myapplication.ui.location;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.irnhakim.myapplication.R;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;


import java.util.Random;

public class LocationFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            getCurrentLocation(googleMap);
        }
    };

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    // Nama tempat dan koordinatnya
    private static final String[] PLACE_NAMES = {
            "Nasi soto ayam mamah Nayra",
            "Republic Kebab Premium",
            "RM Lamun Ombak",
            "Cumi Bakar Rezeki",
            "Seafood BomBom"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
            mapFragment.getMapAsync(callback);
        }
    }

    private void getCurrentLocation(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        googleMap.setMyLocationEnabled(true);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setInterval(5000); // Update location every 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));

                    // Add 5 random markers around the current location
                    for (int i = 1; i <= 5; i++) {
                        double radius = Math.random() * 980 + 20; // Random radius between 20m and 1km
                        double angle = Math.random() * Math.PI * 2; // Random angle between 0 and 2pi
                        double offsetX = radius * Math.cos(angle);
                        double offsetY = radius * Math.sin(angle);

                        LatLng markerLatLng = new LatLng(currentLatLng.latitude + offsetX / 111320, currentLatLng.longitude + offsetY / (111320 * Math.cos(currentLatLng.latitude)));

                        String markerTitle = "";
                        switch (i) {
                            case 1:
                                markerTitle = "Nasi Soto Ayam Mamah Nayra";
                                break;
                            case 2:
                                markerTitle = "Republic Kebab Premium";
                                break;
                            case 3:
                                markerTitle = "RM Lamun Ombak";
                                break;
                            case 4:
                                markerTitle = "Cumi Bakar Rezeki";
                                break;
                            case 5:
                                markerTitle = "Seafood BomBom";
                                break;
                        }

                        googleMap.addMarker(new MarkerOptions().position(markerLatLng).title(markerTitle));
                    }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}