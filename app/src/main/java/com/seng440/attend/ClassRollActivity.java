package com.seng440.attend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


public class ClassRollActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_FINE_LOCATION;
    private int count = 0;
    private String androidId;
    private String nameText;
    private String classText;
    private Message mMessage;
    private Course course;
    private String courseString;
    private MessageListener mMessageListener;
    private BottomNavigationView mTeacherNav;
    private TableLayout rollTable;
    private FusedLocationProviderClient mFusedLocationClient;
    private Task<Location> loco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_roll);
        course = new Course("SENG440");

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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        loco = mFusedLocationClient.getLastLocation();
        rollTable = (TableLayout) findViewById(R.id.rollTable);
        classText = getIntent().getStringExtra("CLASS");
        ((TextView)findViewById(R.id.className)).setText(classText);
        courseString = getIntent().getStringExtra("STUDENTS");
        course.parseStudents(courseString);
        reAddStudents();
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d("FOUND MESSAGE", "Found message: " + new String(message.getContent()));
                String messageString = new String(message.getContent());
                String student = messageString.split(",")[0];
                String id = messageString.split(",")[3];
                String locationString = messageString.split(",")[4] + ","  + messageString.split(",")[5];
                addNewStudentToTable(student, id, locationString);

            }

            @Override
            public void onLost(Message message) {
                Log.d("LOST MESSAGE", "Lost sight of message: " + new String(message.getContent()));
            }


        };

        nameText = getIntent().getStringExtra("NAME");
        classText = getIntent().getStringExtra("CLASS");
        mMessage = new Message((nameText.toString() + "," + classText.toString() + ","+ count + "," + androidId.toString()).getBytes());
        count += 1;

        mTeacherNav = (BottomNavigationView) findViewById(R.id.teacher_nav);
        mTeacherNav.setSelectedItemId(R.id.nav_roll);
        mTeacherNav.clearAnimation();
        mTeacherNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent i;
            String classText;
            String nameText;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_classes:
                        i = new Intent(getApplicationContext(), NearbyActivity.class);
                        classText = "SENG440";
                        i.putExtra("CLASS", classText);
                        i.putExtra("STUDENTS", course.toString());
                        nameText = "Kusal";
                        i.putExtra("NAME", nameText);
                        finish();
                        startActivity(i);
                        return true;
                    case R.id.nav_roll:
                        return true;
                    case R.id.nav_map:
                        i = new Intent(getApplicationContext(), LocationActivity.class);
                        classText = "HI there";
                        i.putExtra("STUDENTS", course.toString());
                        i.putExtra("CLASS", classText);
                        nameText = "hello";
                        i.putExtra("NAME", nameText);
                        finish();
                        startActivity(i);
                        return true;
                    default:
                        return false;

                }
            }
        });
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
        new android.os.Handler().postDelayed(
                () -> lookForStudents(), 500);
    }

    private void reAddStudents() {
        ArrayList<Student> students = course.getStudentArray();
        for (Student student: students) {
            TableRow newStudent = new TableRow(this);
            TextView studentName = new TextView(this);
            studentName.setText(student.getName());
            newStudent.addView(studentName);
            TextView student_id = new TextView(this);
            student_id.setText(student.getId());
            newStudent.addView(student_id);
            rollTable.addView(newStudent);
        }
    }

    private void addNewStudentToTable(String student, String id, String locationString) {
        boolean replace = course.addStudent(new Student(student, id));
        Location location = loco.getResult();
        double lat = Double.parseDouble(locationString.split(",")[0]);
        double lon = Double.parseDouble(locationString.split(",")[1]);
        double distance = distance(lat, location.getLatitude(), lon, location.getLongitude());
        ((TextView) findViewById(R.id.lastUpdateText)).setText(String.valueOf(distance));

        if (distance < 300) {
            if (replace) {
                for (int i = 0, j = rollTable.getChildCount(); i < j; i++) {
                    View view = rollTable.getChildAt(i);
                    if (view instanceof TableRow) {
                        // then, you can remove the the row you want...
                        // for instance...
                        TableRow row = (TableRow) view;
                        if (((TextView) row.getChildAt(1)).getText().toString().equals(id)) {
                            rollTable.removeViewAt(i);
                            rollTable.removeView(row);
                        }
                    }
                }
            }

            TableRow newStudent = new TableRow(this);
            TextView studentName = new TextView(this);
            studentName.setText(student);
            newStudent.addView(studentName);
            TextView student_id = new TextView(this);
            student_id.setText(id);
            newStudent.addView(student_id);
            rollTable.addView(newStudent);
            respondToStudent(student, id);
        }
    }

    private void lookForStudents() {
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        Nearby.getMessagesClient(this).unpublish(mMessage);
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
        super.onStop();
    }


    private void respondToStudent(String phoneId, String name) {
        mMessage = new Message((name + "," + phoneId + "," + course.getCourseName()).getBytes());
        Nearby.getMessagesClient(this).publish(mMessage);
    }

    public void exportClass(android.view.View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","abc@mail.com", null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "address");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, course.getStudents());
        startActivity(Intent.createChooser(emailIntent, "Send Email..."));

    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

}
