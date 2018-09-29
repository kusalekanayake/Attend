package com.seng440.attend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

public class ClassRollActivity extends AppCompatActivity {

    int count = 0;
    String advertiserEndpointId;
    private GoogleApiClient mGoogleApiClient;


    Message mMessage;
    MessageListener mMessageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_roll);
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
        String nameText = getIntent().getStringExtra("NAME");
        String classText = getIntent().getStringExtra("CLASS");
        mMessage = new Message((nameText.toString() + " from " + classText.toString() + "\n count: "+ count).getBytes());
        count += 1;
    }


    @Override
    public void onStart() {
        super.onStart();
        String nameText = getIntent().getStringExtra("NAME");
        String classText = getIntent().getStringExtra("CLASS");
        mMessage = new Message((nameText.toString() + " from " + classText.toString() + "\ncount: "+ count).getBytes());
        count += 1;
        Nearby.getMessagesClient(this).publish(mMessage);
        Nearby.getMessagesClient(this).subscribe(mMessageListener);

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
        String nameText = getIntent().getStringExtra("NAME");
        String classText = getIntent().getStringExtra("CLASS");
        mMessage = new Message((nameText.toString() + " from " + classText.toString() + "\ncount: "+ count).getBytes());
        count += 1;
        Nearby.getMessagesClient(this).subscribe(mMessageListener);

    }

    public void startSendingMessage(android.view.View view) {
        ((TextView)findViewById(R.id.textView3)).setText("Sending message...");
        String nameText = getIntent().getStringExtra("NAME");
        String classText = getIntent().getStringExtra("CLASS");
        mMessage = new Message((nameText.toString() + " from " + classText.toString() + "\ncount: "+ count).getBytes());
        count += 1;
        Nearby.getMessagesClient(this).publish(mMessage);

    }
}
