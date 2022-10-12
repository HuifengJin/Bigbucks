package com.ibm.security.appscan.altoromutual.model;

import com.ibm.security.appscan.altoromutual.util.DBUtil;

import java.sql.SQLException;

public class Holdings {
//    private long holdings_id = -1;
    private String stockName;
    private double amountOwn;

    public Holdings(String stockName, double amountOwn) {
//        this.holdings_id = holdings_id;
        this.stockName = stockName;
        this.amountOwn = amountOwn;
    }

    public String getStockName(){return stockName;}
    public double getAmountOwn(){return amountOwn;}
}
