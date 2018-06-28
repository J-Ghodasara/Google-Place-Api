package com.example.jayghodasara.maps

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
import android.net.Uri
import retrofit2.Call
import retrofit2.Response
import java.util.*


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

    fun Buildlocationcallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                loc = p0!!.locations[p0.locations.size - 1]

                latlng = LatLng(loc.latitude, loc.longitude)
                if (myLocation != null) {
                    myLocation!!.remove()
                }

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


        })

        nearby.setOnClickListener(View.OnClickListener {

            var nearbyadd: String = address.text.toString()

            nearByplace(nearbyadd)
        })
    }

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


    fun geturl(lat: Double, lng: Double, nearbyplace: String): String {

        var googleplaceurl: StringBuilder = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googleplaceurl.append("location=" + lat + "," + lng)
        googleplaceurl.append("&radius=" + 10000)
        googleplaceurl.append("&type=" + nearbyplace)
        googleplaceurl.append("&sensor=true")
        googleplaceurl.append("&key=" + "AIzaSyBK91TnH6szzbG3QgBpivMdz_VSuTet1JM")

        return googleplaceurl.toString()
    }

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
