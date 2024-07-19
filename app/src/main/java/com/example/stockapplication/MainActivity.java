package com.example.stockapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class MainActivity extends  AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    ProgressBar progressBar;
    Toolbar toolbar;
    SearchView searchView;
    ListView listView;
    List<String> suggestions;
    ArrayAdapter<String> suggestionAdapter;
    double Networth = 25000.00;
    TextView networthview;


    double WalletMoney = 0.0;



    List<Stock> stockList;
    RecyclerView recyclerView;
    FavoritesAdapter favoritesAdapter;

    boolean walletloaded = false;
    boolean portfolioloaded = false;
    boolean favortiesloaded = false;

    List<PortfolioData> portList;
    RecyclerView recyclerViewport;
    PortfolioAdapter portfolioAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        stockList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewfav);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesAdapter = new FavoritesAdapter(this, stockList);
        recyclerView.setAdapter(favoritesAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(favoritesAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ItemTouchHelper itemTouchHelperfavorites = new ItemTouchHelper(simpleCallbackfavorties);
        itemTouchHelperfavorites.attachToRecyclerView(recyclerView);




        portList = new ArrayList<>();
        recyclerViewport = findViewById(R.id.recyclerViewport);
        recyclerViewport.setLayoutManager(new LinearLayoutManager(this));
        portfolioAdapter = new PortfolioAdapter(this, portList);
        recyclerViewport.setAdapter(portfolioAdapter);

        ItemTouchHelper itemTouchHelperportfolio = new ItemTouchHelper(simpleCallbackportfolio);
        itemTouchHelperportfolio.attachToRecyclerView(recyclerViewport);



        TextView dateTextView = findViewById(R.id.date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
        dateTextView.setText(currentDate);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        listView = findViewById(R.id.listView);
        suggestions = new ArrayList<>();
        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        listView.setAdapter(suggestionAdapter);

        TextView finnhublink = findViewById(R.id.finnhublink);
        finnhublink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String finnhuburl = "https://finnhub.io/";
                Uri uri = Uri.parse(finnhuburl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        networthview = findViewById(R.id.networthdata);



        fetchWatchlist();
        fetchPortfolio();
        fetchwallet();

        networthview.setText( "$" + String.format("%.2f", Networth));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                fetchSuggestions(newText);
                return true;
            }
        });

        return true;
    }

    private void fetchwallet() {
        String walletUrl = "https://finalbackend-419019.wl.r.appspot.com/api/get-wallet";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, walletUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                walletloaded = response != null && response.length() != 0;

                if(walletloaded && portfolioloaded && favortiesloaded){
                    progressBar.setVisibility(View.GONE);

                }else{
                    progressBar.setVisibility(View.VISIBLE);
                }
                try {
                    JSONObject walletData = response.getJSONObject(0);
                    double money = walletData.getDouble("money");
                    StateManagement.saveWalletData(getApplicationContext(), (float)money);

                    WalletMoney = walletData.getDouble("money");

                    String formattedMoney = String.format("$%.2f", money);

                    TextView moneyTextView = findViewById(R.id.moneyTextView);
                    moneyTextView.setText(formattedMoney);




                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing wallet data", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error fetching wallet data", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchSuggestions(String query) {
        if (query.isEmpty()) {
            suggestions.clear();
            suggestionAdapter.notifyDataSetChanged();
            return;
        }

        String suggestionUrl = "https://finalbackend-419019.wl.r.appspot.com/api/auto/" + query;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, suggestionUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray resultArray = response.getJSONArray("result");
                    suggestions.clear();
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject suggestionObject = resultArray.getJSONObject(i);
                        String symbol = suggestionObject.getString("symbol");
                        String description = suggestionObject.getString("description");

                        if (!symbol.contains(".")) {
                            suggestions.add(symbol + " | " + description);
                        }
                    }
                    suggestionAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(request);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                String[] parts = selectedItem.split("\\|");
                String symbol = parts[0].trim();
                handleSymbolSelection(symbol);
            }
        });
    }

    private void handleSymbolSelection(String symbol) {
        searchView.setQuery(symbol, true);
    }

    private void fetchWatchlist() {
        String watchlistUrl = "https://finalbackend-419019.wl.r.appspot.com/api/get-watchlist";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, watchlistUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0) {
                            favortiesloaded = true;
                            if (walletloaded && portfolioloaded && favortiesloaded) {
                                progressBar.setVisibility(View.GONE);
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        }
                        System.out.println("THIS IS FETCH FAVORITES BEFORE REMOVE AND SAVE MAIN");

                        StateManagement.removeFavoritesDataFromPrefs(getApplicationContext());
                        StateManagement.saveFavoritesData(getApplicationContext(), response);

                        System.out.println("THIS IS FETCH FAVORITES and after remove and save MAIN");

//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject stockObject = response.getJSONObject(i);
//                                String ticker = stockObject.getString("ticker");
//                                String name = stockObject.getString("name");
//                                fetchLatestPrice(ticker, name);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchLatestPrice(String ticker, String name) {
        String latestPriceUrl = "https://finalbackend-419019.wl.r.appspot.com/api/latestprice/" + ticker;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, latestPriceUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            double latestPrice = response.getDouble("c");
                            double change = response.getDouble("d");
                            double changePrice = response.getDouble("dp");

                            Stock stock = new Stock(ticker, name, latestPrice, change, changePrice);
                            stock.setLatestPrice(latestPrice);
                            stock.setChange(change);
                            stock.setChangePrice(changePrice);

                            stockList.add(stock);
                            favoritesAdapter.notifyDataSetChanged();


                            favortiesloaded = true;
                            if(walletloaded && portfolioloaded && favortiesloaded){
                                progressBar.setVisibility(View.GONE);
                                networthview.setText( "$" + String.format("%.2f", Networth));
                            }else{
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchPortfolio() {
        String portfoliourl = "https://finalbackend-419019.wl.r.appspot.com/api/get-portfolio";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, portfoliourl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0) {
                            portfolioloaded = true;
                            if (walletloaded && portfolioloaded && favortiesloaded) {
                                progressBar.setVisibility(View.GONE);
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        }



                        StateManagement.removePortfolioDataFromPrefs(getApplicationContext());
                        StateManagement.savePortfolioData(getApplicationContext(), response);


//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject portstockObject = response.getJSONObject(i);
//                                String ticker = portstockObject.getString("ticker");
//                                String name = portstockObject.getString("name");
//                                double quantity = portstockObject.getDouble("quantity");
//                                double totalCost = portstockObject.getDouble("total");
//                                double dblatestprice = portstockObject.getDouble("currentPrice");
//
//                                fetchPortfolioPrice(ticker, name, quantity, totalCost, dblatestprice);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchPortfolioPrice(String ticker, String name, double quantity, double totalCost, double dblatestprice) {
        String latestPriceUrl = "https://finalbackend-419019.wl.r.appspot.com/api/latestprice/" + ticker;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, latestPriceUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double totalportvalue = 0.0;
                            double latestPrice = response.getDouble("c");

                            double marketvalueport;
                            double avgCost;
                            double calmid;
                            double changepricetotal;
                            double totalCoststock;
                            double pricetotalpercent;


                            if(quantity == 0){
                                marketvalueport = 0;
                                changepricetotal = 0;
                                pricetotalpercent =0;
                                totalportvalue += marketvalueport;
                                double newnetworth = WalletMoney + totalportvalue;

                                networthview.setText("$"+ String.format("%.2f", newnetworth));
                            }else{
                                marketvalueport = latestPrice * quantity;
                                avgCost = (totalCost / quantity);
                                calmid = latestPrice - avgCost;
                                changepricetotal = calmid * quantity;
                                totalCoststock = dblatestprice * quantity;
                                pricetotalpercent = (changepricetotal / totalCoststock) * 100;

                                totalportvalue += marketvalueport;
                                double newnetworth = WalletMoney + totalportvalue;

                                networthview.setText("$"+ String.format("%.2f", newnetworth));

                            }




                            PortfolioData port = new PortfolioData(ticker, quantity, marketvalueport, changepricetotal, pricetotalpercent);
                            port.setquantity(quantity);
                            port.setvaluecalcheckdocs(marketvalueport);
                            port.setvaluecalcheckdocs2(changepricetotal);
                            port.setvaluecalcheckdocs3(pricetotalpercent);

                            portList.add(port);
                            portfolioAdapter.notifyDataSetChanged();

                            double newnetworth = WalletMoney + totalportvalue;

                            networthview.setText("$"+ String.format("%.2f", newnetworth));

                            portfolioloaded = true;
                            if(walletloaded && portfolioloaded && favortiesloaded){
                                progressBar.setVisibility(View.GONE);
                            }else{
                                progressBar.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    ItemTouchHelper.SimpleCallback simpleCallbackportfolio = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START |ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromposition = viewHolder.getAdapterPosition();
            int toposition = target.getAdapterPosition();

            Collections.swap(portList, fromposition, toposition);
            recyclerViewport.getAdapter().notifyItemMoved(fromposition, toposition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    ItemTouchHelper.SimpleCallback simpleCallbackfavorties = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START |ItemTouchHelper.END, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromposition = viewHolder.getAdapterPosition();
            int toposition = target.getAdapterPosition();

            Collections.swap(stockList, fromposition, toposition);
            recyclerView.getAdapter().notifyItemMoved(fromposition, toposition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key.equals(StateManagement.WALLET_DATA_LOAD)) {
            double money = StateManagement.getWalletData(this);
            System.out.println("IN MAIN ACTIVITY SHARED PREF" + money);
            WalletMoney = money;

            String formattedMoney = String.format("$%.2f", money);

            TextView moneyTextView = findViewById(R.id.moneyTextView);
            moneyTextView.setText(formattedMoney);
        } else if(key.equals(StateManagement.PORTFOLIO_DATA_LOAD)) {
            System.out.println("STATE MANAGEMENT PORTOFLIO ON SHARED PREF function");


            try {
                portList.clear();
                JSONArray response = StateManagement.getPortfolioData(this);
                for (int i = 0; i < response.length(); i++) {
                    JSONObject portstockObject = response.getJSONObject(i);
                    String ticker = portstockObject.getString("ticker");
                    String name = portstockObject.getString("name");
                    double quantity = portstockObject.getDouble("quantity");
                    double totalCost = portstockObject.getDouble("total");
                    double dblatestprice = portstockObject.getDouble("currentPrice");

                    if(quantity == 0){
                        StateManagement.deleteStockDataFromPortfolio(getApplicationContext(), ticker);
                    }else{
                        fetchPortfolioPrice(ticker, name, quantity, totalCost, dblatestprice);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(key.equals(StateManagement.FAVORITES_DATA_LOAD)){
            System.out.println("STATE MANAGEMENT Favorites ON SHARED PREF function");
            try {
                stockList.clear();
                JSONArray response = StateManagement.getFavoritesData(this);
                for (int i = 0; i < response.length(); i++) {
                    JSONObject stockObject = response.getJSONObject(i);
                    String ticker = stockObject.getString("ticker");
                    String name = stockObject.getString("name");

                    fetchLatestPrice(ticker, name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        StateManagement.registerStatePrefs(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StateManagement.unregisterStatePrefs(this, this);
    }
}


