package com.seng440.attend;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    }


    @Override
    public void onStart() {
        super.onStart();
        mMessage = new Message((nameText.toString() + "," + classText.toString() + ","+ count + "," + androidId.toString()).getBytes());
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
        mMessage = new Message((nameText.toString() + "," + classText.toString() + ","+ count + "," + androidId.toString()).getBytes());
        count += 1;
        Nearby.getMessagesClient(this).subscribe(mMessageListener);

    }

    public void startSendingMessage(android.view.View view) {
        ((TextView)findViewById(R.id.textView3)).setText("Sending message...");
        mMessage = new Message((nameText.toString() + "," + classText.toString() + ","+ count + "," + androidId.toString()).getBytes());
        count += 1;
        Nearby.getMessagesClient(this).publish(mMessage);

    }

}
