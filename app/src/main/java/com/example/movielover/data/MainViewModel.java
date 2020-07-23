package com.example.movielover.data;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainViewModel extends AndroidViewModel {

    // creating a database object
    private static MovieDatabase database;
    // creating a movie list
    private LiveData<List<Movie>> movies;
    private LiveData<List<FavouriteMovie>> favMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());   // присваиваем значение переменной
        movies  = database.movieDao().getAllMovies();   // будет автоматически добавляться в др потоке
        favMovies = database.movieDao().getAllFavMovies();
    }

    // methods to get access to the data
    public Movie getMovieById(int id) {
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FavouriteMovie getFavMovieById(int id) {
        try {
            return new GetFavMovieTask().execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<FavouriteMovie>> getFavMovies() {
        return favMovies;
    }

    public void deleteAllMovies() {
        new DeleteMoviesTask().execute();
    }

    public void insertMovie(Movie movie) {
        new InsertMovieTask().execute(movie);
    }

    public void deleteMovie(Movie movie) {
        new DeleteTask().execute(movie);
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void insertFavMovie(FavouriteMovie movie) {
        new InsertFavMovieTask().execute(movie);
    }

    public void deleteFavMovie(FavouriteMovie movie) {
        new DeleteFavTask().execute(movie);
    }


    private static class DeleteFavTask extends AsyncTask<FavouriteMovie, Void, Void> {

        @Override
        protected Void doInBackground(FavouriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().deleteFavMovie(movies[0]);
            }
            return null;
        }
    }

    private static class InsertFavMovieTask extends AsyncTask<FavouriteMovie, Void, Void> {

        @Override
        protected Void doInBackground(FavouriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertFavMovie(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Movie, Void, Void> {

        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class InsertMovieTask extends AsyncTask<Movie, Void, Void> {

        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertMovie(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteMoviesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... integers) {
            database.movieDao().deleteAllMovies();
            return null;
        }
    }

    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class GetFavMovieTask extends AsyncTask<Integer, Void, FavouriteMovie> {

        @Override
        protected FavouriteMovie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getFavMovieById(integers[0]);
            }
            return null;
        }
    }
}
