package com.example.movielover.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface MovieDao {
    // methods
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM favourite_movies")
    LiveData<List<FavouriteMovie>> getAllFavMovies();

    // to get a defined movie
    @Query("SELECT * FROM movies WHERE id == :movieId")
    Movie getMovieById(int movieId);

    @Query("SELECT * FROM favourite_movies WHERE id == :movieId")
    FavouriteMovie getFavMovieById(int movieId);

    // method to remove all data
    @Query("DELETE FROM movies")
    void deleteAllMovies();

    // to insert data
    @Insert
    void insertMovie(Movie movie);

    // to delete one element
    @Delete
    void deleteMovie(Movie movie);

    @Insert
    void insertFavMovie(FavouriteMovie movie);

    // to delete one element
    @Delete
    void deleteFavMovie(FavouriteMovie movie);
}
