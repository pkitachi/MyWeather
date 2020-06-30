package com.example.myweather

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*

class SettingsActivity : AppCompatActivity() {

    companion object{
        var tempUnits : Int? = null
    }
    private val sharedPrefFile = "saveFile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        var editText: EditText = findViewById(R.id.username)
        var button: Button = findViewById(R.id.saveButton)
        var radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        editText.setText(MainActivity.username)
        tempUnits = sharedPreferences.getString("tempUnits","0")!!.toInt()
        if(tempUnits!=0){
            radioGroup.check(R.id.fahrenheit)
        }
        button.setOnClickListener {
            MainActivity.username = editText.text.toString()
            val editor: SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("username",MainActivity.username)
            editor.putString("tempUnits",tempUnits.toString())
            editor.apply()
            editor.commit()
            onBackPressed()
        }
        radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                if(radio.text!!.toString().contains("Celsius")){
                    Toast.makeText(applicationContext,"Temperature units changed to Celsius", Toast.LENGTH_SHORT).show()
                    tempUnits = 0
                }
                else{
                    Toast.makeText(applicationContext,"Temperature units changed to Fahrenheit", Toast.LENGTH_SHORT).show()
                    tempUnits=1
                }
            })
    }

}