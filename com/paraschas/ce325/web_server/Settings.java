// Settings.java


package com.paraschas.ce325.web_server;


import java.lang.Integer;
import java.util.List;
import java.util.ArrayList;


/**
 * Store the settings of the server.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.3.0
 */
public class Settings {
    // The IP address of the host machine the server will listen to.
    private String ipAddress;
    private int listenPort;
    private int statisticsPort;
    private String accessLogPath;
    private String errorLogPath;
    private String documentRootPath;
    // TODO
    // make boolean
    private String runPhp;
    private List<String> denyAccessIps;


    /**
     * Settings constructor.
     */
    public Settings() {
        denyAccessIps = new ArrayList<String>();
    }


    /**
     * Set the IP address.
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    /**
     * Get the IP address.
     *
     * @return ipAddress the IP address.
     */
    public String getIpAddress() {
        return ipAddress;
    }


    /**
     * Set the listening port.
     */
    public void setListenPort(String listenPort) {
        this.listenPort = Integer.parseInt(listenPort);
    }


    /**
     * Get the listening port.
     *
     * @return listenPort the listening port.
     */
    public int getListenPort() {
        return listenPort;
    }


    /**
     * Set the statistics port.
     */
    public void setStatisticsPort(String statisticsPort) {
        this.statisticsPort = Integer.parseInt(statisticsPort);
    }


    /**
     * Get the statistics port.
     *
     * @return statisticsPort the statistics port.
     */
    public int getStatisticsPort() {
        return statisticsPort;
    }


    /**
     * Set the access log file path.
     */
    public void setAccessLogPath(String accessLogPath) {
        this.accessLogPath = accessLogPath;
    }


    /**
     * Get the access log file path.
     *
     * @return accessLogPath the access log file path.
     */
    public String getAccessLogPath() {
        return accessLogPath;
    }


    /**
     * Set the error log file path.
     */
    public void setErrorLogPath(String errorLogPath) {
        this.errorLogPath = errorLogPath;
    }


    /**
     * Get the error log file path.
     *
     * @return errorLogPath the error log file path.
     */
    public String getErrorLogPath() {
        return errorLogPath;
    }


    /**
     * Set the document root directory path.
     */
    public void setDocumentRootPath(String documentRootPath) {
        this.documentRootPath = documentRootPath;
    }


    /**
     * Get the document root directory path.
     *
     * @return documentRootPath the document root directory path.
     */
    public String getDocumentRootPath() {
        return documentRootPath;
    }


    /**
     * Set the runPhp variable.
     */
    public void setRunPhp(String runPhp) {
        this.runPhp = runPhp;
    }


    /**
     * Get the runPhp variable.
     *
     * @return runPhp the runPhp variable.
     */
    public String getRunPhp() {
        return runPhp;
    }


    /**
     * Add an IP address to the denyAccessIps list.
     */
    public void addDenyAccessIp(String denyAccessIp) {
        denyAccessIps.add(denyAccessIp);
    }


    /**
     * Get the denyAccessIps list.
     *
     * @return denyAccessIps the denyAccessIps list.
     */
    public List<String> getDenyAccessIps() {
        return denyAccessIps;
    }
}
