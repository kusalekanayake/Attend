package com.seng440.attend;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceTransitionsIntentService extends IntentService{


    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionService");
    }


    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d("ENTERING.............", "onHandleIntent: ");
            // do something
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d("EXITING.............", "onHandleIntent: ");
            // do something else
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Log.d("DWELLING.............", "onHandleIntent: ");
        }
    }


}
