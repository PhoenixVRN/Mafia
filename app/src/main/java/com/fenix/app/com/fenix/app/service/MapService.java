package com.fenix.app.com.fenix.app.service;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapService {

    /**
     * Add a marker and move the camera
     */
    public Marker MoveToMyLocation(GoogleMap map, String title) {
        LatLng location = new LatLng(-34, 151);

        MarkerOptions options = new MarkerOptions().position(location);
        if (!Strings.isEmptyOrWhitespace(title))
            options.title("title");

        Marker marker = map.addMarker(options);

        map.moveCamera(CameraUpdateFactory.newLatLng(location));

        return marker;
    }

}
