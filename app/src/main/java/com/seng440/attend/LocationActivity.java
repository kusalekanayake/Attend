package com.seng440.attend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationActivity extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView myLatitude;
    private TextView myLongitude;
    private LocationRequest mLocationRequest;
    private BottomNavigationView mTeacherNav;
    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        myLatitude = (TextView) findViewById(R.id.latitudeText);
        myLongitude = findViewById(R.id.LongitudeText);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        course = getIntent().getStringExtra("STUDENTS");

        mTeacherNav = (BottomNavigationView) findViewById(R.id.teacher_nav);
        mTeacherNav.setSelectedItemId(R.id.nav_map);
        mTeacherNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent i;
            String classText;
            String nameText;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_roll:
                        i = new Intent(getApplicationContext(), ClassRollActivity.class);
                        classText = "SENG440";
                        i.putExtra("STUDENTS", course);
                        i.putExtra("CLASS", classText);
                        nameText = "Kusal";
                        i.putExtra("NAME", nameText);
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

    protected void startLocationUpdates(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10*1000);
        mLocationRequest.setFastestInterval(2000);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);


    }

    @SuppressLint("MissingPermission")
    public void checkLocationButton(View view) {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d("Test Location Button", String.valueOf(location.getAccuracy()));
                            myLatitude.setText(String.valueOf(location.getLatitude()));
                            myLongitude.setText(String.valueOf(location.getLongitude()));


                        }
                        // Got last known location. In some rare situations this can be null.
                        if (location == null) {
                            Log.d("Test location msg", "NULL");
                            // Logic to handle location object
                        }
                    }
                });
    }
    public void checkMapButton(android.view.View view) {
        Intent i = new Intent(getApplicationContext(), MapsActivity2.class);
        startActivity(i);
    }
    public void checkStudentButton(android.view.View view){
        Intent i = new Intent(getApplicationContext(), studentGeofence.class);
        startActivity(i);
    }

}
