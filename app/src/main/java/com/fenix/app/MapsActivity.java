package com.fenix.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.MapService;
import com.fenix.app.service.PusherService;
import com.fenix.app.util.JsonUtil;
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
    private static final String P_CHANNEL = "map";
    private static final String P_EVENT = "location";

    //#endregion

    //#region Services

    private MapService mapService = new MapService(this);
    private PusherService pusherService = new PusherService(this);

    //#endregion

    //#region Variables

    public List<ActorDto> aliens = new ArrayList<>();

    private ActorDto my = new ActorDto("MY_NAME", null);
    private boolean myLocationFollow = false;

    //#endregion

    //#region Controls

    //#region My

    //#region myPushButton
    private Button myPushButton;
    private final View.OnClickListener myPushButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            Log.i("My", "myPushButton click");

            pusherService.Push(P_CHANNEL, P_EVENT, my);

            if (aliens.size() == 0) {
                aliens.add(new ActorDto("alien 1", new LatLng(-10, -10)));
            } else if (aliens.size() == 1) {
                aliens.add(new ActorDto("alien 2", new LatLng(0, 0)));
            } else {
                aliens.add(new ActorDto("alien 3", new LatLng(10, 10)));
            }
            aliensSpinnerAdapter.clear();
            aliensSpinnerAdapter.addAll(aliens);
        }
    };
    //#endregion

    //#region myNameTextView
    private TextView myNameTextView;
    private final TextWatcher myNameTextViewWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            my.setName(s.toString());
        }
    };
    //#endregion

    //#region myAreaButton
    private Button myAreaButton;
    private View.OnClickListener myAreaButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            mapService.MoveCameraToMe(MY_ZOOM);
        }
    };
    //#endregion

    //#region mySwitch
    private Switch mySwitch;
    private CompoundButton.OnCheckedChangeListener mySwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            myLocationFollow = b;
        }
    };
    //#endregion

    //#endregion

    //#region Aliens

    private TextView aliensTextView;

    //#region aliensSpinner
    Spinner aliensSpinner;
    ArrayAdapter<ActorDto> aliensSpinnerAdapter;
    //#endregion

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
        pusherService.Bind(P_CHANNEL, P_EVENT);

        // myPushButton
        myPushButton = (Button) findViewById(R.id.myPushButton);
        myPushButton.setOnClickListener(myPushButtonListener);

        // myNameTextView
        myNameTextView = findViewById(R.id.myNameTextView);
        myNameTextView.addTextChangedListener(myNameTextViewWatcher);

        // myAreaButton
        myAreaButton = (Button) findViewById(R.id.myAreaButton);
        myAreaButton.setOnClickListener(myAreaButtonListener);

        // aliensSpinner
        aliens.add(my);
        aliensSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, aliens);
        aliensSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aliensSpinner = findViewById(R.id.aliensSpinner);
        aliensSpinner.setAdapter(aliensSpinnerAdapter);
        aliensSpinner.setOnItemSelectedListener(this);

        // mySwitch
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        mySwitch.setOnCheckedChangeListener(mySwitchListener);

        // aliensTextView
        aliensTextView = findViewById(R.id.aliensTextView);
        aliensTextView.setMovementMethod(new ScrollingMovementMethod());

    }

    //#region Map events

    @Override
    public boolean onMyLocationButtonClick() {

        Log.i("Alien", "Find my location click!");

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        my.setLocation(new LatLng(location.getLatitude(), location.getLongitude()));

        Toast.makeText(this, "Current location:\n" + my.getLocation(), Toast.LENGTH_LONG).show();
        Log.i("Map", "My location click:" + my.getLocation());
    }

    @Override
    public void onMyLocationChange(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (my.getLocation() == null || LocationUtil.distance(my.getLocation(), latLng) >= MY_FOLLOW_DISTANCE) {
            my.setLocation(latLng);

            //pusherService.Push(P_CHANNEL, P_EVENT, "test");

            if (myLocationFollow) {
                mapService.MoveCameraToMe(MY_ZOOM);
            }

            Log.i("Map", "Changed location: " + my.getLocation());
        }
    }

    //#endregion

    //#region Pusher events

    @Override
    public void onEvent(PusherEvent event) {
        Log.i("Pusher", "Received event with data: " + event.toString());

        String json = event.getData();
        ActorDto dto = JsonUtil.Parse(ActorDto.class, json);

        aliensTextView.append(dto + "\n");
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