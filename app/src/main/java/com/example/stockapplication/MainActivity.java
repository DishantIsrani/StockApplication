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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                progressBar.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    fetchSuggestions(newText);
                    listView.setVisibility(View.VISIBLE);
                } else {
                    suggestions.clear();
                    adapter.notifyDataSetChanged();
                    listView.setVisibility(View.GONE);
                }
                return true;
            }
        });
        return true;
    }




    private void fetchSuggestions(String query) {
        String autoCompleteUrl = "https://finalbackend-419019.wl.r.appspot.com/api/auto/" + query;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, autoCompleteUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        suggestions.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject suggestion = response.getJSONObject(i);
                                String symbol = suggestion.getString("symbol");
                                String description = suggestion.getString("description");
                                suggestions.add(symbol + " - " + description);
                            }
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


    private void fetchwallet() {
        String walletUrl = "https://finalbackend-419019.wl.r.appspot.com/api/get-wallet";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, walletUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject walletData = response.getJSONObject(0);
                    double money = walletData.getDouble("money");

                    String formattedMoney = String.format("$%.2f", money);

                    TextView moneyTextView = findViewById(R.id.moneyTextView);
                    moneyTextView.setText(formattedMoney);

                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing wallet data", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(MainActivity.this, "Error fetching wallet data", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

}














