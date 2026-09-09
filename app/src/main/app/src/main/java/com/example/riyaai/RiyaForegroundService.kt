package com.example.riyaai

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class RiyaForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, "riya_channel_id")
            .setContentTitle("Riya AI is Active")
            .setContentText("24x7 background me OpenAI ke sath jud chuki hai...")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()

        startForeground(1, notification)

        // Background me OpenAI API ko call karenge
        callOpenAIAPI()

        return START_STICKY
    }

    private fun callOpenAIAPI() {
        // Teri OpenAI API Key yahan integrated hai
        val apiKey = "Sk-proj-grFMzzxVsnRbhdhWDP5LdyZSzSft3XMfCHh4p3YcHJq2woUNvlIVIZxpzluUlUJRfTY6ny1fjgT3BlbkFJgPkpAs-XjY-2IUVaYGE3cp2k3fzmlwtCddykbNfu5GxOxRaMk_KLmHY99JdODj7FBLcs-csuAA"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://api.openai.com/v1/chat/completions")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $apiKey")
                connection.doOutput = true

                val jsonBody = """
                    {
                        "model": "gpt-4o-mini",
                        "messages": [{"role": "user", "content": "Hello Riya, are you active?"}]
                    }
                """.trimIndent()

                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(jsonBody)
                outputStreamWriter.flush()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // API connection successful aur AI ka response mil gaya
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "riya_channel_id",
                "Riya AI Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }
}
