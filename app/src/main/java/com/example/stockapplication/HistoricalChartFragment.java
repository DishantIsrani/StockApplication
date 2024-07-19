package com.example.stockapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HistoricalChartFragment extends Fragment {

    public HistoricalChartFragment() {
        // Required empty public constructor
    }

    public static HistoricalChartFragment newInstance(String query) {
        HistoricalChartFragment fragment = new HistoricalChartFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        fragment.setArguments(args);
        return fragment;
    }

    WebView Historicalchart;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);


        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String query = getArguments().getString("query");
            // Use the query value as needed


            Historicalchart = view.findViewById(R.id.historicalchart);
            Historicalchart.getSettings().setJavaScriptEnabled(true);

            Historicalchart.addJavascriptInterface(new HistoricalChartFragment.sendquerytoHTML(query), "AndroidInterface");

            Historicalchart.loadUrl("file:///android_asset/smavbp.html");
            Historicalchart.setWebViewClient(new WebViewClient());

        }

//        Historicalchart = view.findViewById(R.id.historicalchart);
//        Historicalchart.getSettings().setJavaScriptEnabled(true);
//
//        // Load the URL
//        Historicalchart.loadUrl("file:///android_asset/smavbp.html");
//
//        // Set a WebViewClient to handle page navigation
////        hourlychart.setWebViewClient(new WebViewClient());

        return view;
    }



    class sendquerytoHTML {
        private String symbol;

        sendquerytoHTML(String query) {
            System.out.println("initialise in func"+ query);
            this.symbol = query;
            System.out.println("symbol in func"+ this.symbol);

        }

        @JavascriptInterface
        public String getQuery() {
            System.out.println("symbol in interface"+ symbol);
            return symbol;
        }
    }
}
