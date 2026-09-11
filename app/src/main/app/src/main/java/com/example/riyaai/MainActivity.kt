package com.example.riyaai

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val textView = TextView(this).apply {
            text = "Riya AI Working!"
            textSize = 22f
            setPadding(50, 100, 50, 50)
        }
        
        setContentView(textView)
    }
}
