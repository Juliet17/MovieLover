package com.example.movielover.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.movielover.data.Movie;
import com.example.movielover.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieAdapter extends RecyclerView.Adapter <MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private OnPosterClickListener onPosterClickListener;
    private OnReachEndListener onReachEndListener;

    public MovieAdapter() {  // constructor
        movies = new ArrayList<>();
    }

    public interface OnPosterClickListener {
        void onPosterClick(int position);
    }

    public interface OnReachEndListener {  // метод вызывается при достижении конца страницы
        void onReachEnd();
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if (movies.size() >= 20 && position > movies.size() -4 && onReachEndListener != null) {  // чтобы дальнейшие элементы подгружались, когда пользователь близок к концу стр
            onReachEndListener.onReachEnd();
        }
        Movie movie = movies.get(position);  // getting image
        Picasso.get().load(movie.getPosterPath()).into(holder.imageViewSmallPoster);  // downloading image
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewSmallPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPosterClickListener != null) {
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public void clear() {   // clearing the list
        this.movies.clear();
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void addMovies (List<Movie> movies) {   // method to add movies in the ArrayList
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public List<Movie> getMovies() {
        return movies;
    }
}