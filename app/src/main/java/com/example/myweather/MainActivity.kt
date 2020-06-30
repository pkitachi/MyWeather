package com.example.myweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.myweather.ui.main.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity(), Ivolley {
    companion object {
        var curWeather: JSONObject = JSONObject()
        var username : String = "user"
        var lat:String = ""
        var lng:String = ""
    }
    private val sharedPrefFile = "saveFile"
    var locationGps : Location? = null
    var locationNetwork : Location? = null
    private var hasGPS = false
    private var hasNetwork = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        sectionsPagerAdapter.getItemId(0)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        fab.setOnClickListener { view ->
            openSettings()
        }
        username = sharedPreferences.getString("username","user")!!
        lat = sharedPreferences.getString("lat","")!!
        lng = sharedPreferences.getString("lng","")!!
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocation()
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    100
                )
                Toast.makeText(this,"Please relaunch the App after giving the permission",Toast.LENGTH_LONG).show()
            }
        }

        if(username == "user"){
            openSettings()
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLocation(){
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if( hasGPS || hasNetwork ){
            if(hasGPS){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0F,object:
                    LocationListener{
                    override fun onLocationChanged(location: Location?) {
                        if(location!=null){
                            locationGps = location
                        }
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String?) {}
                    override fun onProviderDisabled(provider: String?) {}
                })
                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(localGpsLocation!=null){
                    locationGps = localGpsLocation
                }
            }
            if(hasNetwork){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0F,object:
                    LocationListener{
                    override fun onLocationChanged(location: Location?) {
                        if(location!=null){
                            locationNetwork = location
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                    override fun onProviderEnabled(provider: String?) {}

                    override fun onProviderDisabled(provider: String?) {}

                })
                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if(localNetworkLocation!=null){
                    locationNetwork = localNetworkLocation
                }
            }
            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    lat = locationNetwork!!.latitude.toString()
                    lng = locationNetwork!!.longitude.toString()
                }else{
                    lat = locationGps!!.latitude.toString()
                    lng = locationGps!!.longitude.toString()
                }
            }
            else if(locationGps!= null){
                lat = locationGps!!.latitude.toString()
                lng = locationGps!!.longitude.toString()
            }
            else if(locationNetwork!= null){
                lat = locationNetwork!!.latitude.toString()
                lng = locationNetwork!!.longitude.toString()
            }
        }
        else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }
    private fun openSettings(){
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putString("lat",lat)
        editor.putString("lng",lng)
        editor.apply()
        editor.commit()
        super.onDestroy()
    }
}