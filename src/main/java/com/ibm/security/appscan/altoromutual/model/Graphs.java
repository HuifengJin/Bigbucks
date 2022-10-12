package com.ibm.security.appscan.altoromutual.model;


//import com.sun.tools.corba.se.idl.constExpr.Times;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


public class Graphs extends ApplicationFrame {


//    private static TimeSeriesCollection mutiple;

    public Graphs(String title) throws IOException, ParseException {
        super(title);

//        mutiple = new TimeSeriesCollection();

        ChartPanel chartPanel1 = (ChartPanel) createStockPanel("AAPL");
        chartPanel1.setPreferredSize(new java.awt.Dimension(500, 270));
        getContentPane().add(chartPanel1, BorderLayout.CENTER);



    }

//    public Graphs(String title, String type) {
//        super(title);
//
//    }

    public static JPanel createStockPanel(String stockSymbol) throws IOException, ParseException {
//        JFreeChart chart = createChart(createDataset(stockSymbol),stockSymbol);
        YahooStock yahooStock = new YahooStock();
        String[] stockSymbols = {"TSLA", "AAPL", "^IXIC"};

        Stock appleStock = yahooStock.getStock(stockSymbols).get("AAPL");
        Stock teslaStock = yahooStock.getStock(stockSymbols).get("TSLA");
        Stock sp500Index = yahooStock.getStock(stockSymbols).get("^IXIC");

//        JFreeChart chart14_1 = createSimplePriceGraph(appleStock);
        JFreeChart chart14_2 = createSimpleReturnGraph(appleStock, 1);
        JFreeChart chart14_3 = createTodayVsYesterdayGraph(appleStock, 1);
        JFreeChart chart14_4 = createSimpleReturnHistogram(appleStock, 1);
        JFreeChart chart7_1 = createStockVsIndexReturnGraph(appleStock, sp500Index, 1);
        JFreeChart chart7_2 = createPercentChangeStockVsIndexGraph(appleStock, sp500Index, 1);
        JFreeChart chart7_3 = createStockVsIndexScatterGraph(appleStock, sp500Index, 1);


//        ChartPanel panel = new ChartPanel(chart14_1, false);
//        ChartPanel panel = new ChartPanel(chart14_2, false);
//        ChartPanel panel = new ChartPanel(chart14_3, false);

        ChartPanel panel = new ChartPanel(chart7_1, false);
//        ChartPanel panel = new ChartPanel(chart7_2, false);
//        ChartPanel panel = new ChartPanel(chart14_4, false);

        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        return panel;
    }




    private static JFreeChart createTimeSeriesChart(XYDataset dataset, String title, String xLabel, String yLabel) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,  // title
                xLabel,             // x-axis label
                yLabel,   // y-axis label
                dataset,false,false,false);

        chart.setBackgroundPaint(Color.WHITE);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;

            renderer.setDrawSeriesLineAsPath(true);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

        LegendTitle legend = new LegendTitle(chart.getPlot());
        legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
        legend.setBackgroundPaint(Color.white);
        legend.setPosition(RectangleEdge.BOTTOM);
        chart.addLegend(legend);

        return chart;

    }

    private static JFreeChart createTimeSeriesScatterPlot(XYDataset dataset, String title, String xLabel, String yLabel) {

//        JFreeChart chart = ChartFactory.createTimeSeriesChart(
//                title,  // title
//                xLabel,             // x-axis label
//                yLabel,   // y-axis label
//                dataset,false,false,false);
        JFreeChart chart = ChartFactory.createScatterPlot(
                title, "Today's Returns", "Yesterday's Returns", dataset, org.jfree.chart.plot.PlotOrientation.VERTICAL, false, true, true);

        chart.setBackgroundPaint(Color.WHITE);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;

            renderer.setDrawSeriesLineAsPath(true);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

        return chart;

    }





    private static JFreeChart createXYLineGraph(XYDataset dataset, String title, String xLabel, String yLabel) {
        JFreeChart chart = ChartFactory.createScatterPlot(
                title, xLabel, yLabel, dataset, org.jfree.chart.plot.PlotOrientation.VERTICAL, false, true, true);
        LegendTitle legend = new LegendTitle(chart.getPlot());
        legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
        legend.setBackgroundPaint(Color.white);
        legend.setPosition(RectangleEdge.BOTTOM);
        chart.addLegend(legend);


        return chart;
    }





    private static XYDataset createDatasetFromYahoo(String stockSymbol, List<Double> priceHistory, List<Long> dates) throws ParseException {
        TimeSeries s = new TimeSeries(stockSymbol);


        for (int i=0; i<priceHistory.size(); i++) {
            Double currPrice = priceHistory.get(i);
            Date currentDate = new Date(dates.get(i));
            s.add(new Day(currentDate), currPrice);
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s);
        return dataset;


    }

    private static TimeSeries createDatasetFromYahoo4(String stockSymbol, List<Double> priceHistory, List<Long> dates) throws ParseException {
        TimeSeries s = new TimeSeries(stockSymbol);


        for (int i=0; i<priceHistory.size(); i++) {
            Double currPrice = priceHistory.get(i);
            Date currentDate = new Date(dates.get(i));
            s.add(new Day(currentDate), currPrice);
        }

        return s;


    }


    private static XYSeries createReturnsDataset(String stockSymbol, List<Double> priceHistory, List<Double> priceHistory2) throws ParseException {
        XYSeries s = new XYSeries(stockSymbol);
        List<Double> subsetPriceHistory = priceHistory.subList(1,priceHistory.size());
        for (int i=0; i<subsetPriceHistory.size(); i++) {
            s.add(subsetPriceHistory.get(i), priceHistory2.get(i));
        }

        return s;
    }

    private static XYDataset createDatasetFromYahoo3(String stockSymbol, List<Double> priceHistory, List<Long> dates, String stockSymbol2, List<Double> priceHistory2, List<Long> dates2) throws ParseException {
        TimeSeries s = new TimeSeries(stockSymbol);
        TimeSeries s2 = new TimeSeries(stockSymbol2);


        for (int i=0; i<priceHistory.size(); i++) {
            Double currPrice = priceHistory.get(i);
            Date currentDate = new Date(dates.get(i));
            s.add(new Day(currentDate), currPrice);
        }

        for (int i=0; i<priceHistory2.size(); i++) {
            Double currPrice = priceHistory2.get(i);
            Date currentDate = new Date(dates2.get(i));
            s2.add(new Day(currentDate), currPrice);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s);
        dataset.addSeries(s2);



        return dataset;


    }



    // Quant Finance graphs- https://learning.oreilly.com/library/view/quantitative-finance/9781439871683/K13284_C014.xhtml#_idParaDest-154

    // chart 14.1- given stock symbol, start data and end date
    // plot adjusted close by day
    public static JFreeChart createSimplePriceGraph(Stock stock, Integer timeFrame) throws IOException, ParseException {
        YahooStock yahooStock = new YahooStock();
        List<HistoricalQuote>  priceHistory = yahooStock.getHistory(stock.getSymbol(),timeFrame,"DAILY");
        ArrayList<Double> priceHistoryDoubles = new ArrayList<Double>();
        ArrayList<Long> dates = new ArrayList<>();

        for (HistoricalQuote quote: priceHistory) {
            priceHistoryDoubles.add(quote.getAdjClose().doubleValue());
            dates.add(quote.getDate().getTimeInMillis());
        }

        XYDataset dataset = createDatasetFromYahoo(stock.getSymbol(), priceHistoryDoubles, dates);
        String title = stock.getSymbol() + " Adjusted Close Price";
        JFreeChart chart = createTimeSeriesChart(dataset, title, "Date", "Price");

        return chart;
    }


    // chart 14.2- given stock symbol, start data and end date
    // plot return by day
    // return(day k) = P day(k) - P day(k-1)  / P day(k-1)
    public static JFreeChart createSimpleReturnGraph(Stock stock,  Integer timeFrame) throws IOException, ParseException {
        YahooStock yahooStock = new YahooStock();
        List<HistoricalQuote>  priceHistory = yahooStock.getHistory(stock.getSymbol(),timeFrame,"DAILY");
        ArrayList<Double> priceHistoryDoubles = new ArrayList<Double>();
        ArrayList<Long> dates = new ArrayList<>();

        ArrayList<Double> returns = getReturns(priceHistory, dates);


        XYDataset dataset = createDatasetFromYahoo(stock.getSymbol(), returns, dates);
        String title = stock.getSymbol() + " Simple Return";

        JFreeChart chart = createTimeSeriesChart(dataset,title, "Date", "Return");

        return chart;
    }

    private static ArrayList<Double> getReturns(List<HistoricalQuote> priceHistory, ArrayList<Long> dates) {
        ArrayList<Double> returns = new ArrayList<Double>();

        for (int i = 0; i< priceHistory.size()-1; i++) {
            HistoricalQuote quote1 = priceHistory.get(i);
            HistoricalQuote quote2 = priceHistory.get(i+1);

            Double price1 = quote1.getAdjClose().doubleValue();
            Double price2 = quote2.getAdjClose().doubleValue();

            Double return1 = (price2-price1)/price1;
            returns.add(return1);
            dates.add(quote2.getDate().getTimeInMillis());
        }
        return returns;
    }

    // chart 14.3- given stock symbol, start data and end date
    // plot today's returns vs yesterday's returns
    public static JFreeChart createTodayVsYesterdayGraph(Stock stock,  Integer timeFrame) throws IOException, ParseException {
        YahooStock yahooStock = new YahooStock();
        List<HistoricalQuote>  priceHistory = yahooStock.getHistory(stock.getSymbol(),timeFrame,"DAILY");
        ArrayList<Long> dates = new ArrayList<>();

        ArrayList<Double> returns = new ArrayList<Double>();
        ArrayList<Double> yesterdaysReturns = new ArrayList<Double>();

        for (int i=0; i<priceHistory.size()-1; i++) {
            HistoricalQuote quote1 = priceHistory.get(i);
            HistoricalQuote quote2 = priceHistory.get(i+1);

            Double price1 = quote1.getAdjClose().doubleValue();
            Double price2 = quote2.getAdjClose().doubleValue();

            Double return1 = (price2-price1)/price1;
            returns.add(return1);
            dates.add(quote1.getDate().getTimeInMillis());
        }

        for (int i=1; i<returns.size(); i++) {
            yesterdaysReturns.add(returns.get(i-1));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries s = createReturnsDataset(stock.getSymbol(), returns, yesterdaysReturns);
        dataset.addSeries(s);
        String title = "Today's Returns vs Yesterday's Returns for " + stock.getSymbol();
        JFreeChart chart = createXYLineGraph(dataset,title, "Today's Returns (%)", "Yesterday's Returns (%)");
        return chart;
    }


    // chart 14.4- given stock symbol, start data and end date
    // plot return by day
    // return(day k) = P day(k) - P day(k-1)  / P day(k-1)
    public static JFreeChart createSimpleReturnHistogram(Stock stock,  Integer timeFrame) throws IOException, ParseException {
        YahooStock yahooStock = new YahooStock();
        List<HistoricalQuote>  priceHistory = yahooStock.getHistory(stock.getSymbol(),timeFrame,"DAILY");
        ArrayList<Double> priceHistoryDoubles = new ArrayList<Double>();
        ArrayList<Long> dates = new ArrayList<>();

        ArrayList<Double> returns = getReturns(priceHistory, dates);
//        double[] arr = returns.stream().mapToDouble(Double::doubleValue).toArray();
        double[] arr= new double[returns.size()];
        int index = 0;
        for(double i : returns){
            arr[index] = i; // unboxing is automtically done here
            index++;
        }

        XYDataset dataset = createDatasetFromYahoo(stock.getSymbol(), returns, dates);
        String title = stock.getSymbol() + " Simple Return Histogram";

        JFreeChart chart = createTimeSeriesChart(dataset,title, "Date", "Return");


        HistogramDataset dataset2 = new HistogramDataset();
        dataset2.addSeries("key", arr, 20);

        JFreeChart histogram = ChartFactory.createHistogram(title,
                "Data", "Frequency", dataset2);
        return histogram;
    }



    // Balch grpahs- https://learning.oreilly.com/library/view/what-hedge-funds/9781631570896/07_Romero_Chapter_07.xhtml

    // chart 7.1- given stock symbol, start date and end date
    // plot stock's returns vs index's returns relative prices
    public static JFreeChart createStockVsIndexReturnGraph(Stock stock, Stock stock2,  Integer timeFrame) throws IOException, ParseException{
        // get first stock's data
        YahooStock yahooStock = new YahooStock();
        List<HistoricalQuote>  priceHistory = yahooStock.getHistory(stock.getSymbol(),timeFrame,"DAILY");
        ArrayList<Double> priceHistoryDoubles = new ArrayList<Double>();
        ArrayList<Long> dates = new ArrayList<>();

        for (HistoricalQuote quote: priceHistory) {
            priceHistoryDoubles.add(quote.getAdjClose().doubleValue());
            dates.add(quote.getDate().getTimeInMillis());
        }

        // get second stock's data
        List<HistoricalQuote>  priceHistory2 = yahooStock.getHistory(stock2.getSymbol(),timeFrame,"DAILY");
        ArrayList<Double> priceHistoryDoubles2 = new ArrayList<Double>();
        ArrayList<Long> dates2 = new ArrayList<>();

        for (HistoricalQuote quote: priceHistory2) {
            priceHistoryDoubles2.add(quote.getAdjClose().doubleValue());
            dates2.add(quote.getDate().getTimeInMillis());
        }

        // calculate relative prices
        ArrayList<Double> relativePrice1 = new ArrayList<Double>();
        ArrayList<Double> relativePrice2 = new ArrayList<Double>();

        for (int i=0; i<priceHistory.size(); i++) {
            relativePrice1.add(priceHistoryDoubles.get(i)/priceHistoryDoubles.get(0));
            relativePrice2.add(priceHistoryDoubles2.get(i)/priceHistoryDoubles2.get(0));
        }

        XYDataset dataset1 = createDatasetFromYahoo3(stock.getSymbol(), relativePrice1, dates, stock2.getSymbol(), relativePrice2, dates2);
        String title = stock.getSymbol() + " and " + stock2.getSymbol() + " Cumulative Returns";
        JFreeChart chart = createTimeSeriesChart(dataset1,title, "Date", "Relative Price");
//        LegendTitle legend = new LegendTitle(chart.getPlot());
//        legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
////        legend.setFrame(new LineBorder());
//        legend.setBackgroundPaint(Color.white);
//        legend.setPosition(RectangleEdge.BOTTOM);
//        chart.addLegend(legend);
        return chart;
    }


    // chart 7.2- given stock symbol, start data and end date
    // plot percentage change in price for stock and index
    public static JFreeChart createPercentChangeStockVsIndexGraph(Stock stock, Stock stock2,  Integer timeFrame) throws IOException, ParseException{
// get first stock's data
        YahooStock yahooStock = new YahooStock();
        List<HistoricalQuote>  priceHistory = yahooStock.getHistory(stock.getSymbol(),timeFrame,"DAILY");
        ArrayList<Double> priceHistoryDoubles = new ArrayList<Double>();
        ArrayList<Long> dates = new ArrayList<>();

        for (HistoricalQuote quote: priceHistory) {
            priceHistoryDoubles.add(quote.getAdjClose().doubleValue());
            dates.add(quote.getDate().getTimeInMillis());
        }

        // get second stock's data
        List<HistoricalQuote>  priceHistory2 = yahooStock.getHistory(stock2.getSymbol(),timeFrame,"DAILY");
        ArrayList<Double> priceHistoryDoubles2 = new ArrayList<Double>();
        ArrayList<Long> dates2 = new ArrayList<>();

        for (HistoricalQuote quote: priceHistory2) {
            priceHistoryDoubles2.add(quote.getAdjClose().doubleValue());
            dates2.add(quote.getDate().getTimeInMillis());
        }

        // calculate percent changes
        ArrayList<Double> returns1 = getReturns(priceHistory, dates);
        ArrayList<Double> returns2 = getReturns(priceHistory2, dates2);



        TimeSeries s1 = createDatasetFromYahoo4(stock.getSymbol(), returns1, dates);
        TimeSeries s2 = createDatasetFromYahoo4(stock2.getSymbol(), returns2, dates2);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);

        String title = "% change for " + stock.getSymbol() + " and " + stock2.getSymbol();
        JFreeChart chart = createTimeSeriesChart(dataset, title, "Date", "Daily Returns (%)");


        return chart;
    }

    // chart 7.3- given stock symbol, start data and end date
    // plot return of stock vs return of index scatter plot
    public static JFreeChart createStockVsIndexScatterGraph(Stock stock, Stock stock2,  Integer timeFrame) throws IOException, ParseException{
        YahooStock yahooStock = new YahooStock();
        List<HistoricalQuote>  priceHistory = yahooStock.getHistory(stock.getSymbol(),timeFrame,"DAILY");
        ArrayList<Long> dates1 = new ArrayList<>();

        ArrayList<Double> returns1 = new ArrayList<Double>();
        ArrayList<Double> yesterdaysReturns1 = new ArrayList<Double>();

        for (int i=0; i<priceHistory.size()-1; i++) {
            HistoricalQuote quote1 = priceHistory.get(i);
            HistoricalQuote quote2 = priceHistory.get(i+1);

            Double price1 = quote1.getAdjClose().doubleValue();
            Double price2 = quote2.getAdjClose().doubleValue();

            Double return1 = (price2-price1)/price1;
            returns1.add(return1);
            dates1.add(quote1.getDate().getTimeInMillis());
        }

//        for (int i=1; i<returns1.size(); i++) {
//            yesterdaysReturns1.add(returns1.get(i-1));
//        }

        // second stock
        List<HistoricalQuote>  priceHistory2 = yahooStock.getHistory(stock2.getSymbol(),timeFrame,"DAILY");
        ArrayList<Long> dates2 = new ArrayList<>();

        ArrayList<Double> returns2 = new ArrayList<Double>();
        ArrayList<Double> yesterdaysReturns2 = new ArrayList<Double>();

        for (int i=0; i<priceHistory2.size()-1; i++) {
            HistoricalQuote quote1 = priceHistory.get(i);
            HistoricalQuote quote2 = priceHistory.get(i + 1);

            Double price1 = quote1.getAdjClose().doubleValue();
            Double price2 = quote2.getAdjClose().doubleValue();

            Double return1 = (price2 - price1) / price1;
            returns2.add(return1);
            dates2.add(quote1.getDate().getTimeInMillis());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries s1 = createReturnsDataset(stock.getSymbol(), returns1, returns2);

        dataset.addSeries(s1);
        String title = stock.getSymbol() + " Returns vs " + stock2.getSymbol() + " Returns";

        JFreeChart chart = createXYLineGraph(dataset,title, stock.getSymbol() + " Returns (%)", stock2.getSymbol() + " Returns (%)");

        chart.removeLegend();

        return chart;
    }




    public static void main(String[] args) throws IOException, ParseException {
        Graphs timeSeriesStock = new Graphs(
                "Time Series Stock");
        timeSeriesStock.pack();
        timeSeriesStock.setVisible(true);


    }




}
