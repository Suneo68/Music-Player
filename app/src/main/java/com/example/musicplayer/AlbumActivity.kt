package com.example.musicplayer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityAlbumBinding

class AlbumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumBinding
    private lateinit var adapter: AlbumViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.coolBlue)

        binding = ActivityAlbumBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val tempList = ArrayList<String>()
        tempList.add("Travel song")
        tempList.add("Let him cook song")
        tempList.add("Travel song")
        tempList.add("Travel song")
        tempList.add("Travel song")
//        Log.d("MusicList", "Tìm thấy ${MusicListMA.size} bài hát")
//        if (MusicListMA.isEmpty()) {
//            binding.totalSongs.text = "Không tìm thấy bài hát nào!"
//        }


        binding.albumRV.setHasFixedSize(true)
        binding.albumRV.setItemViewCacheSize(13)
        binding.albumRV.layoutManager = GridLayoutManager(this@AlbumActivity,2)
        adapter = AlbumViewAdapter(this, tempList)
        binding.albumRV.adapter = adapter
        binding.backBtnABA.setOnClickListener { finish() }
    }
}