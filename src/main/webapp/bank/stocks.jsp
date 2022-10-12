<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

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
		<div class="fl" style="width: 99%;">
			<div style="display:inline;">
				<script type="text/javascript" src="../util/swfobject.js"></script>
				<script type="text/javascript">
						swfobject.registerObject("myId", "9.0.0", "../util/expressInstall.swf");
				</script>
			
				<object id="myId" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="550" height="750">
					<param name="movie" value="../util/EasyStock.swf" />
			   		<!--[if !IE]>-->
					<object type="application/x-shockwave-flash" data="../util/EasyStock.swf" width="550" height="750">
					<!--<![endif]-->
			</div>

			<div class="fl" style="width: 99%;">

				<form id="tForm" name="tForm" action="doStock" method="post" onsubmit="return (confirminput(tForm));">

					<h1>Trade Stocks</h1>

					<table cellSpacing="0" cellPadding="1" width="100%" border="0">
						<tr>
							<td><strong>Action:</strong>
							</td>
							<td>
								<select size="1" id="action" name="action">
										<option value="buy"> buy </option>
										<option value="sell"> sell </option>
								</select>
							</td>
						</tr>
						<tr>
							<td><strong> Stock:</strong>
							</td>
							<td><input type="text" id="stockName" name="stockName"></td>
						</tr>
						<tr>
							<td><strong> Amount:</strong>
							</td>
							<td><input type="text" id="tradeAmount" name="tradeAmount"></td>
						</tr>
						<td><strong>Select Account:</strong></td>
						<td>
							<select size="1" id="Account" name="Account">
								<%
									for (Account account: user.getAccounts()){
										out.println("<option value=\""+account.getAccountId()+"\">" + account.getAccountId() + " " + account.getAccountName() + "</option>");
									}
								%>
							</select>
						</td>
						<tr>
							<td colspan="3" align="center"><input type="submit" name="trade" value="Trade Stock" ID="trade"></td>
						</tr>
						<tr>
							<td colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<span id="_ctl0__ctl0_Content_Main_postResp" align="center"><span style='color: Red'><%=(request.getAttribute("message")==null)?"":request.getAttribute("message") %></span></span>
								<span id="soapResp" name="soapResp" align="center" />
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
    </td>	
</div>
<jsp:include page="/footer.jspf"/>   

<div style="display:none;">
	<script type="text/javascript" src="util/swfobject.js"></script>
	<script type="text/javascript">
			swfobject.registerObject("myId", "9.0.0", "util/expressInstall.swf");
	</script>

	<object id="myId" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="400" height="300">
		<param name="movie" value="util/vulnerable.swf" />
   		<!--[if !IE]>-->
		<object type="application/x-shockwave-flash" data="util/vulnerable.swf" width="400" height="300">
		<!--<![endif]-->
</div>