package com.romellbolton.putmeon;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

public class SpotifyTrackActivity extends SingleFragmentActivity {

    public static Intent newIntent (Context context, Track track) {
        Intent i = new Intent(context, SpotifyTrackActivity.class);
        i.putExtra("TRACK", track);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return SpotifyTrackFragment.newInstance((Track) getIntent().getSerializableExtra("TRACK"));
    }
}
