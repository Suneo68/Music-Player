package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.MusicViewBinding

class MusicAdapter(private val context: Context,
                   private var musicList: ArrayList<Music>,
                   private val playlistDetails: Boolean = false,
                   private val albumDetails: Boolean = false,
                    private val selectionActivity: Boolean = false) : RecyclerView.Adapter<MusicAdapter.MyHolder> () {
    class MyHolder (binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {

        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent, false))
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
            .into(holder.image)


        when {
            playlistDetails -> {
                holder.root.setOnClickListener {
                    sendIntent(ref = "PlaylistDetailsAdapter", pos = position)
                }
            }

            albumDetails -> {
                holder.root.setOnClickListener {
                    sendIntent(ref = "AlbumDetailsAdapter", pos = position)
                }
            }
            selectionActivity -> {
                holder.root.setOnClickListener {
                    if (addSong(musicList[position]))
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.cool_pink))
                    else
                        holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                }
            }
                else -> {
                    holder.root.setOnClickListener {
                        when {
                            MainActivity.search -> sendIntent(
                                ref = "MusicAdapterSearch",
                                pos = position
                            )

                            musicList[position].id == PlayerActivity.nowPlayingId ->
                                sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)

                            else -> sendIntent(ref = "MusicAdapter", pos = position)
                        }
                    }
                }
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<Music>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivities(context, arrayOf(intent), null)
    }

    //Them bai hat vao danh sach
    private fun addSong(song: Music): Boolean{
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if (song.id == music.id){
                PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
        }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
}