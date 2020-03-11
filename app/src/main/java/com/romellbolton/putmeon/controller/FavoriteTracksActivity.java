package com.romellbolton.putmeon.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.romellbolton.putmeon.R;
import com.romellbolton.putmeon.adapter.TrackRecyclerListAdapter;
import com.romellbolton.putmeon.viewmodel.SuggestedTrackViewModel;

public class FavoriteTracksActivity extends AppCompatActivity {

    private TrackRecyclerListAdapter tracksAdapter;
    private SuggestedTrackViewModel trackViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_recycler_view);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        tracksAdapter = new TrackRecyclerListAdapter(this, this);

        trackViewModel = ViewModelProviders.of(this).get(SuggestedTrackViewModel.class);
        trackViewModel.getAllTracks().observe(this, tracks -> tracksAdapter.setData(tracks));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tracksAdapter);
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return true;
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.addPost) {
//            trackViewModel.savePost(new Post("This is a post title", "This is a post content"));
//            return true;
//        } else
//            return super.onOptionsItemSelected(item);
//    }

}
