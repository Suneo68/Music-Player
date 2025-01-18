package com.example.musicplayer

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.AddPlaylistDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter

    companion object {
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.coolBlue)

        binding = ActivityPlaylistBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this@PlaylistActivity,2)
        adapter = PlaylistViewAdapter(this, playlistList = musicPlaylist.ref)
        binding.playlistRV.adapter = adapter
        binding.backBtnPLA.setOnClickListener { finish() }
        binding.addPlaylistBtn.setOnClickListener {
            customAlertDialog()
        }
    }
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@PlaylistActivity).inflate(R.layout.add_playlist_dialog,binding.root,false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist")
            .setPositiveButton("Xong") { dialog, _ ->
                val playlistName = binder.playlistName.text
//                val createdBy = binder.yourName.text
                if(playlistName != null )
//                    if (playlistName.isNotEmpty() && createdBy.isNotEmpty())
                    if (playlistName.isNotEmpty())
                    {
//                        addPlaylist(playlistName.toString(), createdBy.toString())
                        addPlaylist(playlistName.toString())
                    }
                Toast.makeText(
                    this,
                    "Playlist đã được thêm!",
                    Toast.LENGTH_SHORT
                ).show()
            }.show()
    }
//    private fun addPlaylist(name: String, createdBy: String){
    private fun addPlaylist(name: String){
        var playlistExist = false
        for (i in musicPlaylist.ref){
            if(name.equals(i.name)){
                playlistExist = true
                break
            }
        }
        if (playlistExist) Toast.makeText(this, "Tên Playlist đã tồn tại!", Toast.LENGTH_SHORT).show()
        else {
            val tempPlaylist  = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
//            tempPlaylist.createBy = createdBy
            val calender = java.util.Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calender)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}