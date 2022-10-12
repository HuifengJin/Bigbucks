<%@page import="com.ibm.security.appscan.altoromutual.model.OperationsUtil" %>
<%@page import="com.ibm.security.appscan.altoromutual.util.ServletUtil" %>
<%@page import="yahoofinance.*" %>

<%@page import="com.ibm.security.appscan.altoromutual.util.DBUtil" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ page import="java.text.DecimalFormat" %>

<jsp:include page="/header.jspf"/>

<div id="wrapper" style="width: 99%;">
    <jsp:include page="/bank/membertoc.jspf"/>
    <td valign="top" colspan="3" class="bb">
        <%@ page import="com.ibm.security.appscan.altoromutual.model.*" %>
        <%@ page import="java.math.BigDecimal" %>
        <%@ page import="java.util.*" %>
        <%@ page import="yahoofinance.Stock" %>
        <%@ page import="yahoofinance.YahooFinance" %>
        <%@ page import="yahoofinance.histquotes.HistoricalQuote" %>
        <%@ page import="yahoofinance.histquotes.Interval" %>
        <div class="fl" style="width: 99%;">

            <%
                com.ibm.security.appscan.altoromutual.model.User user = (com.ibm.security.appscan.altoromutual.model.User)request.getSession().getAttribute("user");
                String username = user.getUsername();
            %>
            <h1>Stock Holdings</h1>

            <table cellspacing="0" cellpadding="3" rules="all" border="1" id="_ctl0__ctl0_Content_Main_MyTransactions"
                   style="width:100%;border-collapse:collapse;">
                <tr style="color:White;background-color:#BFD7DA;font-weight:bold;">
                    <td>Symbol</td>
                    <td>Name</td>
                    <td>Shares Held</td>
                    <td>Price Per Share</td>
                </tr>
                <%
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
                    <td><%=hsn%></td>
                    <td><%=sn%></td>
                    <td><%=amt%></td>
                    <td><%=vps%></td>
                </tr>
                <%
                }%>
                <tr>
                    <!-- TODO PAGES: <td colspan="4"><span>1</span>&nbsp;<a href="javascript:__doPostBack('_ctl0$_ctl0$Content$Main$MyTransactions$_ctl54$_ctl1','')">2</a></td> -->
                </tr>
            </table>
        </div>

        <div class="fl" style="width: 99%;">
            <h1>Risk Profile</h1>

            <table border="0" style="padding-bottom:10px;">

                <tr>
                    <td valign=top>Portfolio Return: </td>
                    <%
                        double r = user.getPortfolioReturn(username)*100;
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        String R = decimalFormat.format(r);
                    %>
                    <td><%=R + "%"%></td>
                </tr>
                <tr>
                    <td valign=top>Portfolio Volatility: </td>
                    <%
                        double sd = user.getPortfolioVolatility(username)*100;
                        String SD = decimalFormat.format(sd);
                    %>
                    <td><%=SD + "%"%></td>
                </tr>
                <tr>
                    <td valign=top>Risk Free rate: </td>
                    <%
                        BigDecimal tnx = YahooStock.getCurrent("^TNX"); // US Treasury 10 tr bond yield (%) e.g. 1.57
                        double rf = tnx.doubleValue();
                    %>
                    <td><%=rf+ "%"%></td>
                </tr>
                <tr>
                    <td valign=top>Portfolio Sharpe Ratio: </td>
                    <%
                        double sr = user.getPortfolioSharp(username);
                        String SR = decimalFormat.format(sr);
                    %>
                    <td><%=SR %></td>
                </tr>

            </table>
        </div>

    </td>
</div>




<jsp:include page="/footer.jspf"/>
