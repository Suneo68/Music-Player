package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.PlaylistViewBinding

class PlaylistViewAdapter(private val context: Context, private var playlistList: ArrayList<String>) : RecyclerView.Adapter<PlaylistViewAdapter.MyHolder> () {
    class MyHolder (binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root){
        val image = binding.playlistImg
        val name = binding.playlistName
        var root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {

        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent, false))
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = playlistList[position]
        holder.name.isSelected = true
    }
}