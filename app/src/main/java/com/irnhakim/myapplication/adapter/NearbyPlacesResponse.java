package com.irnhakim.myapplication.adapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * NIM      : 10121043
 * Nama     : Ihsan Ramadhan Nul Hakim
 * Kelas    : IF-4
 */

public class NearbyPlacesResponse {
    @SerializedName("results")
    private List<Place> results;

    public List<Place> getResults() {
        return results;
    }
}
