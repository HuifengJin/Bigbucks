package com.ibm.security.appscan.altoromutual.model;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import java.io.IOException;

public class StockTransaction {
    private long StockTransaction_id = -1;
    private String stockName;
    private double amountOwn;
    private double price;
    private String action;
    private String date;

    public StockTransaction(long holdings_id, String date, String stockName, double amountOwn, double price, String action) {
        this.StockTransaction_id = holdings_id;
        this.stockName = stockName;
        this.date = date;
        this.amountOwn = amountOwn;
        this.price = price;
        this.action = action;
    }

    public String getDate() {
        return date;
    }

    public String getStockName() {
        return stockName;
    }

    public double getStockPrice() {
        return price;
    }

    public double getAmountOwn() {
        return amountOwn;
    }

    public String getAction() {
        return action;
    }


}
