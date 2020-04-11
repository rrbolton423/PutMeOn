package com.romellbolton.putmeon.controller

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romellbolton.putmeon.R
import com.romellbolton.putmeon.adapter.FavoriteTracksAdapter
import com.romellbolton.putmeon.adapter.FavoriteTracksAdapter.OnDeleteButtonClickListener
import com.romellbolton.putmeon.adapter.FavoriteTracksAdapter.OnPlayButtonClickListener
import com.romellbolton.putmeon.model.Track
import com.romellbolton.putmeon.viewmodel.TrackViewModel
import java.util.*

class FavoriteTracksActivity : AppCompatActivity(), OnDeleteButtonClickListener, OnPlayButtonClickListener {
    private var mFavoriteTracksAdapter: FavoriteTracksAdapter? = null
    private var mPostViewModel: TrackViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        Objects.requireNonNull(supportActionBar)!!.setTitle(R.string.favorties_title)
        mFavoriteTracksAdapter = FavoriteTracksAdapter(this, this, this)
        mPostViewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        mPostViewModel!!.allTracks?.observe(this, Observer { tracks: List<Track?>? -> mFavoriteTracksAdapter!!.setmData(tracks as MutableList<Track>) })
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mFavoriteTracksAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
    }

    override fun onDeleteButtonClicked(track: Track?) {
        mPostViewModel!!.deletePost(track)
    }

    override fun onPlayButtonClicked(track: Track?) {
        if (track!!.url?.contains("null")!!) {
            Toast.makeText(this, R.string.no_preview_available, Toast.LENGTH_SHORT).show()
        } else {
            val intent = TrackActivity.newIntent(this, Track(track.artist, track.name, null, track.coverURL640x636, null, null, track.url))
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}