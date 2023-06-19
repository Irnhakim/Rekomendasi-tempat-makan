package com.irnhakim.myapplication.adapter;

import com.google.gson.annotations.SerializedName;

/**
 * NIM      : 10121043
 * Nama     : Ihsan Ramadhan Nul Hakim
 * Kelas    : IF-4
 */

public class Place {
    @SerializedName("name")
    private String name;

    @SerializedName("geometry")
    private Geometry geometry;

    public String getName() {
        return name;
    }

    public Geometry getGeometry() {
        return geometry;
    }
}
