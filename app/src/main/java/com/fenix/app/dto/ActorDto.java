package com.fenix.app.dto;

import com.google.android.gms.maps.model.LatLng;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActorDto {

    private String name;
    private LatLng location;

    @Override
    public String toString() {
        return name;
    }
}
