package com.example.musicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class ApplicationClass:Application() {
    companion object {
        const val CHANNEL_ID = "channel1"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
    }
    override fun onCreate() {
        super.onCreate()
        val notificationChannel = NotificationChannel(CHANNEL_ID,"Bài hát đang phát", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.description = "Kenh thong bao bai hat dang phat"

//        notificationChannel.importance = NotificationManager.IMPORTANCE_HIGH
//        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}