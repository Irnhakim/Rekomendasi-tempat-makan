package com.irnhakim.myapplication.adapter;

import com.google.gson.annotations.SerializedName;

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
