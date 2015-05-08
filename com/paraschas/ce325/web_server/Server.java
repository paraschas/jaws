// Server.java


package com.paraschas.ce325.web_server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;


/**
 * A simple web server.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.3
 */
public class Server extends Thread {
    int portNumber;
    Settings settings;


    /**
     * Server constructor.
     */
    public Server(int portNumber, Settings settings) {
        this.portNumber = portNumber;
        this.settings = settings;
    }


    /**
     * Listen for requests and spawn worker threads to service them.
     */
    public void serve() throws IOException {
        try (
            ServerSocket serverSocket = new ServerSocket();
        ) {
            String ipAddress = settings.getIpAddress();
            serverSocket.bind(new InetSocketAddress(ipAddress, portNumber));
            System.out.println("listening on " + ipAddress + ":" + Integer.toString(portNumber));

            // accept requests forever
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    ResourcesWorker resourcesWorker = new ResourcesWorker(clientSocket, settings);
                    resourcesWorker.start();
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
