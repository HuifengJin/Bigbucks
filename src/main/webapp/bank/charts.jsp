<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>

<%
    /**
     This application is for demonstration use only. It contains known application security
     vulnerabilities that were created expressly for demonstrating the functionality of
     application security testing tools. These vulnerabilities may present risks to the
     technical environment in which the application is installed. You must delete and
     uninstall this demonstration application upon completion of the demonstration for
     which it is intended.

     IBM DISCLAIMS ALL LIABILITY OF ANY KIND RESULTING FROM YOUR USE OF THE APPLICATION
     OR YOUR FAILURE TO DELETE THE APPLICATION FROM YOUR ENVIRONMENT UPON COMPLETION OF
     A DEMONSTRATION. IT IS YOUR RESPONSIBILITY TO DETERMINE IF THE PROGRAM IS APPROPRIATE
     OR SAFE FOR YOUR TECHNICAL ENVIRONMENT. NEVER INSTALL THE APPLICATION IN A PRODUCTION
     ENVIRONMENT. YOU ACKNOWLEDGE AND ACCEPT ALL RISKS ASSOCIATED WITH THE USE OF THE APPLICATION.

     IBM AltoroJ
     (c) Copyright IBM Corp. 2008, 2013 All Rights Reserved.
     */
%>

<jsp:include page="/header.jspf"/>

<div id="wrapper" style="width: 99%;">
    <jsp:include page="membertoc.jspf"/>
    <td valign="top" colspan="3" class="bb">
        <%@page import="com.ibm.security.appscan.altoromutual.model.Account"%>

        <%
            com.ibm.security.appscan.altoromutual.model.User user = (com.ibm.security.appscan.altoromutual.model.User)request.getSession().getAttribute("user");
        %>

        <script type="text/javascript">

            function confirminput(myform) {
                var stock1=document.getElementById("stockSymbo1l").value;
                var stock2=document.getElementById("stockSymbo1l").value;

                // if (stock1 is valid stock and stock2 is valid stock){
                //     alert("Please enter 2 valid stock symbols");
                //     return false;
                // }


                return true;
            }

        </script>

        <div class="fl" style="width: 99%;">

            <form id="tForm" name="tForm" method="post" action="showCharts" onsubmit="return (confirming(tForm));">

                <h1>Analyze Stocks</h1>

                <table cellSpacing="0" cellPadding="1" width="100%" border="0">
                    <tr>
                        <td><strong> Stock 1 Symbol:</strong>
                        </td>
                        <td><input type="text" id="stockSymbol1" name="stockSymbol1"></td>
                    </tr>

                    <tr>
                        <td><strong> Index Symbol (Suggested Index S&P500):  </strong>
                        </td>
                        <td><input type="text" id="stockSymbol2" name="stockSymbol2" value= "^GSPC" style="display: none;"></td>
                    </tr>




                    <label for="chartSelection">Choose a chart:</label>
                    <select id="chartSelection" name="chartSelection" onchange="showDiv(this)">
                        <option value="14.1">Adjusted Close Price Graph</option>
                        <option value="14.2">Return Graph</option>
                        <option value="14.3">Simple Returns Graph</option>
                        <option value="14.4">Histogram of Simple Return</option>
                        <option value="7.1">Stock vs Index Return Time Series Graph</option>
                        <option value="7.2">Stock vs Index Daily Price Change Graph</option>
                        <option value="7.3">Scatter Plot of Stock Return vs Index Return</option>
                    </select>

                    <tr>
                        <td colspan="2" align="center"><input type="submit" name="trade" value="Create Chart" ID="createCharts"></td>
                    </tr>



                    <tr>
                        <td colspan="2" align="center">
                            <span id="_ctl0__ctl0_Content_Main_postResp" align="center"><span style='color: #ff0000'><%=(request.getAttribute("message")==null)?"":request.getAttribute("message") %></span></span>
                            <span id="soapResp" name="soapResp" align="center" />
                        </td>
                    </tr>
                    <label for="timeSelection">Choose a time frame:</label>
                    <select id="timeSelection" name="timeSelection">
                        <option value="Past Year">Past Year</option>
                        <option value="Past Two Years">Past Two Years</option>
                        <option value="Past Five Years">Past Five Years</option>
                    </select>
                </table>
            </form>

            <script type="text/javascript">
                function showDiv(select){
                    if((select.value =="7.1") || (select.value =="7.2") || (select.value =="7.3")) {
                        document.getElementById('stockSymbol2').style.display = "block";
                    } else{
                        document.getElementById('stockSymbol2').style.display = "none";
                    }
                }
            </script>



        </div>
    </td>
</div>

<jsp:include page="/footer.jspf"/>
