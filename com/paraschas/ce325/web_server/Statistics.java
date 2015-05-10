// Statistics.java


package com.paraschas.ce325.web_server;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Store and update statistics for the server.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.1.0
 */
public class Statistics {
    final private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private long startedAt;
    private long runningFor;
    private int allServicedRequests;
    private int http400Requests;
    private int http404Requests;
    private int http405Requests;
    private int http500Requests;
    private long averageServiceTime;
    private long totalServiceTime;


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
        totalServiceTime = 0;
    }


    /**
     * Get the starting time of the server.
     */
    public String getStartedAt() {
        return dateTimeFormat.format(new Date(startedAt));
    }


    /**
     * Get the running time of the server.
     */
    public String getRunningFor() {
        long now = System.currentTimeMillis();
        runningFor = now - startedAt;

        long hours = (runningFor / (1000 * 60 * 60)) % 24;
        long minutes = (runningFor / (1000 * 60)) % 60;
        long seconds = (runningFor / 1000) % 60;

        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }


    /**
     * Increment the number of all serviced requests.
     */
    public synchronized void incrementAllServicedRequests() {
        allServicedRequests += 1;
    }


    /**
     * Get the number of all serviced requests.
     *
     * @return allServicedRequests the number of all serviced requests
     */
    public synchronized int getAllServicedRequests() {
        return allServicedRequests;
    }


    /**
     * Increment the number of 400 requests.
     */
    public synchronized void incrementHttp400Requests() {
        http400Requests += 1;
    }


    /**
     * Get the number of 400 requests.
     *
     * @return http400Requests the number of 400 requests
     */
    public synchronized int getHttp400Requests() {
        return http400Requests;
    }


    /**
     * Increment the number of 404 requests.
     */
    public synchronized void incrementHttp404Requests() {
        http404Requests += 1;
    }


    /**
     * Get the number of 404 requests.
     *
     * @return http404Requests the number of 404 requests
     */
    public synchronized int getHttp404Requests() {
        return http404Requests;
    }


    /**
     * Increment the number of 405 requests.
     */
    public synchronized void incrementHttp405Requests() {
        http405Requests += 1;
    }


    /**
     * Get the number of 405 requests.
     *
     * @return http405Requests the number of 405 requests
     */
    public synchronized int getHttp405Requests() {
        return http405Requests;
    }


    /**
     * Increment the number of 500 requests.
     */
    public synchronized void incrementHttp500Requests() {
        http500Requests += 1;
    }


    /**
     * Get the number of 500 requests.
     *
     * @return http500Requests the number of 500 requests
     */
    public synchronized int getHttp500Requests() {
        return http500Requests;
    }


    /**
     * Add to the total time to service requests.
     */
    public synchronized void addTotalServiceTime(long millis) {
        totalServiceTime +=  millis;
    }


    /**
     * Get the average time to service requests.
     *
     * @return averageServiceTime the average time to service requests
     */
    public synchronized String getAverageServiceTime() {
        if (allServicedRequests != 0) {
            averageServiceTime = totalServiceTime / allServicedRequests;
        } else {
            averageServiceTime = 0;
        }

        return Long.toString(averageServiceTime);
    }
}
