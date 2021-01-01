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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.ContextService;
import com.fenix.app.service.MapService;
import com.fenix.app.service.MongoService;
import com.fenix.app.service.entity.ActorService;
import com.fenix.app.service.PusherService;
import com.fenix.app.util.JsonUtil;
import com.fenix.app.util.LocationUtil;
import com.fenix.app.util.ThreadUtil;
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

import lombok.var;



@RequiresApi(api = Build.VERSION_CODES.N)
public class MapsActivity extends AppCompatActivity implements
        MapService.EventListener,
        PusherService.EventListener,
        AdapterView.OnItemSelectedListener {

    //#region Constants

    private static final float MY_FOLLOW_DISTANCE = 0.25f;
    public static final int PUSH_MAP_GRAIN = 10;
    public static final String PUSH_MAP_CHANNEL = "map";
    public static final String PUSH_MAP_CHANNEL_SEPARATOR = "=";

    //#endregion

    //#region Services

    private MapService mapService;
    private PusherService pusherService;

    private MongoService mongoService;
    private ActorService actorService;

    //#endregion

    //#region Variables

    /**
     * Myself
     */
    private ActorDto my = null;
    private boolean myFollow = false;

    /**
     * Visible aliens
     */
    public List<ActorDto> aliens = new ArrayList<>();

    /**
     * Target alien
     */
    private ActorDto target = null;
    private boolean targetFollow = false;
    private Marker targetMarker = null;

    //#endregion

    //#region Controls

    //#region My

    //#region myRegButton
    private Button myRegButton;
    private final View.OnClickListener myPushButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("My", "myRegButton click");

            // Send them all my dto
            //TODO pusherService.Push(PUSH_MAP_CHANNEL, PUSH_LOCATION_EVENT, my);

            // Save current state to DB
            ThreadUtil.Do(() -> {
                actorService.save(my);
            });
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
        }
    };
    //#endregion

    //#region myAreaButton
    private Button myAreaButton;
    private View.OnClickListener myAreaButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mapService.MoveCameraToMe(MapService.LOCAL_ZOOM);
        }
    };
    //#endregion

    //# Вызов окна персонажа
    private ImageButton iconPersBatton;
    private View.OnContextClickListener iconPersBattonListener = new View.OnContextClickListener() {
        @Override
        public boolean onContextClick(View view) {
//            var fragment = null;
            var fragment = new PersonWindow();
            var fm = getFragmentManager();
            var ft = fm.beginTransaction();
            ft.replace(R.id.map, fragment);
            ft.commit();


            return false;
        }
    };
    //# end

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Login if not authorised
        my = ContextService.Context.getActor();
        if (my == null) {
            ThreadUtil.Do(() -> {
                Intent intent = new Intent(MapsActivity.this, LogonActivity.class);
                startActivity(intent);
            }).error(ex -> {
                throw new RuntimeException(ex.toString());
            });
            return;
        }

        // Init view
        setContentView(R.layout.activity_maps);

        // Services
        // Services
        ThreadUtil
                .Do(() -> {
                    mapService = new MapService(this);
                    pusherService = new PusherService(this);

                    mongoService = new MongoService("fenix");
                    actorService = new ActorService(mongoService);
                })
                .then(res -> {
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(mapService);

                    // Ready to push location
                    myFollow = true;

                    // myPushButton
                    myRegButton = (Button) findViewById(R.id.myRegButton);
                    myRegButton.setOnClickListener(myPushButtonListener);

                    // myNameTextView
                    myNameTextView = findViewById(R.id.myNameTextView);
                    myNameTextView.setText(my.getName());
                    myNameTextView.addTextChangedListener(myNameTextViewWatcher);

                    // myAreaButton
                    myAreaButton = (Button) findViewById(R.id.myAreaButton);
                    myAreaButton.setOnClickListener(myAreaButtonListener);

                    // my iconPersBatton
                    iconPersBatton = (ImageButton) findViewById(R.id.iconPersBatton);
                    iconPersBatton.setOnContextClickListener(iconPersBattonListener);

                    // aliensSpinner
                    aliensSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
                    aliensSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    aliensSpinner = findViewById(R.id.aliensSpinner);
                    aliensSpinner.setAdapter(aliensSpinnerAdapter);
                    aliensSpinner.setOnItemSelectedListener(this);

                    // mySwitch
                    mySwitch = (Switch) findViewById(R.id.mySwitch);
                    mySwitch.setOnCheckedChangeListener(mySwitchListener);
                })
                .error(ex -> {
                    throw new RuntimeException(ex.toString());
                });
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

        //Toast.makeText(this, "Current location:\n" + my.getLocation(), Toast.LENGTH_LONG).show();
        Log.i("Map", "My location click:" + my.getLocation());
    }

    @Override
    public void onMyLocationChange(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (my.getLocation() == null || LocationUtil.distance(my.getLocation(), latLng) >= MY_FOLLOW_DISTANCE) {
            my.setLocation(latLng);

            if (myFollow) {
                var myChanelName = PUSH_MAP_CHANNEL + PUSH_MAP_CHANNEL_SEPARATOR + LocationUtil.calcMapNumber(latLng, PUSH_MAP_GRAIN);
                pusherService.BindToMapChannels(myChanelName);

                // channelName is like this "map=2012;1012"
                pusherService.Push(PUSH_MAP_CHANNEL + PUSH_MAP_CHANNEL_SEPARATOR + LocationUtil.calcMapNumber(latLng, PUSH_MAP_GRAIN), my);
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

        // Check alien name with myself
        if (Strings.isEmptyOrWhitespace(dto.getName()) || my.getName().equals(dto.getName()))
            return;

        this.runOnUiThread(() -> {
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