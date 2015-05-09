// Statistics.java


package com.paraschas.ce325.web_server;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Store and update statistics for the server.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.1
 */
public class Statistics {
    final private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    private long startedAt;
    private long runningFor;
    private int allServicedRequests;
    private int http400Requests;
    private int http404Requests;
    private int http405Requests;
    private int http500Requests;
    private long averageServiceTime;


    /**
     * Statistics constructor.
     */
    public Statistics() {
        startedAt = System.currentTimeMillis();
        runningFor = 0;
        allServicedRequests = 0;
        http400Requests = 0;
        http404Requests = 0;
        http405Requests = 0;
        http500Requests = 0;
        averageServiceTime = 0;
    }


    /**
     * Get the starting time of the server.
     */
    public String getStartedAt() {
        return dateFormat.format(new Date(startedAt));
    }


    /**
     * Get the running time of the server.
     */
    public String getRunningFor() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - startedAt;

        long hours = (elapsedTime / (1000 * 60 * 60)) % 24;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long seconds = (elapsedTime / 1000) % 60;
        elapsedTime = elapsedTime % 1000;

        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, elapsedTime);
    }
}
