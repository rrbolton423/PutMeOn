package com.romellbolton.putmeon.controller;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import com.romellbolton.putmeon.model.SuggestedTrack;

public class SpotifyTrackActivity extends SingleFragmentActivity {

    private static final String TRACK_KEY = "TRACK";

    public static Intent newIntent(Context context, SuggestedTrack suggestedTrack) {
        Intent i = new Intent(context, SpotifyTrackActivity.class);
        i.putExtra(TRACK_KEY, suggestedTrack);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return SpotifyTrackFragment.newInstance((SuggestedTrack) getIntent().getSerializableExtra(TRACK_KEY));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
