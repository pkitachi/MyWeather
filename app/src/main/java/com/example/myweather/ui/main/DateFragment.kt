package com.example.myweather.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.myweather.Ivolley
import com.example.myweather.R
import com.example.myweather.SettingsActivity
import com.example.myweather.VolleyReq
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.math.roundToLong

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DateFragment : Fragment(), Ivolley, AdapterView.OnItemSelectedListener,OnMapReadyCallback{
    private var param1: String? = null
    private var param2: String? = null
    var temp: TextView? = null
    var mapView: MapView? = null
    var location: LatLng = LatLng(28.7041,77.1025)
    var locName: String? = null
    var weather: TextView? = null
    var calendar: CalendarView? = null
    var cityId:Int = 0
    var date:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View =inflater.inflate(R.layout.fragment_date, container, false)
        val spinner: Spinner = view.findViewById(R.id.spinner)
        weather = view.findViewById(R.id.weather)
        mapView = view.findViewById(R.id.mapView)
        calendar = view.findViewById(R.id.calendarView)
        temp = view.findViewById(R.id.temp)
        date = SimpleDateFormat("yyyy-MM-dd").format(Date(Timestamp(System.currentTimeMillis()).time))
        calendar!!.setOnDateChangeListener { view, year, month, dayOfMonth ->

            if(month<9){
                date = ""+year+"-0"+(month+1)+"-"+dayOfMonth
                if(dayOfMonth<9){
                    date = ""+year+"-0"+(month+1)+"-0"+dayOfMonth
                }
            }
            else{
                date = ""+year+"-"+(month+1)+"-"+dayOfMonth
                if(dayOfMonth<9){
                    date = ""+year+"-"+(month+1)+"-0"+dayOfMonth
                }
            }
            Log.d("myTag",""+date)
            apiCall()
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cities_array,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this
        initGoogleMap(savedInstanceState)
        return view
    }
    private fun initGoogleMap(savedInstanceState: Bundle?){
        var mapViewBundle: Bundle? = null
        if(savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView!!.onCreate(mapViewBundle)
        mapView!!.getMapAsync(this)
    }
    private fun apiCall(){
        val cityArr : Array<String> = resources.getStringArray(R.array.cities_array)
//        Toast.makeText(requireContext(),cityArr[cityId],Toast.LENGTH_LONG).show()
        VolleyReq(requireContext(), this)
            .getRequest("https://api.openweathermap.org/data/2.5/forecast?q="+cityArr[cityId]+"&appid=71d265bf54669a5ebcfde439ce0e00ba")
    }
    override fun onResponse(response: JSONObject) {
//        Toast.makeText(requireContext(), "" + response.toString(), Toast.LENGTH_LONG).show()
        val list = response.getJSONArray("list")
        Log.d("myTag2",""+date)
        for (i in 0 until list.length() step 8){
            if(date.toString().equals(response.getJSONArray("list").getJSONObject(i).getString("dt_txt").toString().subSequence(0,10)) )
            {
                if(SettingsActivity.tempUnits==0) {
                    temp!!.text =
                        (response.getJSONArray("list").getJSONObject(i).getJSONObject("main")
                            .getString("temp").toFloat() - 273.15).roundToInt().toString() + "°C"
                }
                else{
                    temp!!.text =
                        (((response.getJSONArray("list").getJSONObject(i).getJSONObject("main")
                            .getString("temp").toFloat() - 273.15)*9/5)+32).roundToInt().toString() + "°F"
                }
                weather!!.text = response.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").toString()
            }
        }

    }
    companion object {
        const val MAPVIEW_BUNDLE_KEY : String = "MapViewBundleKey"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        cityId = position
        apiCall()
        rePosition(position)

    }

    private fun rePosition(cityInt: Int){
        if(cityInt == 0){
            mapView!!.getMapAsync(this)
            location = LatLng(28.7041,77.1025)
            locName = "Delhi"
        }
        else if(cityInt == 1){
            mapView!!.getMapAsync(this)
            location = LatLng(19.0760,72.8777)
            locName="Mumbai"
        }
        else if(cityInt == 2){
            mapView!!.getMapAsync(this)
            location = LatLng(28.5355,77.3910)
            locName = "Noida"
        }

    }

    override fun onMapReady(map: GoogleMap?) {
        map!!.addMarker(MarkerOptions().position(location).title(locName))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10F))
        mapView!!.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle: Bundle? = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if(mapViewBundle == null){
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY,mapViewBundle)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        apiCall()
        mapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

}