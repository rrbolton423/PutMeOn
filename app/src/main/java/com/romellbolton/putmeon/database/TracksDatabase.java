package com.romellbolton.putmeon.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.romellbolton.putmeon.model.Track;

@Database(entities = {Track.class}, version = 1)
public abstract class TracksDatabase extends RoomDatabase {

    private static final Object sLock = new Object();
    private static TracksDatabase INSTANCE;

    public static TracksDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        TracksDatabase.class, "Tracks.db")
                        .allowMainThreadQueries()
                        .build();
            }
            return INSTANCE;
        }
    }

    public abstract TrackDao suggestedTrackDao();
}