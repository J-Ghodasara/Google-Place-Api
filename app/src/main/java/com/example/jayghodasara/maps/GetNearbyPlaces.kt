package com.example.jayghodasara.maps

import android.content.Context
import android.os.AsyncTask
import com.google.android.gms.maps.GoogleMap
import java.io.IOException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions



class GetNearbyPlaces : AsyncTask<Any,String,String>() {


    var Googledata:String?=null
    lateinit var mMap:GoogleMap
    var url:String?=null
    var main:MapsActivity=MapsActivity()
    override fun doInBackground(vararg params: Any?): String {

        var context = params[0]



        mMap=main.getmMap()
        url=main.geturl()
        var downloadurl:Downloadurl= Downloadurl()

        try{
            Googledata=downloadurl.readUrl(url!!)

        }catch(e:IOException){
            e.printStackTrace()
        }

        return Googledata.toString()

    }

    override fun onPostExecute(result: String?) {
        var listofhash:List<HashMap<String,String>>
        var parser:Parser= Parser()
        listofhash=parser.parse(result!!)

        showplaces(listofhash)
    }


    fun showplaces(list:List<HashMap<String,String>>){

        for (i in 0 until list.size) {
            val markerOptions = MarkerOptions()
            val googlePlace = list[i]

            val placeName = googlePlace.get("place_name")
            val vicinity = googlePlace.get("vicinity")
            val lat = java.lang.Double.parseDouble(googlePlace.get("lat"))
            val lng = java.lang.Double.parseDouble(googlePlace.get("lng"))

            val latLng = LatLng(lat, lng)
            markerOptions.position(latLng)
            markerOptions.title(placeName + " : " + vicinity)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

            mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10f))
        }
    }
}