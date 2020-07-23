package com.example.movielover;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.movielover.adapters.MovieAdapter;
import com.example.movielover.data.FavouriteMovie;
import com.example.movielover.data.MainViewModel;
import com.example.movielover.data.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavouriteMovies;
    private MovieAdapter adapter;
    private MainViewModel viewModel;

    // запускаем меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {   // в кач-ве параметра принимает пункт меню, на который нажали
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFav = new Intent(this, FavouritesActivity.class);
                startActivity(intentToFav);
                break;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewFavouriteMovies = findViewById(R.id.recyclerViewFavouriteMovies);
        recyclerViewFavouriteMovies.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MovieAdapter();
        recyclerViewFavouriteMovies.setAdapter(adapter);
        // getting fav movies
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        LiveData<List<FavouriteMovie>> favouriteMovie = viewModel.getFavMovies();
        favouriteMovie.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
                List<Movie> movies = new ArrayList<>();
                if (favouriteMovies != null) {
                    movies.addAll(favouriteMovies);
                    adapter.setMovies(movies);
                }
            }
        });

        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(FavouritesActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });

    }
}
