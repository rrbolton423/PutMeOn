package com.romellbolton.putmeon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.romellbolton.putmeon.database.SuggestedTrackDao;
import com.romellbolton.putmeon.database.SuggestedTracksDatabase;
import com.romellbolton.putmeon.model.SuggestedTrack;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuggestedTrackViewModel extends AndroidViewModel {
    private SuggestedTrackDao suggestedTrackDao;
    private ExecutorService executorService;

    public SuggestedTrackViewModel(@NonNull Application application) {
        super(application);
        suggestedTrackDao = SuggestedTracksDatabase.getInstance(application).suggestedTrackDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    LiveData<List<SuggestedTrack>> getAllTracks() {
        return suggestedTrackDao.findAll();
    }

    void savePost(SuggestedTrack suggestedTrack) {
        executorService.execute(() -> suggestedTrackDao.save(suggestedTrack));
    }

    void deletePost(SuggestedTrack suggestedTrack) {
        executorService.execute(() -> suggestedTrackDao.delete(suggestedTrack));
    }
}