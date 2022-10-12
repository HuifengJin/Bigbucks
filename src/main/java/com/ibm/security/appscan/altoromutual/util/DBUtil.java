/**
 * This application is for demonstration use only. It contains known application security
 * vulnerabilities that were created expressly for demonstrating the functionality of
 * application security testing tools. These vulnerabilities may present risks to the
 * technical environment in which the application is installed. You must delete and
 * uninstall this demonstration application upon completion of the demonstration for
 * which it is intended.
 * <p>
 * IBM DISCLAIMS ALL LIABILITY OF ANY KIND RESULTING FROM YOUR USE OF THE APPLICATION
 * OR YOUR FAILURE TO DELETE THE APPLICATION FROM YOUR ENVIRONMENT UPON COMPLETION OF
 * A DEMONSTRATION. IT IS YOUR RESPONSIBILITY TO DETERMINE IF THE PROGRAM IS APPROPRIATE
 * OR SAFE FOR YOUR TECHNICAL ENVIRONMENT. NEVER INSTALL THE APPLICATION IN A PRODUCTION
 * ENVIRONMENT. YOU ACKNOWLEDGE AND ACCEPT ALL RISKS ASSOCIATED WITH THE USE OF THE APPLICATION.
 * <p>
 * IBM AltoroJ
 * (c) Copyright IBM Corp. 2008, 2013 All Rights Reserved.
 */

package com.ibm.security.appscan.altoromutual.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.ibm.security.appscan.Log4AltoroJ;
import com.ibm.security.appscan.altoromutual.model.*;
import com.ibm.security.appscan.altoromutual.model.User.Role;

/**
 * Utility class for database operations
 * @author Alexei
 *
 */
public class DBUtil {

    private static final String PROTOCOL = "jdbc:derby:";
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    public static final String CREDIT_CARD_ACCOUNT_NAME = "Credit Card";
    public static final String CHECKING_ACCOUNT_NAME = "Checking";
    public static final String SAVINGS_ACCOUNT_NAME = "Savings";

    public static final double CASH_ADVANCE_FEE = 2.50;

    private static DBUtil instance = null;
    private Connection connection = null;
    private DataSource dataSource = null;

    //private constructor
    private DBUtil() {
        /*
         **
         **			Default location for the database is current directory:
         **			System.out.println(System.getProperty("user.home"));
         **			to change DB location, set derby.system.home property:
         **			System.setProperty("derby.system.home", "[new_DB_location]");
         **
         */

        String dataSourceName = ServletUtil.getAppProperty("database.alternateDataSource");

        /* Connect to an external database (e.g. DB2) */
        if (dataSourceName != null && dataSourceName.trim().length() > 0) {
            try {
                Context initialContext = new InitialContext();
                Context environmentContext = (Context) initialContext.lookup("java:comp/env");
                dataSource = (DataSource) environmentContext.lookup(dataSourceName.trim());
            } catch (Exception e) {
                e.printStackTrace();
                Log4AltoroJ.getInstance().logError(e.getMessage());
            }

            /* Initialize connection to the integrated Apache Derby DB*/
        } else {
            System.setProperty("derby.system.home", System.getProperty("user.home") + "/altoro/");
            System.out.println("Derby Home=" + System.getProperty("derby.system.home"));

            try {
                //load JDBC driver
                Class.forName(DRIVER).newInstance();
            } catch (Exception e) {
                Log4AltoroJ.getInstance().logError(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static Connection getConnection() throws SQLException {

        if (instance == null)
            instance = new DBUtil();

        if (instance.connection == null || instance.connection.isClosed()) {

            //If there is a custom data source configured use it to initialize
            if (instance.dataSource != null) {
                instance.connection = instance.dataSource.getConnection();

                if (ServletUtil.isAppPropertyTrue("database.reinitializeOnStart")) {
                    instance.initDB();
                }
                return instance.connection;
            }

            // otherwise initialize connection to the built-in Derby database
            try {
                //attempt to connect to the database
                instance.connection = DriverManager.getConnection(PROTOCOL + "altoro");

                if (ServletUtil.isAppPropertyTrue("database.reinitializeOnStart")) {
                    instance.initDB();
                }
            } catch (SQLException e) {
                //if database does not exist, create it and initialize it
                if (e.getErrorCode() == 40000) {
                    instance.connection = DriverManager.getConnection(PROTOCOL + "altoro;create=true");
                    instance.initDB();
                    //otherwise pass along the exception
                } else {
                    throw e;
                }
            }

        }

        return instance.connection;
    }


    /*
     * Create and initialize the database
     */
    private void initDB() throws SQLException {

        Statement statement = connection.createStatement();

        try {
            statement.execute("DROP TABLE PEOPLE");
            statement.execute("DROP TABLE ACCOUNTS");
            statement.execute("DROP TABLE TRANSACTIONS");
            statement.execute("DROP TABLE FEEDBACK");
//            statement.execute("DROP TABLE HOLDINGS");
        } catch (SQLException e) {
            // not a problem
        }

        statement.execute("CREATE TABLE PEOPLE (USER_ID VARCHAR(50) NOT NULL, PASSWORD VARCHAR(20) NOT NULL, FIRST_NAME VARCHAR(100) NOT NULL, LAST_NAME VARCHAR(100) NOT NULL, ROLE VARCHAR(50) NOT NULL, PRIMARY KEY (USER_ID))");
        statement.execute("CREATE TABLE FEEDBACK (FEEDBACK_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1022, INCREMENT BY 1), NAME VARCHAR(100) NOT NULL, EMAIL VARCHAR(50) NOT NULL, SUBJECT VARCHAR(100) NOT NULL, COMMENTS VARCHAR(500) NOT NULL, PRIMARY KEY (FEEDBACK_ID))");
        statement.execute("CREATE TABLE ACCOUNTS (ACCOUNT_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 800000, INCREMENT BY 1), USERID VARCHAR(50) NOT NULL, ACCOUNT_NAME VARCHAR(100) NOT NULL, BALANCE DOUBLE NOT NULL, PRIMARY KEY (ACCOUNT_ID))");
        statement.execute("CREATE TABLE TRANSACTIONS (TRANSACTION_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 2311, INCREMENT BY 1), ACCOUNTID BIGINT NOT NULL, DATE TIMESTAMP NOT NULL, TYPE VARCHAR(100) NOT NULL, AMOUNT DOUBLE NOT NULL, PRIMARY KEY (TRANSACTION_ID))");
//        statement.execute("CREATE TABLE HOLDINGS(HOLDINGS_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1), DATE VARCHAR(50) NOT NULL, USERID VARCHAR(50) NOT NULL,STOCKNAME VARCHAR(100) NOT NULL, AMOUNT DOUBLE NOT NULL, PRICE DOUBLE NOT NULL, ACTION VARCHAR(100) NOT NULL, PRIMARY KEY (HOLDINGS_ID))");


        statement.execute("INSERT INTO PEOPLE (USER_ID,PASSWORD,FIRST_NAME,LAST_NAME,ROLE) VALUES ('admin', 'admin', 'Admin', 'User','admin'), ('jsmith','demo1234', 'John', 'Smith','user'),('jdoe','demo1234', 'Jane', 'Doe','user'),('sspeed','demo1234', 'Sam', 'Speed','user'),('tuser','tuser','Test', 'User','user')");
        statement.execute("INSERT INTO ACCOUNTS (USERID,ACCOUNT_NAME,BALANCE) VALUES ('admin','Corporate', 52394783.61), ('admin','" + CHECKING_ACCOUNT_NAME + "', 93820.44), ('jsmith','" + SAVINGS_ACCOUNT_NAME + "', 10000.42), ('jsmith','" + CHECKING_ACCOUNT_NAME + "', 15000.39), ('jdoe','" + SAVINGS_ACCOUNT_NAME + "', 10.00), ('jdoe','" + CHECKING_ACCOUNT_NAME + "', 25.00), ('sspeed','" + SAVINGS_ACCOUNT_NAME + "', 59102.00), ('sspeed','" + CHECKING_ACCOUNT_NAME + "', 150.00)");
        statement.execute("INSERT INTO ACCOUNTS (ACCOUNT_ID,USERID,ACCOUNT_NAME,BALANCE) VALUES (4539082039396288,'jsmith','" + CREDIT_CARD_ACCOUNT_NAME + "', 100.42),(4485983356242217,'jdoe','" + CREDIT_CARD_ACCOUNT_NAME + "', 10000.97)");
        statement.execute("INSERT INTO TRANSACTIONS (ACCOUNTID,DATE,TYPE,AMOUNT) VALUES (800003,'2017-03-19 15:02:19.47','Withdrawal', -100.72), (800002,'2017-03-19 15:02:19.47','Deposit', 100.72), (800003,'2018-03-19 11:33:19.21','Withdrawal', -1100.00), (800002,'2018-03-19 11:33:19.21','Deposit', 1100.00), (800003,'2018-03-19 18:00:00.33','Withdrawal', -600.88), (800002,'2018-03-19 18:00:00.33','Deposit', 600.88), (800002,'2019-03-07 04:22:19.22','Withdrawal', -400.00), (800003,'2019-03-07 04:22:19.22','Deposit', 400.00), (800002,'2019-03-08 09:00:00.22','Withdrawal', -100.00), (800003,'2019-03-08 09:22:00.22','Deposit', 100.00), (800002,'2019-03-11 16:00:00.10','Withdrawal', -400.00), (800003,'2019-03-11 16:00:00.10','Deposit', 400.00), (800005,'2018-01-10 15:02:19.47','Withdrawal', -100.00), (800004,'2018-01-10 15:02:19.47','Deposit', 100.00), (800004,'2018-04-14 04:22:19.22','Withdrawal', -10.00), (800005,'2018-04-14 04:22:19.22','Deposit', 10.00), (800004,'2018-05-15 09:00:00.22','Withdrawal', -10.00), (800005,'2018-05-15 09:22:00.22','Deposit', 10.00), (800004,'2018-06-11 11:01:30.10','Withdrawal', -10.00), (800005,'2018-06-11 11:01:30.10','Deposit', 10.00)");
//        statement.execute("INSERT INTO HOLDINGS(USERID,DATE,STOCKNAME,AMOUNT,PRICE,ACTION) VALUES('test1','2020-01-01','AAPL',100,100,'Buy')");


        Log4AltoroJ.getInstance().logInfo("Database initialized");
    }

    /**
     * Retrieve feedback details
     * @param feedbackId specific feedback ID to retrieve or Feedback.FEEDBACK_ALL to retrieve all stored feedback submissions
     */
    public static ArrayList<Feedback> getFeedback(long feedbackId) {
        ArrayList<Feedback> feedbackList = new ArrayList<Feedback>();

        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            String query = "SELECT * FROM FEEDBACK";

            if (feedbackId != Feedback.FEEDBACK_ALL) {
                query = query + " WHERE FEEDBACK_ID = " + feedbackId + "";
            }

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("NAME");
                String email = resultSet.getString("EMAIL");
                String subject = resultSet.getString("SUBJECT");
                String message = resultSet.getString("COMMENTS");
                long id = resultSet.getLong("FEEDBACK_ID");
                Feedback feedback = new Feedback(id, name, email, subject, message);
                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            Log4AltoroJ.getInstance().logError("Error retrieving feedback: " + e.getMessage());
        }

        return feedbackList;
    }


    /**
     * Authenticate user
     * @param user user name
     * @param password password
     * @return true if valid user, false otherwise
     * @throws SQLException
     */
    public static boolean isValidUser(String user, String password) throws SQLException {
        if (user == null || password == null || user.trim().length() == 0 || password.trim().length() == 0)
            return false;

        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*)FROM PEOPLE WHERE USER_ID = '" + user + "' AND PASSWORD='" + password + "'"); /* BAD - user input should always be sanitized */

        if (resultSet.next()) {

            if (resultSet.getInt(1) > 0)
                return true;
        }
        return false;
    }

    public static boolean isExistUser(String user) throws SQLException {
        if (user == null || user.trim().length() == 0)
            return false;

        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*)FROM PEOPLE WHERE USER_ID = '" + user + "'"); /* BAD - user input should always be sanitized */

        if (resultSet.next()) {

            if (resultSet.getInt(1) > 0)
                return true;
        }
        return false;
    }

    /**
     * Get user information
     * @param username
     * @return user information
     * @throws SQLException
     */
    public static User getUserInfo(String username) throws SQLException {
        if (username == null || username.trim().length() == 0)
            return null;

        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT FIRST_NAME,LAST_NAME,ROLE FROM PEOPLE WHERE USER_ID = '" + username + "' "); /* BAD - user input should always be sanitized */

        String firstName = null;
        String lastName = null;
        String roleString = null;
        if (resultSet.next()) {
            firstName = resultSet.getString("FIRST_NAME");
            lastName = resultSet.getString("LAST_NAME");
            roleString = resultSet.getString("ROLE");
        }

        if (firstName == null || lastName == null)
            return null;

        User user = new User(username, firstName, lastName);

        if (roleString.equalsIgnoreCase("admin"))
            user.setRole(Role.Admin);

        return user;
    }

    /**
     * Get all accounts for the specified user
     * @param username
     * @return
     * @throws SQLException
     */
    public static Account[] getAccounts(String username) throws SQLException {
        if (username == null || username.trim().length() == 0)
            return null;

        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT ACCOUNT_ID, ACCOUNT_NAME, BALANCE FROM ACCOUNTS WHERE USERID = '" + username + "' "); /* BAD - user input should always be sanitized */

        ArrayList<Account> accounts = new ArrayList<Account>(3);
        while (resultSet.next()) {
            long accountId = resultSet.getLong("ACCOUNT_ID");
            String name = resultSet.getString("ACCOUNT_NAME");
            double balance = resultSet.getDouble("BALANCE");
            Account newAccount = new Account(accountId, name, balance);
            accounts.add(newAccount);
        }

        return accounts.toArray(new Account[accounts.size()]);
    }

    /**
     * Get all holdings for the specified user
     * @param username
     * @return
     * @throws SQLException
     */
    public static Holdings[] getHoldings(String username) throws SQLException{
        if (username == null || username.trim().length() == 0)
            return null;
        if (username == "admin")
            return getAllHoldings();
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet =statement.executeQuery("SELECT STOCKNAME, AMOUNT FROM HOLDINGS WHERE USERID = '"+ username +"' "); /* BAD - user input should always be sanitized */
        ArrayList<Holdings> holdings = new ArrayList<Holdings>(3);
        while (resultSet.next()){
            String stockname = resultSet.getString("STOCKNAME");
            double amount = resultSet.getDouble("AMOUNT");
            Holdings newHoldings = new Holdings(stockname, amount);
            holdings.add(newHoldings);
        }
        return holdings.toArray(new Holdings[holdings.size()]);
    }

    //for the admin to get all users' holdings
    public static Holdings[] getAllHoldings() throws SQLException{
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet =statement.executeQuery("SELECT DISTINCT STOCKNAME FROM HOLDINGS ");
        ArrayList<Holdings> holdings = new ArrayList<Holdings>(3);
        while (resultSet.next()){
            String stockname = resultSet.getString("STOCKNAME");
            double amount = 0;
            Holdings newHoldings = new Holdings(stockname, amount);
            holdings.add(newHoldings);
        }
        ArrayList<Holdings> sumHoldings = new ArrayList<Holdings>(3);
        for(int i=0; i< holdings.size(); i++){
            String name = holdings.get(i).getStockName();
            ResultSet resultSet1 = statement.executeQuery("SELECT AMOUNT FROM HOLDINGS WHERE STOCKNAME = '"+name+"'");
            double sumAmount = 0;
            while(resultSet1.next()){
                 sumAmount += resultSet.getDouble("AMOUNT");
            }
            Holdings newHoldings = new Holdings(name, sumAmount);
            sumHoldings.add(newHoldings);
        }
        return sumHoldings.toArray(new Holdings[holdings.size()]);
    }


    public static StockTransaction[] getStockTransaction(String username) throws SQLException {
        if (username == null || username.trim().length() == 0)
            return null;
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet =statement.executeQuery("SELECT STOCKTRANSACTIONS_ID, DATE, STOCKNAME, AMOUNT,PRICE,ACTION FROM STOCKTRANSACTION WHERE USERID = '"+ username +"' "); /* BAD - user input should always be sanitized */
        ArrayList<StockTransaction> stockTransaction = new ArrayList<StockTransaction>(3);
        while (resultSet.next()){
            long holdtId = resultSet.getLong("STOCKTRANSACTIONS_ID");
            String date = resultSet.getString("DATE");
            String stockname = resultSet.getString("STOCKNAME");
            double amount = resultSet.getDouble("AMOUNT");
            double price = resultSet.getDouble("PRICE");
            String action = resultSet.getString("ACTION");
            StockTransaction newStockTransaction = new StockTransaction(holdtId, date, stockname, amount,price,action);
            stockTransaction.add(newStockTransaction);
        }
        return stockTransaction.toArray(new StockTransaction[stockTransaction.size()]);

    }


    /**
     * Transfer funds between specified accounts
     * @param username
     * @param toActId
     * @param fromActId
     * @param amount
     * @return
     */
    public static String transferFunds(String username, long toActId, long fromActId, double amount) {

        try {
            if (amount <= 0){
                return "Transfer amount is invalid";
            }

            User user = getUserInfo(username);

            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            Account fromAccount = Account.getAccount(fromActId);
            Account toAccount = Account.getAccount(toActId);

            if (fromAccount == null) {
                return "Originating account is invalid";
            }

            if (toAccount == null)
                return "Destination account is invalid";

            java.sql.Timestamp date = new Timestamp(new java.util.Date().getTime());

            //in real life we would want to do these updates and transaction entry creation
            //as one atomic operation

            long userCC = user.getCreditCardNumber();

            /* this is the account that the payment will be made from, thus negative amount!*/
            double fromAmount = -amount;
            /* this is the account that the payment will be made to, thus positive amount!*/
            double toAmount = amount;

            /* Credit card account balance is the amount owed, not amount owned
             * (reverse of other accounts). Therefore we have to process balances differently*/
            if (fromAccount.getAccountId() == userCC)
                fromAmount = -fromAmount;
            else{
                if (fromAmount + fromAccount.getBalance() < 0){
                    return "Insufficient balance, fund transferring failed.";
                }
            }

            //create transaction record
            statement.execute("INSERT INTO TRANSACTIONS (ACCOUNTID, DATE, TYPE, AMOUNT) VALUES " +
                    "(" + fromAccount.getAccountId() + ",'" + date + "'," + ((fromAccount.getAccountId() == userCC) ?
                    "'Cash Advance'" : "'Withdrawal'") + "," + fromAmount + ")," +
                    "(" + toAccount.getAccountId() + ",'" + date + "'," +
                    ((toAccount.getAccountId() == userCC) ? "'Payment'" : "'Deposit'") + "," + toAmount + ")");

            Log4AltoroJ.getInstance().logTransaction(fromAccount.getAccountId() + " - "
                    + fromAccount.getAccountName(), toAccount.getAccountId() + " - "
                    + toAccount.getAccountName(), amount);

            //add cash advance fee since the money transfer was made from the credit card
            if (fromAccount.getAccountId() == userCC) {
                statement.execute("INSERT INTO TRANSACTIONS (ACCOUNTID, DATE, TYPE, AMOUNT) VALUES " +
                        "(" + fromAccount.getAccountId() + ",'" + date + "','Cash Advance Fee'," + CASH_ADVANCE_FEE + ")");
                fromAmount += CASH_ADVANCE_FEE;
                Log4AltoroJ.getInstance().logTransaction(String.valueOf(userCC), "N/A", CASH_ADVANCE_FEE);
            }

            if (toAccount.getAccountId() == userCC)
                toAmount = -toAmount;

            //update account balances
            statement.execute("UPDATE ACCOUNTS SET BALANCE = " + (fromAccount.getBalance() + fromAmount) +
                    " WHERE ACCOUNT_ID = " + fromAccount.getAccountId());
            statement.execute("UPDATE ACCOUNTS SET BALANCE = " + (toAccount.getBalance() + toAmount) +
                    " WHERE ACCOUNT_ID = " + toAccount.getAccountId());

            return null;

        } catch (SQLException e) {
            return "Transaction failed. Please try again later.";
        }
    }

    public static String transferStock(String username, long accountId, String tick, BigDecimal price, int shareNum) {
        try {

            User user = getUserInfo(username);

            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            Account account = Account.getAccount(accountId);
            double amount = price.doubleValue() * shareNum;

            if (account == null)
                return "Destination account is invalid";

            Timestamp date = new Timestamp(new Date().getTime());

            //in real life we would want to do these updates and transaction entry creation
            //as one atomic operation
            boolean isCreditCard = account.getAccountId() == user.getCreditCardNumber();

//            if (isCreditCard)
                amount = -amount;

            double newBalance = account.getBalance() + amount;

            // deposit(sell)
            if (amount > 0) {
                statement.execute("INSERT INTO TRANSACTIONS (ACCOUNTID, DATE, TYPE, AMOUNT) VALUES " +
                        "(" + account.getAccountId() + ",'" + date + "'," + (isCreditCard
                        ? "'Payment'" : "'Deposit'") + "," + amount + ")");
            }
            // withdraw(buy)
            else {
                if (newBalance < 0){
                    return "Insufficient balance, stock transaction failed.";
                }
                statement.execute("INSERT INTO TRANSACTIONS (ACCOUNTID, DATE, TYPE, AMOUNT) VALUES " +
                        "(" + account.getAccountId() + ",'" + date + "'," + (isCreditCard
                        ? "'Cash Advance'" : "'Withdrawal'") + "," + amount + ")");
            }

			//add cash advance fee since the money transfer was made from the credit card
			if (isCreditCard){
				statement.execute("INSERT INTO TRANSACTIONS (ACCOUNTID, DATE, TYPE, AMOUNT) VALUES " +
                        "("+account.getAccountId()+",'"+date+"','Cash Advance Fee',"+CASH_ADVANCE_FEE+")");
				newBalance += CASH_ADVANCE_FEE;
				Log4AltoroJ.getInstance().logTransaction(String.valueOf(account.getAccountId()),
                        "N/A", CASH_ADVANCE_FEE);
			}

            statement.execute("UPDATE ACCOUNTS SET BALANCE = " + newBalance +
                    " WHERE ACCOUNT_ID = " + account.getAccountId());

            return null;

        } catch (SQLException e) {
            return "Transaction failed. Please try again later.";
        }
    }

    public static boolean isTickExisted(String ticker)
    {
        try {

            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            //String query = "SELECT * FROM HISTORY WHERE TICK = '" + ticker + "' AND DATE = '" + date + "' AND PRICE = " + price;
            String query = "SELECT * FROM HISTORY WHERE TICK = '" + ticker + "'";
            ResultSet resultSet = null;
            try {
                resultSet = statement.executeQuery(query);
            } catch (SQLException e)
            {}

            if(resultSet == null)
            {
                return false;
            }
            else if(resultSet.next())
            {
                return true;
            }
            else
            {
                return false;
            }

        } catch (SQLException e) {
            return false;
        }
    }

    public static String addHistoryData(String ticker, String date, double price) {
        try {

            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            try
            {
                statement.execute("CREATE TABLE HISTORY (HISTORY_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), TICK VARCHAR(20) NOT NULL, DATE VARCHAR(100) NOT NULL, PRICE DOUBLE NOT NULL, PRIMARY KEY (HISTORY_ID))");
            } catch (SQLException e)
            {}

            try
            {
                statement.execute("INSERT INTO HISTORY (TICK, DATE, PRICE) VALUES ('" + ticker + "','" + date + "', " + price + ")");
            } catch (SQLException e)
            {
                return "insert table failed. Please try again later.";
            }


            return null;

        } catch (SQLException e) {
            return "addHistoryData failed. Please try again later.";
        }
    }

    /**
     * Get transaction information for the specified accounts in the date range (non-inclusive of the dates)
     * @param startDate
     * @param endDate
     * @param accounts
     * @param rowCount
     * @return
     */
    public static Transaction[] getTransactions(String startDate, String endDate, Account[] accounts, int rowCount) throws SQLException {

        if (accounts == null || accounts.length == 0)
            return null;

        Connection connection = getConnection();


        Statement statement = connection.createStatement();

        if (rowCount > 0)
            statement.setMaxRows(rowCount);

        StringBuffer acctIds = new StringBuffer();
        acctIds.append("ACCOUNTID = " + accounts[0].getAccountId());
        for (int i = 1; i < accounts.length; i++) {
            acctIds.append(" OR ACCOUNTID = " + accounts[i].getAccountId());
        }

        String dateString = null;

        if (startDate != null && startDate.length() > 0 && endDate != null && endDate.length() > 0) {
            dateString = "DATE BETWEEN '" + startDate + " 00:00:00' AND '" + endDate + " 23:59:59'";
        } else if (startDate != null && startDate.length() > 0) {
            dateString = "DATE > '" + startDate + " 00:00:00'";
        } else if (endDate != null && endDate.length() > 0) {
            dateString = "DATE < '" + endDate + " 23:59:59'";
        }

        String query = "SELECT * FROM TRANSACTIONS WHERE (" + acctIds.toString() + ") " + ((dateString == null) ? "" : "AND (" + dateString + ") ") + "ORDER BY DATE DESC";
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (errorCode == 30000)
                throw new SQLException("Date-time query must be in the format of yyyy-mm-dd HH:mm:ss", e);

            throw e;
        }
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        while (resultSet.next()) {
            int transId = resultSet.getInt("TRANSACTION_ID");
            long actId = resultSet.getLong("ACCOUNTID");
            Timestamp date = resultSet.getTimestamp("DATE");
            String desc = resultSet.getString("TYPE");
            double amount = resultSet.getDouble("AMOUNT");
            transactions.add(new Transaction(transId, actId, date, desc, amount));
        }

        return transactions.toArray(new Transaction[transactions.size()]);
    }

    public static String[] getBankUsernames() {

        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            //at the moment this query limits transfers to
            //transfers between two user accounts
            ResultSet resultSet = statement.executeQuery("SELECT USER_ID FROM PEOPLE");

            ArrayList<String> users = new ArrayList<String>();

            while (resultSet.next()) {
                String name = resultSet.getString("USER_ID");
                users.add(name);
            }

            return users.toArray(new String[users.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static Account getAccount(long accountNo) throws SQLException {

        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT ACCOUNT_NAME, BALANCE FROM ACCOUNTS WHERE ACCOUNT_ID = " + accountNo + " "); /* BAD - user input should always be sanitized */

        ArrayList<Account> accounts = new ArrayList<>(3);
        while (resultSet.next()) {
            String name = resultSet.getString("ACCOUNT_NAME");
            double balance = resultSet.getDouble("BALANCE");
            Account newAccount = new Account(accountNo, name, balance);
            accounts.add(newAccount);
        }

        if (accounts.size() == 0)
            return null;

        return accounts.get(0);
    }

    public static String addAccount(String username, String acctType) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO ACCOUNTS (USERID,ACCOUNT_NAME,BALANCE) VALUES ('" + username + "','" + acctType + "', 0)");
            return null;
        } catch (SQLException e) {
            return e.toString();
        }
    }

    public static String addHoldings(String username,String stockticker, double shareNum) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            try {
                statement.execute("CREATE TABLE HOLDINGS(HOLDINGS_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1), USERID VARCHAR(50) NOT NULL,STOCKNAME VARCHAR(100) NOT NULL, AMOUNT DOUBLE NOT NULL, PRIMARY KEY (HOLDINGS_ID))");
            } catch (SQLException e) {
                // not a problem
            }
            try {
                ResultSet rs1 = statement.executeQuery("SELECT COUNT(*)FROM HOLDINGS WHERE STOCKNAME = '" + stockticker + "' AND USERID='" + username + "'");
                if (rs1.next()) {
                    //if holding does not exist, insert
                    if (rs1.getInt(1) == 0) {
                        try {
                            statement.execute("INSERT INTO HOLDINGS (USERID,STOCKNAME,AMOUNT) VALUES ('" + username + "','" + stockticker + "', " + shareNum + ")");
                        } catch (SQLException e) {
                            return "insert fail:" + e.toString();
                        }
                    } else {
                        //if holding exists, update
                        try {
                            ResultSet rs2 = statement.executeQuery("SELECT AMOUNT FROM HOLDINGS WHERE STOCKNAME = '" + stockticker + "' AND USERID='" + username + "'");
                            if (rs2.next()) {
                            double current_amount = rs2.getDouble(1);
                                try {
                                    double amount = current_amount + shareNum;
                                    //if amount = 0, delete the line
                                    if(amount == 0){
                                            statement.execute("DELETE FROM HOLDINGS " +
                                                    "WHERE STOCKNAME = '" + stockticker + "' AND USERID='" + username + "'");
                                    }
                                    else {
                                        statement.execute("UPDATE HOLDINGS SET AMOUNT = " + amount +
                                                " WHERE STOCKNAME = '" + stockticker + "' AND USERID='" + username + "'");
                                    }
                                } catch (SQLException e) {
                                    return "update fail:" + e.toString();
                                }
                            }
                        }catch(SQLException e){
                            return "second selection failure:" + e.toString();
                        }
                    }
                }
            }catch(SQLException e){
                return "first selection failure:" + e.toString();
            }
            return null;
        }catch(SQLException e){
            return "other failure:" + e.toString();
        }
    }

    public static String addStockTransaction(String username,String date,String stockticker, double amount,double price, String action) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            try {
                statement.execute("CREATE TABLE STOCKTRANSACTION(STOCKTRANSACTIONS_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1), DATE VARCHAR(50) NOT NULL, USERID VARCHAR(50) NOT NULL,STOCKNAME VARCHAR(100) NOT NULL, AMOUNT DOUBLE NOT NULL, PRICE DOUBLE NOT NULL, ACTION VARCHAR(100) NOT NULL, PRIMARY KEY (STOCKTRANSACTIONS_ID))");
            } catch (SQLException e) {
                // not a problem
            }
            statement.execute("INSERT INTO STOCKTRANSACTION(USERID,DATE,STOCKNAME,AMOUNT,PRICE,ACTION) VALUES ('"+username+"','"+date+"','"+stockticker+"',"+amount+","+price+",'"+action+"')");
            return null;
        } catch (SQLException e){
            return e.toString();
        }
    }


    public static String applyNewAccount(String username, String acctType) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO ACCOUNTS (USERID,ACCOUNT_NAME,BALANCE) VALUES ('" + username + "','" + acctType + "', 10000)");
            return null;
        } catch (SQLException e) {
            return e.toString();
        }
    }

    public static String addSpecialUser(String username, String password, String firstname, String lastname) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO SPECIAL_CUSTOMERS (USER_ID,PASSWORD,FIRST_NAME,LAST_NAME,ROLE) VALUES ('" + username + "','" + password + "', '" + firstname + "', '" + lastname + "','user')");
            return null;
        } catch (SQLException e) {
            return e.toString();

        }
    }

    public static String addUser(String username, String password, String firstname, String lastname) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            if (firstname == null) {
                firstname = "";
            }

            if (lastname == null) {
                lastname = "";
            }

            statement.execute("INSERT INTO PEOPLE (USER_ID,PASSWORD,FIRST_NAME,LAST_NAME,ROLE) VALUES ('" + username + "','" + password + "', '" + firstname + "', '" + lastname + "','user')");
            return null;
        } catch (SQLException e) {
            return e.toString();

        }
    }

    public static String changePassword(String username, String password) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            statement.execute("UPDATE PEOPLE SET PASSWORD = '" + password + "' WHERE USER_ID = '" + username + "'");
            return null;
        } catch (SQLException e) {
            return e.toString();

        }
    }


    public static long storeFeedback(String name, String email, String subject, String comments) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO FEEDBACK (NAME,EMAIL,SUBJECT,COMMENTS) VALUES ('" + name + "', '" + email + "', '" + subject + "', '" + comments + "')", Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = statement.getGeneratedKeys();
            long id = -1;
            if (rs.next()) {
                id = rs.getLong(1);
            }
            return id;
        } catch (SQLException e) {
            Log4AltoroJ.getInstance().logError(e.getMessage());
            return -1;
        }
    }
}