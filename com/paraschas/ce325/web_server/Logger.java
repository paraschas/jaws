// Logger.java


package com.paraschas.ce325.web_server;


import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Orchestrate logging.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.1
 */
public class Logger {
    final private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Settings settings;
    private File accessLog;
    private File errorLog;
    PrintWriter accessLogWritter;
    PrintWriter errorLogWritter;


    /**
     * Logger constructor.
     */
    public Logger(Settings settings) {
        this.settings = settings;

        try {
            accessLog = new File( settings.getAccessLogPath() );
            accessLog.getParentFile().mkdirs();
            accessLog.createNewFile();
            accessLogWritter = new PrintWriter(accessLog);

            errorLog = new File( settings.getErrorLogPath() );
            errorLog.getParentFile().mkdirs();
            errorLog.createNewFile();
            errorLogWritter = new PrintWriter(errorLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Add an entry to the access log.
     */
    public synchronized void addToAccessLog(String entry) {
        accessLogWritter.println(entry);
        accessLogWritter.flush();
    }


    /**
     * Add an entry to the error log.
     */
    public synchronized void addToErrorLog(String entry) {
        errorLogWritter.println(entry);
        errorLogWritter.flush();
    }
}
