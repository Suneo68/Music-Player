package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.AlbumViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlbumViewAdapter(private val context: Context, private var albumList: ArrayList<Album>) : RecyclerView.Adapter<AlbumViewAdapter.MyHolder> () {
    class MyHolder (binding: AlbumViewBinding) : RecyclerView.ViewHolder(binding.root){
        val image = binding.albumImg
        val name = binding.albumName
        var root = binding.root
        val delete = binding.albumDeleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {

        return MyHolder(AlbumViewBinding.inflate(LayoutInflater.from(context),parent, false))
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = albumList[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(albumList[position].name)
                .setMessage("Bạn có muốn xoá không?")
                .setPositiveButton("Đồng ý") { dialog, _ ->
                   AlbumActivity.musicAlbum.ref.removeAt(position)
                    refreshAlbum()
                    Toast.makeText(
                        context,
                        "Album này đã được xoá!",
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
        holder.root.setOnClickListener {
            val intent = Intent(context, AlbumDetails::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }
        if (AlbumActivity.musicAlbum.ref[position].album.size > 0)
        {
            Glide.with(context)
                .load({AlbumActivity.musicAlbum.ref[position].album[0].artUri })
                .apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
                .into(holder.image)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshAlbum(){
        albumList = ArrayList()
        albumList.addAll(AlbumActivity.musicAlbum.ref)
        notifyDataSetChanged()
    }
}