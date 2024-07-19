package com.example.stockapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.MessagePattern;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StateManagement {

    private static final String STOCK_DATA = "stock_prefs_manager";
    public static final String WALLET_DATA_LOAD = "WALLET_DATA_LOAD";
    public static final String PORTFOLIO_DATA_LOAD = "PORTFOLIO_DATA_LOAD";
    public static final String FAVORITES_DATA_LOAD = "FAVORITES_DATA_LOAD";

    public static void saveWalletData(Context context, float walletAmount){
        System.out.println("IN SAVE WALLET DATA RIGHT NOW" + walletAmount);
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        statesharedPrefsEditor.putFloat(WALLET_DATA_LOAD, walletAmount);
        statesharedPrefsEditor.apply();

        System.out.println("NEW WALLET DATA RIGHT NOW" + getWalletData(context));
    }

    public static double getWalletData(Context context){
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        return (double) statesharedPrefs.getFloat(WALLET_DATA_LOAD, 0);
    }

    public static void eraseWallet(Context context){
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        statesharedPrefsEditor.remove(WALLET_DATA_LOAD);
        statesharedPrefsEditor.apply();
    }

    public static void savePortfolioData(Context context, JSONArray portfolio){
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        statesharedPrefsEditor.putString(PORTFOLIO_DATA_LOAD, portfolio.toString());
        statesharedPrefsEditor.apply();
    }

    public static JSONArray getPortfolioData(Context context) throws JSONException {
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        return new JSONArray(statesharedPrefs.getString(PORTFOLIO_DATA_LOAD, ""));
    }

//    public static JSONArray getPortfolioData(Context context) throws JSONException {
//        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
//        String portfolioDataString = statesharedPrefs.getString(PORTFOLIO_DATA_LOAD, "");
//        if (portfolioDataString.isEmpty()) {
//            return new JSONArray();
//        } else {
//            return new JSONArray(portfolioDataString);
//        }
//    }



    public static void addStockDataToPortfolio(Context context, JSONObject stock) throws JSONException {
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        JSONArray portfolio = getPortfolioData(context);
        portfolio.put(stock);
        statesharedPrefsEditor.putString(PORTFOLIO_DATA_LOAD, portfolio.toString());
        statesharedPrefsEditor.apply();
    }

    public static void deleteStockDataFromPortfolio(Context context, String stockTicker) throws JSONException {
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        JSONArray portfolio = getPortfolioData(context);
        for(int i=0;i<portfolio.length();i++){
            JSONObject stock = (JSONObject) portfolio.get(i);

            if(stock.getString("ticker").equals(stockTicker)){
                portfolio.remove(i);
                break;
            }
        }
        statesharedPrefsEditor.putString(PORTFOLIO_DATA_LOAD, portfolio.toString());
        statesharedPrefsEditor.apply();
    }

    public static void removePortfolioDataFromPrefs(Context context) {
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        statesharedPrefsEditor.remove(PORTFOLIO_DATA_LOAD);
        statesharedPrefsEditor.apply();
    }


    public static void updateStockDataInPortfolio(Context context, JSONObject updatedStock) throws JSONException {
        Log.d("stock-prefs-update", "In update");
        Log.d("stock-prefs-update", "New: "+updatedStock);
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        JSONArray portfolio = getPortfolioData(context);
        for(int i=0;i<portfolio.length();i++){
            JSONObject stock = (JSONObject) portfolio.get(i);
            Log.d("stock-prefs-update", i+" "+stock.toString());
            Log.d("stock-prefs-update", i+" "+portfolio.toString());
            if(stock.getString("ticker").equals(updatedStock.getString("ticker"))){
                portfolio.remove(i);
                Log.d("stock-prefs-update", i+" after remove "+portfolio.toString());
                portfolio.put(updatedStock);
                Log.d("stock-prefs-update", i+" after put "+portfolio.toString());
                break;
            }
        }
        Log.d("stock-prefs-update", "FINAL "+portfolio.toString());
        statesharedPrefsEditor.putString(PORTFOLIO_DATA_LOAD, portfolio.toString());
        statesharedPrefsEditor.apply();
    }

    public static void saveFavoritesData(Context context, JSONArray favorites){
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        statesharedPrefsEditor.putString(FAVORITES_DATA_LOAD, favorites.toString());
        statesharedPrefsEditor.apply();
    }

    public static JSONArray getFavoritesData(Context context) throws JSONException {
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        return new JSONArray(statesharedPrefs.getString(FAVORITES_DATA_LOAD, ""));
    }

//    public static JSONArray getFavoritesData(Context context) {
//        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
//        String favoritesDataString = statesharedPrefs.getString(FAVORITES_DATA_LOAD, "");
//
//        if (favoritesDataString.isEmpty()) {
//            return new JSONArray();
//        } else {
//            try {
//                return new JSONArray(favoritesDataString);
//            } catch (JSONException e) {
//                e.printStackTrace();
//                return new JSONArray();
//            }
//        }
//    }


    public static void addStockToFavorites(Context context, JSONObject stock) throws JSONException {
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        JSONArray favorites = getFavoritesData(context);
        favorites.put(stock);
        statesharedPrefsEditor.putString(FAVORITES_DATA_LOAD, favorites.toString());
        statesharedPrefsEditor.apply();
    }

    public static void removeStockFromFavorites(Context context, String stockTicker) throws JSONException {
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        JSONArray favorites = getFavoritesData(context);
        for(int i=0;i<favorites.length();i++){
            JSONObject stock = (JSONObject) favorites.get(i);
            if(stock.getString("ticker").equals(stockTicker)){
                favorites.remove(i);
                break;
            }
        }
        statesharedPrefsEditor.putString(FAVORITES_DATA_LOAD, favorites.toString());
        statesharedPrefsEditor.apply();
    }


    public static void removeFavoritesDataFromPrefs(Context context) {
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor statesharedPrefsEditor = statesharedPrefs.edit();
        statesharedPrefsEditor.remove(FAVORITES_DATA_LOAD);
        statesharedPrefsEditor.apply();
    }

    public static void registerStatePrefs(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        statesharedPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterStatePrefs(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences statesharedPrefs = context.getSharedPreferences(STOCK_DATA, Context.MODE_PRIVATE);
        statesharedPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}