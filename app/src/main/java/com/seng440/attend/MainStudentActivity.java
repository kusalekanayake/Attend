package com.seng440.attend;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.tasks.Task;


public class MainStudentActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_FINE_LOCATION;
    private int count = 0;
    private String androidId;
    private String nameText;
    private String classText;
    private Message mMessage;
    private MessageListener mMessageListener;
    private boolean loading = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private String locationString = "";
    private Task<Location> loco;

    private BottomNavigationView mTeacherNav;
    private String radius;
    private String lat;
    private String lon;
    private String loggedIn = "logged out";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                }
            }
            return;
        }
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        ((TextView) findViewById(R.id.textView2)).setText(androidId);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        loco = mFusedLocationClient.getLastLocation();
        loggedIn = getIntent().getStringExtra("STATUS");
        if (loggedIn != null && loggedIn.equals("logged in")) {
            ((TextView) findViewById(R.id.textView3)).setText("Connected");
            ((ImageView) findViewById(R.id.imageView2)).setImageResource(R.drawable.green);
        }
        getGeofence();


        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d("FOUND MESSAGE", "Found message: " + new String(message.getContent()));
                String messageText = new String(message.getContent());
//                ((TextView) findViewById(R.id.textView2)).setText(new String(message.getContent()));
                if (messageText.split(",")[0].equals(androidId)) {
                    ((TextView) findViewById(R.id.textView3)).setText("Connected");
                    ((ImageView) findViewById(R.id.imageView2)).setImageResource(R.drawable.green);
                    loggedIn = "logged in";
                    loading = false;
                } else if (messageText.split(",")[0].equals("GEOFENCE")) {
                    radius = messageText.split(",")[1];
                    lat = messageText.split(",")[2];
                    lon = messageText.split(",")[3];
                    ((TextView) findViewById(R.id.textView2)).setText("Geofence found, navigate to maps to see the boundary.");



                }
            }

            @Override
            public void onLost(Message message) {
                Log.d("LOST MESSAGE", "Lost sight of message: " + new String(message.getContent()));
            }
        };
        nameText = getIntent().getStringExtra("NAME");
        classText = getIntent().getStringExtra("CLASS");
        mMessage = new Message((nameText.toString() + "," + classText.toString() + "," + count + "," + androidId.toString()).getBytes());
        count += 1;

        mTeacherNav = (BottomNavigationView) findViewById(R.id.student_nav);


        mTeacherNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent i;
            String classText;
            String nameText;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_student_roll:
                        return true;
                    case R.id.nav_student_map:
                        if (radius != null) {
                            i = new Intent(getApplicationContext(), StudentMapsActivity.class);
                            if (radius != null) {
                                i.putExtra("RADIUS", String.valueOf(radius));
                                i.putExtra("LAT", String.valueOf(lat));
                                i.putExtra("LONG", String.valueOf(lon));
                                i.putExtra("STATUS", loggedIn);

                            }
                            finish();
                            startActivity(i);
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        return false;

                }
            }
        });
        new android.os.Handler().postDelayed(
                () -> lookForMessages(), 500);
    }

    private void lookForMessages() {
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public void startSendingMessage(android.view.View view) {
        Location lol = loco.getResult();
        locationString = String.valueOf(lol.getLatitude()) + "," + String.valueOf(lol.getLongitude());
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
//        ((TextView) findViewById(R.id.textView2)).setText(locationString);

        mMessage = new Message((nameText + "," + classText + ","+ count + "," + androidId + "," + locationString).getBytes());
        count += 1;
        Nearby.getMessagesClient(this).publish(mMessage);
        if (!loading) {
            loading = true;
            loadingSpinner(1);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit this session?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainStudentActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void loadingSpinner(int state) {
        if (loading) {
            switch (state) {
                case 1:
                    ((ImageView) findViewById(R.id.imageView2)).setImageResource(R.drawable.load1);
                    new android.os.Handler().postDelayed(
                            () -> loadingSpinner(2), 333);
                    break;
                case 2:
                    ((ImageView) findViewById(R.id.imageView2)).setImageResource(R.drawable.load2);
                    new android.os.Handler().postDelayed(
                            () -> loadingSpinner(3), 333);
                    break;
                case 3:
                    ((ImageView) findViewById(R.id.imageView2)).setImageResource(R.drawable.load3);
                    new android.os.Handler().postDelayed(
                            () -> loadingSpinner(1), 333);
                    break;
                default:
                    ((ImageView) findViewById(R.id.imageView2)).setImageResource(R.drawable.load1);
                    new android.os.Handler().postDelayed(
                            () -> loadingSpinner(2), 333);
                    break;
            }
        }
    }

    private void getGeofence(){
        String radiusString = getIntent().getStringExtra("RADIUS");
        if (radiusString != null) {
            radius = radiusString;
            lat = getIntent().getStringExtra("LAT");
            lon = getIntent().getStringExtra("LONG");
            ((TextView) findViewById(R.id.textView2)).setText("Geofence found, navigate to maps to see the boundary.");
        }
    }

}
