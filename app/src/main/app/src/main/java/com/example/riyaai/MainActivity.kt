package com.example.riyaai

import android.app.Activity
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            runOnUiThread {
                showErrorOnScreen("CRASH LOG:\n\n" + throwable.stackTraceToString())
            }
        }

        super.onCreate(savedInstanceState)

        try {
            val textView = TextView(this).apply {
                text = "Riya AI Working!"
                textSize = 22f
                setPadding(50, 100, 50, 50)
            }
            setContentView(textView)
        } catch (e: Throwable) {
            showErrorOnScreen("ONCREATE ERROR:\n\n" + e.stackTraceToString())
        }
    }

    private fun showErrorOnScreen(message: String) {
        val scrollView = ScrollView(this)
        val textView = TextView(this).apply {
            text = message
            textSize = 14f
            setPadding(30, 60, 30, 30)
        }
        scrollView.addView(textView)
        setContentView(scrollView)
    }
}
