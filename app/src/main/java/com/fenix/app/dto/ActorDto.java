package com.fenix.app.dto;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;


public class ActorDto {

    private String name;
    private LatLng location;

    public ActorDto(String name, LatLng location) {
        this.setName(name);
        this.setLocation(location);
    }

    @Override
    public String toString() {
        return name + " " + location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
