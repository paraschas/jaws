// Server.java


package com.paraschas.ce325.web_server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;


/**
 * A server that spawns worker threads to service requests for resources or the statistics page.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.3.0
 */
public class Server extends Thread {
    private Settings settings;
    private Statistics statistics;
    private Logger logger;
    private String serverType;


    /**
     * Server constructor.
     */
    public Server(Settings settings, Statistics statistics, Logger logger, String serverType) {
        this.settings = settings;
        this.statistics = statistics;
        this.logger = logger;
        this.serverType = serverType;
    }


    /**
     * Listen for requests and spawn worker threads to service them.
     */
    public void serve() throws IOException {
        try (
            ServerSocket serverSocket = new ServerSocket();
        ) {
            String ipAddress = settings.getIpAddress();

            int portNumber;
            if ( serverType.equals("Resources") ) {
                portNumber = settings.getListenPort();
            // serverType.equals("Statistics")
            } else {
                portNumber = settings.getStatisticsPort();
            }

            serverSocket.bind(new InetSocketAddress(ipAddress, portNumber));
            System.out.println("listening on " + ipAddress + ":" + Integer.toString(portNumber));

            // accept requests forever
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    if ( serverType.equals("Resources") ) {
                        ResourcesWorker resourcesWorker = new ResourcesWorker(clientSocket, settings, statistics, logger);
                        resourcesWorker.start();
                    // serverType.equals("Statistics")
                    } else {
                        StatisticsWorker statisticsWorker = new StatisticsWorker(clientSocket, settings, statistics, logger);
                        statisticsWorker.start();
                    }
                } catch (IOException e) {
                    System.out.println( e.getMessage() );
                }
            }
        } catch (IOException e) {
            System.out.println( e.getMessage() );
        }
    }


    public void run() {
        try {
            serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
