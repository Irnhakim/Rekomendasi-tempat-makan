package com.irnhakim.myapplication.adapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbyPlacesResponse {
    @SerializedName("results")
    private List<Place> results;

    public List<Place> getResults() {
        return results;
    }
}
