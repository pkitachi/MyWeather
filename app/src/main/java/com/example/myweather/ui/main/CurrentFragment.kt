package com.example.myweather.ui.main

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.myweather.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_current.*
import org.json.JSONObject
import java.net.URL
import java.net.HttpURLConnection
import kotlin.math.roundToInt


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CurrentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentFragment : Fragment(), Ivolley {
    private var param1: String? = null
    private var param2: String? = null
    var temp: TextView? = null
    var tempInfo: TextView? = null
    var weather: TextView? = null
    var username : TextView? = null
    var tempUnits : TextView? = null
    var city : TextView? = null
    var windSpeed : TextView? = null


    private fun apiCall() {
        if (MainActivity.lat != null && MainActivity.lng != null) {
            VolleyReq(requireContext(), this)
                .getRequest(
                    "https://api.openweathermap.org/data/2.5/weather?lat=" + MainActivity.lat + "&lon=" + MainActivity.lng + "&appid=71d265bf54669a5ebcfde439ce0e00ba"
                )

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        apiCall()
    }
    override fun onResponse(response: JSONObject) {
//        Toast.makeText(requireContext(), "PK " + response.toString(), Toast.LENGTH_LONG).show()
        MainActivity.curWeather = response
        refresh()
    }

    override fun onError(error: String) {
        Toast.makeText(requireContext(), "" + "Error Refreshing Data", Toast.LENGTH_LONG).show()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_current, container, false)

        username = view.findViewById(R.id.username)
        username!!.text = "Hello "+MainActivity.username+"!"
        temp = view.findViewById<TextView>(R.id.temp)
        tempInfo = view.findViewById<TextView>(R.id.tempInfo)
        weather = view.findViewById<TextView>(R.id.weather)
        tempUnits = view.findViewById(R.id.tempUnits)
        city = view.findViewById(R.id.city)
        windSpeed = view.findViewById(R.id.windSpeed)
        return view
    }

    override fun onStart() {
        super.onStart()
        username!!.text = "Hello "+MainActivity.username+"!"
        refresh()
    }
    private fun refresh(){
        try{
            val resp = MainActivity.curWeather
            if(SettingsActivity.tempUnits == 0){
                temp!!.text = (resp.getJSONObject("main").getString("temp").toFloat()-273.15).roundToInt().toString()
                tempInfo!!.text = "High: "+(resp.getJSONObject("main").getString("temp_max").toFloat()-273.15).roundToInt().toString()+"° "+" Low: "+(resp.getJSONObject("main").getString("temp_min").toFloat()-273.15).roundToInt().toString()+"°"
                tempUnits!!.text="°C"
            }
            else{
                temp!!.text = (((resp.getJSONObject("main").getString("temp").toFloat()-273.15)*9/5)+32).roundToInt().toString()
                tempInfo!!.text = "High: "+(((resp.getJSONObject("main").getString("temp_max").toFloat()-273.15)*9/5)+32).roundToInt().toString()+"° "+" Low: "+(((resp.getJSONObject("main").getString("temp_min").toFloat()-273.15)*9/5)+32).roundToInt().toString()+"°"
                tempUnits!!.text="°F"
            }
            city!!.text = resp.getString("name").toString()
            weather!!.text = resp.getJSONArray("weather").getJSONObject(0).getString("main").toString()
            windSpeed!!.text = "Wind Speed : "+resp.getJSONObject("wind").getString("speed").toString()

        }
        catch (e:Exception){}
    }
    companion object {


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CurrentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CurrentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}