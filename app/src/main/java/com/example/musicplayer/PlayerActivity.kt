package com.example.musicplayer

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object{
        lateinit var musicListPA : ArrayList<Music>
        var songPosition : Int = 0
        var mediaPlayer : MediaPlayer? = null
        var isPlaying : Boolean = false
    }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.coolBlue)

        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initializeLayout()
        binding.playPausePABtn.setOnClickListener{
            if(isPlaying) pauseMusic()
            else playMusic()
        }

        binding.previousPABtn.setOnClickListener { prevNextSong(increment = false) }
        binding.nextPABtn.setOnClickListener { prevNextSong(increment = true) }
    }


    private fun setLayout(){

        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
            .into(binding.songImgPA)
        binding.songNamePA.text = musicListPA[songPosition].title
    }
    private fun createMeidaPlayer(){
        try{
            if(mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            isPlaying = true
            binding.playPausePABtn.setIconResource(R.drawable.pause_icon)
        } catch (e: Exception){ return}
    }

    //chuyen man sang Player khi chon bai
    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
                createMeidaPlayer()
            }
            "MainActivity" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
                createMeidaPlayer()
            }

        }
    }

    private fun playMusic(){
        binding.playPausePABtn.setIconResource(R.drawable.pause_icon)
        isPlaying = true
        mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPausePABtn.setIconResource(R.drawable.play_icon)
        isPlaying = false
        mediaPlayer!!.pause()
    }

    //nut sau va truoc bai hat hien tai
    private fun prevNextSong(increment: Boolean){
        if(increment)
        {
            setSongPosition(increment = true)
            setLayout()
            createMeidaPlayer()
        }
        else{
            setSongPosition(increment = false)
            setLayout()
            createMeidaPlayer()
        }
    }

    private fun setSongPosition(increment: Boolean){
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