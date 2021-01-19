package com.fenix.app;

import android.content.Intent;
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

import com.fenix.app.dto.ActorDto;
import com.fenix.app.dto.ItemBox;
import com.fenix.app.service.ContextService;
import com.fenix.app.service.MapService;
import com.fenix.app.service.MongoService;
import com.fenix.app.service.PusherService;
import com.fenix.app.service.entity.ActorService;
import com.fenix.app.service.entity.ItemService;
import com.fenix.app.service.entity.ProgressTextView;
import com.fenix.app.util.DateUtil;
import com.fenix.app.util.LocationUtil;
import com.fenix.app.util.ThreadUtil;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.connection.ConnectionStateChange;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.var;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MapsActivity extends AppCompatActivity implements
        MapService.EventListener,
        PusherService.EventListener {

    //#region Constants

    private static final float MY_FOLLOW_DISTANCE = 0.25f; // 25sm
    private static final float MY_VIEW_DISTANCE = 150f; // 150m
    public static final int PUSH_MAP_GRAIN = 10; // ~1000m
    public static final String PUSH_MAP_CHANNEL = "map";
    public static final String PUSH_MAP_CHANNEL_SEPARATOR = "=";

    //#endregion

    //#region Services

    private MapService mapService;
    private PusherService pusherService;

    private MongoService mongoService;
    private ActorService actorService;

    private ItemService itemService;

    private Timer timerService;
    private TimerTask timerTaskPerSecond = new TimerTask() {
        @Override
        public void run() {
            onTimerPerSecond();
        }
    };
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
    public final List<ActorMarkerPair> aliens = new ArrayList<>();
    // Список лута
    public final List<ItemMarkerPair> items = new ArrayList<>();

    /**
     * Target alien
     */
    private ActorMarkerPair target = null;
    private boolean targetFollow = false;

    volatile boolean inPusherEventWork = false;
    volatile boolean inTimerEventWork = false;

    //#endregion

    //#region Controls

    //#region My

    //#region myRegButton
    private ImageButton hitButton;
    private final View.OnClickListener hitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("My", "hitButton click");

            //TODO Тут логика после выбора врага!
            TextView textDead = findViewById(R.id.infoText);
            ProgressTextView progressTextViewAlien = (ProgressTextView) findViewById(R.id.progressAlienHP);
            if (my != null && target != null && target.actor != null) {
                ThreadUtil
                        .Do(() -> actorService.hit(my, target.actor))
                        .then(alien ->
                        {
                            target.actor = (ActorDto) alien;
                            progressTextViewAlien.setVisibility(View.VISIBLE);
                            progressTextViewAlien.setValue(target.actor.getPerson().getHp(), target.actor.getPerson().getMaxhp()); // устанавливаем нужное значение
                            if (target.actor.getPerson().getHp() <= 0) {
                                progressTextViewAlien.setVisibility(View.INVISIBLE);
                                textDead.setVisibility(View.VISIBLE);
                                textDead.setText(target.actor.getName() + " СДОХ НАХ");
                                // ощздаеём
                                var item = new ItemBox();
                                item.setItemID(UUID.randomUUID().toString());
                                item.setName(target.actor.getName() + " CORPS");
                                item.getArmorItems().add(target.actor.getPerson().getArmorHead());
                                item.getArmorItems().add(target.actor.getPerson().getArmorTorso());
                                item.getArmorItems().add(target.actor.getPerson().getArmorGloves());
                                item.getArmorItems().add(target.actor.getPerson().getArmorBoots());
                                item.getArmorItems().add(target.actor.getPerson().getArmorBoots());

                                item.getWeaponItems().add(target.actor.getPerson().getWeaponHeadLeft());
                                item.getWeaponItems().add(target.actor.getPerson().getWeaponHeadRight());

                                item.getObjectsItems().add(target.actor.getPerson().getBag());


                                ThreadUtil.Do(() -> {
                                    try {
                                        itemService.save(item);
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }).then((res) -> {
                                    textDead.setText("");
                                    textDead.setVisibility(View.INVISIBLE);
                                });
//                    TODO написать что этот бобик сдох, удалить из списка или перевести в подраздел "трупаки"
                            }
                        })
                        .error(ex -> Log.e("hitButtonListener", ex.toString()));
            }
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
            LatLng latLng = mapService.MoveCameraToMe(MapService.LOCAL_ZOOM);

            // I'm ready to action
            if (myFollow)
                ThreadUtil.Do(() -> setMyLocation(latLng, true));
        }
    };
    //#endregion

    //#region Вызов окна персонажа
    private ImageButton iconPersBatton;
    private View.OnContextClickListener iconPersBattonListener = view -> {
//            var fragment = null;
        var fragment = new PersonWindow();
        var fm = getFragmentManager();
        var ft = fm.beginTransaction();
        ft.replace(R.id.map, fragment);
        ft.commit();


        return false;
    };
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
    AdapterView.OnItemSelectedListener aliensSpinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            // Clear color of old target
            var oldTarget = target;
            if (oldTarget != null) {
                var oldMarker = oldTarget.marker;
                if (oldMarker != null)
                    mapService.ChangeMarkerColor(oldMarker, -1f);
            }

            // Take the new target
            var newTarget = aliens.get(position);

            if (newTarget != null && newTarget.marker != null) {
                Log.i("AlienSpinner", newTarget.actor.getName());

                // Paint new target
                mapService.ChangeMarkerColor(newTarget.marker, BitmapDescriptorFactory.HUE_RED);
            }

            // Set the new target to activity
            MapsActivity.this.target = newTarget;
            var AlienDTO = newTarget.actor;
            // TODO здесь алиен ДТО
            ProgressTextView progressTextViewAlien = (ProgressTextView) findViewById(R.id.progressAlienHP);
            if (AlienDTO != null && AlienDTO.getPerson() != null) {
                progressTextViewAlien.setVisibility(View.VISIBLE);
                progressTextViewAlien.setValue(AlienDTO.getPerson().getHp(), AlienDTO.getPerson().getMaxhp());
                // устанавливаем нужное значение
            } else {
                progressTextViewAlien.setVisibility(View.INVISIBLE);

            }
            ProgressTextView progressTextViewMy = (ProgressTextView) findViewById(R.id.progressMyHP);
            progressTextViewMy.setValue(my.getPerson().getHp(), my.getPerson().getMaxhp());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.i("AlienSpinner", "Nothing selected");

            // Clear color of old target
            var oldTarget = target;
            if (oldTarget != null) {
                var oldMarker = oldTarget.marker;
                if (oldMarker != null)
                    mapService.ChangeMarkerColor(oldMarker, -1f);
            }

            // Clear target
            target = null;
        }

    };
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

                    itemService = new ItemService(mongoService);
                })
                .then(res -> {
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(mapService);

                    // Ready to push location
                    myFollow = true;

                    // timerService
                    timerService = new Timer();
                    timerService.schedule(timerTaskPerSecond, 1000, 1000);

                    // myPushButton
                    hitButton = (ImageButton) findViewById(R.id.hitButton);
                    hitButton.setOnClickListener(hitButtonListener);

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
                    aliensSpinner.setOnItemSelectedListener(aliensSpinnerItemSelectedListener);
                    {
                        var actorNull = new ActorDto();
                        actorNull.setName("Никто");
                        actorNull.setEmail("");
                        actorNull.setLocation(new LatLng(1000000f, 1000000f));
                        aliens.add(new ActorMarkerPair(actorNull, null));
                        aliensSpinnerAdapter.addAll(aliens);
                    }

                    // mySwitch
                    //                   mySwitch = (Switch) findViewById(R.id.mySwitch);
                    //                   mySwitch.setOnCheckedChangeListener(mySwitchListener);
                })
                .error(ex -> {
                    throw new RuntimeException(ex.toString());
                });
    }

    /**
     * Adding alien actor
     */
    protected void trySyncAlien(final ActorDto alien) {
        if (my.getEmail().equals(alien.getEmail())) {
            my = alien;
            myNameTextView.setText(my.getName());
            // TODO логика с MY ДТО

            ProgressTextView progressTextViewMy = (ProgressTextView) findViewById(R.id.progressMyHP);
            progressTextViewMy.setValue(my.getPerson().getHp(), my.getPerson().getMaxhp()); // устанавливаем нужное значение
            return; // It's not a alien
        }


        if (StringUtils.isEmpty(alien.getEmail()))
            return; // It's a Null
// если дистанция до лоха больше чем я могу его пистануть,
// или бобик уже сдох,
// или просто не активен 2 минуты, то уберите его с глаз моих нах....
        var dateDiff = DateUtil.dateDiff(new Date(), DateUtil.fromISO(alien.getLastAccessTime()));
        if (LocationUtil.distance(my.getLocation(), alien.getLocation()) > MY_VIEW_DISTANCE
                || alien.getPerson().getHp() <= 0
                || dateDiff > 2 * 60 * 1000) {
            // Sync already linked
            var toRemove = new ArrayList<ActorMarkerPair>();
            aliens.forEach(p -> {
                if (p.actor.getEmail().equals(alien.getEmail()))
                    toRemove.add(p);
            });

            if (toRemove.size() > 0) {
                toRemove.forEach(pair -> {
                    aliens.remove(pair);
                    if (pair.marker != null)
                        pair.marker.remove();
                });
            }
        } else {
            // Sync already linked

            var linked = new ArrayList<ActorMarkerPair>();
            aliens.forEach(p -> {
                if (p.actor.getEmail().equals(alien.getEmail()))
                    linked.add(p);
            });

            linked.forEach(pair -> {
                pair.actor.set(alien);
                pair.marker.setPosition(alien.getLocation());
            });

            // Add new
            if (linked.size() == 0) {
                var pair = new ActorMarkerPair(alien, mapService.MarkerToLocation(alien.getName(), alien.getLocation(), targetFollow));
                aliens.add(pair);
            }
        }
        aliensSpinnerAdapter.clear();
        aliensSpinnerAdapter.addAll(aliens);


/*
        if(my != null &&  target != null && target.actor!= null)
        {
            actorService.hit(my, target.actor);
            int enamyhp = target.actor.getPerson().getHp();
            int maxHP = target.actor.getPerson().getMaxhp();
            ProgressTextView progressTextView = (ProgressTextView) findViewById(R.id.progressAlienHP);
            progressTextView.setValue(enamyhp, maxHP); // устанавливаем нужное значение


        }*/
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
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        // I'm ready to action
        if (myFollow)
            ThreadUtil.Do(() -> setMyLocation(latLng, true));

        //Toast.makeText(this, "Current location:\n" + my.getLocation(), Toast.LENGTH_LONG).show();
        Log.i("Map", "My location click:" + my.getLocation());
    }

    @Override
    public void onMyLocationChange(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (my.getLocation() == null || LocationUtil.distance(my.getLocation(), latLng) >= MY_FOLLOW_DISTANCE) {

            ThreadUtil.Do(() -> setMyLocation(latLng, false));

            Log.i("Map", "Changed location: " + my.getLocation());
        }
    }

    private void setMyLocation(LatLng latLng, boolean push) {
        if (latLng == null)
            return;

        my = updateMyLocation(latLng, push);
    }

    private ActorDto updateMyLocation(LatLng myLocation, boolean push) {

        // channelName is like this "map=2012;1012"
        var myChanelName = PUSH_MAP_CHANNEL + PUSH_MAP_CHANNEL_SEPARATOR + LocationUtil.calcMapNumber(myLocation, PUSH_MAP_GRAIN);
        pusherService.BindToMapChannels(myChanelName);

        // Если dto не готова - просто возвращаем и ничего не делаем
        if (my.getPerson() == null)
            return my;

        // Save my profile to database
        var result = actorService.updateLocation(my, myLocation);

        // Push my id(email) to all if need
        if (push)
            pusherService.Push(myChanelName, my.getEmail());

        return result;
    }

    //#endregion

    //#region Pusher events

    @Override
    public void onEvent(PusherEvent event) {
        if (inPusherEventWork)
            return;
        inPusherEventWork = true;
        try {

            Log.i("Pusher", "Received event with data: " + event.toString());

            String email = event.getData();
            if (email == null)
                return;

            // Clean email and check alien email with myself
            email = StringUtils.strip(email, "\"");
            if (Strings.isEmptyOrWhitespace(email) || my.getEmail().equals(email))
                return;

            // Read data from database by alien email
            ActorDto alienDto = actorService.load(email);

            // Sync aliens list and mark alien on map
            MapsActivity.this.runOnUiThread(() -> trySyncAlien(alienDto));

        } finally {
            inPusherEventWork = false;
        }
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

    //#region Timer events

    private void onTimerPerSecond() {
        if (inTimerEventWork)
            return;

        // I'm ready to action
        if (myFollow)
            ThreadUtil.Do(() -> {
                if (inTimerEventWork)
                    return;
                inTimerEventWork = true;
                try {
                    Log.i("TimerPerSecond", "tick");

                    // Update my last access time
                    my = actorService.updateAccessTime(my);

                    // Load visible aliens from database
                    var aliensList = actorService.findByGeoPoint(my.getLocation(), MY_VIEW_DISTANCE);

                    // Add current invisible aliens
                    this.aliens.forEach(pair -> {
                        if (!aliensList.contains(pair.actor))
                            aliensList.add(pair.actor);
                    });

                    // Sync aliens list and mark alien on map
                    MapsActivity.this.runOnUiThread(() -> aliensList.forEach(this::trySyncAlien));

                } finally {
                    inTimerEventWork = false;
                }
            });
    }

    //#endregion

    @AllArgsConstructor
    private class ActorMarkerPair {
        public ActorDto actor;
        public final Marker marker;

        @Override
        public String toString() {
            return actor.getName();
        }
    }

    // Пара
    @AllArgsConstructor
    private class ItemMarkerPair {
        public ItemBox item;
        public final Marker marker;

        @Override
        public String toString() {
            return item.getName();
        }
    }
}