package com.ibm.security.appscan.altoromutual.servlet;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ibm.security.appscan.Log4AltoroJ;
import com.ibm.security.appscan.altoromutual.util.DBUtil;
import com.ibm.security.appscan.altoromutual.util.ServletUtil;

import static java.lang.System.out;

@WebServlet("/RegisterServlet")

public class RegisterServlet extends HttpServlet{
    public RegisterServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = null;
        String message = null;

        try {
            username = request.getParameter("username");
            if (username != null)
                username = username.trim().toLowerCase();

            String firstname = request.getParameter("firstname");
            String lastname = request.getParameter("lastname");
            String password1 = request.getParameter("password1");
            String password2 = request.getParameter("password2");

            if (username == null || username.trim().length() == 0
                    || password1 == null || password1.trim().length() == 0
                    || password2 == null || password2.trim().length() == 0)
                message = "Username or password should not be empty.";

            else if(password1.length() < 10){
                message = "Password must be at least 10 characters. ";
            }

            else if (!password1.equals(password2)){
                message = "Entered passwords did not match.";
            }

            else if (DBUtil.isExistUser(username))
            {
                message = "Register Failed: We're sorry, but this username was found in our system. Please use a different username.";
                //Log4AltoroJ.getInstance().RegisterError("Register failed >>> User: " +username + " >>> Password: " + password1);
                //throw new Exception(message);
            }

            else {
                message = DBUtil.addUser(username, password1, firstname, lastname);
            }

            if (message != null)
                message = "Error: " + message;
            else
                message = "Requested operation has completed successfully.";

            request.getSession().setAttribute("message", message);

        } catch (Exception ex) {
            request.getSession(true).setAttribute("registerError", ex.getLocalizedMessage());
        }

        response.sendRedirect("register.jsp");
    }

}
