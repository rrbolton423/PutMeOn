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
import com.romellbolton.putmeon.model.SuggestedTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.TrackHolder> {

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(SuggestedTrack post);
    }

    private List<SuggestedTrack> data;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnDeleteButtonClickListener onDeleteButtonClickListener;

    public PostsAdapter(Context context, OnDeleteButtonClickListener listener) {
        this.data = new ArrayList<>();
        this.context = context;
        this.onDeleteButtonClickListener = listener;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.track_list_item, parent, false);
        return new TrackHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrackHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<SuggestedTrack> newData) {
        if (data != null) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(data, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            data.clear();
            data.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            data = newData;
        }
    }

    class TrackHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView artistName;
        Button mediaPlayerButton;
        ImageView coverArt;
        Button deleteTrackButton;

        TrackHolder(View v) {
            super(v);
            songName = v.findViewById(R.id.songNameID);
            artistName = v.findViewById(R.id.artistNameID);
            mediaPlayerButton = v.findViewById(R.id.openMediaPlayer);
            deleteTrackButton = v.findViewById(R.id.deleteTrackButton);
            coverArt = v.findViewById(R.id.coverArt);
        }

        void bind(final SuggestedTrack track) {
            if (track != null) {
                songName.setText(track.getName());
                artistName.setText(track.getArtist());
                try {
                    Picasso.get().load(track.getCoverURL64x64()).into(coverArt);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                deleteTrackButton.setOnClickListener(v -> {
                    if (onDeleteButtonClickListener != null)
                        onDeleteButtonClickListener.onDeleteButtonClicked(track);
                });

            }
        }
    }

    class PostDiffCallback extends DiffUtil.Callback {

        private final List<SuggestedTrack> oldTracks, newTracks;

        public PostDiffCallback(List<SuggestedTrack> oldPosts, List<SuggestedTrack> newPosts) {
            this.oldTracks = oldPosts;
            this.newTracks = newPosts;
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
