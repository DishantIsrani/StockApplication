package com.example.stockapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<Stock> stocks;
    private Context context;

    public FavoritesAdapter(Context context, List<Stock> stocks) {
        this.context = context;
        this.stocks = stocks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (stocks == null || stocks.isEmpty()) {
            return;
        }

        Stock stock = stocks.get(position);
        holder.tickerTextView.setText(stock.getTicker());
        holder.nameTextView.setText(stock.getName());
        holder.latestPriceTextView.setText("$"+String.format("%.2f", stock.getLatestPrice()));
        holder.changeTextView.setText("  $"+String.format("%.2f", stock.getChange()));
        holder.changePriceTextView.setText( "(" + String.format("%.2f", stock.getChangePrice()) +"%)");


        if (stock.getChangePrice() > 0) {
            holder.changeIcon.setImageResource(R.drawable.trending_up);
            holder.changePriceTextView.setTextColor(Color.GREEN);
            holder.changeTextView.setTextColor(Color.GREEN);
        } else if (stock.getChangePrice() < 0) {
            holder.changeIcon.setImageResource(R.drawable.trending_down);
            holder.changePriceTextView.setTextColor(Color.RED);
            holder.changeTextView.setTextColor(Color.RED);
        } else {
            holder.changeIcon.setVisibility(View.GONE);
            holder.changePriceTextView.setTextColor(Color.BLACK);
            holder.changeTextView.setTextColor(Color.BLACK);
        }


        holder.rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SecondActivity.class);
                intent.putExtra("query", stock.getTicker());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (stocks == null) {
            return 0;
        }
        return stocks.size();
    }


    public void deleteItem(int position) {
        Stock deletedItem = stocks.get(position);
        stocks.remove(position);
        notifyItemRemoved(position);

        // Make a request to your backend API to delete the stock
        String deleteUrl = "https://finalbackend-419019.wl.r.appspot.com/api/delete-watchlistmain/" + deletedItem.getTicker();
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle successful deletion response
                        Log.d("DELETE_SUCCESS", "Stock deleted successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Log.e("DELETE_ERROR", "Error deleting stock: " + error.toString());
                    }
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(context).add(deleteRequest);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tickerTextView;
        TextView nameTextView;
        TextView latestPriceTextView;
        TextView changeTextView;
        TextView changePriceTextView;
        ImageView changeIcon;
        ImageView rightArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tickerTextView = itemView.findViewById(R.id.companyfav);
            nameTextView = itemView.findViewById(R.id.industryfav);
            latestPriceTextView = itemView.findViewById(R.id.latestpricefav);
            changeTextView = itemView.findViewById(R.id.changefav);
            changePriceTextView = itemView.findViewById(R.id.changepricefav);
            changeIcon = itemView.findViewById(R.id.changeIconfav);
            rightArrow = itemView.findViewById(R.id.rightArrow);
        }
    }
}




