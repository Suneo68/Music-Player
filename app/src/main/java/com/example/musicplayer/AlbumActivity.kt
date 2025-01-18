package com.example.musicplayer

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.PlaylistActivity.Companion.musicPlaylist
import com.example.musicplayer.databinding.ActivityAlbumBinding
import com.example.musicplayer.databinding.AddAlbumDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class AlbumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumBinding
    private lateinit var adapter: AlbumViewAdapter

    companion object {
        var musicAlbum: MusicAlbum = MusicAlbum()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.coolBlue)

        binding = ActivityAlbumBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.albumRV.setHasFixedSize(true)
        binding.albumRV.setItemViewCacheSize(13)
        binding.albumRV.layoutManager = GridLayoutManager(this@AlbumActivity,2)
        adapter = AlbumViewAdapter(this, albumList = musicAlbum.ref)
        binding.albumRV.adapter = adapter
        binding.backBtnABA.setOnClickListener { finish() }
        binding.addAlbumBtn.setOnClickListener {
            customAlertDialog()
        }
    }
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@AlbumActivity).inflate(R.layout.add_album_dialog,binding.root,false)
        val binder = AddAlbumDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Album")
            .setPositiveButton("Xong") { dialog, _ ->
                val albumName = binder.albumName.text
                val createdBy = binder.yourName.text
                if(albumName != null && createdBy != null)
                    if (albumName.isNotEmpty() && createdBy.isNotEmpty())
                    {
                        addAlbum(albumName.toString(), createdBy.toString())

                    }
                Toast.makeText(
                    this,
                    "Album đã được thêm!",
                    Toast.LENGTH_SHORT
                ).show()
            }.show()
    }
        private fun addAlbum(name: String, createdBy: String){
        var albumExist = false
        for (i in musicPlaylist.ref){
            if(name.equals(i.name)){
                albumExist = true
                break
            }
        }
        if (albumExist) Toast.makeText(this, "Tên Album đã tồn tại!", Toast.LENGTH_SHORT).show()
        else {
            val tempAlbum = Album()
            tempAlbum.name = name
            tempAlbum.album = ArrayList()
            tempAlbum.createBy = createdBy
            val calender = java.util.Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            tempAlbum.createdOn = sdf.format(calender)
            musicAlbum.ref.add(tempAlbum)
            adapter.refreshAlbum()
        }
    }
}