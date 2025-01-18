package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object{
        lateinit var musicListPA : ArrayList<Music>
        var songPosition : Int = 0
        var isPlaying : Boolean = false
        var musicService: MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
        var nowPlayingId: String = ""
        var isFavorite: Boolean = false
        var fIndex: Int = -1

    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.coolBlue)

        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initializeLayout()
        binding.backBtnPA.setOnClickListener{ finish()}
        binding.playPausePABtn.setOnClickListener{
            if(isPlaying) pauseMusic()
            else playMusic()
        }

        binding.previousPABtn.setOnClickListener { prevNextSong(increment = false) }
        binding.nextPABtn.setOnClickListener { prevNextSong(increment = true) }
        binding.seekBarPA.setOnSeekBarChangeListener(@SuppressLint("AppCompatCustomView")
        object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
        binding.repeatBtnPA.setOnClickListener{
            if (!repeat){
                repeat = true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_green))
            } else{
                repeat = false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
            }
        }
        binding.equalizerBtnPA.setOnClickListener {
            binding.equalizerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_green))
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, applicationContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 13)
            } catch (e: Exception){Toast.makeText(this, "Tính năng không được hỗ trợ!!", Toast.LENGTH_SHORT).show()}
        }
        binding.timerBtnPA.setOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer) {
                showBottomSheetDialog()
                binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_green))
            }
            else{
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Hẹn Giờ")
                    .setMessage("Bạn có muốn đóng Hẹn Giờ không?")
                    .setPositiveButton("Đồng ý"){_, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.yellow))
                    }
                    .setNegativeButton("Huỷ"){dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }
        binding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài hát!!"))
            binding.shareBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_green))
            Thread {
                Thread.sleep(2000)
                runOnUiThread {
                    binding.shareBtnPA.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.cool_blue
                        )
                    )
                }
            }.start()
        }
        binding.favoriteBtnPA.setOnClickListener {
            if(isFavorite){
                isFavorite = false
                binding.favoriteBtnPA.setImageResource(R.drawable.favorite_empty_icon)
                FavoriteActivity.favoriteSongs.removeAt(fIndex)
            } else{
                isFavorite = true
                binding.favoriteBtnPA.setImageResource(R.drawable.favorite_icon)
                FavoriteActivity.favoriteSongs.add(musicListPA[songPosition])
            }
        }
    }


    private fun setLayout(){

        fIndex = favoriteChecker(musicListPA[songPosition].id)
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
            .into(binding.songImgPA)
        binding.songNamePA.text = musicListPA[songPosition].title
        if (repeat) binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_green))
        if (isFavorite) binding.favoriteBtnPA.setImageResource(R.drawable.favorite_icon)
        else binding.favoriteBtnPA.setImageResource(R.drawable.favorite_empty_icon)

    }
    private fun createMeidaPlayer(){
        try{
            if(musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playPausePABtn.setIconResource(R.drawable.pause_icon)
            musicService?.showNotification(R.drawable.pause_icon)
            binding.tvSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener (this)
            nowPlayingId = musicListPA[songPosition].id
        } catch (e: Exception){ return}
    }

    //chuyen man sang Player khi chon bai
    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "FavoriteAdapter" ->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavoriteActivity.favoriteSongs)
                setLayout()
            }

            "NowPlaying" ->{
                setLayout()
                binding.tvSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
                if(isPlaying) binding.playPausePABtn.setIconResource(R.drawable.pause_icon)
                else binding.playPausePABtn.setIconResource(R.drawable.play_icon)
            }

            "MusicAdapterSearch" ->{
                //Bắt đầu phát nhạc
                val intent = Intent(this,MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "MusicAdapter" -> {
                //Bắt đầu phát nhạc
                val intent = Intent(this,MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
            }
            "MainActivity" -> {
                //Bắt đầu phát nhạc
                val intent = Intent(this,MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
            }
            "FavoriteShuffle" -> {
                //Bắt đầu phát nhạc
                val intent = Intent(this,MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavoriteActivity.favoriteSongs)
                musicListPA.shuffle()
                setLayout()
            }
            "PlaylistDetailsAdapter" -> {
                //Bắt đầu phát nhạc
                val intent = Intent(this,MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                setLayout()
            }
            "AlbumDetailsAdapter" -> {
                //Bắt đầu phát nhạc
                val intent = Intent(this,MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(AlbumActivity.musicAlbum.ref[AlbumDetails.currentAlbumPos].album)
                setLayout()
            }
            "PlaylistDetailsShuffle" -> {
                //Bắt đầu phát nhạc
                val intent = Intent(this,MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                musicListPA.shuffle()
                setLayout()
            }
            "AlbumDetailsShuffle" -> {
                //Bắt đầu phát nhạc
                val intent = Intent(this,MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(AlbumActivity.musicAlbum.ref[AlbumDetails.currentAlbumPos].album)
                musicListPA.shuffle()
                setLayout()
            }
        }
    }

    private fun playMusic(){
        binding.playPausePABtn.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()

    }

    private fun pauseMusic(){
        binding.playPausePABtn.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
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



    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMeidaPlayer()
        musicService!!.seekBarSetup()

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMeidaPlayer()
        try {
            setLayout()
        }catch (e:Exception){return}
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      " +
            "which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      " +
            "contracts for common intents available in\n      " +
            "{@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      " +
            "testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      " +
            "{@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      " +
            "with the appropriate {@link ActivityResultContract} and handling the result in the\n      " +
            "{@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || requestCode == RESULT_OK) {
            binding.equalizerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.black))
            return
        }
    }

    private fun showBottomSheetDialog(){
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(applicationContext, "Nhạc sẽ dừng sau 15 phút nữa!", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_green))
            min15 = true
            Thread{
                Thread.sleep((15 * 60000).toLong())
                if (min15 ) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(applicationContext, "Nhạc sẽ dừng sau 30 phút nữa!", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_green))
            min30 = true
            Thread{
                Thread.sleep((30 * 60000).toLong())
                if (min30 ) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(applicationContext, "Nhạc sẽ dừng sau 60 phút nữa!", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_green))
            min60 = true
            Thread{
                Thread.sleep((60 * 60000).toLong())
                if (min60 ) exitApplication()
            }.start()
            dialog.dismiss()
        }
    }
}