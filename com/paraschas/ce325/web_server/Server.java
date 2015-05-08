// Server.java


package com.paraschas.ce325.web_server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;

import com.paraschas.ce325.web_server.Settings;


/**
 * A simple web server.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.2
 */
public class Server {
    Settings settings;


    /**
     * Server constructor.
     */
    public Server(Settings settings) {
        this.settings = settings;
    }


    /**
     * Listen for requests and spawn worker threads to service them.
     */
    public void serve(String ipAddress, int portNumber) throws IOException {
        try (
            ServerSocket serverSocket = new ServerSocket();
        ) {
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
}
