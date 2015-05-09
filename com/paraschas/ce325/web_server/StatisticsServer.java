// StatisticsServer.java


package com.paraschas.ce325.web_server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;


/**
 * A server that spawns worker threads to service requests for the statistics page.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.1.0
 */
public class StatisticsServer extends Thread {
    Settings settings;
    Statistics statistics;


    /**
     * StatisticsServer constructor.
     */
    public StatisticsServer(Settings settings, Statistics statistics) {
        this.settings = settings;
        this.statistics = statistics;
    }


    /**
     * Listen for requests and spawn worker threads to service them.
     */
    public void serve() throws IOException {
        try (
            ServerSocket serverSocket = new ServerSocket();
        ) {
            String ipAddress = settings.getIpAddress();
            int portNumber = settings.getStatisticsPort();

            serverSocket.bind(new InetSocketAddress(ipAddress, portNumber));
            System.out.println("listening on " + ipAddress + ":" + Integer.toString(portNumber));

            // accept requests forever
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    StatisticsWorker statisticsWorker = new StatisticsWorker(clientSocket, settings, statistics);
                    statisticsWorker.start();
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
