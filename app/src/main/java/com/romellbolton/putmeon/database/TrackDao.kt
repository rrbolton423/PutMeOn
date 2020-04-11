package com.romellbolton.putmeon.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romellbolton.putmeon.model.Track

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(tracks: List<Track?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(track: Track?)

    @Update
    fun update(track: Track?)

    @Delete
    fun delete(track: Track?)

    @Query("SELECT * FROM Track")
    fun findAll(): LiveData<List<Track?>?>?
}