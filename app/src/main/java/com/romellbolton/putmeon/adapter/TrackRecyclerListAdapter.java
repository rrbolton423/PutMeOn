package com.romellbolton.putmeon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.romellbolton.putmeon.R;
import com.romellbolton.putmeon.model.SuggestedTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrackRecyclerListAdapter extends RecyclerView.Adapter<TrackRecyclerListAdapter.TrackHolder> {

    private ArrayList<SuggestedTrack> trackInfoArrayList;
    Context context;

    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClick(Button b, View v, SuggestedTrack inf, int pos);
    }

    public void setItemClickListener(ItemClickListener setItemClickListener) {
        this.itemClickListener = setItemClickListener;
    }

    public TrackRecyclerListAdapter(Context setContext, ArrayList<SuggestedTrack> setTrackInfos) {
        this.context = setContext;
        this.trackInfoArrayList = setTrackInfos;
    }

    @Override
    public TrackHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.track_list_item, viewGroup, false);
        return new TrackHolder(v);
    }

    @Override
    public void onBindViewHolder(final TrackHolder trackHolder, final int i) {
        final SuggestedTrack trackerInfo = trackInfoArrayList.get(i);
        trackHolder.songName.setText(trackerInfo.getName());
        trackHolder.artistName.setText(trackerInfo.getArtist());
        try {
            Picasso.get().load(trackerInfo.getCoverURL64x64()).into(trackHolder.coverArt);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        trackHolder.mediaPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(trackHolder.mediaPlayerButton, v, trackerInfo, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return trackInfoArrayList.size();
    }


    class TrackHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView artistName;
        Button mediaPlayerButton;
        ImageView coverArt;

        TrackHolder(View v) {
            super(v);
            songName = v.findViewById(R.id.songNameID);
            artistName = v.findViewById(R.id.artistNameID);
            mediaPlayerButton = v.findViewById(R.id.openMediaPlayer);
            coverArt = v.findViewById(R.id.coverArt);
        }
    }
}
