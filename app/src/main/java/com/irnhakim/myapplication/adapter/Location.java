package com.irnhakim.myapplication.adapter;

import com.google.gson.annotations.SerializedName;

/**
 * NIM      : 10121043
 * Nama     : Ihsan Ramadhan Nul Hakim
 * Kelas    : IF-4
 */

public class Location {
    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
