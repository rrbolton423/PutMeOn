package com.romellbolton.putmeon.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.romellbolton.putmeon.model.Track;

import java.util.List;

@Dao
public interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<Track> tracks);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Track track);

    @Update
    void update(Track track);

    @Delete
    void delete(Track track);

    @Query("SELECT * FROM Track")
    LiveData<List<Track>> findAll();
}