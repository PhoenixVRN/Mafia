package com.fenix.app.dto;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class AlienDto {
    public String name;
    public LatLng location;

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
