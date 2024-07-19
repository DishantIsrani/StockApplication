package com.example.stockapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import kotlin.text.StringsKt;
import android.os.AsyncTask;

public class SecondActivity extends AppCompatActivity implements newsdialog, SharedPreferences.OnSharedPreferenceChangeListener {


    private RequestQueue requestQueue;
    private WebView trendsChart;
    private WebView supriseChart;
    private ImageView changeIcon;


    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsItem> newsItemList = new ArrayList<>();


    Dialog newsdialog, tradedialog, buydialog, selldialog;
    Button Tradebutton, buydone, selldone;


    TextView newsSource;
    TextView newsTime;
    TextView newsHeadline;
    TextView newsDescription;
    ImageView Google;
    ImageView Twitter;
    ImageView Facebook;


    TextView tradeview;
    TextView stockpriceview;
    TextView AmountText;
    double priceofstock;

    int quantitysubmit;
    int totalquantity;

    double existingTotalcost;
    TextView buytext;
    TextView selltext;


    String companyName;
    double mainCurrentPrice;
    MenuItem staricon;
    boolean isStockInWatchlist = false;
    int existingquantity;
    double Walletmoney;


    double updatetotalprice;
    ProgressBar secondprogress;
    int dataFetchCounter = 0;

    String query;
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        query = getIntent().getStringExtra("query");

        getSupportActionBar().setTitle(query);

        secondprogress = findViewById(R.id.progressBarsecond);
        secondprogress.setVisibility(View.VISIBLE);


        changeIcon = findViewById(R.id.changeIcon);


        checkStockInWatchlist(query);


        loadingstatus();
        fetchCompanyData(query);
        fetchLatestPrice(query);
//        fetchPortfolioData(query);
        fetchPeers(query);
        fetchInsiderSentiments(query);
        fetchTrends(query);
        fetchSurprises(query);
        fetchNews(query);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(newsItemList, this, this);
        recyclerView.setAdapter(newsAdapter);


        newsdialog = new Dialog(SecondActivity.this);
        newsdialog.setContentView(R.layout.newsdialog);
        newsdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        tradedialog = new Dialog(SecondActivity.this);
        tradedialog.setContentView(R.layout.tradebutton);
        tradedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        buydialog = new Dialog(SecondActivity.this);
        buydialog.setContentView(R.layout.sellsuccess);
        buydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        selldialog = new Dialog(SecondActivity.this);
        selldialog.setContentView(R.layout.sellsuccess);
        selldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        newsSource = newsdialog.findViewById(R.id.Source);
        newsTime = newsdialog.findViewById(R.id.Time);
        newsHeadline = newsdialog.findViewById(R.id.headline);
        newsDescription = newsdialog.findViewById(R.id.description);
        Google = newsdialog.findViewById(R.id.imageView);
        Twitter = newsdialog.findViewById(R.id.imageView2);
        Facebook = newsdialog.findViewById(R.id.imageView3);




        fetchwallet();
        Tradebutton = findViewById(R.id.tradebutton);
        Tradebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tradeview = tradedialog.findViewById(R.id.TradeTextView);
                tradeview.setText("Trade " + companyName + " shares");


                AmountText = tradedialog.findViewById(R.id.remainingamount);

                AmountText.setText(String.format("%.2f", Walletmoney) + " left in wallet to buy " + query);


                EditText quantityentered = tradedialog.findViewById(R.id.inputquantity);
                stockpriceview = tradedialog.findViewById(R.id.stockprice);
                stockpriceview.setText(" 0 * " + String.format("%.2f", mainCurrentPrice) + "/share = ");


                quantityentered.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        if (!TextUtils.isEmpty(s)) {
                            try {
                                int quantityInt = Integer.parseInt(s.toString());

                                if (quantityInt < 0) {
                                    Toast.makeText(getApplicationContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                priceofstock = (quantityInt * mainCurrentPrice);
                                stockpriceview.setText(s.toString() + " * " + String.format("%.2f", mainCurrentPrice) + "/share = " + String.format("%.2f", priceofstock));
                            } catch (NumberFormatException e) {
                                Toast.makeText(getApplicationContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            stockpriceview.setText("0 * " + String.format("%.2f", mainCurrentPrice) + "/share = ");
                        }
                    }


//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        int quantityInt = Integer.parseInt(String.valueOf(s));
//                        priceofstock = (quantityInt * mainCurrentPrice);
//                        stockpriceview.setText(s.toString() + " * " + String.format("%.2f", mainCurrentPrice) + "/share = " + String.format("%.2f", priceofstock));
//                        String input = s.toString();
//                        if (!input.isEmpty()) {
//                            try {
//                                if (quantityInt < 0) {
//                                    Toast.makeText(getApplicationContext(), "Cannot buy/sell non-positive shares", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    stockpriceview.setText(s.toString() + " * " + String.format("%.2f", mainCurrentPrice) + "/share = " + String.format("%.2f", priceofstock));
//                                }
//                            } catch (NumberFormatException e) {
//                                Toast.makeText(getApplicationContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                tradedialog.show();

                TextView successtext = buydialog.findViewById(R.id.quantitytext);


                Button buybutton = tradedialog.findViewById(R.id.buybutton);
                buybutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Integer.parseInt(String.valueOf(quantityentered.getText())) <= 0) {
                            Toast.makeText(getApplicationContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double totalPrice = Double.parseDouble(String.valueOf(quantityentered.getText())) * mainCurrentPrice;

                        if (totalPrice > Walletmoney) {

                            Toast.makeText(getApplicationContext(), "Not enough money to buy", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double buyWalletMoney = Walletmoney - totalPrice;
                        updatetotalprice = existingTotalcost + totalPrice;



                        JSONObject requestData = new JSONObject();
                        try {
                            totalquantity = existingquantity + quantitysubmit;
                            quantitysubmit = Integer.parseInt(quantityentered.getText().toString());
                            requestData.put("ticker", query);
                            requestData.put("name", companyName);
                            requestData.put("currentPrice", mainCurrentPrice);
                            requestData.put("quantity", quantitysubmit);
                            requestData.put("total", totalPrice);

                            System.out.println("PRINTITNG THE STOCK DATA IN MY LOG" + requestData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String portdataurl = "https://finalbackend-419019.wl.r.appspot.com/api/add-portfolio";

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, portdataurl, requestData,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        System.out.println("THIS IS THE RESPONSEEE!!!" + response);
//                                        StateManagement.savePortfolioData(getApplicationContext(), requestData);
                                        try {
                                            if (existingquantity > 0) {
                                                StateManagement.updateStockDataInPortfolio(getApplicationContext(), requestData);
//                                                StateManagement.addStockDataToPortfolio(getApplicationContext(), requestData);
                                            } else {
                                                StateManagement.addStockDataToPortfolio(getApplicationContext(), requestData);
                                            }

                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        Toast.makeText(getApplicationContext(), query + " Stock bought successfully", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                });


                        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);


                        JSONObject walletData = new JSONObject();
                        try {
                            walletData.put("money", buyWalletMoney);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String walletUpdateUrl = "https://finalbackend-419019.wl.r.appspot.com/api/add-wallet";


                        JsonObjectRequest walletUpdateRequest = new JsonObjectRequest(Request.Method.POST, walletUpdateUrl, walletData,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        StateManagement.saveWalletData(getApplicationContext(), (float) buyWalletMoney);
//                                        Toast.makeText(getApplicationContext(), "Wallet updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                });


                        Volley.newRequestQueue(getApplicationContext()).add(walletUpdateRequest);

                        tradedialog.dismiss();
                        successtext.setText("Bought");
                        buydialog.show();

                        buytext = buydialog.findViewById(R.id.quantitytext);
                        buytext.setText("You have Successfully bought " + quantitysubmit + "\n stocks of " + query);

                        buydone = buydialog.findViewById(R.id.donebutton);
                        buydone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buydialog.dismiss();
                            }
                        });
                    }
                });


                Button sellbutton = tradedialog.findViewById(R.id.Sellbutton);
                sellbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("A" + existingquantity);

                        if (Integer.parseInt(String.valueOf(quantityentered.getText())) <= 0) {
                            Toast.makeText(getApplicationContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double totalPrice = Double.parseDouble(String.valueOf(quantityentered.getText())) * mainCurrentPrice;

                        if (Integer.parseInt(quantityentered.getText().toString()) > existingquantity) {

                            Toast.makeText(getApplicationContext(), "Not enough shares to sell", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        totalquantity = existingquantity - Integer.parseInt(quantityentered.getText().toString());
                        System.out.println("b" + existingquantity  + Integer.parseInt(quantityentered.getText().toString()));
                        double sellWalletMoney = Walletmoney + totalPrice;

                        updatetotalprice = existingTotalcost - totalPrice;

                        System.out.println("C" + existingquantity);
                        quantitysubmit = Integer.parseInt(quantityentered.getText().toString());
                        System.out.println(existingquantity + "D" +  quantitysubmit);

                        JSONObject sellrequestData = new JSONObject();
                        try {
                            sellrequestData.put("ticker", query);
                            sellrequestData.put("name", companyName);
                            sellrequestData.put("currentPrice", mainCurrentPrice);
                            sellrequestData.put("quantity", totalquantity);
                            sellrequestData.put("total", updatetotalprice);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(existingquantity + "E" + quantitysubmit + sellrequestData);


                        String sellportdataurl = "https://finalbackend-419019.wl.r.appspot.com/api/sell-portfolio";
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, sellportdataurl, sellrequestData,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
//                                            StateManagement.updateStockDataInPortfolio(getApplicationContext(), sellrequestData);

                                            if (totalquantity == 0) {
//                                                StateManagement.updateStockDataInPortfolio(getApplicationContext(), sellrequestData);
                                                StateManagement.deleteStockDataFromPortfolio(getApplicationContext(), query);
                                            } else {
                                                System.out.println("UPDATED STOCK IN THE PORTFOLIO");
                                                StateManagement.updateStockDataInPortfolio(getApplicationContext(), sellrequestData);
                                                System.out.println("AFTER UPDATED STOCK IN THE PORTFOLIO");
                                            }


                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }

                                        Toast.makeText(getApplicationContext(), query + " Stock Sold successfully", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                });


                        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);


                        JSONObject walletData2 = new JSONObject();
                        try {
                            walletData2.put("money", sellWalletMoney);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String walletUpdateUrl2 = "https://finalbackend-419019.wl.r.appspot.com/api/add-wallet";


                        JsonObjectRequest walletUpdateRequest2 = new JsonObjectRequest(Request.Method.POST, walletUpdateUrl2, walletData2,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        StateManagement.saveWalletData(getApplicationContext(), (float) sellWalletMoney);
//                                        Toast.makeText(getApplicationContext(), "Wallet updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                });


                        Volley.newRequestQueue(getApplicationContext()).add(walletUpdateRequest2);

                        tradedialog.dismiss();
                        successtext.setText("SOLD");
                        selldialog.show();

                        selltext = selldialog.findViewById(R.id.quantitytext);
                        selltext.setText("You have Successfully sold " + quantitysubmit + "\n stocks of " + query);


                        selldone = selldialog.findViewById(R.id.donebutton);
                        selldone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    StateManagement.updateStockDataInPortfolio(getApplicationContext(), sellrequestData);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                selldialog.dismiss();
                            }
                        });
                    }
                });

            }
        });


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), query, flag);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_hour_chart);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_hist_chart);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue));

    }


    private void loadingstatus() {
        dataFetchCounter++;
        if (dataFetchCounter >= 9) {
            secondprogress.setVisibility(View.GONE);

        }
    }


    @Override
    public void onnewsclick(int position) {
//        Toast.makeText(SecondActivity.this, position+"", Toast.LENGTH_SHORT).show();
        newsSource.setText(newsItemList.get(position).getSource());
        newsTime.setText(newsItemList.get(position).getFormattedDateTime() + "");
        newsHeadline.setText(newsItemList.get(position).getHeadline());
        newsDescription.setText(newsItemList.get(position).getDescription());
        newsdialog.show();


//        String link = newsItemList.get(position).getSource();
        Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newsurllink = newsItemList.get(position).getNewsurllink();
                Uri uri = Uri.parse(newsurllink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        Twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newsurllink = newsItemList.get(position).getNewsurllink();
                Uri uri = Uri.parse("https://twitter.com/intent/tweet?text=Check out this link:  &url=" + newsurllink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newsurllink = newsItemList.get(position).getNewsurllink();
                String urlToShare = "https://www.facebook.com/sharer/sharer.php?u=" + Uri.encode(newsurllink);

                Uri uri = Uri.parse(urlToShare);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        String symbol;
        int flag;

        public SectionsPagerAdapter(FragmentManager fm, String query, int flag) {
            super(fm);
            this.flag = flag;
            this.symbol = query;
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HourlyChartFragment.newInstance(symbol, flag);
                case 1:
                    return HistoricalChartFragment.newInstance(symbol);
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            return 2;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu Secondmenu) {
        getMenuInflater().inflate(R.menu.second_menu, Secondmenu);
        System.out.println("StarIcon " + staricon);
        staricon = Secondmenu.findItem(R.id.action_favorite);
        System.out.println("StarIcon after" + staricon);

        if (isStockInWatchlist) {
            staricon.setIcon(ContextCompat.getDrawable(SecondActivity.this, R.drawable.baseline_star_24));
        } else {
            staricon.setIcon(ContextCompat.getDrawable(SecondActivity.this, R.drawable.baseline_star_outline_24));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            if (isStockInWatchlist == true) {
                deleteStockFromWatchlist(getIntent().getStringExtra("query"));
            } else {
                addStockToWatchlist(getIntent().getStringExtra("query"), companyName);
            }
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkStockInWatchlist(String query) {
        String watchlistUrl = "https://finalbackend-419019.wl.r.appspot.com/api/get-watchlist/";

        JsonArrayRequest watchlistRequest = new JsonArrayRequest(Request.Method.GET, watchlistUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    isStockInWatchlist = false;
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject stock = response.getJSONObject(i);
                        String ticker = stock.getString("ticker");
                        if (ticker.equals(query)) {
                            isStockInWatchlist = true;
                            break;
                        }
                    }
                    try {
                        if (isStockInWatchlist) {
                            staricon.setIcon(ContextCompat.getDrawable(SecondActivity.this, R.drawable.baseline_star_24));
                        } else {
                            staricon.setIcon(ContextCompat.getDrawable(SecondActivity.this, R.drawable.baseline_star_outline_24));
                        }
                    } catch (Exception e) {
                        System.out.println("stariconexception" + e);
                    }

                    loadingstatus();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(SecondActivity.this, "Error parsing watchlist data", Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
//                        Toast.makeText(SecondActivity.this, "Error fetching watchlist data", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(watchlistRequest);
    }

    private void fetchCompanyData(String query) {
        String companyUrl = "https://finalbackend-419019.wl.r.appspot.com/api/companydesc/" + query;

        JsonObjectRequest companyRequest = new JsonObjectRequest(Request.Method.GET, companyUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String ticker = response.getString("ticker");
                    companyName = response.getString("name");
                    String IPO = response.getString("ipo");
                    String Industry = response.getString("finnhubIndustry");
                    String Weburl = response.getString("weburl");
                    String logourl = response.getString("logo");

                    ImageView Logo = findViewById(R.id.logo);
                    Picasso.get().load(logourl).into(Logo);

                    TextView Ticker = findViewById(R.id.Ticker);
                    Ticker.setText(ticker);

                    TextView Companyname = findViewById(R.id.CompanyName);
                    Companyname.setText(companyName);

                    TextView IPOTextView = findViewById(R.id.ipodata);
                    IPOTextView.setText(IPO);

                    TextView IndustryTextView = findViewById(R.id.industrydata);
                    IndustryTextView.setText(Industry);

                    TextView WebTextView = findViewById(R.id.webpagedata);
                    WebTextView.setText(Weburl);
                    loadingstatus();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(SecondActivity.this, "Error parsing company data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
//                Toast.makeText(SecondActivity.this, "Error fetching company data", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(companyRequest);
    }

    private void fetchLatestPrice(String query) {
        String latestPriceUrl = "https://finalbackend-419019.wl.r.appspot.com/api/latestprice/" + query;

        JsonObjectRequest latestPriceRequest = new JsonObjectRequest(Request.Method.GET, latestPriceUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mainCurrentPrice = response.getDouble("c");
                    double priceChange = response.getDouble("d");
                    double priceChangePercent = response.getDouble("dp");
                    double high = response.getDouble("h");
                    double low = response.getDouble("l");
                    double open = response.getDouble("o");
                    double previousClose = response.getDouble("pc");

                    fetchPortfolioData(query);

                    TextView latestPriceTextView = findViewById(R.id.latestprice);
                    latestPriceTextView.setText("$" + String.format("%.2f", mainCurrentPrice));

                    TextView changepriceTextView = findViewById(R.id.changeprice);
                    changepriceTextView.setText("$" + String.format("%.2f", priceChange));

                    TextView changeTextView = findViewById(R.id.change);
                    changeTextView.setText("(" + String.format("%.2f", priceChangePercent) + "%)");

                    TextView openPriceDataTextView = findViewById(R.id.openpricedata);
                    openPriceDataTextView.setText("$" + open);

                    TextView lowPriceDataTextView = findViewById(R.id.lowpricedata);
                    lowPriceDataTextView.setText("$" + low);

                    TextView highPriceDataTextView = findViewById(R.id.highpricedata);
                    highPriceDataTextView.setText("$" + high);

                    TextView prevCloseDataTextView = findViewById(R.id.prevclosedata);
                    prevCloseDataTextView.setText("$" + previousClose);


                    if (priceChange > 0) {
                        changeIcon.setImageResource(R.drawable.trending_up);
                        changepriceTextView.setTextColor(Color.GREEN);
                        changeTextView.setTextColor(Color.GREEN);
                        flag = 1;
                    } else if (priceChange < 0) {
                        changeIcon.setImageResource(R.drawable.trending_down);
                        changepriceTextView.setTextColor(Color.RED);
                        changeTextView.setTextColor(Color.RED);
                        flag = 0;
                    } else {
                        changeIcon.setVisibility(View.GONE);
                        changepriceTextView.setTextColor(Color.BLACK);
                        changeTextView.setTextColor(Color.BLACK);
                    }
                    loadingstatus();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(SecondActivity.this, "Error parsing latest price data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
//                Toast.makeText(SecondActivity.this, "Error fetching latest price data", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(latestPriceRequest);

    }

    private void fetchPortfolioData(String query) {
        String portfolioUrl = "https://finalbackend-419019.wl.r.appspot.com/api/get-portfolio";

        JsonArrayRequest portfolioRequest = new JsonArrayRequest(Request.Method.GET, portfolioUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                StateManagement.savePortfolioData(getApplicationContext(), response);
                try {
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject jsonObject = response.getJSONObject(i);
                        String ticker = jsonObject.getString("ticker");
                        if (ticker.equals(query)) {
                            existingquantity = jsonObject.getInt("quantity");
                            existingTotalcost = jsonObject.getDouble("total");
                            double mrkval = (mainCurrentPrice * existingquantity);
                            double avgCost;
                            double change;
                            if(existingquantity == 0){
                                avgCost = 0;
                                change = 0;
                            }else{
                                avgCost = (existingTotalcost / existingquantity);
                                change = (avgCost - mainCurrentPrice);
                            }





                            TextView sharesOwnData = findViewById(R.id.sharesowndata);
                            sharesOwnData.setText(String.valueOf(existingquantity));

                            TextView totalCostData = findViewById(R.id.totalcostdata);
                            totalCostData.setText("$" + String.format("%.2f", existingTotalcost));

                            TextView avgCostData = findViewById(R.id.avgcostdata);
                            avgCostData.setText("$" + String.format("%.2f", avgCost));

                            TextView changeData = findViewById(R.id.changedata);
                            changeData.setText("$" + String.format("%.2f", change));

                            TextView marketValueData = findViewById(R.id.marketvaluedata);
                            marketValueData.setText("$" + String.format("%.2f", mrkval));


                            if (change > 0) {
                                changeData.setTextColor(Color.GREEN);
                                marketValueData.setTextColor(Color.GREEN);
                            } else if (change < 0) {
                                changeData.setTextColor(Color.RED);
                                marketValueData.setTextColor(Color.RED);
                            } else {
                                changeData.setTextColor(Color.BLACK);
                                marketValueData.setTextColor(Color.BLACK);
                            }
                            loadingstatus();
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(SecondActivity.this, "Error parsing portfolio data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
//                Toast.makeText(SecondActivity.this, "Error fetching portfolio data", Toast.LENGTH_SHORT).show();
            }
        });


        Volley.newRequestQueue(this).add(portfolioRequest);
    }

    public void fetchPeers(String query) {
        String peersUrl = "https://finalbackend-419019.wl.r.appspot.com/api/peers/" + query;

        JsonArrayRequest peersRequest = new JsonArrayRequest(peersUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<String> peersList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        String peer = response.getString(i);
                        if (!peer.contains(".")) {
                            peersList.add(peer);
                        }
                    }

                    RecyclerView recyclerViewpeers = findViewById(R.id.peersRecyclerView);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SecondActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewpeers.setLayoutManager(layoutManager);

                    PeersAdapter adapter = new PeersAdapter(peersList);
                    recyclerViewpeers.setAdapter(adapter);


                    loadingstatus();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(SecondActivity.this, "Error parsing peers data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
//                Toast.makeText(SecondActivity.this, "Error fetching peers data", Toast.LENGTH_SHORT).show();
            }
        });


        Volley.newRequestQueue(this).add(peersRequest);
    }

    public void fetchInsiderSentiments(String query) {
        String url = "https://finalbackend-419019.wl.r.appspot.com/api/insidersenti/" + query;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");

                            double totalMspr = 0;
                            double positiveMspr = 0;
                            double negativeMspr = 0;
                            double totalChange = 0;
                            double positiveChange = 0;
                            double negativeChange = 0;

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject entry = data.getJSONObject(i);
                                double mspr = entry.getDouble("mspr");
                                double change = entry.getDouble("change");

                                totalMspr += mspr;
                                totalChange += change;

                                if (mspr > 0) {
                                    positiveMspr += mspr;
                                } else {
                                    negativeMspr += mspr;
                                }

                                if (change > 0) {
                                    positiveChange += change;
                                } else {
                                    negativeChange += change;
                                }
                            }

                            TextView TotalMSRP = findViewById(R.id.totalmsrp);
                            TotalMSRP.setText(String.format("%.2f", totalMspr));

                            TextView TotalChange = findViewById(R.id.totalchange);
                            TotalChange.setText(String.format("%.2f", totalChange));

                            TextView TotalPositive = findViewById(R.id.totalpositive);
                            TotalPositive.setText(String.format("%.2f", positiveMspr));

                            TextView TotalNegative = findViewById(R.id.totalnegative);
                            TotalNegative.setText(String.format("%.2f", negativeMspr));

                            TextView ChangePositive = findViewById(R.id.changepositive);
                            ChangePositive.setText(String.format("%.2f", positiveChange));

                            TextView ChangeNegative = findViewById(R.id.changenegative);
                            ChangeNegative.setText(String.format("%.2f", negativeChange));


                            TextView InsiderName = findViewById(R.id.insidernamedata);
                            InsiderName.setText(query);

                            loadingstatus();
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

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    class sendquerytoHTML {
        private String symbol;

        sendquerytoHTML(String query) {
            System.out.println("initialise in func" + query);
            this.symbol = query;
            System.out.println("symbol in func" + this.symbol);

        }

        @JavascriptInterface
        public String getQuery() {
            System.out.println("symbol in interface" + symbol);
            return symbol;
        }
    }

    public void fetchTrends(String query) {
        trendsChart = findViewById(R.id.trendscharts);
        trendsChart.getSettings().setJavaScriptEnabled(true);

        trendsChart.addJavascriptInterface(new sendquerytoHTML(query), "AndroidInterface");

        trendsChart.setWebViewClient(new WebViewClient());

        trendsChart.loadUrl("file:///android_asset/trends.html");
        loadingstatus();
    }

    public void fetchSurprises(String query) {
        supriseChart = findViewById(R.id.suprisescharts);
        supriseChart.getSettings().setJavaScriptEnabled(true);

        supriseChart.addJavascriptInterface(new sendquerytoHTML(query), "AndroidInterface");

        supriseChart.setWebViewClient(new WebViewClient());

        supriseChart.loadUrl("file:///android_asset/suprises.html");
        loadingstatus();
    }

    private void fetchNews(String query) {
        String newsUrl = "https://finalbackend-419019.wl.r.appspot.com/api/news/" + query;

        JsonArrayRequest newsRequest = new JsonArrayRequest(Request.Method.GET, newsUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loadingstatus();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject newsObject = response.getJSONObject(i);
                                String image = newsObject.optString("image", "");
                                // Check if the news item has an image source
                                if (!image.isEmpty()) {
                                    String source = newsObject.optString("source", "");
                                    long datetime = newsObject.optLong("datetime", 0);
                                    String headline = newsObject.optString("headline", "");
                                    String description = newsObject.optString("summary", "");
                                    String newsurllink = newsObject.optString("url");

                                    NewsItem newsItem = new NewsItem(image, source, datetime, headline, description, newsurllink);
                                    newsItemList.add(newsItem);
                                }
                            }

                            newsAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(SecondActivity.this, "Error parsing news data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
//                        Toast.makeText(SecondActivity.this, "Error fetching news data", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(newsRequest);
    }

    private void addStockToWatchlist(String query, String companyName) {
        String addWatchlistUrl = "https://finalbackend-419019.wl.r.appspot.com/api/add-watchlist/";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("ticker", query);
            requestBody.put("name", companyName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest addWatchlistRequest = new JsonObjectRequest(Request.Method.POST, addWatchlistUrl, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            StateManagement.addStockToFavorites(getApplicationContext(), requestBody);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        staricon.setIcon(ContextCompat.getDrawable(SecondActivity.this, R.drawable.baseline_star_24));
                        isStockInWatchlist = true;
                        Toast.makeText(SecondActivity.this,  query + " is added to watchlist", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                });

        Volley.newRequestQueue(this).add(addWatchlistRequest);
    }

    private void deleteStockFromWatchlist(String query) {

        String deleteUrl = "https://finalbackend-419019.wl.r.appspot.com/api/delete-watchlistmain/" + query;

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            StateManagement.removeStockFromFavorites(getApplicationContext(), query);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        staricon.setIcon(ContextCompat.getDrawable(SecondActivity.this, R.drawable.baseline_star_outline_24));
                        isStockInWatchlist = false;
                        Toast.makeText(SecondActivity.this, query + " is removed from watchlist", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(deleteRequest);
    }

    private void fetchwallet() {
        String walletUrl = "https://finalbackend-419019.wl.r.appspot.com/api/get-wallet";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, walletUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject walletData = response.getJSONObject(0);
                    Walletmoney = walletData.getDouble("money");

                    StateManagement.saveWalletData(getApplicationContext(), (float) Walletmoney);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        Volley.newRequestQueue(this).add(request);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key.equals(StateManagement.WALLET_DATA_LOAD)) {
            double money = StateManagement.getWalletData(this);
            System.out.println("IN SECOND ACTIVITY SHARED PREF" + money);

            Walletmoney = money;
        } else if (key.equals(StateManagement.PORTFOLIO_DATA_LOAD)) {
            System.out.println("STATE MANAGEMENT PORTOFLIO ON SHARED PREF function");
            try {
                JSONArray response = StateManagement.getPortfolioData(this);
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.getJSONObject(i);
                    String ticker = jsonObject.getString("ticker");
                    if (ticker.equals(query)) {
                        existingquantity = jsonObject.getInt("quantity");
                        existingTotalcost = jsonObject.getDouble("total");
                        double mrkval = mainCurrentPrice * existingquantity;
                        double avgCost;
                        double change;
                        if(existingquantity == 0){
                            avgCost = 0;
                            change = 0;
                        }else{
                            avgCost = (existingTotalcost / existingquantity);
                            change = (avgCost - mainCurrentPrice);
                        }

                        TextView sharesOwnData = findViewById(R.id.sharesowndata);
                        sharesOwnData.setText(String.valueOf(existingquantity));

                        TextView totalCostData = findViewById(R.id.totalcostdata);
                        totalCostData.setText("$" + String.format("%.2f", existingTotalcost));

                        TextView avgCostData = findViewById(R.id.avgcostdata);
                        avgCostData.setText("$" + String.format("%.2f", avgCost));

                        TextView changeData = findViewById(R.id.changedata);
                        changeData.setText("$" + String.format("%.2f", change));

                        TextView marketValueData = findViewById(R.id.marketvaluedata);
                        marketValueData.setText("$" + String.format("%.2f", mrkval));


                        if (change > 0) {
                            changeData.setTextColor(Color.GREEN);
                            marketValueData.setTextColor(Color.GREEN);
                        } else if (change < 0) {
                            changeData.setTextColor(Color.RED);
                            marketValueData.setTextColor(Color.RED);
                        } else {
                            changeData.setTextColor(Color.BLACK);
                            marketValueData.setTextColor(Color.BLACK);
                        }

                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
//                    Toast.makeText(SecondActivity.this, "Error parsing portfolio data", Toast.LENGTH_SHORT).show();
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