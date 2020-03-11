package com.romellbolton.putmeon.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.romellbolton.putmeon.model.SuggestedTrack;

@Database(entities = {SuggestedTrack.class}, version = 1)
public abstract class SuggestedTracksDatabase extends RoomDatabase {

    private static SuggestedTracksDatabase INSTANCE;

    public abstract SuggestedTrackDao suggestedTrackDao();

    private static final Object sLock = new Object();

    public static SuggestedTracksDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        SuggestedTracksDatabase.class, "Tracks.db")
                        .allowMainThreadQueries()
                        .build();
            }
            return INSTANCE;
        }
    }
}