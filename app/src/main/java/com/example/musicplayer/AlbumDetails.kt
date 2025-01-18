package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityAlbumDetailsBinding

class AlbumDetails : AppCompatActivity() {

    lateinit var binding: ActivityAlbumDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object {
        var currentAlbumPos: Int = -1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAlbumDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentAlbumPos = intent.extras?.get("index") as Int
        binding.albumDetailsRV.setItemViewCacheSize(10)
        binding.albumDetailsRV.setHasFixedSize(true)
        binding.albumDetailsRV.layoutManager = LinearLayoutManager(this)
        AlbumActivity.musicAlbum.ref[currentAlbumPos].album.addAll(MainActivity.MusicListMA)
        AlbumActivity.musicAlbum.ref[currentAlbumPos].album.shuffle()
        adapter = MusicAdapter(
            this,
            AlbumActivity.musicAlbum.ref[currentAlbumPos].album,
            playlistDetails = false, albumDetails = true)
        binding.albumDetailsRV.adapter = adapter
        binding.backBtnALD.setOnClickListener { finish() }
        binding.shuffleBtnALD.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "AlbumDetailsShuffle")
            startActivity(intent)
        }
        binding.addBtnALD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
    }
    //Nội dung album
    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.albumNameALD.text = AlbumActivity.musicAlbum.ref[currentAlbumPos].name
        binding.InfoALD.text = "Có ${adapter.itemCount} bài hát.\n\n" +
                "Ngày tạo:\n${AlbumActivity.musicAlbum.ref[currentAlbumPos].createdOn}\n\n" +
                " -- ${AlbumActivity.musicAlbum.ref[currentAlbumPos].createBy}"
        if (adapter.itemCount > 0) {
            Glide.with(this)
                .load({AlbumActivity.musicAlbum.ref[currentAlbumPos].album[0].artUri })
                .apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
                .into(binding.albumImgALD)
            binding.shuffleBtnALD.visibility = View.VISIBLE
        }
    }
}