package com.romellbolton.putmeon.controller;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import com.romellbolton.putmeon.model.Track;

public class TrackActivity extends SingleFragmentActivity {

    private static final String TRACK_KEY = "TRACK";

    public static Intent newIntent(Context context, Track track) {
        Intent i = new Intent(context, TrackActivity.class);
        i.putExtra(TRACK_KEY, track);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return TrackFragment.newInstance((Track) getIntent().getSerializableExtra(TRACK_KEY));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
