package com.irnhakim.myapplication.ui.location;



import com.irnhakim.myapplication.adapter.PlacesApiService;
import com.irnhakim.myapplication.adapter.NearbyPlacesResponse;
import com.irnhakim.myapplication.adapter.Place;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.irnhakim.myapplication.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        //locationRequest.setInterval(60000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                Location location = locationResult.getLastLocation();
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                String nama = "Current Location";
                googleMap.addMarker(new MarkerOptions().position(currentLatLng).title(nama));
                // Mencari restoran terdekat
                findNearbyRestaurants(currentLatLng, googleMap);
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void findNearbyRestaurants(LatLng currentLatLng, GoogleMap googleMap) {
        String apikey = "AIzaSyB9MxxJBmzLHdfsMEZSdV0vORR_MRwirPI";
        String placesUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+currentLatLng.latitude+","+currentLatLng.longitude+"&radius=1000&type=restaurant&key="+apikey;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesApiService placesApiService = retrofit.create(PlacesApiService.class);
        Call<NearbyPlacesResponse> call = placesApiService.getNearbyPlaces(placesUrl);
        call.enqueue(new Callback<NearbyPlacesResponse>() {
            @Override
            public void onResponse(Call<NearbyPlacesResponse> call, Response<NearbyPlacesResponse> response) {
                if (response.isSuccessful()) {
                    NearbyPlacesResponse nearbyPlacesResponse = response.body();
                    if (nearbyPlacesResponse != null && nearbyPlacesResponse.getResults() != null) {
                        List<Place> places = nearbyPlacesResponse.getResults();
                        addMarkersForPlaces(places, googleMap);
                    } else {
                        // No valid results
                        Log.e("API Response", "No valid results found.");
                    }
                } else {
                    // Handle error response
                    Log.e("API Response", "Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<NearbyPlacesResponse> call, Throwable t) {
                // Handle failure
                Log.e("API Request", "Request failed: " + t.getMessage());
            }
        });
    }

    private void addMarkersForPlaces(List<Place> places, GoogleMap googleMap) {
        int maxMarkers = 100; // Jumlah maksimum marker yang ingin ditambahkan
        int addedMarkers = 0; // Jumlah marker yang sudah ditambahkan

        for (Place place : places) {
            if (addedMarkers >= maxMarkers) {
                break;
            }

            double lat = place.getGeometry().getLocation().getLat();
            double lng = place.getGeometry().getLocation().getLng();
            LatLng placeLatLng = new LatLng(lat, lng);
            String placeName = place.getName();

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(placeLatLng)
                    .title(placeName);

            googleMap.addMarker(new MarkerOptions()
                    .position(placeLatLng)
                    .title(placeName));
            addedMarkers++;
        }
    }
}
