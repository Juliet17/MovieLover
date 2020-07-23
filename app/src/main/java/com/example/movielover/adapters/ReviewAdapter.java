package com.example.movielover.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.movielover.R;
import com.example.movielover.data.Review;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    // list of reviews
    private ArrayList<Review> reviews;

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.textViewContent.setText(review.getContent());  // устанавливаем тексты
        holder.textViewAuthor.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviews.size();   // returns qty of reviews
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewAuthor;
        private TextView textViewContent;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewContent = itemView.findViewById(R.id.textViewContent);
        }
    }
}