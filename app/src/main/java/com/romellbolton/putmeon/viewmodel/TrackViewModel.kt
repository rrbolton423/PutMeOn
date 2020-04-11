package com.romellbolton.putmeon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.romellbolton.putmeon.database.TrackDao
import com.romellbolton.putmeon.database.TracksDatabase
import com.romellbolton.putmeon.model.Track
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TrackViewModel(application: Application) : AndroidViewModel(application) {
    private val trackDao: TrackDao
    private val executorService: ExecutorService
    val allTracks: LiveData<List<Track?>?>?
        get() = trackDao.findAll()

    fun savePost(track: Track?) {
        executorService.execute { trackDao.save(track) }
    }

    fun deletePost(track: Track?) {
        executorService.execute { trackDao.delete(track) }
    }

    init {
        trackDao = TracksDatabase.getInstance(application).suggestedTrackDao()
        executorService = Executors.newSingleThreadExecutor()
    }
}