package com.fenix.app.com.fenix.app.service;

import android.location.Location;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapService {


    /**
     * Add a marker and move the camera
     */
    public Marker MarkerToMyLocation(GoogleMap map, String title) {
        Location location = map.getMyLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        if (!Strings.isEmptyOrWhitespace(title))
            options.title("title");

        Marker marker = map.addMarker(options);

        //map.moveCamera(CameraUpdateFactory.newLatLng(location));

        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.addMarker(options);
        map.moveCamera(cu);

        return marker;
    }

    /**
     * Map around me and zoom if need
     */
    public void MoveCameraToMe(GoogleMap map, float zoom) {

        Location location = map.getMyLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        float currentZoom = map.getCameraPosition().zoom;
        if(currentZoom > zoom) {
            zoom = currentZoom;
        }

        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        map.animateCamera(cu);
    }
}
