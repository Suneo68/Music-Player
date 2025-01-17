package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.AlbumViewBinding

class AlbumViewAdapter(private val context: Context, private var albumList: ArrayList<String>) : RecyclerView.Adapter<AlbumViewAdapter.MyHolder> () {
    class MyHolder (binding: AlbumViewBinding) : RecyclerView.ViewHolder(binding.root){
        val image = binding.albumImg
        val name = binding.albumName
        var root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {

        return MyHolder(AlbumViewBinding.inflate(LayoutInflater.from(context),parent, false))
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = albumList[position]
        holder.name.isSelected = true
    }
}