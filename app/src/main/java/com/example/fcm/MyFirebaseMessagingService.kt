package com.example.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Build.*
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService:FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            val title=it.title
            val body=it.body

            sendNotification(title ?: "Default Title", body ?: "Default Body")
        }
        if (remoteMessage.data.isNotEmpty()) {
            val customData = remoteMessage.data["key"] // Server se jo key bheji
            sendNotification("Custom Data", customData ?: "No Data")
        }
    }

    @SuppressLint("ServiceCast")
    private fun sendNotification(title: String, message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default_channel_id"

        // Android Oreo (8.0) aur upar ke liye channel banao
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Intent aur PendingIntent setup
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action ke liye alag PendingIntent (optional)
        val replyIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("action", "reply") // Extra data bhej sakte ho
        }
        val replyPendingIntent = PendingIntent.getActivity(
            this,
            1, // Alag request code for action
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Notification banane ka code
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.bulb) // Chhota icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority
            .setContentIntent(pendingIntent) // Click pe kya hoga
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("FCM Token: $token")
    }

}