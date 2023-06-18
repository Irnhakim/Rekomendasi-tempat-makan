package com.irnhakim.myapplication.adapter;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface PlacesApiService {
    @GET
    Call<NearbyPlacesResponse> getNearbyPlaces(@Url String url);
}
