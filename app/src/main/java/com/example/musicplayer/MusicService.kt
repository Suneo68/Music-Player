package com.example.musicplayer

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.musicplayer.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.PlayerActivity.Companion.musicService
import com.example.musicplayer.PlayerActivity.Companion.nowPlayingId
import com.example.musicplayer.PlayerActivity.Companion.songPosition
import com.example.musicplayer.R.drawable

class MusicService : Service() {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer?= null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable
   override fun onBind(intent: Intent?): IBinder {

       mediaSession = MediaSessionCompat(applicationContext,"My Music")

       mediaSession.isActive = true
       return myBinder
   }


    inner class MyBinder: Binder(){
        fun currentService() : MusicService {
           return this@MusicService
       }
   }

    fun showNotification(playPauseBtn: Int){

        val prevIntent = Intent(applicationContext, NotificationReceiver::class.java)
            .setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(applicationContext, 0 ,
                                                prevIntent, PendingIntent.FLAG_UPDATE_CURRENT )
        val playIntent = Intent(applicationContext, NotificationReceiver::class.java)
            .setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(applicationContext, 0 ,
            playIntent, PendingIntent.FLAG_UPDATE_CURRENT )
        val nextIntent = Intent(applicationContext, NotificationReceiver::class.java)
            .setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(applicationContext, 0 ,
            nextIntent, PendingIntent.FLAG_UPDATE_CURRENT )
        val exitIntent = Intent(applicationContext, NotificationReceiver::class.java)
            .setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(applicationContext, 0 ,
            exitIntent, PendingIntent.FLAG_UPDATE_CURRENT )

        val imgArt = getImgArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if(imgArt != null){
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, drawable.music)
        }

        val notification = NotificationCompat.Builder(applicationContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].album)
            .setSmallIcon(drawable.single_music_icon)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(drawable.back_icon,"Previous", prevPendingIntent)
            .addAction(playPauseBtn,"Play", playPendingIntent)
            .addAction(drawable.next_icon,"Next", nextPendingIntent)
            .addAction(drawable.exitapp_icon,"Exit", exitPendingIntent)
            .build()


//        val intent = Intent(this, MusicService::class.java)
//        startForegroundService(intent)
        startForeground(13, notification)
        Log.d("NotificationDebug", "Notification created successfully.")

    }

    fun createMeidaPlayer(){
        try{
            if(musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.playPausePABtn.setIconResource(drawable.pause_icon)
            musicService?.showNotification(drawable.pause_icon)
            PlayerActivity.binding.tvSeekBarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = mediaPlayer!!.duration
            nowPlayingId = musicListPA[songPosition].id
        } catch (e: Exception){ return}
    }
    //Thanh seekbar o Player
    fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
}