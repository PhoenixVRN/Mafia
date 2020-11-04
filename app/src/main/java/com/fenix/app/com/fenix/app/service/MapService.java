package com.fenix.app.com.fenix.app.service;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fenix.app.util.PermissionUtil;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapService implements OnMapReadyCallback {

    public GoogleMap map;

    private AppCompatActivity activity;

    public MapService(AppCompatActivity activity){
        this.activity = activity;
    }

    /**
     * Add a marker and move the camera
     */
    public Marker MarkerToMyLocation(String title) {
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
    public void MoveCameraToMe(float zoom) {

        Location location = map.getMyLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        float currentZoom = map.getCameraPosition().zoom;
        if(currentZoom > zoom) {
            zoom = currentZoom;
        }

        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        map.animateCamera(cu);
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    public void enableMyLocation() {
        //#region maps_check_location_permission
        if (ContextCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtil.requestPermission(this.activity, PermissionUtil.LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        //#endregion
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        enableMyLocation();

        map.setOnMyLocationButtonClickListener((GoogleMap.OnMyLocationButtonClickListener) this.activity);
        map.setOnMyLocationClickListener((GoogleMap.OnMyLocationClickListener) this.activity);
        map.setOnMyLocationChangeListener((GoogleMap.OnMyLocationChangeListener) this.activity);
        map.getUiSettings().setZoomControlsEnabled(true);

    }
}
