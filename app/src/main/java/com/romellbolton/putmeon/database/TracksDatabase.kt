package com.romellbolton.putmeon.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.romellbolton.putmeon.model.Track

@Database(entities = [Track::class], version = 1)
abstract class TracksDatabase : RoomDatabase() {
    abstract fun suggestedTrackDao(): TrackDao?

    companion object {
        private val sLock = Any()
        private var INSTANCE: TracksDatabase? = null
        fun getInstance(context: Context): TracksDatabase? {
            synchronized(sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            TracksDatabase::class.java, "Tracks.db")
                            .allowMainThreadQueries()
                            .build()
                }
                return INSTANCE
            }
        }
    }
}