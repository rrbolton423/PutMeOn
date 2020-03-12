package com.romellbolton.putmeon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.romellbolton.putmeon.database.TrackDao;
import com.romellbolton.putmeon.database.TracksDatabase;
import com.romellbolton.putmeon.model.Track;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackViewModel extends AndroidViewModel {
    private TrackDao trackDao;
    private ExecutorService executorService;

    public TrackViewModel(@NonNull Application application) {
        super(application);
        trackDao = TracksDatabase.getInstance(application).suggestedTrackDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Track>> getAllTracks() {
        return trackDao.findAll();
    }

    public void savePost(Track track) {
        executorService.execute(() -> trackDao.save(track));
    }

    public void deletePost(Track track) {
        executorService.execute(() -> trackDao.delete(track));
    }
}