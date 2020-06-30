package com.example.myweather

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class VolleyReq {
    private var mrequestQueue: RequestQueue?=null
    private var context: Context?=null
    private var ivolley: Ivolley?=null
    var imageLoader:ImageLoader?=null

    val requestQueue: RequestQueue
        get(){
            if (mrequestQueue == null)
                mrequestQueue = Volley.newRequestQueue(context!!.applicationContext)
            return mrequestQueue!!
        }
    constructor(context: Context,ivolley: Ivolley){
        this.context = context
        this.ivolley = ivolley
        mrequestQueue = requestQueue
        this.imageLoader = ImageLoader(mrequestQueue,object :ImageLoader.ImageCache{
            private val mCache = LruCache<String,Bitmap>(10)
            override fun getBitmap(url: String?): Bitmap {
                return mCache.get(url)
            }

            override fun putBitmap(url: String?, bitmap: Bitmap?) {
                mCache.put(url,bitmap)
            }

        })
    }
    private constructor(context: Context){
        this.context = context
        mrequestQueue = requestQueue
        this.imageLoader = ImageLoader(mrequestQueue,object :ImageLoader.ImageCache{
            private val mCache = LruCache<String,Bitmap>(10)
            override fun getBitmap(url: String?): Bitmap {
                return mCache.get(url)
            }

            override fun putBitmap(url: String?, bitmap: Bitmap?) {
                mCache.put(url,bitmap)
            }

        })
    }
    fun <T> addToRequestQueue(req: Request<T>){
        requestQueue.add(req);
    }

    fun getRequest(url:String){
        val getRequest = JsonObjectRequest(Request.Method.GET,url,null,Response.Listener { response ->
            ivolley!!.onResponse(response)
        },Response.ErrorListener { error ->
            ivolley!!.onError(error.message!!)
        })

        addToRequestQueue(getRequest)
    }

    companion object {
        private var mInstance: VolleyReq? = null

        @Synchronized
        fun getInstance(context: Context): VolleyReq {
            if (mInstance == null) {
                mInstance = VolleyReq(context)
            }
            return mInstance!!
        }
        @Synchronized
        fun getInstance(context: Context, ivolley: Ivolley): VolleyReq {
            if (mInstance == null) {
                mInstance = VolleyReq(context,ivolley)
            }
            return mInstance!!
        }
    }

}