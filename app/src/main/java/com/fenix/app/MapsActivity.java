package com.fenix.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fenix.app.com.fenix.app.service.MapService;
import com.fenix.app.util.PermissionUtils;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationChangeListener {

    private MapService mapService = new MapService();
    private GoogleMap map;
    private Marker alienMarker;
    private Marker myMarker;
    private LatLng myLocation;

    //#region alienButton
    private Button alienButton;
    private View.OnClickListener alienButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            MapsActivity.this.mapTextView.append("Alient Button click!\n");

            if (MapsActivity.this.alienMarker != null) {
                MapsActivity.this.alienMarker.remove();
            }

            // Add a marker and move the camera
            LatLng myLocation = new LatLng(0, 0);
            MarkerOptions myMarkerOptions = new MarkerOptions().position(myLocation).title("My Marker 2");
            MapsActivity.this.alienMarker = MapsActivity.this.map.addMarker(myMarkerOptions);
            MapsActivity.this.map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        }
    };
    //#endregion

    //#region myButton
    private Button myButton;
    private View.OnClickListener myButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            //TODO https://developers.google.com/maps/documentation/android-sdk/marker?hl=ru

            if (MapsActivity.this.myMarker != null) {
                MapsActivity.this.myMarker.remove();
            }
            MapsActivity.this.myMarker = MapsActivity.this.mapService.MarkerToMyLocation(MapsActivity.this.map, "My Marker 1!!!");
        }
    };
    //#endregion

    private TextView mapTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Button
        alienButton = (Button) findViewById(R.id.alienButton);
        alienButton.setOnClickListener(alienButtonListener);

        // TextView
        mapTextView = findViewById(R.id.logTextView);
        mapTextView.setMovementMethod(new ScrollingMovementMethod());

        // myButton
        myButton = (Button) findViewById(R.id.myButton);
        myButton.setOnClickListener(myButtonListener);
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
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnMyLocationChangeListener(this);
        this.enableMyLocation();
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        //#region maps_check_location_permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        //#endregion
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        this.mapTextView.append("MyLocation button clicked!\n");
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        Toast.makeText(this, "Current location:\n" + myLocation, Toast.LENGTH_LONG).show();
        this.mapTextView.append("Current location: " + myLocation + "\n");
    }

    @Override
    public void onMyLocationChange(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (myLocation == null || !myLocation.toString().equalsIgnoreCase(latLng.toString())) {
            myLocation = latLng;

            Toast.makeText(this, "Changed location: " + myLocation, Toast.LENGTH_SHORT).show();
            this.mapTextView.append("Changed location: " + myLocation + "!\n");
        }
    }
}