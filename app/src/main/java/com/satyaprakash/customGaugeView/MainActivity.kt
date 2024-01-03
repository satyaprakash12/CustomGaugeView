package com.satyaprakash.customGaugeView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
   private lateinit var vitalGauge:CustomGaugeView
    private lateinit var button:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vitalGauge=findViewById<CustomGaugeView>(R.id.gauge)
        button=findViewById(R.id.button)
        var c=0
        button.setOnClickListener(
        ){
            vitalGauge.setProgressBar(c, true)
            if(c >=100) c= 0
            c += 1
        }
    }
}