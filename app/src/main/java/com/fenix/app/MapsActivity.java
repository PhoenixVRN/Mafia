package com.fenix.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fenix.app.com.fenix.app.service.MapService;
import com.fenix.app.com.fenix.app.service.PusherService;
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
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationChangeListener,
        ConnectionEventListener, SubscriptionEventListener {

    //#region Constants
    private static final float MY_ZOOM = 17;
    private static final float MY_FOLLOW_DISTANCE = 0.25f;
    //#endregion

    //#region Services

    private MapService mapService = new MapService(this);
    private PusherService pusherService = new PusherService(this);
    //#endregion

    //#region Variables
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
            MapsActivity.this.alienMarker = mapService.map.addMarker(myMarkerOptions);
            mapService.map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

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
            mapService.MoveCameraToMe(MY_ZOOM);
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
        mapFragment.getMapAsync(mapService);

        // Connect to Pusher-channel
        pusherService.Bind("map", "location");

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

    //#region Map events

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
                mapService.MoveCameraToMe(MY_ZOOM);
            }

            Log.i("Map", "\"Changed location: " + myLocation.toString());
        }
    }

    //#endregion

    //#region Pusher events

    @Override
    public void onEvent(PusherEvent event) {
        Log.i("Pusher", "Received event with data: " + event.toString());
    }

    @Override
    public void onConnectionStateChange(ConnectionStateChange change) {
        Log.i("Pusher", "State changed from " + change.getPreviousState() +
                " to " + change.getCurrentState());
    }

    @Override
    public void onError(String message, String code, Exception e) {
        Log.i("Pusher", "There was a problem connecting! " +
                "\ncode: " + code +
                "\nmessage: " + message +
                "\nException: " + e
        );
    }

    //#endregion

}