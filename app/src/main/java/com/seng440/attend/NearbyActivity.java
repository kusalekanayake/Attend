package com.seng440.attend;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;

public class NearbyActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();

    int count = 0;
    String advertiserEndpointId;
    private GoogleApiClient mGoogleApiClient;

    Message mMessage;
//    MessageListener mMessageListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d("FOUND MESSAGE", "Found message: " + new String(message.getContent()));
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

    }

    public void startConnecting(android.view.View view) {
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
        Nearby.getMessagesClient(this).publish(mMessage);
//        Nearby.getMessagesClient();
    }

    public void startSendingMessage(android.view.View view) {
        count++;
        mMessage = new Message(("Hello World - " + count).getBytes());
        Nearby.getMessagesClient(this).publish(mMessage);

    }

    MessageListener mMessageListener = new MessageListener() {
        /**
         * Called when a message is discovered nearby.
         */
        @Override
        public void onFound(final Message message) {


            TextView textElement;
            textElement = (TextView)findViewById(R.id.textView3);
            textElement.setText(message.toString());

            Log.i(TAG, "Found message: " + message);
        }

        /**
         * Called when the Bluetooth Low Energy (BLE) signal associated with a message changes.
         *
         * This is currently only called for BLE beacon messages.
         *
         * For example, this is called when we see the first BLE advertisement
         * frame associated with a message; or when we see subsequent frames with
         * significantly different received signal strength indicator (RSSI)
         * readings.
         *
         * For more information, see the MessageListener Javadocs.
         */
        @Override
        public void onBleSignalChanged(final Message message, final BleSignal bleSignal) {
            Log.i(TAG, "Message: " + message + " has new BLE signal information: " + bleSignal);
        }

        /**
         * Called when Nearby's estimate of the distance to a message changes.
         *
         * This is currently only called for BLE beacon messages.
         *
         * For more information, see the MessageListener Javadocs.
         */
        @Override
        public void onDistanceChanged(final Message message, final Distance distance) {
            Log.i(TAG, "Distance changed, message: " + message + ", new distance: " + distance);
        }

        /**
         * Called when a message is no longer detectable nearby.
         */
        @Override
        public void onLost(final Message message) {
            Log.i(TAG, "Lost message: " + message);
        }
    };
}
