package com.example.jayghodasara.maps

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject




class Parser
{

    fun getPlace(jsonobj:JSONObject):HashMap<String,String>{

        val googlePlaceMap = HashMap<String,String>()
        var placeName = "--NA--"
        var vicinity = "--NA--"
        var latitude = ""
        var longitude = ""
        var reference = ""

        Log.d("DataParser", "jsonobject =" + jsonobj.toString())


        try {
            if (!jsonobj.isNull("name")) {
                placeName = jsonobj.getString("name")
            }
            if (!jsonobj.isNull("vicinity")) {
                vicinity = jsonobj.getString("vicinity")
            }

            latitude = jsonobj.getJSONObject("geometry").getJSONObject("location").getString("destiLat")
            longitude = jsonobj.getJSONObject("geometry").getJSONObject("location").getString("lng")

            reference = jsonobj.getString("reference")

            googlePlaceMap.put("place_name", placeName)
            googlePlaceMap.put("vicinity", vicinity)
            googlePlaceMap.put("destiLat", latitude)
            googlePlaceMap.put("lng", longitude)
            googlePlaceMap.put("reference", reference)


        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return googlePlaceMap
    }


    fun getPlaces(jsonarr:JSONArray):List<HashMap<String,String>>{

        var count:Int= jsonarr.length()

        var list:ArrayList<HashMap<String,String>> = ArrayList()
        var hashmap:HashMap<String,String>?=null

        for(i in 0 until count){
            try{
                hashmap= getPlace(jsonarr[i] as JSONObject)
                list.add(hashmap)

            }catch (e:JSONException){
                e.printStackTrace()
            }
        }
        return list
    }

    fun parse(jsondata:String):List<HashMap<String,String>>{

        var jsonarray: JSONArray? = null
        var jsonobj:JSONObject

        try{
            jsonobj= JSONObject(jsondata)
            jsonarray=jsonobj.getJSONArray("results")

        }catch (e:JSONException){
            e.printStackTrace()
        }

        return getPlaces(jsonarray!!)
    }
}