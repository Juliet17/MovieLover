package com.example.movielover.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class, FavouriteMovie.class}, version = 3, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase database;
    private final static String DB_NAME = "movies.db";

    // блок синхроизации, чтобы нельзя было создать 2 бд, если кто-то обратится из разных потоков
    private static final Object  LOCK = new Object();

    public static MovieDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, MovieDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();  // fallback - чтобы при запуске создавались новые таблицы
            }
        }
        return database;
    }

    // method that returns Dao
    public abstract MovieDao movieDao();
}
