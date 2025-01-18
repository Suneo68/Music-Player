package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {

    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object {
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.coolBlue)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlaylistPos = intent.extras?.get("index") as Int
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(
            this,
            PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist,
            playlistDetails = true)
        binding.playlistDetailsRV.adapter = adapter
        binding.backBtnPLD.setOnClickListener { finish() }
        binding.shuffleBtnPLD.setOnClickListener {
            // Toast.makeText(this@MainActivity, "Đã Nhấn", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }
        binding.addBtnPLD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
        binding.removeBtnPLD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Loại bỏ")
                .setMessage("Bạn chắc chắn loại bỏ toàn bộ chứ?")
                .setPositiveButton("Đồng ý") { dialog, _ ->
                    PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    Toast.makeText(
                        this,
                        "Đã loại bỏ hoàn toàn!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Huỷ") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

        }
    }

    //Nội dung playlist
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePLD.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.InfoPLD.text = "Có ${adapter.itemCount} bài hát.\n\n" +
                "Ngày tạo:\n${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n"
        if (adapter.itemCount > 0) {
            Glide.with(this)
                .load({PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri })
                .apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
                .into(binding.playlistImgPLD)
            binding.shuffleBtnPLD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        //cho nơi lưu trữ bài hát Playlist
        val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }

}