<%--
  Created by IntelliJ IDEA.
  User: wangwenjing
  Date: 2022/4/1
  Time: 12:48 PM
  To change this template use File | Settings | File Templates.
--%>

<%@page import="com.ibm.security.appscan.altoromutual.util.DBUtil" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>

<jsp:include page="/header.jspf"/>
<div id="wrapper" style="width: 99%;">
  <jsp:include page="/bank/membertoc.jspf"/>
  <td valign="top" colspan="3" class="bb">
    <%@page import="com.ibm.security.appscan.altoromutual.util.ServletUtil"%>
      <%@ page import="yahoofinance.Stock" %>
    <%@ page import="yahoofinance.YahooFinance" %>
    <%@ page import="java.math.BigDecimal" %>
    <%@ page import="java.text.SimpleDateFormat" %>
    <%@ page import="java.util.Date" %>
      <%@ page import="java.util.Locale" %>
    <%@ page import="java.text.NumberFormat" %>
      <%@ page import="java.text.DecimalFormat" %>
      <%@ page import="static com.ibm.security.appscan.altoromutual.model.User.Role.Admin" %>
      <%@ page import="com.ibm.security.appscan.altoromutual.model.*" %>

          <%
        String[] users;
      User user1 = (User)request.getSession().getAttribute("user");
      if (user1.getRole() != Admin){
          users = new String[0];
            out.print("Not permitted");
        }else{
            System.out.println("ok");
            users = ServletUtil.getBankUsers();
        }
%>
    <h1>Users' Stock Holdings List</h1>

    <table cellspacing="0" cellpadding="3" rules="all" border="1" id="_ctl0__ctl0_Content_Main_MyTransactions"
           style="width:100%;border-collapse:collapse;">
      <tr style="color:White;background-color:#BFD7DA;font-weight:bold;">
        <td>User Name</td>
        <td>Symbol</td>
        <td>Name</td>
        <td>Shares Held</td>
        <td>Price Per Share</td>
      </tr>
      <%

        for (int j = 0; j < users.length; j++) {
          String username = users[j];
          Holdings[] hold = DBUtil.getHoldings(username);
          for (int i = 0; i < hold.length; i++) {
            double amt = hold[i].getAmountOwn();
            String hsn = hold[i].getStockName().toUpperCase();
            Stock stock = YahooFinance.get(hsn);
            BigDecimal currPrice = stock.getQuote().getPrice();
            double vps = currPrice.doubleValue();
            String sn = stock.getName();
      %>

      <tr>
        <td><%=username%></td>
        <td><%=hsn%></td>
        <td><%=sn%></td>
        <td><%=amt%></td>
        <td><%=vps%></td>
      </tr>
      <%}
        }%>
      <tr>
        <!-- TODO PAGES: <td colspan="4"><span>1</span>&nbsp;<a href="javascript:__doPostBack('_ctl0$_ctl0$Content$Main$MyTransactions$_ctl54$_ctl1','')">2</a></td> -->
      </tr>
    </table>

    <h1>Current Day's Market Orders</h1>

      <table cellspacing="0" cellpadding="3" rules="all" border="1" id="_ctl0__ctl0_Content_Main_MyTransactions"
             style="width:100%;border-collapse:collapse;">
        <tr style="color:White;background-color:#BFD7DA;font-weight:bold;">
          <td>User Name</td>
          <td>Date</td>
          <td>Ticker Symbol</td>
          <td>Name</td>
          <td>Action</td>
          <td>Amount</td>
          <td>Traded Price Per Share</td>
        </tr>
        <%
          String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
          Locale usa = new Locale("en", "US");
          NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(usa);

          for (int j = 0; j < users.length; j++) {
            String username = users[j];
            StockTransaction[] transaction = DBUtil.getStockTransaction(username);
            for (int i = 0; i < transaction.length; i++) {
              String date = transaction[i].getDate();
              String act = transaction[i].getAction();
              double amt = transaction[i].getAmountOwn();
              String hsn = transaction[i].getStockName().toUpperCase();
              Stock stock = YahooFinance.get(hsn);
              String sn = stock.getName();
              double sps = transaction[i].getStockPrice();
              String spsDF = dollarFormat.format(sps);
              if (act.equals("sell")) {
                amt = (-1) * amt;
              }
              //change the date format
              date = date.split(" ")[0];
              if (date.equals(today)) {

        %>

        <tr>
          <td><%=username%></td>
          <td><%=date%></td>
          <td><%=hsn%></td>
          <td><%=sn%></td>
          <td><%=act%></td>
          <td><%=amt%></td>
          <td><%=spsDF%></td>
        </tr>
        <% }}
        }%>
        <tr>
          <!-- TODO PAGES: <td colspan="4"><span>1</span>&nbsp;<a href="javascript:__doPostBack('_ctl0$_ctl0$Content$Main$MyTransactions$_ctl54$_ctl1','')">2</a></td> -->
        </tr>
      </table>

        <%
                com.ibm.security.appscan.altoromutual.model.User user = (com.ibm.security.appscan.altoromutual.model.User)request.getSession().getAttribute("user");
                String username = user.getUsername();
            %>
      <h1>Overall Risk-Return Profile</h1>
      <table cellspacing="0" cellpadding="3" rules="all" border="1" id="_ctl0__ctl0_Content_Main_MyTransactions"
             style="width:100%;border-collapse:collapse;">
        <tr style="color:White;background-color:#BFD7DA;font-weight:bold;">
          <td>User Name</td>
          <td>Annualized Portfolio Return</td>
          <td>Annualized Portfolio Volatility</td>
          <td>Sharp Ratio</td>
        </tr>
        <%

          for (int j = 0; j < users.length; j++) {
            String name = users[j];
            double r = user.getPortfolioReturn(name)*100;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String R = decimalFormat.format(r);
            double sd = user.getPortfolioVolatility(name)*100;
            String SD = decimalFormat.format(sd);
            double sr = user.getPortfolioSharp(name);
            String SR = decimalFormat.format(sr);
        %>

        <tr>
          <td><%=name%></td>
          <td><%=R + "%"%></td>
          <td><%=SD + "%"%></td>
          <td><%=SR%></td>
        </tr>
        <%
        }%>
        <tr>
          <!-- TODO PAGES: <td colspan="4"><span>1</span>&nbsp;<a href="javascript:__doPostBack('_ctl0$_ctl0$Content$Main$MyTransactions$_ctl54$_ctl1','')">2</a></td> -->
        </tr>
      </table>

</div>


</td>
</div>

<jsp:include page="/footer.jspf"/>