package com.fenix.app;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker myMarker;

    private Button mapButton;
    private View.OnClickListener mapButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            MapsActivity.this.mapTextView.append("Button click! ");
        }
    };

    private Button myButton;
    private View.OnClickListener myButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            MapsActivity.this.mapTextView.append("Button click! ");
        }
    };

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
        mapButton = (Button) findViewById(R.id.button);
        mapButton.setOnClickListener(mapButtonListener);

        // TextView
        mapTextView = findViewById(R.id.textView);
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
        this.googleMap = googleMap;

        // Add a marker and move the camera
        LatLng myLocation = new LatLng(-34, 151);
        MarkerOptions myMarkerOptions = new MarkerOptions().position(myLocation).title("My Marker");
        myMarker = this.googleMap.addMarker(myMarkerOptions);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }
}