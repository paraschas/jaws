// Worker.java


package com.paraschas.ce325.web_server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.StringTokenizer;


/**
 * Worker to service requests spawned as a new thread.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.1
 */
public class Worker extends Thread {
    Socket clientSocket;
    BufferedReader input = null;
    DataOutputStream output = null;


    /**
     * Worker constructor.
     */
    public Worker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


    /**
     * Send the response to a request to the client.
     */
    public void sendResponse(int statusCode, String responseString, boolean isFile) throws Exception {
        String status;
        String serverDetails = "Jaws web server" + "\r\n";
        String contentLength = null;
        String fileName = null;
        String contentType = "Content-Type: text/html" + "\r\n";
        FileInputStream fin = null;

        if (statusCode == 200) {
            status = "HTTP/1.1 200 OK" + "\r\n";
        } else {
            status = "HTTP/1.1 404 Not Found" + "\r\n";
        }

        if (isFile) {
            fileName = responseString;
            fin = new FileInputStream(fileName);
            contentLength = "Content-Length: " + Integer.toString(fin.available()) + "\r\n";
            if (!fileName.endsWith(".htm") && !fileName.endsWith(".html")) {
                contentType = "Content-Type: \r\n";
            }
        } else {
            contentLength = "Content-Length: " + responseString.length() + "\r\n";
        }

        output.writeBytes(status);
        output.writeBytes(serverDetails);
        output.writeBytes(contentType);
        output.writeBytes(contentLength);
        output.writeBytes("Connection: close\r\n");
        output.writeBytes("\r\n");

        if (isFile) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fin.read(buffer)) != -1 ) {
                output.write(buffer, 0, bytesRead);
            }
            fin.close();
        } else {
            output.writeBytes(responseString);
        }

        output.close();
    }


    public void run() {
        try {
            System.out.println();
            System.out.println("new connection: " + clientSocket.getInetAddress() +
                    ":" + clientSocket.getPort());

            input = new BufferedReader(new InputStreamReader( clientSocket.getInputStream() ));
            output = new DataOutputStream( clientSocket.getOutputStream() );

            String requestString = input.readLine();
            String headerLine = requestString;

            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            String httpQueryString = tokenizer.nextToken();

            StringBuffer responseBuffer = new StringBuffer();
            responseBuffer.append("request:<br>");

            System.out.println("request:");
            while ( input.ready() ) {
                // Read the HTTP complete HTTP Query
                responseBuffer.append(requestString + "<br>");
                System.out.println(requestString);
                requestString = input.readLine();
            }

            if ( httpMethod.equals("GET") ) {
                if ( httpQueryString.equals("/") ) {
                    // The default home page
                    sendResponse(200, responseBuffer.toString(), false);
                } else {
                    // This is interpreted as a file name
                    String fileName = httpQueryString.replaceFirst("/", "");
                    fileName = URLDecoder.decode(fileName, "UTF-8");
                    if (new File(fileName).isFile()){
                        sendResponse(200, fileName, true);
                    } else {
                        sendResponse(404, "<b>The Requested resource not found ..." +
                                "Usage: http://127.0.0.1:8000 or http://127.0.0.1:8000/<fileName></b>", false);
                    }
                }
            } else {
                sendResponse(404, "<b>The Requested resource not found ..." +
                    "Usage: http://127.0.0.1:8000 or http://127.0.0.1:8000/<fileName></b>", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
