// Server.java


package com.paraschas.ce325.web_server;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
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
    public void interruptibleServe(int portNumber) throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println();
                System.out.println("CTRL-D received, shutting down");
            }
        });

        try {
            serve(portNumber);
        } catch (IOException e) {
            System.out.println( e.getMessage() );
        }
    }


    /**
     * Main server method.
     */
    public void serve(int portNumber) throws IOException {
        System.out.println("listening on " + Integer.toString(portNumber));

        try (
                ServerSocket serverSocket = new ServerSocket();
            ) {
            // TODO
            // store the IP address in a variable
            serverSocket.bind(new InetSocketAddress("localhost", portNumber));
            try (
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
                ) {
                // no catch here
                System.out.println("new client connection: " + clientSocket.getInetAddress() +
                        ":" + clientSocket.getPort());
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    out.println(inputLine);
                    System.out.println("echoing: " + inputLine);
                }
            } catch (IOException e) {
                System.out.println( e.getMessage() );
            }
        }
    }
}
