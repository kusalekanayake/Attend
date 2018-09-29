package com.seng440.attend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        myLatitude = (TextView) findViewById(R.id.latitudeText);
        myLongitude = findViewById(R.id.LongitudeText);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        Intent i = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(i);
    }

}
