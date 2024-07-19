package com.example.stockapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stockapplication.R;

import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    newsdialog newsdialogshown;
    List<NewsItem> newsItemList;
    private Context context;

    public NewsAdapter(List<NewsItem> newsItemList, Context context, newsdialog newsdialogshown) {
        this.newsItemList = newsItemList;
        this.context = context;
        this.newsdialogshown = newsdialogshown;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news, parent, false);
        return new NewsViewHolder(itemView, newsdialogshown);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsItemList.get(position);

        // Bind data to views based on position
        if (position == 0) {
            // First news item layout
            holder.imageLarge.setVisibility(View.VISIBLE);
            holder.titleLarge.setVisibility(View.VISIBLE);
            holder.sourceLarge.setVisibility(View.VISIBLE);
            holder.LargeCard.setVisibility(View.VISIBLE);

            holder.titleLarge.setText(newsItem.getHeadline());
//            holder.sourceLarge.setText(context.getString(R.string.source_time,
//                    newsItem.getSource(), newsItem.getFormattedDateTime()));
            holder.sourceLarge.setText(context.getString(R.string.source_time,
                    newsItem.getSource(), newsItem.getElapsedTime()));

            // Load large image for first news item
            Glide.with(context)
                    .load(newsItem.getImage())
                    .centerCrop()
                    .into(holder.imageLarge);

            // Hide small image view
            holder.imageSmall.setVisibility(View.GONE);
            holder.source.setVisibility(View.GONE);
            holder.SmallCard.setVisibility(View.GONE);
            holder.title.setVisibility(View.GONE);
        } else {
            // Other news items layout
            holder.imageLarge.setVisibility(View.GONE);
            holder.LargeCard.setVisibility(View.GONE);
            holder.titleLarge.setVisibility(View.GONE);
            holder.sourceLarge.setVisibility(View.GONE);

//            holder.source.setText(context.getString(R.string.source_time,
//                    newsItem.getSource(), newsItem.getFormattedDateTime()));

            holder.source.setText(context.getString(R.string.source_time,
                    newsItem.getSource(), newsItem.getElapsedTime()));
            holder.title.setText(newsItem.getHeadline());
//            holder.source.setText(context.getString(R.string.source_time, newsItem.getElapsedTime()));


            // Load small image for other news items
            Glide.with(context)
                    .load(newsItem.getImage())
                    .centerCrop()
                    .into(holder.imageSmall);

            // Show small image view
            holder.SmallCard.setVisibility(View.VISIBLE);
            holder.imageSmall.setVisibility(View.VISIBLE);
            holder.source.setVisibility(View.VISIBLE);
            holder.title.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        // Limit to first 20 items
        return Math.min(newsItemList.size(), 20);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageLarge;
        public ImageView imageSmall;
        public TextView sourceTimeSmall;
        public TextView source;
        public TextView titleLarge;
        public TextView sourceLarge;
        public TextView title;
        private CardView LargeCard;
        private CardView SmallCard;

        public NewsViewHolder(@NonNull View itemView, newsdialog newsdialogshown) {
            super(itemView);
            SmallCard = itemView.findViewById(R.id.cardsmall);
            LargeCard = itemView.findViewById(R.id.cardlarge);
            imageLarge = itemView.findViewById(R.id.imageLarge);
            imageSmall = itemView.findViewById(R.id.imageSmall);
            sourceTimeSmall = itemView.findViewById(R.id.sourceTimeSmall);
            source = itemView.findViewById(R.id.source);
            titleLarge = itemView.findViewById(R.id.titleLarge);
            sourceLarge = itemView.findViewById(R.id.Sourcelarge);
            title = itemView.findViewById(R.id.title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(newsdialogshown != null){
                        int pos = getAbsoluteAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            newsdialogshown.onnewsclick(pos);
                        }
                    }
                }
            });
        }
    }
}
