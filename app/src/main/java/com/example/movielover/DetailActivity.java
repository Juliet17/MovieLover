package com.example.movielover;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movielover.adapters.ReviewAdapter;
import com.example.movielover.adapters.TrailerAdapter;
import com.example.movielover.data.FavouriteMovie;
import com.example.movielover.data.MainViewModel;
import com.example.movielover.data.Movie;
import com.example.movielover.data.Review;
import com.example.movielover.data.Trailer;
import com.example.movielover.utils.JSONUtils;
import com.example.movielover.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewAddToFav;
    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ScrollView scrollViewInfo;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;

    private MainViewModel viewModel;

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
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lang = Locale.getDefault().getLanguage();
        imageViewAddToFav = findViewById(R.id.imageViewAddToFav);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);

        // getting movie id
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        // устанавливаем нужные значения элементам
        Picasso.get().load(movie.getBigPosterPath()).placeholder(R.drawable.placeholder_popcorn).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        setFavourite();
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));   // to get to the trailer
                startActivity(intentToTrailer);
            }
        });
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));   // setting LayoutManagers
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);   // setting adaptors
        recyclerViewTrailers.setAdapter(trailerAdapter);
        // getting the data
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(), lang);
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId(), lang);
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);
        scrollViewInfo.smoothScrollTo(0, 0);  // чтобы ScrollView перематывался на самый верх
    }

    public void onClickChangeFav(View view) {
        if (favouriteMovie == null) {
            viewModel.insertFavMovie(new FavouriteMovie(movie));
            Toast.makeText(this, R.string.add_to_fav, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavMovie(favouriteMovie);
            Toast.makeText(this, R.string.remove_from_fav, Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    // проверка, существует ли фильм в избранном. Если нет -- устанавливается соотв. звезда и наоборот
    private void setFavourite() {
        favouriteMovie = viewModel.getFavMovieById(id);
        if (favouriteMovie == null) {
            imageViewAddToFav.setImageResource(R.drawable.not_fav);
        } else {
            imageViewAddToFav.setImageResource(R.drawable.fav);
        }
    }
}
