package com.irnhakim.myapplication.adapter;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * NIM      : 10121043
 * Nama     : Ihsan Ramadhan Nul Hakim
 * Kelas    : IF-4
 */

public interface PlacesApiService {
    @GET
    Call<NearbyPlacesResponse> getNearbyPlaces(@Url String url);
}
