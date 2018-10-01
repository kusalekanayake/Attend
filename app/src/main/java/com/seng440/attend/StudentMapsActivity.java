package com.seng440.attend;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class StudentMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GeofencingClient mGeofencingClient;
    private Geofence mGeofence;
    private PendingIntent mGeofencePendingIntent;
    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    private BottomNavigationView mTeacherNav;
    private FusedLocationProviderClient mFusedLocaitonClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Circle posCircle;
    private MessageListener mMessageListener;
    private float radius;
    private LatLng fenceLocation;
    private LatLng testFence = new LatLng(-43.5226642, 172.5810532);
    private LatLng testRadius;
    private String loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocaitonClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_student_geofence);
        getGeofence();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGeofencingClient = new GeofencingClient(this);
        loggedIn = getIntent().getStringExtra("STATUS");

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
        new android.os.Handler().postDelayed(
                () -> lookForStudents(), 1500);

        mTeacherNav = (BottomNavigationView) findViewById(R.id.student_nav);
        mTeacherNav.setSelectedItemId(R.id.nav_student_map);
        mTeacherNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent i;
            String classText;
            String nameText;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_student_roll:
                        i = new Intent(getApplicationContext(), MainStudentActivity.class);
                        classText = "SENG440";
                        i.putExtra("CLASS", classText);
                        nameText = "Kusal";
                        i.putExtra("NAME", nameText);
                        i.putExtra("STATUS", loggedIn);
                        i.putExtra("RADIUS", String.valueOf(radius));
                        i.putExtra("LAT", String.valueOf(fenceLocation.latitude));
                        i.putExtra("LONG", String.valueOf(fenceLocation.longitude));
                        finish();
                        startActivity(i);
                        return true;
                    case R.id.nav_student_map:

                        return true;
                    default:
                        return false;

                }
            }
        });

    }
    private void lookForStudents() {
        Log.d("LOST MESSAGE", "SUBSCRIBIng");

        Nearby.getMessagesClient(getApplicationContext()).subscribe(mMessageListener);
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit this session?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        StudentMapsActivity.this.finish();
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        posCircle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(0,0))
                .radius(10)
                .strokeColor(Color.RED)
                .fillColor(Color.RED));
        createLocationRequest();
        createLocationCallback(mMap);
        startLocationUpdates();

        // Add a marker in Sydney and move the camera


        mMap.moveCamera(CameraUpdateFactory.newLatLng(fenceLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        addGeofence(mMap);
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ADDED GEOFENCE.......", "ADDED THE GEOFENCE");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FAILED GEOFENCE.......", "FAILED THE GEOFENCE");
            }
        });
    }

    private void addGeofence(GoogleMap mMap) {

        mGeofence = new Geofence.Builder()
                .setRequestId("1")
                .setCircularRegion(fenceLocation.latitude, fenceLocation.longitude, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(fenceLocation)
                .radius(radius)
                .strokeColor(Color.BLUE)
                .fillColor(Color.alpha(Color.BLUE)));

        mGeofenceList.add(mGeofence);

    }

    private GeofencingRequest getGeofencingRequest() {

        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofences(mGeofenceList)
                .build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(250);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocaitonClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void createLocationCallback(final GoogleMap mMap){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult){
                if(locationResult ==null){
                    return;
                }
                for (Location location : locationResult.getLocations()){

                    posCircle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));

                }
            }
        };
    }

    private void getGeofence(){
        String radiusString = getIntent().getStringExtra("RADIUS");
        Log.d("SENGG", "About to send geofence");
        double lat;
        double lon;
        if (radiusString != null) {
            radius = Float.parseFloat(radiusString);
            Log.d("SEND", "sent geofence");
            lat = Double.parseDouble(getIntent().getStringExtra("LAT"));
            lon = Double.parseDouble(getIntent().getStringExtra("LONG"));


        }else{
            radius = 1000;
            lat = testFence.latitude;
            lon = testFence.longitude;
        }
        fenceLocation = new LatLng(lat,lon);
    }



}


