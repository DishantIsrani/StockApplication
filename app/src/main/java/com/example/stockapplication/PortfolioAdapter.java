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
public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {

    private List<PortfolioData> portfoliostocks;
    private Context context;


    public PortfolioAdapter(Context context, List<PortfolioData> portfoliostocks) {
        this.context = context;
        this.portfoliostocks = portfoliostocks;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (portfoliostocks == null || portfoliostocks.isEmpty()) {
            return;
        }


        PortfolioData portstock = portfoliostocks.get(position);
        holder.tickerTextView.setText(portstock.getTicker());
        holder.quantitytextview.setText(String.format("%.0f", portstock.getquantity()) + " shares");
        holder.MarketValueTextView.setText("$"+String.format("%.2f", portstock.getvaluecalcheckdocs()));
        holder.ChangeTotalPriceTextView.setText("  $"+String.format("%.2f", portstock.getvaluecalcheckdocs2()));
        holder.changePricePercentTextView.setText( "(" + String.format("%.2f", portstock.getvaluecalcheckdocs3()) +"%)");



        if (portstock.getvaluecalcheckdocs2() > 0) {
            holder.changeIcon.setImageResource(R.drawable.trending_up);
            holder.changePricePercentTextView.setTextColor(Color.GREEN);
            holder.ChangeTotalPriceTextView.setTextColor(Color.GREEN);
        } else if (portstock.getvaluecalcheckdocs2() < 0) {
            holder.changeIcon.setImageResource(R.drawable.trending_down);
            holder.changePricePercentTextView.setTextColor(Color.RED);
            holder.ChangeTotalPriceTextView.setTextColor(Color.RED);
        } else {
            holder.changeIcon.setVisibility(View.GONE);
            holder.changePricePercentTextView.setTextColor(Color.BLACK);
            holder.ChangeTotalPriceTextView.setTextColor(Color.BLACK);
        }


        holder.rightArrowport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SecondActivity.class);
                intent.putExtra("query", portstock.getTicker());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (portfoliostocks == null) {
            return 0;
        }
        return portfoliostocks.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tickerTextView;
        TextView quantitytextview;
        TextView MarketValueTextView;
        TextView ChangeTotalPriceTextView;
        TextView changePricePercentTextView;
        ImageView changeIcon;
        ImageView rightArrowport;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tickerTextView = itemView.findViewById(R.id.companyport);
            quantitytextview = itemView.findViewById(R.id.quantityport);
            MarketValueTextView = itemView.findViewById(R.id.marketvalueport);
            ChangeTotalPriceTextView = itemView.findViewById(R.id.changepricetotalcostport);
            changePricePercentTextView = itemView.findViewById(R.id.changepricepercentport);
            changeIcon = itemView.findViewById(R.id.changeIconport);

            rightArrowport = itemView.findViewById(R.id.rightArrowport);
        }
    }


}
