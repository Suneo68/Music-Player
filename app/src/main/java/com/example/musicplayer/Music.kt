package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.Service
import android.media.MediaMetadataRetriever
import com.example.musicplayer.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.PlayerActivity.Companion.songPosition
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music (val id:String,
                  val title:String,
                  val album:String,
                  val artist:String,
                  val duration: Long = 0,
                  val path:String,
                    val artUri: String)
@SuppressLint("DefaultLocale")
fun formatDuration(duration: Long): String{

    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
            - minutes*TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d",minutes, seconds)
}

fun getImgArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setSongPosition(increment: Boolean){
    if (!PlayerActivity.repeat){
        if(increment)
        {
            if(musicListPA.size - 1 == songPosition)
                songPosition = 0
            else ++songPosition
        } else{
            if(0 == songPosition)
                songPosition = musicListPA.size - 1
            else --songPosition
        }
    }
}
fun exitApplication(){
    if (PlayerActivity.musicService != null) {
        PlayerActivity.musicService!!.stopForeground(Service.STOP_FOREGROUND_REMOVE)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null
    }
    exitProcess(1)
}

fun favoriteChecker(id: String): Int {
    PlayerActivity.isFavorite = false
    FavoriteActivity.favoriteSongs.forEachIndexed { index, music ->
        if(id == music.id){
            PlayerActivity.isFavorite = true
            return index
        }
    }
    return -1
}