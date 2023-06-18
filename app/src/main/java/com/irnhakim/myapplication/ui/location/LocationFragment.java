package com.irnhakim.myapplication.ui.location;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.irnhakim.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private final List<NearbyPlace> nearbyPlaces = new ArrayList<>();
    private static final int MAX_MARKERS = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        performApiCall("restaurant");
        performApiCall("cafe");
    }

    private void performApiCall(String placeType) {
        String apiKey = "YOUR_API_KEY";
        int radius = 1000; // Radius in meters
        LatLng currentLatLng = new LatLng(-34, 151); // Update with your current location

        String location = currentLatLng.latitude + "," + currentLatLng.longitude;

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesApiService apiService = retrofit.create(PlacesApiService.class);
        Call<ApiResponse> call = apiService.getNearbyPlaces(location, radius, placeType, apiKey);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getResults() != null) {
                        List<Result> results = apiResponse.getResults();
                        for (Result result : results) {
                            if (nearbyPlaces.size() >= MAX_MARKERS) {
                                break;
                            }

                            Geometry geometry = result.getGeometry();
                            Location location = geometry.getLocation();
                            String placeName = result.getName();

                            double latitude = location.getLat();
                            double longitude = location.getLng();

                            NearbyPlace nearbyPlace = new NearbyPlace(latitude, longitude, placeName);
                            nearbyPlaces.add(nearbyPlace);
                        }
                    }
                }

                addMarkersForNearbyPlaces();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle API call failure
            }
        });
    }

    private void addMarkersForNearbyPlaces() {
        if (googleMap == null) {
            return;
        }

        googleMap.clear();

        for (NearbyPlace nearbyPlace : nearbyPlaces) {
            LatLng placeLatLng = new LatLng(nearbyPlace.getLatitude(), nearbyPlace.getLongitude());
            String placeName = nearbyPlace.getName();

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(placeLatLng)
                    .title(placeName);

            googleMap.addMarker(markerOptions);
        }
    }
}
