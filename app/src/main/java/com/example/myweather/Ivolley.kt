package com.example.myweather

import org.json.JSONObject

interface Ivolley {
    fun onResponse(response:JSONObject){

    }
    fun onError(error:String){

    }
}