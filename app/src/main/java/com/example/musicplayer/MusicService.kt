package com.example.musicplayer

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MusicService : Service() {

 //   private var myBinder = MyBinder()
   // var mediaPlayer
   override fun onBind(intent: Intent?): IBinder? {
       return null
   }
   // inner class MyBinder: Binder(){
       // fun currentService() : MusicService {
      //      return this@MusicService
    //    }
   // }

}