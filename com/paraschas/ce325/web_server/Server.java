// Server.java


package com.paraschas.ce325.web_server;


import com.paraschas.ce325.web_server.Settings;


/**
 * A simple web server.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.1
 */
public class Server {
    Settings settings;


    /**
     * Server constructor.
     */
    public Server(Settings settings) {
        this.settings = settings;
    }


    // DEBUG
    public void doSomething() {
        System.out.println("hello world");
    }
}
