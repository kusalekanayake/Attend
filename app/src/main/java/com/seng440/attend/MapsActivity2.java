package com.seng440.attend;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private float radius;
    private SeekBar seekBar;
    private LatLng markerPos;
    private Circle circle;
    private FloatingActionButton fab;
    private Message mMessage;
    private MessageListener mMessageListener;
    private String classText;
    private String students;
    private BottomNavigationView mTeacherNav;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        classText = getIntent().getStringExtra("CLASS");
        students = getIntent().getStringExtra("STUDENTS");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        radius = (float)seekBar.getProgress();
        fab = findViewById(R.id.floatingActionButton);
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d("FOUND MESSAGE", "found");
                Log.d("FOUND MESSAGE", "Found message: " + new String(message.getContent()));
                String messageText = new String(message.getContent());

            }

            @Override
            public void onLost(Message message) {
                Log.d("LOST MESSAGE", "Lost sight of message: " + new String(message.getContent()));
            }
        };
        floatingButtonListener();
        mTeacherNav = (BottomNavigationView) findViewById(R.id.teacher_nav);
        mTeacherNav.setSelectedItemId(R.id.nav_map);
        mTeacherNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent i;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_roll:
                        i = new Intent(getApplicationContext(), ClassRollActivity.class);
                        i.putExtra("STUDENTS", students);
                        i.putExtra("CLASS", classText);
                        finish();
                        startActivity(i);
                        return true;
                    case R.id.nav_map:
                        return true;
                    default:
                        return false;

                }
            }
        });
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit this session?\nYou will lose track of your roll.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MapsActivity2.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mFusedLocationClient.getLastLocation()
         .addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(point).title("Marker"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                    Log.d("Test Location Button", String.valueOf(location.getAccuracy()));
                    markerPos =point;
                    circle = mMap.addCircle(new CircleOptions()
                            .center(point)
                            .radius(radius)
                            .strokeColor(Color.RED)
                            .fillColor(Color.alpha(Color.BLUE)));


                }
                // Got last known location. In some rare situations this can be null.
                if (location == null) {
                    Log.d("Test location msg", "NULL");
                    // Logic to handle location object
                }
            }
        });

        setMapLongClick(mMap);
        setSeekBarListener(seekBar, mMap);




    }


    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                markerPos = latLng;
                circle = map.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(radius)
                        .strokeColor(Color.RED)
                        .fillColor(Color.alpha(Color.BLUE)));

                map.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }


    private void setSeekBarListener(SeekBar seekBar, final GoogleMap map){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                radius = (float)progress;
                circle.setRadius(radius);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void floatingButtonListener(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ClassRollActivity.class);

                intent.putExtra("RADIUS", String.valueOf(radius));
                intent.putExtra("LAT", String.valueOf(markerPos.latitude));
                intent.putExtra("LONG", String.valueOf(markerPos.longitude));
                intent.putExtra("CLASS", classText);
                intent.putExtra("STUDENTS", students);

                finish();
                startActivity(intent);
//                Nearby.getMessagesClient(getApplicationContext()).subscribe(mMessageListener);
//
//                Log.d("SENDING MES", "SENDING MESSAGE");
//                mMessage = new Message(( String.valueOf(radius) + "," + String.valueOf(markerPos.latitude) + ","+ String.valueOf(markerPos.longitude)).getBytes());
//                Nearby.getMessagesClient(getApplicationContext()).publish(mMessage);
            }
        });

    }

}
