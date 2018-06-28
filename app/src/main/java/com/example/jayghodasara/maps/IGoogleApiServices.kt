package com.example.jayghodasara.maps

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IGoogleApiServices{

@GET
fun getnearbyplaces(@Url url:String): Call<POJO>
}