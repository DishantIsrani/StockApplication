package com.example.stockapplication;
import android.annotation.SuppressLint;
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

import java.io.IOException;
import java.io.InputStream;

public class HourlyChartFragment extends Fragment {


    public HourlyChartFragment() {

    }

//    public static HourlyChartFragment newInstance(String query, int flag) {
//        HourlyChartFragment fragment = new HourlyChartFragment();
//        Bundle args = new Bundle();
//        args.putString("query", query);
//        args.putInt("flag", flag);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static HourlyChartFragment newInstance(String query, int flag) {
        HourlyChartFragment fragment = new HourlyChartFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        args.putInt("flag", flag);
        fragment.setArguments(args);
        return fragment;
    }


    WebView hourlychart;


    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);



        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String query = getArguments().getString("query");
            int flag = getArguments().getInt("flag");

            hourlychart = view.findViewById(R.id.HourlyCharts);
            hourlychart.getSettings().setJavaScriptEnabled(true);

//            hourlychart.addJavascriptInterface(new sendquerytoHTML(query), "AndroidInterface");
            hourlychart.addJavascriptInterface(new sendquerytoHTML(query, flag), "AndroidInterface");

            hourlychart.loadUrl("file:///android_asset/hourlychart.html");
            hourlychart.setWebViewClient(new WebViewClient());

        }

        return view;
    }



    class sendquerytoHTML {
        private String symbol;
        int flag;

        sendquerytoHTML(String query, int flag) {
            System.out.println("initialise in func"+ query);
            this.symbol = query;
            this.flag = flag;
            System.out.println("symbol in func"+ this.symbol);

        }

        @JavascriptInterface
        public String getQuery() {
            System.out.println("symbol in interface"+ symbol);
            return symbol;
        }

        @JavascriptInterface
        public int getFlag(){
            return flag;
        }

    }
}
