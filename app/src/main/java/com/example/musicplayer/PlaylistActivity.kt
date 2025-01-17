package com.example.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.coolBlue)

        binding = ActivityPlaylistBinding.inflate(layoutInflater)

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


        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this@PlaylistActivity,2)
        adapter = PlaylistViewAdapter(this, tempList)
        binding.playlistRV.adapter = adapter
        binding.backBtnPLA.setOnClickListener { finish() }
        binding.addPlaylistBtn.setOnClickListener {
            customAlertDialog()
        }
    }
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@PlaylistActivity).inflate(R.layout.add_playlist_dialog,binding.root,false)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist")
            .setPositiveButton("Đồng ý") { dialog, _ ->
                Toast.makeText(
                    this,
                    "Playlist đã được thêm!",
                    Toast.LENGTH_SHORT
                ).show()
            }.show()

    }
}