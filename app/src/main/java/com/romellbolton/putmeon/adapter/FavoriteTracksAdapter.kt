package com.romellbolton.putmeon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romellbolton.putmeon.R
import com.romellbolton.putmeon.adapter.FavoriteTracksAdapter.TrackHolder
import com.romellbolton.putmeon.model.Track
import com.squareup.picasso.Picasso
import java.util.*

class FavoriteTracksAdapter(context: Context, deleteButtonClickListener: OnDeleteButtonClickListener?, playButtonClickListener: OnPlayButtonClickListener?) : RecyclerView.Adapter<TrackHolder>() {
    private var mData: MutableList<Track>?
    private val mLayoutInflater: LayoutInflater
    private val mOnDeleteButtonClickListener: OnDeleteButtonClickListener?
    private val mOnPlayButtonClickListener: OnPlayButtonClickListener?
    fun setmData(newData: MutableList<Track>) {
        if (mData != null) {
            val trackDiffCallback = TrackDiffCallback(mData!!, newData)
            val diffResult = DiffUtil.calculateDiff(trackDiffCallback)
            mData!!.clear()
            mData!!.addAll(newData)
            diffResult.dispatchUpdatesTo(this)
        } else {
            // first initialization
            mData = newData
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val itemView = mLayoutInflater.inflate(R.layout.favorite_track_list_item, parent, false)
        return TrackHolder(itemView)
    }

    interface OnDeleteButtonClickListener {
        fun onDeleteButtonClicked(track: Track?)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(mData!![position])
    }

    override fun getItemCount(): Int {
        return mData!!.size
    }

    interface OnPlayButtonClickListener {
        fun onPlayButtonClicked(track: Track?)
    }

    inner class TrackHolder(v: View) : RecyclerView.ViewHolder(v) {
        var songName: TextView
        var artistName: TextView
        var playTrackButton: Button
        var coverArt: ImageView
        var deleteTrackButton: Button
        fun bind(track: Track?) {
            if (track != null) {
                songName.text = track.name
                artistName.text = track.artist
                try {
                    Picasso.get().load(track.coverURL640x636).into(coverArt)
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                playTrackButton.setOnClickListener { mOnPlayButtonClickListener?.onPlayButtonClicked(track) }
                deleteTrackButton.setOnClickListener { mOnDeleteButtonClickListener?.onDeleteButtonClicked(track) }
            }
        }

        init {
            songName = v.findViewById(R.id.songNameID)
            artistName = v.findViewById(R.id.artistNameID)
            playTrackButton = v.findViewById(R.id.openMediaPlayer)
            deleteTrackButton = v.findViewById(R.id.deleteTrackButton)
            coverArt = v.findViewById(R.id.coverArt)
        }
    }

    internal inner class TrackDiffCallback(private val oldTracks: List<Track>, private val newTracks: List<Track>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldTracks.size
        }

        override fun getNewListSize(): Int {
            return newTracks.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldTracks[oldItemPosition].uid == newTracks[newItemPosition].uid
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldTracks[oldItemPosition] == newTracks[newItemPosition]
        }

    }

    init {
        mData = ArrayList()
        mOnDeleteButtonClickListener = deleteButtonClickListener
        mOnPlayButtonClickListener = playButtonClickListener
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}