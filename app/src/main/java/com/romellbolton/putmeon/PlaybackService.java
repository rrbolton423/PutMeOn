package com.romellbolton.putmeon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class PlaybackService extends Service {

    public static String ACTION_PLAY = "com.example.android.spotifystreamer.PLAY";
    public static String ACTION_PAUSE = "com.example.android.spotifystreamer.PAUSE";

    public static String UPDATE_MAX_POS = "com.example.android.spotifystreamer.UPDATE_MAX_POS";
    public static String UPDATE_CUR_POS = "com.example.android.spotifystreamer.UPDATE_CUR_POS";

    public static String GET_MAX_POS = "com.example.android.spotifystreamer.GET_MAX_POS";
    public static String GET_CUR_POS = "com.example.android.spotifystreamer.GET_CUR_POS";

    public static String CHANGE_SEEK = "com.example.android.spotifystreamer.CHANGE_SEEK";

    private boolean startPlaying = false;

    private MediaPlayer mMediaPlayer = null;
    private final Handler handler = new Handler();
    private Intent intent;
    private BroadcastReceiver receiver;

    private Runnable sendSeekUpdate = new Runnable() {
        public void run() {
            if(mMediaPlayer != null) {
                sendSeekPos();
                Log.d("PlaybackService","pos sent");
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mMediaPlayer = new MediaPlayer();
        //intent =

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(GET_CUR_POS);
        filter.addAction(GET_MAX_POS);
        filter.addAction(CHANGE_SEEK);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String intentAction = intent.getAction();

                if(intentAction.equals(ACTION_PLAY)){
                    if(mMediaPlayer != null) {
                        //if(mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                    }
                    else if(mMediaPlayer == null) {
                        startPlaying = true;
                    }
                }
                else if(intentAction.equals(ACTION_PAUSE)){
                    if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    }
                }
                else if(intentAction.equals(GET_CUR_POS)){
                    sendSeekPos();
                }
                else if(intentAction.equals(GET_MAX_POS)){
                    sendMaxPos();
                }
                else if(intentAction.equals(CHANGE_SEEK)) {
                    int seek = intent.getIntExtra("seekto", 0);
                    if(mMediaPlayer != null) {
                        mMediaPlayer.seekTo(seek);
                    }
                }

            }
        };
        registerReceiver(receiver, filter);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String url = intent.getStringExtra("previewUrl");
            handler.removeCallbacks(sendSeekUpdate);
            handler.postDelayed(sendSeekUpdate, 1000);

            try {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        sendMaxPos();
                        if(startPlaying)
                            mMediaPlayer.start();
                    }
                });
                mMediaPlayer.prepareAsync();

            } catch (IOException e) {
                Log.w("IO Exception: ", e.toString());
            }
        }

        return START_STICKY;
    }

    public void onDestroy() {
        handler.removeCallbacks(sendSeekUpdate);
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();

        /* remove our receiver */
        unregisterReceiver(receiver);
    }

    public void onCompletion(MediaPlayer _mediaPlayer) {
        stopSelf();
    }

    private void sendMaxPos(){
        if(mMediaPlayer != null) {
            intent = new Intent(UPDATE_MAX_POS);
            intent.putExtra("maxPos", mMediaPlayer.getDuration());
            sendBroadcast(intent);
        }
    }

    private void sendSeekPos() {
        if(mMediaPlayer != null) {
            intent = new Intent(UPDATE_CUR_POS);
            intent.putExtra("curPos", mMediaPlayer.getCurrentPosition());
            sendBroadcast(intent);
        }
    }
}

