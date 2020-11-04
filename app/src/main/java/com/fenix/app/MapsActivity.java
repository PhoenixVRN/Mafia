package com.fenix.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fenix.app.dto.AlienDto;
import com.fenix.app.service.MapService;
import com.fenix.app.service.PusherService;
import com.fenix.app.util.LocationUtil;
import com.fenix.app.util.TextViewUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationChangeListener,
        ConnectionEventListener, SubscriptionEventListener,
        AdapterView.OnItemSelectedListener {

    //#region Constants

    private static final float MY_ZOOM = 17;
    private static final float MY_FOLLOW_DISTANCE = 0.25f;

    //#endregion

    //#region Services

    private MapService mapService = new MapService(this);
    private PusherService pusherService = new PusherService(this);

    //#endregion

    //#region Variables

    public List<AlienDto> aliens = new ArrayList<>();

    private LatLng myLocation;
    private boolean myLocationFollow = false;

    //#endregion

    //#region Controls

    //#region Aliens

    //#region alienButton
    private Button alienButton;
    private final View.OnClickListener alienButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            Toast.makeText(MapsActivity.this, "New Alien cumming!", Toast.LENGTH_SHORT).show();

            aliensTextView.append("New Alien cumming!\n");
            TextViewUtil.ScrollToBottom(aliensTextView);

            if (aliens.size() == 0) {
                aliens.add(new AlienDto() {{
                    name = "my";
                    location = myLocation;
                }});
            } else if (aliens.size() == 1) {
                aliens.add(new AlienDto() {{
                    name = "alien 1";
                    location = new LatLng(0, 0);
                }});
            } else {
                aliens.add(new AlienDto() {{
                    name = "alien 2";
                    location = new LatLng(10, 10);
                }});
            }
            aliensSpinnerAdapter.clear();
            aliensSpinnerAdapter.addAll(aliens);

            Log.i("Alien", "New Alien cumming!");
        }
    };
    //#endregion

    private TextView aliensTextView;

    //#region aliensSpinner
    Spinner aliensSpinner;
    ArrayAdapter<AlienDto> aliensSpinnerAdapter;
    //#endregion

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

        // alienButton
        alienButton = (Button) findViewById(R.id.alienButton);
        alienButton.setOnClickListener(alienButtonListener);

        // aliensTextView
        aliensTextView = findViewById(R.id.aliensTextView);
        aliensTextView.setMovementMethod(new ScrollingMovementMethod());

        // aliensSpinner
        aliensSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        aliensSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aliensSpinner = findViewById(R.id.aliensSpinner);
        aliensSpinner.setAdapter(aliensSpinnerAdapter);
        aliensSpinner.setOnItemSelectedListener(this);

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

        Log.i("Alien", "New Alien cumming!");
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        Toast.makeText(this, "Current location:\n" + myLocation, Toast.LENGTH_LONG).show();
        this.aliensTextView.append("Current location: " + myLocation + "\n");
    }

    @Override
    public void onMyLocationChange(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (myLocation == null || LocationUtil.distance(myLocation, latLng) >= MY_FOLLOW_DISTANCE) {
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

    //#region Alien Spinner

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i("AlienSpinner", "Item Selected");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i("AlienSpinner", "Nothing selected");
    }

    //#endregion

}