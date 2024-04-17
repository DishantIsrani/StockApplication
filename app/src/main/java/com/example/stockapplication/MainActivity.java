package com.example.stockapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.android.volley.NetworkResponse;
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

    ProgressBar progressBar;
    Toolbar toolbar;
    SearchView searchView;
    ListView listView;
    List<String> suggestions;
    ArrayAdapter<String> suggestionAdapter;


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
        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        listView.setAdapter(suggestionAdapter);

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


}