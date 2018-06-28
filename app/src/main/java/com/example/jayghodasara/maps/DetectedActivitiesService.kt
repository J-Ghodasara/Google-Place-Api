package com.example.jayghodasara.maps

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

class DetectedActivitiesService: IntentService(""){
    override fun onHandleIntent(intent: Intent?) {

        var activityrec:ActivityRecognitionResult= ActivityRecognitionResult.extractResult(intent)

        var arraylist:ArrayList<DetectedActivity> = activityrec.probableActivities as ArrayList<DetectedActivity>

        for(activity in arraylist )
        broadcastActivity(activity)
    }

    fun broadcastActivity(activity: DetectedActivity){
        var intent:Intent= Intent("activity_intent")
        intent.putExtra("type",activity.type)
        intent.putExtra("confidence",activity.confidence)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


}