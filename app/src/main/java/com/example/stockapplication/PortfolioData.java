package com.example.stockapplication;


public class PortfolioData {

    private String ticker;
    private double quantity;
    private double valuecalcheckdocs;
    private double valuecalcheckdocs2;
    private double valuecalcheckdocs3;


    public PortfolioData(String ticker, double quantity, double valuecalcheckdocs, double valuecalcheckdocs2, double valuecalcheckdocs3) {

        this.ticker = ticker;
        this.quantity = quantity;
        this.valuecalcheckdocs = valuecalcheckdocs;
        this.valuecalcheckdocs2 = valuecalcheckdocs2;
        this.valuecalcheckdocs3 = valuecalcheckdocs3;
    }

    public String getTicker() {
        return ticker;
    }

    public double getquantity() {
        return quantity;
    }

    public void setquantity(double quantity) {
        this.quantity = quantity;
    }

    public double getvaluecalcheckdocs() {
        return valuecalcheckdocs;
    }

    public void setvaluecalcheckdocs(double valuecalcheckdocs) {
        this.valuecalcheckdocs = valuecalcheckdocs;
    }

    public double getvaluecalcheckdocs2() {
        return valuecalcheckdocs2;
    }

    public void setvaluecalcheckdocs2(double valuecalcheckdocs2) {
        this.valuecalcheckdocs2 = valuecalcheckdocs2;
    }

    public double getvaluecalcheckdocs3() {
        return valuecalcheckdocs3;
    }

    public void setvaluecalcheckdocs3(double valuecalcheckdocs3) {
        this.valuecalcheckdocs3 = valuecalcheckdocs3;
    }




}


//
//package com.example.stockapp;
//
//public class Stock {
//    private String ticker;
//    private String name;
//    private double latestPrice;
//    private double change;
//    private double changePrice;
//
//    public Stock(String ticker, String name, double latestPrice, double change, double changePrice) {
//
//        this.ticker = ticker;
//        this.name = name;
//        this.latestPrice = latestPrice;
//        this.change = change;
//        this.changePrice = changePrice;
//    }
//
//    public String getTicker() {
//        return ticker;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public double getLatestPrice() {
//        return latestPrice;
//    }
//
//    public void setLatestPrice(double latestPrice) {
//        this.latestPrice = latestPrice;
//    }
//
//    public double getChange() {
//        return change;
//    }
//
//    public void setChange(double change) {
//        this.change = change;
//    }
//
//    public double getChangePrice() {
//        return changePrice;
//    }
//
//    public void setChangePrice(double changePrice) {
//        this.changePrice = changePrice;
//    }
//
//
//}