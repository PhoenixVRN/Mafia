package com.fenix.app;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.MapService;
import com.fenix.app.service.PusherService;
import com.fenix.app.util.JsonUtil;
import com.fenix.app.util.LocationUtil;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import lombok.var;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationChangeListener,
        ConnectionEventListener, SubscriptionEventListener,
        AdapterView.OnItemSelectedListener {

    //#region Constants

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

    private ActorDto my = new ActorDto("John", null);
    private boolean myRegistered = false;

    private ActorDto target = null;
    private boolean targetFollow = false;
    private Marker targetMarker = null;

    //#endregion

    //#region Controls

    //#region My

    //#region myRegButton
    private Button myRegButton;
    private final View.OnClickListener myPushButtonListener = v -> {
//        Intent intent = new Intent(MapsActivity.this, ScrActivity.class);
//        startActivity(intent);
        Log.i("My", "myRegButton click");

        // Set my name
        my.setName(this.myNameTextView.getText().toString());

        // I'am is registered now
        myRegistered = true;

        // Send them all my dto
        pusherService.Push(P_CHANNEL, P_EVENT, my);

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
        }
    };
    //#endregion

    //#region myAreaButton
    private Button myAreaButton;
    private View.OnClickListener myAreaButtonListener = v -> mapService.MoveCameraToMe(MapService.LOCAL_ZOOM);
    //#endregion

    //#region mySwitch
    private Switch mySwitch;
    private CompoundButton.OnCheckedChangeListener mySwitchListener = (compoundButton, b) -> targetFollow = b;
    //#endregion

    //#endregion

    //#region Aliens

    //#region aliensSpinner
    Spinner aliensSpinner;
    ArrayAdapter aliensSpinnerAdapter;
    //#endregion

    //#endregion

    //#endregion

    /**
     * Activity initializer
     *
     * @param savedInstanceState - previous state
     */
    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       setContentView(R.layout.activity_log_scr);
 //       Thread.sleep(10000);
        var tred = new Thread(()->{
            MapsActivity.this.runOnUiThread(()->{
                Intent intent = new Intent(MapsActivity.this, ScrActivity.class);
                startActivity(intent);
            });
        });
        tred.start();
        setContentView(R.layout.activity_maps);

  //      Thread.sleep(10000);
  //      setContentView(R.layout.activity_log_scr);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapService);

        // Connect to Pusher-channel
        pusherService.Bind(P_CHANNEL, P_EVENT);

        // myPushButton
        myRegButton = (Button) findViewById(R.id.myRegButton);
        myRegButton.setOnClickListener(myPushButtonListener);

        // myNameTextView
        myNameTextView = findViewById(R.id.myNameTextView);
        myNameTextView.addTextChangedListener(myNameTextViewWatcher);

        // myAreaButton
        myAreaButton = (Button) findViewById(R.id.myAreaButton);
        myAreaButton.setOnClickListener(myAreaButtonListener);

        // aliensSpinner
        aliensSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        aliensSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aliensSpinner = findViewById(R.id.aliensSpinner);
        aliensSpinner.setAdapter(aliensSpinnerAdapter);
        aliensSpinner.setOnItemSelectedListener(this);

        // mySwitch
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        mySwitch.setOnCheckedChangeListener(mySwitchListener);
    }

    /**
     * Adding alien actor
     */
    protected void tryAddAlien(final ActorDto alien) {

        long alreadyLinked = aliens.stream()
                .filter(s -> s.getName().equals(alien.getName()))
                .count();

        if (alreadyLinked > 0)
            return;

        aliens.add(alien);

        aliensSpinnerAdapter.clear();
        aliensSpinnerAdapter.addAll(aliens);
    }

    /**
     * Finding alien o map
     */
    public void tryFindOnMap(ActorDto alien) {
        if (target != null && target.getName().equals(alien.getName())) {

            // Remove previous marker
            if (targetMarker != null)
                targetMarker.remove();

            // Save new marker
            targetMarker = mapService.MarkerToLocation(alien.getName(), alien.getLocation(), targetFollow);
        }
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

            if (myRegistered)
                pusherService.Push(P_CHANNEL, P_EVENT, my);

            Log.i("Map", "Changed location: " + my.getLocation());
        }
    }

    //#endregion

    //#region Pusher events

    @Override
    public void onEvent(PusherEvent event) {
        this.runOnUiThread(() -> {
            Log.i("Pusher", "Received event with data: " + event.toString());

            String json = event.getData();
            ActorDto dto = JsonUtil.Parse(ActorDto.class, json);

            // Check alien name with myself
            if (Strings.isEmptyOrWhitespace(dto.getName()) || my.getName().equals(dto.getName()))
                return;

            // Sync aliens list
            tryAddAlien(dto);

            // Mark alien on map
            tryFindOnMap(dto);
        });
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
        target = aliens.get(position);
        Log.i("AlienSpinner", target.getName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i("AlienSpinner", "Nothing selected");
        target = null;
    }

    //#endregion

}