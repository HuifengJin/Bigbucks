<%--
  Created by IntelliJ IDEA.
  User: wangwenjing
  Date: 2022/3/16
  Time: 6:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<jsp:include page="header.jspf"/>
<div id="wrapper" style="width: 99%;">
    <jsp:include page="/toc.jspf"/>
    <td valign="top" colspan="3" class="bb">
        <div class="fl" style="width: 99%;">
            <p><span style="color:#FF0066;font-size:12pt;font-weight:bold;">
		<%
            java.lang.String error = (String)request.getSession().getAttribute("message");

            if (error != null && error.trim().length() > 0){
                out.print(error);
                request.getSession().removeAttribute("message");
            }
        %>
		</span></p>
            <h1>Create New User</h1>

            <form action="RegisterServlet" method="post">
                <table>
                    <tr>
                        <td>
                            First Name:
                        </td>
                        <td>
                            <input type="text" id="firstname" name="firstname" value="" style="width: 150px;">
                        </td>
                        <td>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Last Name:
                        </td>
                        <td>
                            <input type="text" id="lastname" name="lastname" style="width: 150px;">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Username:
                        </td>
                        <td>
                            <input type="text" id="username" name="username" value="" style="width: 150px;">
                        </td>
                        <td>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Password:
                        </td>
                        <td>
                            <input type="password" id="password1" name="password1" value="" style="width: 150px;">
                        </td>
                        <td>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Re-enter password:
                        </td>
                        <td>
                            <input type="password" id="password2" name="password2" value="" style="width: 150px;">
                        </td>
                        <td>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <input type="submit" name="btnSubmit" value="Create">
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </td>
</div>
<jsp:include page="footer.jspf"/>