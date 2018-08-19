package com.seng440.attend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_COARSE_LOCATION;
    private int MY_PERMISSIONS_REQUEST_BLUETOOTH;
    private int MY_PERMISSIONS_REQUEST_BLUETOOTH_ADMIN;
    private int MY_PERMISSIONS_REQUEST_WIFI_STATE;
    private int MY_PERMISSIONS_REQUEST_WIFI_CHANGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
    }

    public void checkStudentButton(android.view.View view) {
        Log.d("Test Student Button", "testing");
        // TODO Auto-generated method stub
        Intent i = new Intent(getApplicationContext(),NearbyActivity.class);
        startActivity(i);
//        setContentView(R.layout.activity_nearby);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
                    }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BLUETOOTH)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH},
                        MY_PERMISSIONS_REQUEST_BLUETOOTH);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BLUETOOTH_ADMIN)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                        MY_PERMISSIONS_REQUEST_BLUETOOTH_ADMIN);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_WIFI_STATE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                        MY_PERMISSIONS_REQUEST_WIFI_STATE);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CHANGE_WIFI_STATE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                        MY_PERMISSIONS_REQUEST_WIFI_CHANGE);
            }
        }

    }
}
