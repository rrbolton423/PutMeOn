package com.romellbolton.putmeon;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class PlaybackScreenFragment extends DialogFragment {

    TextView songTX;
    TextView albumTX;
    TextView artistTX;
    TextView elapsedTimeTX;
    TextView maxTimeTX;
    ImageView imageView;
    ImageButton nextBT;
    ImageButton playBT;
    ImageButton pauseBT;
    ImageButton previousBT;
    SeekBar seekBar;
    String url = "";
    String trackName;
    String image = "";
    String albumName;
    String artistName;
    String coverURL = "";

    int trackPosition;
    IntentFilter filter;
    private static Intent playbackService = null;
    private static BroadcastReceiver receiver = null;
    private boolean isPlaying = false;
    private static boolean saveState = false;
    private boolean  manualSeeking = false;
    static final String SONG_URL = "URL";
    static final String COVER_URL = "COVER_URL";
    static final String IMAGE_URL = "IMAGE";
    static final String ARTIST_NAME = "NAME";
    static final String ALBUM_NAME = "ALBUM";
    static final String SONG_NAME = "SONG";
    static final String ARTIST_ID = "ARTIST_ID";
    static final String SONG_ID = "SONG_ID";

    static final String TRACK_POSITION = "TRACK_POSITION";
    private Track currentTrack = new Track();



    public PlaybackScreenFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog() != null) getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.fragment_playback_screen, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            url = intent.getStringExtra(SONG_URL);
            trackName = intent.getStringExtra(SONG_NAME);
            artistName = intent.getStringExtra(ARTIST_NAME);
            albumName = intent.getStringExtra(ALBUM_NAME);
            image = intent.getStringExtra(IMAGE_URL);
//            trackPosition = intent.getIntExtra(TRACK_POSITION, 0);
            coverURL = intent.getStringExtra(IMAGE_URL);
            image = intent.getStringExtra(IMAGE_URL);
            image = intent.getStringExtra(IMAGE_URL);


        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            url = arguments.getString(PlaybackScreenFragment.SONG_URL);
            image = arguments.getString(PlaybackScreenFragment.IMAGE_URL);
            artistName = arguments.getString(PlaybackScreenFragment.ARTIST_NAME);
            trackName = arguments.getString(PlaybackScreenFragment.SONG_NAME);
            albumName = arguments.getString(PlaybackScreenFragment.ALBUM_NAME);
//            trackPosition = arguments.getInt(TRACK_POSITION);
        }

        elapsedTimeTX = view.findViewById(R.id.elaspedTime);
        maxTimeTX = view.findViewById(R.id.duration);
        seekBar = view.findViewById(R.id.seekBar);
        artistTX = view.findViewById(R.id.textview_playback_artist);
        artistTX.setText(artistName);
        songTX = view.findViewById(R.id.textview_playback_song);
//        trackName = TopTracksFragment.allTracks.get(trackPosition).trackName;
        songTX.setText(trackName);
        albumTX = view.findViewById(R.id.textview_playback_album);
        albumTX.setText(albumName);
        nextBT = view.findViewById(R.id.button_next_playback);
        previousBT = view.findViewById(R.id.button_previous_playback);
        playBT = view.findViewById(R.id.button_play_playback);
        pauseBT = view.findViewById(R.id.button_pause_playback);
        imageView = view.findViewById(R.id.imageview_playback);
        if (!image.equals("")) {
            Picasso.get().load(image).into(imageView);
        }


        playBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong();
            }
        });
//        nextBT.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nextSong();
//            }
//        });
        pauseBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseSong();
            }
        });
//        previousBT.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                previousSong();
//            }
//        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int newProgress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){
                    newProgress = progress;
                    int prg = (int) (newProgress * .001);
                    elapsedTimeTX.setText("0:" + (prg < 10 ? "0" : "") + prg);
                    manualSeeking = fromUser;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                manualSeeking = false;
                Intent seekIntent = new Intent(PlaybackService.CHANGE_SEEK);
                seekIntent.putExtra("seekto", newProgress);
                getActivity().sendBroadcast(seekIntent);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(PlaybackService.UPDATE_CUR_POS);
        filter.addAction(PlaybackService.UPDATE_MAX_POS);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(PlaybackService.UPDATE_CUR_POS)) {
                    int position = intent.getIntExtra("curPos", 0);
                    int prg = (int) (position * .001);

                    //if (!manualSeeking) {
                    seekBar.setProgress(position);
                    elapsedTimeTX.setText("0:" + (prg < 10 ? "0" : "") + prg);
                    //}
                }
                else if(action.equals(PlaybackService.UPDATE_MAX_POS)){
                    int duration = intent.getIntExtra("maxPos", 0);

                    maxTimeTX.setText("0:" + (int) (duration * .001));
                    seekBar.setMax(duration);
                }
            }
        };
        this.getActivity().registerReceiver(receiver, filter);
        if( savedInstanceState != null ){
            //savedPosition = savedInstanceState.getInt("position");
            trackPosition = savedInstanceState.getInt("trackIndex");
            isPlaying = savedInstanceState.getBoolean("playing");

            //Log.d("playerFragment", "playing: " + playing);
            if(isPlaying) {
                Log.d("playerFragment", "playing");
                togglePlayPause();
                this.getActivity().registerReceiver(receiver, filter);
            }
            getMax();
            getSeek();
        }

        loadData();


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Log.d("playerfragment", "onSaveInstanceState");

        saveState = true;
        savedInstanceState.putInt("trackIndex", trackPosition);
        savedInstanceState.putBoolean("playing", isPlaying);
    }

    private void getSeek() {
        Intent intent = new Intent();
        intent.setAction(PlaybackService.GET_CUR_POS);
        this.getActivity().sendBroadcast(intent);
    }

    private void getMax() {
        Intent intent = new Intent();
        intent.setAction(PlaybackService.GET_MAX_POS);
        this.getActivity().sendBroadcast(intent);
    }

//    private void previousSong() {
//        trackPosition--;
//        pauseSong();
//        if (trackPosition < 0) {
//            trackPosition = TopTracksFragment.allTracks.size() - 1;
//        }
//        loadData(trackPosition);
//        //  playSong();
//    }

    private void pauseSong() {
        if (isPlaying) {
            togglePlayPause();
            isPlaying = false;
        }

        Intent intent = new Intent();
        intent.setAction(PlaybackService.ACTION_PAUSE);
        this.getActivity().sendBroadcast(intent);
    }

    private void playSong() {
        isPlaying = true;
        togglePlayPause();

        Intent intent = new Intent();
        intent.setAction(PlaybackService.ACTION_PLAY);
        this.getActivity().sendBroadcast(intent);
    }

    private void togglePlayPause() {
        if (playBT.getVisibility() == View.VISIBLE) {
            playBT.setVisibility(View.GONE);
            pauseBT.setVisibility(View.VISIBLE);
        } else {
            pauseBT.setVisibility(View.GONE);
            playBT.setVisibility(View.VISIBLE);
        }
    }

//    private void nextSong() {
//        trackPosition++;
//        pauseSong();
//        if (trackPosition > TopTracksFragment.allTracks.size() - 1) {
//            trackPosition = 0;
//        }
//        loadData(trackPosition);
//        //playSong();
//
//    }

    private void loadData() {
        artistTX.setText(artistName);
        songTX.setText(trackName);
        if (!image.equals("")) {
            Picasso.get().load(image).into(imageView);
        }
        if (!saveState) {

            if (playbackService != null) {
                this.getActivity().stopService(playbackService);
            }
            playbackService = new Intent(this.getActivity(), PlaybackService.class);
            playbackService.setAction(PlaybackService.ACTION_PLAY);
            playbackService.putExtra("previewUrl", url);
            this.getActivity().startService(playbackService);
//
        } else {
            saveState = false;
        }
    }
    @Override
    public void onDestroy() {
        Log.d("playerfragment","onDestroy");
        try {
            this.getActivity().unregisterReceiver(receiver);
        } catch ( IllegalArgumentException e ) {
            Log.w("playerfragment", e.getMessage());
        }
        // make sure we clean up player resources before this fragment is destroyed
        isPlaying = false;
        super.onDestroy();
    }

}
