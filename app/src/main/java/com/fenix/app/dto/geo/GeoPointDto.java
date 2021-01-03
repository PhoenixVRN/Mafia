package com.fenix.app.dto.geo;

import com.google.android.gms.maps.model.LatLng;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class GeoPointDto {

    private String type = "Point";

    private double[] coordinates;

    public GeoPointDto(LatLng location) {
        this.coordinates = new double[]{location.latitude, location.longitude};
    }
}
