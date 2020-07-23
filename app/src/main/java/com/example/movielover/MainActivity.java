package com.example.movielover;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.movielover.adapters.MovieAdapter;
import com.example.movielover.data.MainViewModel;
import com.example.movielover.data.Movie;
import com.example.movielover.utils.JSONUtils;
import com.example.movielover.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private Switch switchSort;
    private TextView textViewTopRated;
    private TextView textViewPopularity;
    private ProgressBar progressBarLoading;

    private MainViewModel viewModel;

    private static final int LOADER_ID = 17;
    private LoaderManager loaderManager;

    private static int page = 1;   // переменная, кот. считает подгруженные страницы
    private static boolean isLoading = false;
    private static int sortMethod;

    private static String lang;

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
        }
        return super.onOptionsItemSelected(item);
    }

    // about columns qty
    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);  // got the screen width in dp
        return width / 185 > 2 ? width / 185 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lang = Locale.getDefault().getLanguage();     // getting the language of the device
        loaderManager = LoaderManager.getInstance(this);   // получаем экземпляр загрузчика, кот. отвечает за все загрузки приложения
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        switchSort = findViewById(R.id.switchSort);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));  // grid of columns
        movieAdapter = new MovieAdapter();
        switchSort.setChecked(true);
        recyclerViewPosters.setAdapter(movieAdapter);  // устанавливаем адаптер у recyclerView
        // listener for Switch
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
                setSortMethod(isChecked);
            }
        });
        switchSort.setChecked(false);
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading) {
                    downLoadData(sortMethod, page);
                }
            }
        });
        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (page == 1) {
                    movieAdapter.setMovies(movies);
                }
            }
        });

    }

    public void onClickSetPopularity(View view) {
        setSortMethod(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setSortMethod(true);
        switchSort.setChecked(true);
    }

    private void setSortMethod(boolean isTopRated) {
        if (isTopRated) {
            textViewTopRated.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            textViewPopularity.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
            sortMethod = NetworkUtils.TOP_RATED;
        } else {
            sortMethod = NetworkUtils.POPULARITY;
            textViewPopularity.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            textViewTopRated.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        }
        downLoadData(sortMethod, page);
    }

    private void downLoadData(int sortMethod, int page) {
        URL url = NetworkUtils.buildURL(sortMethod, page, lang);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());  // вставляем данные в bundle
        loaderManager.restartLoader(LOADER_ID, bundle, this);    // запускаем загрузчик. 3й параметр - Слушатель. Он реализован в этой активности, поэтому this
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle bundle) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, bundle);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                progressBarLoading.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if (!movies.isEmpty()) {
            if (page == 1 ) {
                viewModel.deleteAllMovies();
                movieAdapter.clear();
            }
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
            movieAdapter.addMovies(movies);  // setting the film list in Adapter
            page++;
        }
        isLoading = false;
        progressBarLoading.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}