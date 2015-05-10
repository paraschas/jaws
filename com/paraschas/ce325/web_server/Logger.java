// Logger.java


package com.paraschas.ce325.web_server;


import java.io.File;
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


    /**
     * Logger constructor.
     */
    public Logger(Settings settings) {
        this.settings = settings;

        try {
            accessLog = new File( settings.getAccessLogPath() );
            accessLog.getParentFile().mkdirs();
            accessLog.createNewFile();

            errorLog = new File( settings.getErrorLogPath() );
            errorLog.getParentFile().mkdirs();
            errorLog.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
