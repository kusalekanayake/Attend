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

    private int count = 0;
    private String androidId;
    private String nameText;
    private String classText;
    private Message mMessage;
    private MessageListener mMessageListener;
    private boolean loading = false;


    private BottomNavigationView mTeacherNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        ((TextView) findViewById(R.id.textView2)).setText(androidId);

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d("FOUND MESSAGE", "Found message: " + new String(message.getContent()));
                String messageText = new String(message.getContent());
                ((TextView) findViewById(R.id.textView2)).setText(new String(message.getContent()));
                if (messageText.split(",")[0].equals(androidId)) {
                    ((TextView) findViewById(R.id.textView3)).setText("Connected");
                    ((ImageView) findViewById(R.id.imageView2)).setImageResource(R.drawable.green);
                    loading = false;
                }
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


    public void startSendingMessage(android.view.View view) {
        ((ImageView)findViewById(R.id.imageView2)).setImageResource(R.drawable.loading);
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
        mMessage = new Message((nameText.toString() + "," + classText.toString() + ","+ count + "," + androidId.toString()).getBytes());
        count += 1;
        Nearby.getMessagesClient(this).publish(mMessage);
        if (!loading) {
            loading = true;
            loadingSpinner(1);
        }
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



}
