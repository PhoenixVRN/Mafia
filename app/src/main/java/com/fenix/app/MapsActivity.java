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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fenix.app.com.fenix.app.service.MapService;
import com.fenix.app.util.LocationUtils;
import com.fenix.app.util.PermissionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationChangeListener {

    //#region Constants
    private static final float MY_ZOOM = 17;
    private static final float MY_FOLLOW_DISTANCE = 0.25f;
    //#endregion

    //#region Variables
    private MapService mapService = new MapService();
    private GoogleMap map;
    private Marker alienMarker;
    private LatLng myLocation;
    private boolean myLocationFollow = false;
    //#endregion

    //#region Controls

    //#region alienButton
    private Button alienButton;
    private View.OnClickListener alienButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            MapsActivity.this.mapEventsLog.append("Alient Button click!\n");

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

    //#region My

    /**
     * Map around me
     */
    private Button myButton;
    private View.OnClickListener myButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            mapService.MoveCameraToMe(MapsActivity.this.map, MY_ZOOM);
        }
    };


    /**
     * Follow my location
     */
    private Switch mySwitch;
    private CompoundButton.OnCheckedChangeListener mySwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            myLocationFollow = b;
        }
    };

    //#endregion

    private TextView mapEventsLog;

    //#endregion

    /**
     * Activity initializer
     *
     * @param savedInstanceState - previous state
     */
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
        mapEventsLog = findViewById(R.id.logTextView);
        mapEventsLog.setMovementMethod(new ScrollingMovementMethod());

        // myButton
        myButton = (Button) findViewById(R.id.myButton);
        myButton.setOnClickListener(myButtonListener);

        // mySwitch
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        mySwitch.setOnCheckedChangeListener(mySwitchListener);
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
        map.getUiSettings().setZoomControlsEnabled(true);
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

    /**
     * Function to append a string to a TextView as a new line
     * and scroll to the bottom if needed
     *
     * @param msg - message to log
     */
    private void addLogMessage(String msg) {
        // append the new string
        mapEventsLog.append(msg + "\n");
        // find the amount we need to scroll.  This works by
        // asking the TextView's internal layout for the position
        // of the final line and then subtracting the TextView's height
        final int scrollAmount = mapEventsLog.getLayout().getLineTop(mapEventsLog.getLineCount()) - mapEventsLog.getHeight();
        // if there is no need to scroll, scrollAmount will be <=0
        if (scrollAmount > 0)
            mapEventsLog.scrollTo(0, scrollAmount);
        else
            mapEventsLog.scrollTo(0, 0);
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        this.mapEventsLog.append("MyLocation button clicked!\n");
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        Toast.makeText(this, "Current location:\n" + myLocation, Toast.LENGTH_LONG).show();
        this.mapEventsLog.append("Current location: " + myLocation + "\n");
    }

    @Override
    public void onMyLocationChange(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (myLocation == null || LocationUtils.distance(myLocation, latLng) >= MY_FOLLOW_DISTANCE) {
            myLocation = latLng;
            if (myLocationFollow) {
                mapService.MoveCameraToMe(MapsActivity.this.map, MY_ZOOM);
            }

            addLogMessage("Changed location: " + myLocation + "!");
        }
    }
}