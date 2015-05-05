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
        } else if (statusCode == 400) {
            status = "HTTP/1.1 400 Bad Request" + "\r\n";
        } else if (statusCode == 404) {
            status = "HTTP/1.1 404 Not Found" + "\r\n";
        // TODO
        // "The response MUST include an Allow header containing a list of valid methods for the requested resource."
        } else if (statusCode == 405) {
            status = "HTTP/1.1 405 Method Not Allowed" + "\r\n";
        } else if (statusCode == 500) {
            status = "HTTP/1.1 500 Internal Server Error" + "\r\n";
        } else {
            // TODO
            // how do we deal with errors?
            System.out.println("error, status code not supported");
            status = "no status" + "\r\n";
        }

        // TODO
        // this should change completely
        if ( isFile ) {
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

            StringBuffer response = new StringBuffer();
            response.append("request<br>");
            response.append("-------<br>");

            // DEBUG
            System.out.println("request");
            System.out.println("-------");

            String inputLine;
            inputLine = input.readLine();

            StringTokenizer tokenizer = new StringTokenizer(inputLine);
            String httpMethod = tokenizer.nextToken();
            String queryString = tokenizer.nextToken();

            while ( input.ready() ) {
                inputLine = input.readLine();

                // DEBUG
                System.out.println(inputLine);

                response.append(inputLine + "<br>");
            }

            if ( httpMethod.equals("GET") ) {
                // TODO
                // serve index.html or index.htm if at least one of them exists,
                // serve dynamically generated html code with the contents of the directory
                // that was requested
                if ( queryString.equals("/") ) {
                    // send a dummy response
                    sendResponse(200, response.toString(), false);
                } else {
                    // This is interpreted as a file name
                    String fileName = queryString.replaceFirst("/", "");
                    fileName = URLDecoder.decode(fileName, "UTF-8");
                    if (new File(fileName).isFile()){
                        sendResponse(200, fileName, true);
                    } else {
                        sendResponse(404, "<b>Not Found</b>", false);
                    }
                }
            } else {
                sendResponse(405, "<b>Method Not Allowed</b>", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
