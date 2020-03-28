package com.romellbolton.putmeon.controller;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.romellbolton.putmeon.R;
import com.romellbolton.putmeon.model.Track;
import com.romellbolton.putmeon.util.AppStatus;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;

public class TrackFragment extends Fragment {
    private static final String TAG = "SpotifyTrackFragment";
    private static final String TRACK_POSITION = "spotify_track_position";
    private static final String TRACK_KEY = "TRACK";

    private TextView mArtistNameTextView;
    private TextView mTrackNameTextView;
    private ImageView mTrackImageView;

    private ImageButton mPauseButton;
    private Button mShareButton;

    private static Track mSpotifyTrack;
    private static MediaPlayer mMediaPlayer;
    private static ProgressDialog mProgressDialog;

    private static String mPreviewURL;
    private String mArtistName;
    private String mTrackName;
    private String mImageURL;

    static TrackFragment newInstance(Track track) {
        Bundle args = new Bundle();
        args.putSerializable(TRACK_KEY, track);

        TrackFragment fragment = new TrackFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMediaPlayer = new MediaPlayer();
        assert getArguments() != null;
        mSpotifyTrack = (Track) getArguments().getSerializable(TRACK_KEY);

        assert mSpotifyTrack != null;
        mArtistName = mSpotifyTrack.getArtist();
        mTrackName = mSpotifyTrack.getName();
        mPreviewURL = mSpotifyTrack.getURL();
        mImageURL = mSpotifyTrack.getCoverURL640x636();

        if (AppStatus.getInstance(Objects.requireNonNull(getContext())).isOnline()) {
            new DownloadImage().execute();
        } else {
            Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TRACK_POSITION, mMediaPlayer.getCurrentPosition());
        Log.i(TAG, "Position now before rotate: " + mMediaPlayer.getCurrentPosition());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(TRACK_POSITION);
            Log.i(TAG, "Position now after rotate: " + position);
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(TRACK_POSITION);
            mMediaPlayer.seekTo(position);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track_playing, container, false);

        mArtistNameTextView = v.findViewById(R.id.spotify_track_playing_artist_name);
        mTrackImageView = v.findViewById(R.id.spotify_track_playing_image);
        mTrackNameTextView = v.findViewById(R.id.spotify_track_playing_title);

        mPauseButton = v.findViewById(R.id.spotify_track_playing_button_pause);
        mPauseButton.setOnClickListener(v1 -> {
            if (mMediaPlayer.isPlaying()) {
                mPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                mMediaPlayer.pause();
            } else {
                mPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
                mMediaPlayer.start();
            }
        });

        mShareButton = v.findViewById(R.id.spotify_track_playing_share);
        mShareButton.setOnClickListener(v12 -> {
            String title = getString(R.string.listen_to_this_song);
            String text = getString(R.string.message_currently_listening_to,
                    mTrackNameTextView.getText(),
                    mArtistNameTextView.getText(),
                    mPreviewURL);

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, text);
            i.putExtra(Intent.EXTRA_TITLE, title);

            i = Intent.createChooser(i, "Share to others via");
            startActivity(i);
        });
        return v;
    }

    private void setActivityTitle(String text) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(text);
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle(getString(R.string.loading_track));
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);

            if (mSpotifyTrack != null) {
                String imageURL = mSpotifyTrack.getCoverURL640x636();
                try {
                    InputStream input = new java.net.URL(imageURL).openStream();
                    bitmap = BitmapFactory.decodeStream(input);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Picasso.get().load(mImageURL).into(mTrackImageView);
            mTrackNameTextView.setText(mTrackName);
            mArtistNameTextView.setText(mArtistName);
            setActivityTitle(mTrackName);
            bootstrapPlayer();
            mProgressDialog.dismiss();
        }

        private void bootstrapPlayer() {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mMediaPlayer.setOnCompletionListener(mp -> mPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp));
            playSpotifyTrack(mPreviewURL);
        }

        private void playSpotifyTrack(String url) {
            try {
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                Toast.makeText(getContext(), getString(R.string.now_playing) + mTrackName + getString(R.string.by) + mArtistName, LENGTH_LONG).show();
            } catch (IOException ioe) {
                Log.e(TAG, getString(R.string.unable_to_play_track), ioe);
                Toast.makeText(getContext(), R.string.unable_to_play_track, LENGTH_LONG).show();
            }
        }
    }
}