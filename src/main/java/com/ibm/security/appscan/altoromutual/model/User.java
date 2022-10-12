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
package com.ibm.security.appscan.altoromutual.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import com.ibm.security.appscan.altoromutual.util.DBUtil;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;


/**
 * This class models a user
 * @author Alexei
 *
 */
public class User implements java.io.Serializable{

	private static final long serialVersionUID = -4566649173574593144L;
	
	public static enum Role{User, Admin};
	
	private String username, firstName, lastName;
	private Role role = Role.User;
	
	private Date lastAccessDate = null;
	
	public User(String username, String firstName, String lastName) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		lastAccessDate = new Date();
	}
	
	public void setRole(Role role){
		this.role = role;
	}
	
	public Role getRole(){
		return role;
	}
	
	public Date getLastAccessDate() {
		return lastAccessDate;
	}

	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public Account[] getAccounts(){
		try {
			return DBUtil.getAccounts(username);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Account lookupAccount(Long accountNumber) {
		for (Account account : getAccounts()) {
			if (account.getAccountId() == accountNumber)
				return account;
		}
		return null;
	}
	
	public long getCreditCardNumber(){
		for (Account account: getAccounts()){
			if (DBUtil.CREDIT_CARD_ACCOUNT_NAME.equals(account.getAccountName()))
				return account.getAccountId();
		}
		return -1L;
	}
	
	public Transaction[] getUserTransactions(String startDate, String endDate, Account[] accounts) throws SQLException {
		
		Transaction[] transactions = null;
		transactions = DBUtil.getTransactions(startDate, endDate, accounts, -1);
		return transactions; 
	}

	public Holdings[] getUserHoldings(String username) throws SQLException {
		try {
			return DBUtil.getHoldings(username);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public StockTransaction[] getUserStockTransaction(String username) throws SQLException {
		try {
			return DBUtil.getStockTransaction(username);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public List<Double> getHoldingValue(String username) throws SQLException {
		try {
			Holdings[] holdings = DBUtil.getHoldings(username);
			Map<String, List<Double> > holdingMap = new HashMap<String, List<Double> >();
			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();
			from.add(Calendar.YEAR, -1); // from 1 year ago

			for(Holdings h : holdings)
			{
				double amt = h.getAmountOwn();
				String tick = h.getStockName();
				List<Double> priceList = new ArrayList<Double>();
				Stock stock = YahooFinance.get(tick);
				List<HistoricalQuote> stockHistQuotes = stock.getHistory(from, to, Interval.DAILY);
				for (HistoricalQuote hist : stockHistQuotes)
				{
					double price = hist.getAdjClose().doubleValue()*amt;
					priceList.add(price);
				}
				holdingMap.put(tick,priceList);
			}

			List<Double> valueList = new ArrayList<Double>();
			for(String key : holdingMap.keySet())
			{
				if(valueList.isEmpty())
				{
					valueList.addAll(holdingMap.get(key));
				}
				else
				{
					List<Double> value = holdingMap.get(key);
					for(int i = 0; i < valueList.size(); i++)
					{
						double newValue = valueList.get(i) + value.get(i);
						valueList.set(i, newValue);
					}
				}
			}

			return valueList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public double getPortfolioReturn(String username)throws SQLException {
		List<Double> value = getHoldingValue(username);
		List<Double> ror = new ArrayList<Double>();
		for(int i=1; i< value.size(); i++){
			double temp = value.get(i)/value.get(i-1)-1;
			ror.add(temp);
		}
		double sum = 0;
		for(int i=0; i<ror.size(); i++){
			sum += ror.get(i);
		}
		double average = sum/ror.size();
		return average*252;
	}

	public double getPortfolioVolatility(String username)throws SQLException {
		List<Double> value = getHoldingValue(username);
		List<Double> ror = new ArrayList<Double>();
		for(int i=1; i< value.size(); i++){
			double temp = value.get(i)/value.get(i-1)-1;
			ror.add(temp);
		}
		double sum = 0;
		for(int i=0; i<ror.size(); i++){
			sum += ror.get(i);
		}
		double average = sum/ror.size();

		double sumForSigma = 0,var = 0;
		for(int i=0; i< ror.size(); i++){
			sumForSigma += Math.pow((ror.get(i) - average), 2);
		}
		var = sumForSigma/(ror.size()-1);
		double sigma = Math.sqrt(var);
		return sigma*Math.pow(252,0.5);
	}

	public double getPortfolioSharp(String username) throws SQLException, IOException {
		BigDecimal tnx = YahooStock.getCurrent("^TNX"); // US Treasury 10 tr bond yield (%) e.g. 1.57
		double mean = getPortfolioReturn(username);
		double sd = getPortfolioVolatility(username);
		double rf = tnx.doubleValue()/100;
		double sharp = (mean-rf)/sd;
		return sharp;
	}

}
