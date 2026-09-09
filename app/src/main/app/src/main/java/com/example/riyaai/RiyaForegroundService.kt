package com.example.riyaai

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class RiyaForegroundService : Service() {

    private var isRecording = false
    private var recordingThread: Thread? = null
    private var audioRecord: AudioRecord? = null

    // API Key integrated
    private val OPENAI_API_KEY = "AQ.Ab8RN6K8GhzP5pIRD9Go-gjoGRCSc-yj6NUtWY0E0Iv6REvUFA"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, "riya_channel_id")
            .setContentTitle("Riya AI is Active")
            .setContentText("Microphone background me sun raha hai aur AI connected hai...")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()

        startForeground(1, notification)

        startAudioRecording()

        return START_STICKY
    }

    private fun startAudioRecording() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val sampleRate = 16000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            audioRecord?.startRecording()
            isRecording = true

            recordingThread = Thread {
                val data = ByteArray(bufferSize)
                while (isRecording) {
                    val read = audioRecord?.read(data, 0, data.size) ?: 0
                    if (read > 0) {
                        // Audio chunk captured
                    }
                }
            }
            recordingThread?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendTextToOpenAI(promptText: String) {
        Thread {
            try {
                val url = URL("https://api.openai.com/v1/chat/completions")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Authorization", "Bearer $OPENAI_API_KEY")
                conn.doOutput = true

                val jsonBody = """
                    {
                        "model": "gpt-3.5-turbo",
                        "messages": [{"role": "user", "content": "$promptText"}]
                    }
                """.trimIndent()

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(jsonBody)
                writer.flush()
                writer.close()

                val responseCode = conn.responseCode
                Log.d("RiyaAI", "OpenAI Response Code: $responseCode")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
        recordingThread = null
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
