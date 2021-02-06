package com.fenix.app.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fenix.app.R;
import com.fenix.app.dto.ActorDto;
import com.fenix.app.dto.ItemBox;
import com.fenix.app.dto.MapItemBase;
import com.fenix.app.util.PermissionUtil;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapService implements OnMapReadyCallback {

    public static final float LOCAL_ZOOM = 17;

    public GoogleMap map;

    private EventListener activity;

    public MapService(EventListener activity) {
        this.activity = activity;
    }

    /**
     * Add a marker and move the camera if need
     */
    public Marker MarkerToLocation(String title, LatLng latLng, boolean moveCamera) {
        return MarkerToLocation(title, latLng, moveCamera, null);
    }

    /**
     * Add a custom marker and move the camera if need
     */
    public Marker MarkerToLocation(String title, LatLng latLng, boolean moveCamera, BitmapDescriptor icon) {
        if (latLng == null)
            return null;

        if (icon == null)

//            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            icon = BitmapDescriptorFactory.fromResource(R.drawable.sword_marker);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(icon);

        if (!Strings.isEmptyOrWhitespace(title))
            options.title(title);

        Marker marker = map.addMarker(options);

        if (moveCamera) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, LOCAL_ZOOM);
            map.moveCamera(cu);
        }

        return marker;
    }


    /**
     * Change a marker color
     */
    public void ChangeMarkerColor(Marker marker, MapItemBase item, int color) {
        marker.setIcon(getIcon(item, color));
    }

    /**
     * Get colored Icon for Item
     */
    public BitmapDescriptor getIcon(final MapItemBase item, int color) {
        BitmapDescriptor icon = null;

        if (item instanceof ActorDto) {
            if (color == Color.RED)
                icon = BitmapDescriptorFactory.fromResource(R.drawable.sword_marker_red);
            else
                icon = BitmapDescriptorFactory.fromResource(R.drawable.sword_marker);
        } else if (item instanceof ItemBox) {
            if (color == Color.RED)
                icon = BitmapDescriptorFactory.fromResource(R.drawable.box_marker_red);
            else
                icon = BitmapDescriptorFactory.fromResource(R.drawable.box_marker);
        }

        return icon;
    }

    /**
     * Try to find my location
     */
    public LatLng FindMyLocation() {

        if (map == null)
            return null; // Map still not ready

        Location location = map.getMyLocation();
        if (location == null)
            return null; // Map not ready yet

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        return latLng;
    }

    /**
     * Map around me and zoom if need
     */
    public LatLng MoveCameraToMe(float zoom) {

        LatLng latLng = FindMyLocation();
        if (latLng == null)
            return null; // Map not ready yet

        float currentZoom = map.getCameraPosition().zoom;
        if (currentZoom > zoom) {
            zoom = currentZoom;
        }

        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        map.animateCamera(cu);

        return latLng;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    public void enableMyLocation() {
        //#region maps_check_location_permission
        if (ContextCompat.checkSelfPermission((Context) this.activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtil.requestPermission((AppCompatActivity) this.activity, PermissionUtil.LOCATION_PERMISSION_REQUEST_CODE,
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

    public interface EventListener extends
            GoogleMap.OnMyLocationButtonClickListener,
            GoogleMap.OnMyLocationClickListener,
            GoogleMap.OnMyLocationChangeListener {
    }
}
