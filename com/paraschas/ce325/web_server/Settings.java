// Settings.java


package com.paraschas.ce325.web_server;


import java.util.List;
import java.util.ArrayList;


/**
 * Store the settings of the server.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.2.0
 */
public class Settings {
    String listenPort;
    String statisticsPort;
    String accessLogPath;
    String errorLogPath;
    String documentRootPath;
    String runPhp;
    List<String> denyAccessIps;


    /**
     * Settings constructor.
     */
    public Settings() {
        denyAccessIps = new ArrayList<String>();
    }


    /**
     * Set the listening port.
     */
    public void setListenPort(String listenPort) {
        this.listenPort = listenPort;
    }


    /**
     * Set the statistics port.
     */
    public void setStatisticsPort(String statisticsPort) {
        this.statisticsPort = statisticsPort;
    }


    /**
     * Set the access log file path.
     */
    public void setAccessLogPath(String accessLogPath) {
        this.accessLogPath = accessLogPath;
    }


    /**
     * Set the error log file path.
     */
    public void setErrorLogPath(String errorLogPath) {
        this.errorLogPath = errorLogPath;
    }


    /**
     * Set the document root directory path.
     */
    public void setDocumentRootPath(String documentRootPath) {
        this.documentRootPath = documentRootPath;
    }


    /**
     * Set the runPhp variable.
     */
    public void setRunPhp(String runPhp) {
        this.runPhp = runPhp;
    }


    /**
     * Add an IP address to the denyAccessIps list.
     */
    public void addDenyAccessIp(String denyAccessIp) {
        denyAccessIps.add(denyAccessIp);
    }


    /**
     * Get the listening port.
     *
     * @return listenPort the listening port.
     */
    public String getListenPort() {
        return listenPort;
    }


    /**
     * Get the statistics port.
     *
     * @return statisticsPort the statistics port.
     */
    public String getStatisticsPort() {
        return statisticsPort;
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
     * Get the error log file path.
     *
     * @return errorLogPath the error log file path.
     */
    public String getErrorLogPath() {
        return errorLogPath;
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
     * Get the runPhp variable.
     *
     * @return runPhp the runPhp variable.
     */
    public String getRunPhp() {
        return runPhp;
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
