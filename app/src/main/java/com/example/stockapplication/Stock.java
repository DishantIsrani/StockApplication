package com.example.stockapplication;

public class Stock {
    private String ticker;
    private String name;
    private double latestPrice;
    private double change;
    private double changePrice;

    public Stock(String ticker, String name, double latestPrice, double change, double changePrice) {

        this.ticker = ticker;
        this.name = name;
        this.latestPrice = latestPrice;
        this.change = change;
        this.changePrice = changePrice;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePrice() {
        return changePrice;
    }

    public void setChangePrice(double changePrice) {
        this.changePrice = changePrice;
    }


}