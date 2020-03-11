package com.romellbolton.putmeon.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.romellbolton.putmeon.model.SuggestedTrack;

import java.util.List;

@Dao
public interface SuggestedTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<SuggestedTrack> suggestedTracks);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(SuggestedTrack track);

    @Update
    void update(SuggestedTrack track);

    @Delete
    void delete(SuggestedTrack track);

    @Query("SELECT * FROM SuggestedTrack")
    LiveData<List<SuggestedTrack>> findAll();
}