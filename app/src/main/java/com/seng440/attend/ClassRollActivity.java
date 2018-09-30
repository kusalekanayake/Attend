package com.seng440.attend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;


public class ClassRollActivity extends AppCompatActivity {

    private int count = 0;
    private String androidId;
    private String nameText;
    private String classText;
    private Message mMessage;
    private Course course;
    private MessageListener mMessageListener;
    private BottomNavigationView mTeacherNav;
    private TableLayout rollTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        course = new Course("SENG440");
        setContentView(R.layout.activity_class_roll);
        rollTable = (TableLayout) findViewById(R.id.rollTable);
        classText = getIntent().getStringExtra("CLASS");
        ((TextView)findViewById(R.id.className)).setText(classText);

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d("FOUND MESSAGE", "Found message: " + new String(message.getContent()));
                String messageString = new String(message.getContent());
                String student = messageString.split(",")[0];
                String id = messageString.split(",")[3];
                addNewStudentToTable(student, id);

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
    }

    private void addNewStudentToTable(String student, String id) {
        boolean replace = course.addStudent(new Student(student, id));
        if (replace) {
            for(int i = 0, j = rollTable.getChildCount(); i < j; i++) {
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
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","abc@mail.com", null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "address");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, course.getStudents());
        startActivity(Intent.createChooser(emailIntent, "Send Email..."));

    }



}
