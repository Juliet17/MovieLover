package com.example.movielover.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.movielover.R;
import com.example.movielover.data.Trailer;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>{
    private ArrayList<Trailer> trailers;
    private OnTrailerClickListener onTrailerClickListener;

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // overriding ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);   // returning a new object ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.textViewVideoName.setText(trailer.getName());  // устанавливаем название у textView
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    // to open video in internet we need to add Listener
    public interface OnTrailerClickListener {
        void onTrailerClick(String url);   // video url is its param
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewVideoName;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewVideoName = itemView.findViewById(R.id.textViewVideoName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTrailerClickListener != null) {
                        onTrailerClickListener.onTrailerClick(trailers.get(getAdapterPosition()).getKey());   // got the position and to get url, invoke getKey();
                    }
                }
            });
        }
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public void setOnTrailerClickListener(OnTrailerClickListener onTrailerClickListener) {
        this.onTrailerClickListener = onTrailerClickListener;
    }
}
