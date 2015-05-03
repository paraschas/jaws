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
     * TODO
     * Interruptible serve method that gracefully shuts down the server on a shutdown signal.
     *
     * http://stackoverflow.com/questions/2541597/how-to-gracefully-handle-the-sigkill-signal-in-java
     */
    public void interruptibleServe(String ipAddress, int portNumber) throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println();
                System.out.println("CTRL-D received, shutting down");
            }
        });

        try {
            serve(ipAddress, portNumber);
        } catch (IOException e) {
            System.out.println( e.getMessage() );
        }
    }


    /**
     * Main server method.
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

                    Worker worker = new Worker(clientSocket);
                    worker.start();
                } catch (IOException e) {
                    System.out.println( e.getMessage() );
                }
            }
        } catch (IOException e) {
            System.out.println( e.getMessage() );
        }
    }
}
