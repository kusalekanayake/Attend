package com.seng440.attend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;


public class NearbyActivity extends AppCompatActivity {

    String advertiserEndpointId;
    private GoogleApiClient mGoogleApiClient;

    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(180).build();
    Message mMessage;
    MessageListener mMessageListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d("FOUND MESSAGE", "Found message: " + new String(message.getContent()));
                ((TextView)findViewById(R.id.textView2)).setText(message.toString());

            }

            @Override
            public void onLost(Message message) {
                Log.d("LOST MESSAGE", "Lost sight of message: " + new String(message.getContent()));
            }
        };

        mMessage = new Message("Hello World".getBytes());
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
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
        Nearby.getMessagesClient(this).subscribe(mMessageListener);

    }

    public void startSendingMessage(android.view.View view) {
        ((TextView)findViewById(R.id.textView3)).setText("Sending message...");
        Nearby.getMessagesClient(this).publish(mMessage);

    }

}
