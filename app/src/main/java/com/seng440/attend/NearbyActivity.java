package com.seng440.attend;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;


public class NearbyActivity extends AppCompatActivity {

    int count = 0;
    String androidId;
    String nameText;
    String classText;
    Message mMessage;
    MessageListener mMessageListener;

    private BottomNavigationView mTeacherNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d("FOUND MESSAGE", "Found message: " + new String(message.getContent()));
                ((TextView)findViewById(R.id.textView2)).setText(new String(message.getContent()));
                ((ImageView)findViewById(R.id.imageView2)).setImageResource(R.drawable.green);
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
        mTeacherNav.clearAnimation();

        mTeacherNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent i;
            String classText;
            String nameText;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_classes:
                        return true;
                    case R.id.nav_roll:
                        i = new Intent(getApplicationContext(), ClassRollActivity.class);
                        classText = "SENG440";
                        i.putExtra("CLASS", classText);
                        nameText = "Kusal";
                        i.putExtra("NAME", nameText);
                        finish();
                        startActivity(i);
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


    public void startAdvertising(android.view.View view) {
        ((TextView)findViewById(R.id.textView3)).setText("Advertising...");
    }

    public void startConnecting(android.view.View view) {
        ((TextView)findViewById(R.id.textView3)).setText("Connecting...");
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
        ((TextView)findViewById(R.id.textView3)).setText("Connected.");


    }

    public void startSendingMessage(android.view.View view) {
        ((TextView)findViewById(R.id.textView3)).setText("Sending message...");
        mMessage = new Message((nameText.toString() + "," + classText.toString() + ","+ count + "," + androidId.toString()).getBytes());
        count += 1;
        Nearby.getMessagesClient(this).publish(mMessage);

    }


    public void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.teacher_nav);
    }

}
