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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.irnhakim.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            getCurrentLocation(googleMap);
        }
    };

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private PlacesClient placesClient;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

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
            Places.initialize(requireContext(), getString(R.string.google_maps_key));
            placesClient = Places.createClient(requireContext());
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
        locationRequest.setInterval(5000); // Update location every 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                    findNearbyPlaces(location, googleMap);
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void findNearbyPlaces(Location location, GoogleMap googleMap) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

        placesClient.findCurrentPlace(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FindCurrentPlaceResponse response = task.getResult();

                if (response != null) {
                    List<PlaceLikelihood> placeLikelihoods = response.getPlaceLikelihoods();
                    List<Marker> markers = new ArrayList<>();

                    for (int i = 0; i < Math.min(placeLikelihoods.size(), 5); i++) {
                        PlaceLikelihood placeLikelihood = placeLikelihoods.get(i);
                        Place place = placeLikelihood.getPlace();
                        LatLng placeLatLng = place.getLatLng();

                        if (placeLatLng != null) {
                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(placeLatLng)
                                    .title(place.getName()));
                            markers.add(marker);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
