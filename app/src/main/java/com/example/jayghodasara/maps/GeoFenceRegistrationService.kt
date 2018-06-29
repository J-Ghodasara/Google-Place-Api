package com.example.jayghodasara.maps

import android.app.IntentService
import android.content.Intent
import android.util.Log

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent



class GeoFenceRegistrationService: IntentService(""){
    override fun onHandleIntent(intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.d("Geofence", "GeofencingEvent error " + geofencingEvent.errorCode)
        } else {
            val transaction = geofencingEvent.geofenceTransition
            val geofences = geofencingEvent.triggeringGeofences
            val geofence = geofences[0]
            if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER && geofence.requestId == "STAN_UNI") {
                Log.d("Geofence", "You are inside Stanford University")
            } else {
                Log.d("Geofence", "You are outside your location")
            }
        }
    }


}