package com.example.myweather.ui.main

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.myweather.*
import org.json.JSONObject
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MonthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MonthFragment : Fragment(),Ivolley {
    private var param1: String? = null
    private var param2: String? = null
    var month : TextView? = null
    var username : TextView? = null
    var monthList : LinearLayout? = null
    private fun apiCall(){
        VolleyReq(requireContext(), this)
            .getRequest("https://api.openweathermap.org/data/2.5/forecast?q=London,us&appid=71d265bf54669a5ebcfde439ce0e00ba")
    }

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
        val view: View = inflater.inflate(R.layout.fragment_month, container, false)
        month = view.findViewById(R.id.month)
        username = view.findViewById(R.id.username)
        monthList = view.findViewById(R.id.monthList)
        username!!.text ="Hello "+MainActivity.username+"!"
        VolleyReq(requireContext(), this)
            .getRequest("https://api.openweathermap.org/data/2.5/forecast?lat="+MainActivity.lat+"&lon="+MainActivity.lng+"&appid=71d265bf54669a5ebcfde439ce0e00ba")
        return view
    }

    override fun onStart() {
        super.onStart()
        username!!.text ="Hello "+MainActivity.username+"!"
        apiCall()
    }
    override fun onResponse(response: JSONObject) {
//        Toast.makeText(requireContext(), "" + response.getJSONArray("list").getJSONObject(0).toString(), Toast.LENGTH_LONG).show()
        var List = response.getJSONArray("list")
        month!!.text = "Here is how this week's forecast is"
        month!!.textSize = 18F
        for (i in 0 until List.length() step 8){
            var newList = LinearLayout(this.requireContext())
            newList.orientation = LinearLayout.VERTICAL
            newList.textAlignment = LinearLayout.TEXT_ALIGNMENT_CENTER
            var listItem1 = TextView(this.requireContext())
            var listItem2 = TextView(this.requireContext())
            var listItem3 = TextView(this.requireContext())
            if(SettingsActivity.tempUnits==0){
                listItem1.text = (response.getJSONArray("list").getJSONObject(i).getJSONObject("main").getString("temp").toFloat()-273.15).roundToInt().toString()+"°C"
            }
            else{
                listItem1.text = (((response.getJSONArray("list").getJSONObject(i).getJSONObject("main").getString("temp").toFloat()-273.15)*9/5)+32).roundToInt().toString()+"°F"
            }
            listItem1.textSize = 48F
            listItem1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            listItem1.setPadding(20,40,30,10)
            listItem2.text = response.getJSONArray("list").getJSONObject(i).getString("dt_txt").toString().subSequence(0,10)
            listItem2.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            listItem3.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            listItem3.typeface = Typeface.DEFAULT_BOLD
            listItem3.text = response.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").toString()
            newList.addView(listItem1)
            newList.addView(listItem2)
            newList.addView(listItem3)
            monthList!!.addView(newList)

        }
    }

    override fun onError(error: String) {
        Toast.makeText(requireContext(), "Error retrieving data", Toast.LENGTH_LONG).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MonthFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MonthFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}