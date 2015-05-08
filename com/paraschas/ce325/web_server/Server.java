// Server.java


package com.paraschas.ce325.web_server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;


/**
 * A server that spawns worker threads to service requests. It can service requests of either
 * resources or the statistics page.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.1.0
 */
public class Server extends Thread {
    Settings settings;
    String serverType;


    /**
     * Server constructor.
     */
    public Server(Settings settings, String serverType) {
        this.settings = settings;
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
            } else if ( serverType.equals("Statistics") ) {
                portNumber = settings.getStatisticsPort();
            } else {
                // TODO
                // handle error
                portNumber = 0;
            }
            serverSocket.bind(new InetSocketAddress(ipAddress, portNumber));
            System.out.println("listening on " + ipAddress + ":" + Integer.toString(portNumber));

            // accept requests forever
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    if ( serverType.equals("Resources") ) {
                        ResourcesWorker resourcesWorker =
                                new ResourcesWorker(clientSocket, settings);
                        resourcesWorker.start();
                    } else if ( serverType.equals("Statistics") ) {
                        StatisticsWorker statisticsWorker =
                                new StatisticsWorker(clientSocket, settings);
                        statisticsWorker.start();
                    } else {
                        // TODO
                        // handle error
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
