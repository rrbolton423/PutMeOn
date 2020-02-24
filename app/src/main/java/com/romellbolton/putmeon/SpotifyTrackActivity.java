package com.romellbolton.putmeon;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class SpotifyTrackActivity extends SingleFragmentActivity {

    private static final String TRACK_KEY = "TRACK";

    public static Intent newIntent (Context context, SuggestedTrack suggestedTrack) {
        Intent i = new Intent(context, SpotifyTrackActivity.class);
        i.putExtra(TRACK_KEY, suggestedTrack);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return SpotifyTrackFragment.newInstance((SuggestedTrack) getIntent().getSerializableExtra(TRACK_KEY));
    }
}
