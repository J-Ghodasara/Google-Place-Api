package com.example.jayghodasara.maps

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient{

    var retrofit:Retrofit?=null
//
//    fun getClient(baseUrl:String):Retrofit{
//        if(retrofit==null){
//
//            retrofit=Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build()
//        }
//        return retrofit!!
//
//    }


    fun getDirectionClient(baseurl:String):Retrofit{
        if(retrofit==null){

            retrofit=Retrofit.Builder()
                    .baseUrl(baseurl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
        }
        return retrofit!!
    }
}