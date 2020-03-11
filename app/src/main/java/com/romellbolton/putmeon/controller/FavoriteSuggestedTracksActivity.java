package com.romellbolton.putmeon.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.romellbolton.putmeon.R;
import com.romellbolton.putmeon.adapter.FavoriteSuggestedTracksAdapter;
import com.romellbolton.putmeon.model.SuggestedTrack;
import com.romellbolton.putmeon.viewmodel.SuggestedTrackViewModel;

import java.util.Objects;

public class FavoriteSuggestedTracksActivity extends AppCompatActivity implements FavoriteSuggestedTracksAdapter.OnDeleteButtonClickListener, FavoriteSuggestedTracksAdapter.OnPlayButtonClickListener {

    private FavoriteSuggestedTracksAdapter favoriteSuggestedTracksAdapter;
    private SuggestedTrackViewModel postViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.favorties_title);
        favoriteSuggestedTracksAdapter = new FavoriteSuggestedTracksAdapter(this, this, this);

        postViewModel = ViewModelProviders.of(this).get(SuggestedTrackViewModel.class);
        postViewModel.getAllTracks().observe(this, tracks -> favoriteSuggestedTracksAdapter.setData(tracks));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(favoriteSuggestedTracksAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onDeleteButtonClicked(SuggestedTrack track) {
        postViewModel.deletePost(track);
    }

    @Override
    public void onPlayButtonClicked(SuggestedTrack track) {
        if (track.getURL().contains("null")) {
            Toast.makeText(this, R.string.no_preview_available, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = SpotifyTrackActivity.newIntent(this, new SuggestedTrack(track.getArtist(), track.getName(), null, track.getCoverURL640x636(), null, null, track.getUri()));
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}