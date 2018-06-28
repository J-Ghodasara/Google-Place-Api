package com.example.jayghodasara.maps

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.android.gms.location.ActivityRecognitionClient
import android.app.PendingIntent
import android.view.View
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import android.widget.Toast
import com.example.jayghodasara.maps.BackgroundDetectedService.LocalBinder


class BackgroundDetectedService: Service(){

    private var mIntentService: Intent? = null
    private var mPendingIntent: PendingIntent? = null
    private var mActivityRecognitionClient: ActivityRecognitionClient? = null
    var mBinder:Binder= LocalBinder()


    inner class LocalBinder : Binder() {
        fun getServerInstance():BackgroundDetectedService{
            return this@BackgroundDetectedService
        }
    }

    override fun onCreate() {
        super.onCreate()
        mActivityRecognitionClient= ActivityRecognitionClient(this)
        mIntentService= Intent(this,DetectedActivitiesService::class.java)
        mPendingIntent= PendingIntent.getService(this,1,mIntentService,PendingIntent.FLAG_UPDATE_CURRENT)
        requestActivityHandler()
    }

    fun requestActivityHandler(){
        var task:Task<Void> = mActivityRecognitionClient!!.requestActivityUpdates(5000,mPendingIntent)

        task.addOnSuccessListener {
            Toast.makeText(applicationContext,
                    "Successfully requested activity updates",
                    Toast.LENGTH_SHORT)
                    .show()
        }



        task.addOnFailureListener{

            Toast.makeText(applicationContext,
                    "Requesting activity updates failed to start",
                    Toast.LENGTH_SHORT)
                    .show()

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         super.onStartCommand(intent, flags, startId)
         return  START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
          return mBinder
    }

}