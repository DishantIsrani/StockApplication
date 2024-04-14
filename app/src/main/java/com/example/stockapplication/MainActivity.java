package com.example.stockapplication;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
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


public class MainActivity extends  AppCompatActivity{

    JSONObject CompanyDescData;
    TextView data;
    ProgressBar progressBar;
    Toolbar toolbar;
    SearchView searchView;
    ListView listView;
    ArrayAdapter<String> adapter;
    List<String> suggestions;




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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar.setVisibility(View.VISIBLE);

        listView = findViewById(R.id.listView);
        suggestions = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        listView.setAdapter(adapter);


        fetchwallet();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        searchView = (SearchView) searchItem.getActionView();
//
//        // Set up search view listener
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Handle search query submission
//                progressBar.setVisibility(View.VISIBLE);
//                fetchDataFromServer(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return true;
//            }
//        });
//
//        return true;
//    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        searchView = (SearchView) searchItem.getActionView();
//
//        // Set up search view listener
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Handle search query submission
//                progressBar.setVisibility(View.VISIBLE);
//                fetchDataFromServer(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (newText.length() > 0) {
//                    // Fetch suggestions for the entered query
//                    fetchSuggestions(newText);
//                } else {
//                    // Clear suggestions if query is empty
//                    suggestions.clear();
//                    adapter.notifyDataSetChanged();
//                }
//                return true;
//            }
//        });
//
//        return true;
//    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        // Set up search view listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                progressBar.setVisibility(View.VISIBLE);
                fetchDataFromServer(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    // Fetch suggestions for the entered query
                    fetchSuggestions(newText);
                    // Show the ListView
                    listView.setVisibility(View.VISIBLE);
                } else {
                    // Clear suggestions if query is empty
                    suggestions.clear();
                    adapter.notifyDataSetChanged();
                    // Hide the ListView
                    listView.setVisibility(View.GONE);
                }
                return true;
            }
        });

        return true;
    }








//    private void fetchSuggestions(String query) {
//        String autoCompleteUrl = "https://finalbackend-419019.wl.r.appspot.com/api/auto/" + query;
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, autoCompleteUrl, null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        suggestions.clear();
//                        try {
//                            // Parse the JSON response and add suggestions to the list
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject suggestion = response.getJSONObject(i);
//                                String symbol = suggestion.getString("symbol");
//                                String description = suggestion.getString("description");
//                                suggestions.add(symbol + " - " + description);
//                            }
//                            adapter.notifyDataSetChanged();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(MainActivity.this, "Error fetching suggestions", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//        Volley.newRequestQueue(this).add(request);
//    }


    private void fetchSuggestions(String query) {
        String autoCompleteUrl = "https://finalbackend-419019.wl.r.appspot.com/api/auto/" + query;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, autoCompleteUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        suggestions.clear();
                        try {
                            // Parse the JSON response and add suggestions to the list
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject suggestion = response.getJSONObject(i);
                                String symbol = suggestion.getString("symbol");
                                String description = suggestion.getString("description");
                                suggestions.add(symbol + " - " + description);
                            }
                            // Notify the adapter of the data set change
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Error fetching suggestions", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }



    private void fetchDataFromServer(String query) {
        data = findViewById(R.id.data);
        String companyurl = "https://finalbackend-419019.wl.r.appspot.com/api/companydesc/" + query;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, companyurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
//                    CompanyDescData = response;
                    String exchange = response.getString("exchange");
                    String finnhubIndustry = response.getString("finnhubIndustry");

                    String allData = exchange + finnhubIndustry;
                    data.setText(allData);
                    progressBar.setVisibility(View.GONE);

                }catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        Volley.newRequestQueue(this).add(request);


    }



    private void fetchwallet() {
        String walletUrl = "https://finalbackend-419019.wl.r.appspot.com/api/get-wallet";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, walletUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    JSONObject walletData = data.getJSONObject(0);
                    double money = walletData.getDouble("money");

                    // Format the money value to include a "$" sign
                    String formattedMoney = String.format("$%.2f", money);

                    // Update the TextView with the formatted money value
                    TextView moneyTextView = findViewById(R.id.moneyTextView);
                    moneyTextView.setText(formattedMoney);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(MainActivity.this, "Error fetching wallet data", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the Volley queue
        Volley.newRequestQueue(this).add(request);
    }

}














