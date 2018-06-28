package com.example.jayghodasara.maps

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, LocationListener {


    override fun onConnectionSuspended(p0: Int) {

    }

    lateinit var locationReq: LocationRequest
    private lateinit var mMap: GoogleMap
    lateinit var googleClient: GoogleApiClient
    lateinit var loc: Location
    var myLocation: Marker? = null
    lateinit var geocoder: Geocoder
    var latlng: LatLng? = null
    lateinit var destination: LatLng
    var latitude: Double? = null
    var longitude: Double? = null
    lateinit var url: String
    lateinit var locationCallback: LocationCallback
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var destinationMarker: Marker
    var mCount: Int = 1
    var lCount: Int = 1
    var lat: Double? = null
    var lon: Double? = null
    lateinit var iGoogleApiServices: IGoogleApiServices
    lateinit var pojo: POJO
    var GEOFENCE_ID_STAN_UNI= "My_Location"
    var GEOFENCE_RADIUS_IN_METERS= 100
    var pendingIntent:PendingIntent? = null
    lateinit var broadcastReceiver:BroadcastReceiver


    companion object {
    val AREA_LANDMARKS:HashMap<String,LatLng> = HashMap<String,LatLng>()

}
    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i("failed", "in")
    }


    override fun onConnected(p0: Bundle?) {


    }



    fun BuildLocationreq() {
        locationReq = LocationRequest()
        locationReq.interval = 1000
        Log.i("Called", "in onconnected")
        locationReq.fastestInterval = 1000
        locationReq.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    private fun geoFencingReq():GeofencingRequest{
        var builder:GeofencingRequest.Builder= GeofencingRequest.Builder()
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        builder.addGeofence(getGeofence())
        return builder.build()
    }

    fun getGeofence(): Geofence? {
        var latlon:LatLng= AREA_LANDMARKS[GEOFENCE_ID_STAN_UNI]!!

        var geofence:Geofence = Geofence.Builder()
        .setRequestId(GEOFENCE_ID_STAN_UNI)
        .setCircularRegion(latlon.latitude,latlon.longitude, GEOFENCE_RADIUS_IN_METERS.toFloat())
        .setNotificationResponsiveness(1000)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(1000000)
        .build()
        return geofence




    }

    fun startGeoFencing(){
        pendingIntent=pendingIntent()

        try{

            LocationServices.GeofencingApi.addGeofences(googleClient,geoFencingReq(),pendingIntent).setResultCallback(object : ResultCallback<Status>{
                override fun onResult(p0: Status) {
                    Toast.makeText(applicationContext,"Geofencing Started",Toast.LENGTH_LONG).show()
                }

            })
        }catch (e:SecurityException){
            e.printStackTrace()
        }
    }

    fun pendingIntent():PendingIntent{
        if(pendingIntent!=null){
            return pendingIntent as PendingIntent
        }
        var intent:Intent= Intent(this,GeoFenceRegistrationService::class.java)

        return PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun Buildlocationcallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                loc = p0!!.locations[p0.locations.size - 1]

                latlng = LatLng(loc.latitude, loc.longitude)
                if (myLocation != null) {
                    myLocation!!.remove()
                }
                AREA_LANDMARKS[GEOFENCE_ID_STAN_UNI] = latlng!!

                var markerOptions: MarkerOptions = MarkerOptions()
                markerOptions.position(latlng!!)
                markerOptions.title("My Location")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                myLocation = mMap.addMarker(markerOptions)
                Log.i("Move", "animated")
                if (mCount == 1) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))
                    mCount++
                }

            }
        }


    }

//    override fun onStart() {
//        super.onStart()
//        googleClient.reconnect()
//    }

    override fun onStop() {
        super.onStop()
        googleClient.disconnect()
    }


    override fun onLocationChanged(location: Location?) {
    }


    fun checklocationpermission(): Boolean {

        return if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 99)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 99)
            }
            false

        } else
            true

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {

            99 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleClient != null) {
                            createClient()
                        }
                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(applicationContext, " Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        iGoogleApiServices = RetrofitClient.getClient("https://maps.google.com/").create(IGoogleApiServices::class.java)

        var count = 1
        BuildLocationreq()
        Buildlocationcallback()
        //startGeoFencing()
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(locationReq, locationCallback, Looper.myLooper())
        }

        geocoder = Geocoder(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checklocationpermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        change.setOnClickListener(View.OnClickListener {
            if (count == 1) {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                count = 2
            } else {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                count = 1
            }

        })


        recognize.setOnClickListener(View.OnClickListener {

            startTracking()
            Toast.makeText(applicationContext,"Recognizing",Toast.LENGTH_SHORT).show()
        })

        btn.setOnClickListener(View.OnClickListener {
            var add: String = address.text.toString()

            var list: List<Address> = geocoder.getFromLocationName(add, 1)
            var address: Address = list[0]

            lat = address.latitude
            lon = address.longitude
            destination = LatLng(lat!!, lon!!)

            var markerOptions: MarkerOptions = MarkerOptions()
            markerOptions.title(add)
            markerOptions.position(destination)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

            destinationMarker = mMap.addMarker(markerOptions)
            if (lCount == 1) {
                mMap.animateCamera(CameraUpdateFactory.zoomBy(20F))
                mMap.animateCamera(CameraUpdateFactory.newLatLng(destination))
                lCount++
            }

            //For GeoFencing--> Start
            startGeoFencing()
            //For GeoFencing--> End

        })



        broadcastReceiver= object:BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent!!.action == "activity_intent"){
                    var type:Int= intent.getIntExtra("type",-1)
                    var confidence= intent.getIntExtra("confidence",0)
                    handleUserActi(type,confidence)
            }

        }


    }
        startTracking()

        //For Nearby Places--> Start
        nearby.setOnClickListener(View.OnClickListener {

            var nearbyadd: String = address.text.toString()

            nearByplace(nearbyadd)
        })
        //For Nearby Places--> End

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("activity_intent"))
    }

    fun handleUserActi( type:Int, confidence:Int){
        when(type){

            DetectedActivity.IN_VEHICLE ->{
                Toast.makeText(applicationContext,"In vehicle",Toast.LENGTH_SHORT).show()

            }

            DetectedActivity.STILL ->{
                Toast.makeText(applicationContext,"Still",Toast.LENGTH_SHORT).show()
            }

            DetectedActivity.ON_FOOT ->{
                Toast.makeText(applicationContext,"On Foot",Toast.LENGTH_SHORT).show()
            }

            DetectedActivity.TILTING ->{
                Toast.makeText(applicationContext,"Tilting",Toast.LENGTH_SHORT).show()
            }

            DetectedActivity.UNKNOWN ->{
                Toast.makeText(applicationContext,"Unknown",Toast.LENGTH_SHORT).show()
            }

            DetectedActivity.ON_BICYCLE ->{
                Toast.makeText(applicationContext,"On Bicycle",Toast.LENGTH_SHORT).show()
            }

            DetectedActivity.RUNNING ->{
                Toast.makeText(applicationContext,"Running",Toast.LENGTH_SHORT).show()
            }



        }

    }

    fun startTracking(){
        val intent1 = Intent(applicationContext, BackgroundDetectedService::class.java)
        startService(intent1)
    }


    //For Nearby Places--> Start
    fun nearByplace(typeplace: String) {
        mMap.clear()
        val url = geturl(loc.latitude, loc.longitude, typeplace)
        iGoogleApiServices.getnearbyplaces(url).enqueue(object : retrofit2.Callback<POJO> {
            override fun onFailure(call: Call<POJO>?, t: Throwable?) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<POJO>?, response: Response<POJO>?) {
                pojo = response!!.body()!!
                var latlng2: LatLng? = null
                if (response!!.isSuccessful) {
                    for (i in 0 until response.body()!!.results!!.size) {
                        val markerOptions: MarkerOptions = MarkerOptions()
                        val googlePlace = response.body()!!.results[i]
                        val lat = googlePlace.geometry.location.lat
                        val lng = googlePlace.geometry.location.lng
                        val placename = googlePlace.name
                        latlng2 = LatLng(lat, lng)

                        markerOptions.position(latlng2)
                        markerOptions.title(placename)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        mMap.addMarker(markerOptions)

                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng2))
                }
            }


        })
    }

    //For Nearby Places--> End

    override fun onMarkerClick(p0: Marker?): Boolean {

        when {
            p0!! == myLocation -> {
                return false
            }
            p0 == destinationMarker -> {
                val uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", loc.latitude, loc.longitude, "Home Sweet Home", lat, lon, "Travel HERE")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.`package` = "com.google.android.apps.maps"
                startActivity(intent)
            }
            else -> {
                return true
            }
        }

        return true
    }

    fun getmMap(): GoogleMap {
        return mMap
    }

    fun geturl(): String {
        return url
    }

    //For Nearby Places--> Start
    fun geturl(lat: Double, lng: Double, nearbyplace: String): String {

        var googleplaceurl: StringBuilder = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googleplaceurl.append("location=" + lat + "," + lng)
        googleplaceurl.append("&radius=" + 10000)
        googleplaceurl.append("&type=" + nearbyplace)
        googleplaceurl.append("&sensor=true")
        googleplaceurl.append("&key=" + "AIzaSyBK91TnH6szzbG3QgBpivMdz_VSuTet1JM")

        return googleplaceurl.toString()
    }
    //For Nearby Places--> End

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a myLocation near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        Toast.makeText(applicationContext, "OnMapReady", Toast.LENGTH_LONG).show()

        if (ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            createClient()
            mMap.isMyLocationEnabled = true



        }


    }

    fun createClient() {
        synchronized(this) {
            Log.i("Client", "created")
            googleClient = GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
            googleClient.connect()
        }
    }
}
