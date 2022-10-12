package com.ibm.security.appscan.altoromutual.model;

import org.testng.annotations.Test;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
//import com.ibm.security.appscan.altoromutual.model.StockDto;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class YahooStock {


    public Map<String,Stock> getStock (String[] stockSymbols) throws IOException {
        Map<String,Stock> stock = YahooFinance.get(stockSymbols);
        return stock;
    }

    public List<HistoricalQuote> getHistory(String stockSymbol) throws IOException {
        Stock stock = YahooFinance.get(stockSymbol);
        List<HistoricalQuote> history = stock.getHistory();
        for (HistoricalQuote quote: history) {
            System.out.println("====================");
            System.out.println("symbol : "+quote.getSymbol());
            System.out.println("date : "+convertDate(quote.getDate()));
            System.out.println("adj closed price : "+quote.getAdjClose());
            System.out.println("====================");
        }
        return history;
    }

    public BigDecimal getPriceForOneDay(String stockSymbol, int year, String date) throws IOException {

        //here
        BigDecimal price = BigDecimal.valueOf(0);
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, Integer.valueOf("-" + year));

        Stock stock = YahooFinance.get(stockSymbol);
        List<HistoricalQuote> history = stock.getHistory(from, to, getInterval("DAILY"));

        for (HistoricalQuote quote: history) {
            if(convertDate(quote.getDate()).equals(date)){

                price = quote.getAdjClose();

            }
        }
        return price;
    }

    public List<HistoricalQuote> getHistory(String stockSymbol, int year, String searchType) throws IOException {

        //here
        BigDecimal price = BigDecimal.valueOf(0);
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, Integer.valueOf("-" + year));

        Stock stock = YahooFinance.get(stockSymbol);
        List<HistoricalQuote> history = stock.getHistory(from, to, getInterval(searchType));

        return history;
    }
    private static String convertDate(Calendar cal) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = format.format(cal.getTime());
        return formatDate;
    }

    private static Interval getInterval(String searchType) {
        Interval interval = null;
        switch (searchType.toUpperCase()) {
            case "MONTHLY":
                interval = Interval.MONTHLY;
                break;
            case "WEEKLY":
                interval = Interval.WEEKLY;
                break;
            case "DAILY":
                interval = Interval.DAILY;
                break;
        }
        return interval;
    }
    public static BigDecimal getCurrent(String stockSymbol) throws IOException {
        String searchType = "daily";
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, Integer.valueOf("-" + 0));
        BigDecimal price = BigDecimal.valueOf(0);
        Stock stock = YahooFinance.get(stockSymbol);
        price = YahooFinance.get(stockSymbol).getQuote().getPrice();
        price.doubleValue();
        //List<HistoricalQuote> history = stock.getHistory(from, to, getInterval(searchType));

        //for (HistoricalQuote quote: history) {

        //    price = quote.getAdjClose();

        // }
        return price;
    }

    public static BigDecimal getNextDay(String date, String stockSymbol) throws ParseException, IOException {
        String dt = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(dt));
        c.add(Calendar.DATE, 1);
        dt = sdf.format(c.getTime());

        Stock stock = YahooFinance.get(stockSymbol);
        BigDecimal price = BigDecimal.valueOf(0);


        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, Integer.valueOf("-" + 1));
        List<HistoricalQuote> history = stock.getHistory(from, to, getInterval("DAILY"));

        for (HistoricalQuote quote: history) {
            if(convertDate(quote.getDate()).equals(dt)){

                price = quote.getAdjClose();

            }
        }
        return price;
    }




    @Test
    public static void main(String[] args) throws IOException, ParseException {
        YahooStock yahooStock = new YahooStock();
        //System.out.println(yahooStock.getStock("AAPL"));

        // String[] stockSymbols = {"AAPL", "GOOG", "TSLA", "BABA"};
        //System.out.println(yahooStock.getStock(stockSymbols));

        //       yahooStock.getCurrent("AAPL","daily");
//        BigDecimal price = yahooStock.getHistory("AAPL", 1, "daily","2021-04-01");
        // System.out.println(price);

        // BigDecimal price = yahooStock.getNextDay("2021-04-21","AAPL");
        // System.out.println(price);

    }
}

