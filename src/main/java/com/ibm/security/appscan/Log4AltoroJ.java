package com.ibm.security.appscan;

import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

public class Log4AltoroJ {

    private static Log4AltoroJ _theInstance =  new Log4AltoroJ();;
    private static String logFileLocation = null;
    private Logger logger = null;

    public static Log4AltoroJ getInstance(){
        return _theInstance;
    }

    private Log4AltoroJ(){
        logger = Logger.getRootLogger();
        logger.setLevel(Level.INFO);
        PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd} %d{HH:mm:ss} %p %m%n");
        try {
            logFileLocation = System.getProperty("user.home")+"/altoro/AltoroLog.log";
            RollingFileAppender appender = new RollingFileAppender(layout, logFileLocation);
            appender.setMaxFileSize("100KB");
            appender.setMaxBackupIndex(1);
            logger.addAppender(appender);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logError(String error){
        logger.error(error);
    }

    public void RegisterError(String error){logger.error(error);}

    public void logInfo(String message){
        logger.info(message);
    }

    public String getLogFileLocation(){
        return logFileLocation;
    }

    public void logTransaction(String fromAccount, String toAccount, double amount){
        String format = (amount<1)?"$0.00":"$.00";
        String stringAmt = new DecimalFormat(format).format(amount);

        logger.info("Transaction >>> From: " + fromAccount + " >>> To: " + toAccount + " >>> Amount: " + stringAmt);
    }
}