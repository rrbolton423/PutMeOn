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
    private List<Track> data;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnDeleteButtonClickListener onDeleteButtonClickListener;
    private OnPlayButtonClickListener onPlayButtonClickListener;

    public FavoriteTracksAdapter(Context context, OnDeleteButtonClickListener deleteButtonClickListener, OnPlayButtonClickListener playButtonClickListener) {
        this.data = new ArrayList<>();
        this.context = context;
        this.onDeleteButtonClickListener = deleteButtonClickListener;
        this.onPlayButtonClickListener = playButtonClickListener;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Track> newData) {
        if (data != null) {
            TrackDiffCallback trackDiffCallback = new TrackDiffCallback(data, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(trackDiffCallback);

            data.clear();
            data.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            data = newData;
        }
    }

    @NotNull
    @Override
    public TrackHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.favorite_track_list_item, parent, false);
        return new TrackHolder(itemView);
    }

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(Track track);
    }

    @Override
    public void onBindViewHolder(TrackHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
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
                    if (onPlayButtonClickListener != null)
                        onPlayButtonClickListener.onPlayButtonClicked(track);
                });
                deleteTrackButton.setOnClickListener(v -> {
                    if (onDeleteButtonClickListener != null)
                        onDeleteButtonClickListener.onDeleteButtonClicked(track);
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
