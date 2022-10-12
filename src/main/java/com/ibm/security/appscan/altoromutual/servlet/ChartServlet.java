package com.ibm.security.appscan.altoromutual.servlet;

import com.ibm.security.appscan.altoromutual.model.YahooStock;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import yahoofinance.Stock;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

import static com.ibm.security.appscan.altoromutual.model.Graphs.*;

//@WebServlet("/images/*")
public class ChartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String stock1name = String.valueOf(request.getParameter("stockSymbol1"));
        String stock2name = String.valueOf(request.getParameter("stockSymbol2"));

        String chartType = String.valueOf(request.getParameter("chartSelection"));
        String timeFrameString = String.valueOf(request.getParameter("timeSelection"));
        Integer timeFrame = 0;

        if (timeFrameString.equals("Past Year")) {
            timeFrame = 1;
        }
        else if (timeFrameString.equals("Past Two Years")) {
            timeFrame = 2;
        }
        else {
            timeFrame = 5;
        }
        Boolean test = true;

        YahooStock yahooStock = new YahooStock();

        String[] stockSymbols = {stock1name, stock2name};


        Stock stock1 = null;
        Stock stock2 = null;
        JFreeChart chart = null;
        try {
            if (chartType.equals("14.1")) {
                stock1 = yahooStock.getStock(stockSymbols).get(stock1name);
                stock2 = yahooStock.getStock(stockSymbols).get(stock2name);
                chart = createSimplePriceGraph(stock1, timeFrame);
            }
            else if (chartType.equals("14.2")) {
                stock1 = yahooStock.getStock(stockSymbols).get(stock1name);
                stock2 = yahooStock.getStock(stockSymbols).get(stock2name);
                chart = createSimpleReturnGraph(stock1, timeFrame);
            }
            else if (chartType.equals("14.3")) {
                stock1 = yahooStock.getStock(stockSymbols).get(stock1name);
                stock2 = yahooStock.getStock(stockSymbols).get(stock2name);
                chart = createTodayVsYesterdayGraph(stock1, timeFrame);
            }
            else if (chartType.equals("14.4")) {
                stock1 = yahooStock.getStock(stockSymbols).get(stock1name);
                stock2 = yahooStock.getStock(stockSymbols).get(stock2name);
                chart = createSimpleReturnHistogram(stock1, timeFrame);
            }
            else if (chartType.equals("7.1")) {
                stock1 = yahooStock.getStock(stockSymbols).get(stock1name);
                stock2 = yahooStock.getStock(stockSymbols).get(stock2name);
                chart = createStockVsIndexReturnGraph(stock1, stock2, timeFrame);
            }
            else if (chartType.equals("7.2")) {
                stock1 = yahooStock.getStock(stockSymbols).get(stock1name);
                stock2 = yahooStock.getStock(stockSymbols).get(stock2name);
                chart = createPercentChangeStockVsIndexGraph(stock1, stock2, timeFrame);
            }
            else if (chartType.equals("7.3")) {
                stock1 = yahooStock.getStock(stockSymbols).get(stock1name);
                stock2 = yahooStock.getStock(stockSymbols).get(stock2name);
                chart = createStockVsIndexScatterGraph(stock1, stock2, timeFrame);
            }
            else {
                test = false;
//                String[] stockSymbols2 = {"TSLA", "AAPL", "^IXIC"};
//
//                Stock s = yahooStock.getStock(stockSymbols2).get("AAPL");
//                chart = createSimplePriceGraph(s);
            }
            System.out.println("test");
        } catch (ParseException e) {
            System.out.println("here 5");
            e.printStackTrace();
        }

        try {
            final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
            if (test) {
                ChartUtils.writeChartAsPNG(response.getOutputStream(), chart, 600, 400, info);
            }

        } catch (Exception e) {
            System.out.println(e);
        }




//        String message = OperationsUtil.doServletCharts(request, stock1, stock2);

        RequestDispatcher dispatcher = request.getRequestDispatcher("charts.jsp");
        request.setAttribute("message", "");
//        request.setAttribute("image1", imagePath);
        dispatcher.forward(request, response);
    }

}
