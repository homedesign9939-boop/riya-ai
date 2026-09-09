package com.example.riyaai

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.riyaai.R

class MainActivity : AppCompatActivity() {
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnToggle = findViewById<Button>(R.id.btnToggleService)

        btnToggle.setOnClickListener {
            val serviceIntent = Intent(this, RiyaForegroundService::class.java)
            if (isRunning) {
                // Service ko band karte hain (Mic OFF)
                stopService(serviceIntent)
                btnToggle.text = "Start Riya AI (Mic ON)"
                isRunning = false
            } else {
                // Service ko chalu karte hain (Mic ON)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
                btnToggle.text = "Stop Riya AI (Mic OFF)"
                isRunning = true
            }
        }
    }
}
