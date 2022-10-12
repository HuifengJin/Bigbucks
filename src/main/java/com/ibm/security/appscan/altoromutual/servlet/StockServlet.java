package com.ibm.security.appscan.altoromutual.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.security.appscan.altoromutual.util.DBUtil;
import com.ibm.security.appscan.altoromutual.model.OperationsUtil;
import com.ibm.security.appscan.altoromutual.util.ServletUtil;
import yahoofinance.*;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

@WebServlet("/StockServlet")

public class StockServlet extends HttpServlet {
    public StockServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if(!ServletUtil.isLoggedin(request)){
            response.sendRedirect("login.jsp");
            return ;
        }
        String tick = request.getParameter("stockName");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -5); // from 5 year ago

        String message;
        String TradeStock_message;
        String AddHistory_message;
        Stock stock = YahooFinance.get(tick);
        if (stock == null){
            message = "No matching stocks for " + tick;
        }else{
            BigDecimal price = stock.getQuote().getPrice();
            TradeStock_message = OperationsUtil.doServletTradeStock(request,price);
            if(DBUtil.isTickExisted(tick))
            {
                message = TradeStock_message;
            }
            else {
                List<HistoricalQuote> stockHistQuotes = stock.getHistory(from, to, Interval.DAILY);
                AddHistory_message = OperationsUtil.addHistory(stockHistQuotes);
                message = TradeStock_message + "\n" + AddHistory_message;
            }
        }


        RequestDispatcher dispatcher = request.getRequestDispatcher("stocks.jsp");
        request.setAttribute("message", message);
        dispatcher.forward(request, response);
    }
}