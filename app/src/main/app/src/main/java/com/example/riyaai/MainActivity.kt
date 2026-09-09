package com.example.riyaai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private var isRunning = false
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnToggle = findViewById<Button>(R.id.btnToggleService)

        btnToggle.setOnClickListener {
            if (isRunning) {
                stopRiyaService()
                btnToggle.text = "Start Riya AI (Mic ON)"
                isRunning = false
            } else {
                // Check for microphone permission first
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    startRiyaService()
                    btnToggle.text = "Stop Riya AI (Mic OFF)"
                    isRunning = true
                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        REQUEST_RECORD_AUDIO_PERMISSION
                    )
                }
            }
        }
    }

    private fun startRiyaService() {
        val serviceIntent = Intent(this, RiyaForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        Toast.makeText(this, "Riya AI Started", Toast.LENGTH_SHORT).show()
    }

    private fun stopRiyaService() {
        val serviceIntent = Intent(this, RiyaForegroundService::class.java)
        stopService(serviceIntent)
        Toast.makeText(this, "Riya AI Stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startRiyaService()
                val btnToggle = findViewById<Button>(R.id.btnToggleService)
                btnToggle.text = "Stop Riya AI (Mic OFF)"
                isRunning = true
            } else {
                Toast.makeText(this, "Microphone permission is required!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
