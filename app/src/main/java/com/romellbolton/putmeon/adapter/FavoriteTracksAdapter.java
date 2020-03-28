package com.romellbolton.putmeon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.romellbolton.putmeon.R;
import com.romellbolton.putmeon.model.Track;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FavoriteTracksAdapter extends RecyclerView.Adapter<FavoriteTracksAdapter.TrackHolder> {
    private List<Track> mData;
    private LayoutInflater mLayoutInflater;
    private OnDeleteButtonClickListener mOnDeleteButtonClickListener;
    private OnPlayButtonClickListener mOnPlayButtonClickListener;

    public FavoriteTracksAdapter(Context context, OnDeleteButtonClickListener deleteButtonClickListener, OnPlayButtonClickListener playButtonClickListener) {
        this.mData = new ArrayList<>();
        this.mOnDeleteButtonClickListener = deleteButtonClickListener;
        this.mOnPlayButtonClickListener = playButtonClickListener;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setmData(List<Track> newData) {
        if (mData != null) {
            TrackDiffCallback trackDiffCallback = new TrackDiffCallback(mData, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(trackDiffCallback);

            mData.clear();
            mData.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mData = newData;
        }
    }

    @NotNull
    @Override
    public TrackHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.favorite_track_list_item, parent, false);
        return new TrackHolder(itemView);
    }

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(Track track);
    }

    @Override
    public void onBindViewHolder(TrackHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnPlayButtonClickListener {
        void onPlayButtonClicked(Track track);
    }

    class TrackHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView artistName;
        Button playTrackButton;
        ImageView coverArt;
        Button deleteTrackButton;

        TrackHolder(View v) {
            super(v);
            songName = v.findViewById(R.id.songNameID);
            artistName = v.findViewById(R.id.artistNameID);
            playTrackButton = v.findViewById(R.id.openMediaPlayer);
            deleteTrackButton = v.findViewById(R.id.deleteTrackButton);
            coverArt = v.findViewById(R.id.coverArt);
        }

        void bind(final Track track) {
            if (track != null) {
                songName.setText(track.getName());
                artistName.setText(track.getArtist());
                try {
                    Picasso.get().load(track.getCoverURL640x636()).into(coverArt);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                playTrackButton.setOnClickListener(v -> {
                    if (mOnPlayButtonClickListener != null)
                        mOnPlayButtonClickListener.onPlayButtonClicked(track);
                });
                deleteTrackButton.setOnClickListener(v -> {
                    if (mOnDeleteButtonClickListener != null)
                        mOnDeleteButtonClickListener.onDeleteButtonClicked(track);
                });
            }
        }
    }

    class TrackDiffCallback extends DiffUtil.Callback {

        private final List<Track> oldTracks, newTracks;

        TrackDiffCallback(List<Track> oldTracks, List<Track> newTracks) {
            this.oldTracks = oldTracks;
            this.newTracks = newTracks;
        }

        @Override
        public int getOldListSize() {
            return oldTracks.size();
        }

        @Override
        public int getNewListSize() {
            return newTracks.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldTracks.get(oldItemPosition).uid == newTracks.get(newItemPosition).uid;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldTracks.get(oldItemPosition).equals(newTracks.get(newItemPosition));
        }
    }
}
