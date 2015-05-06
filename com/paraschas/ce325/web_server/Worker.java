// Worker.java


package com.paraschas.ce325.web_server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;


/**
 * Worker to service requests spawned as a new thread.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.2
 */
public class Worker extends Thread {
    Socket clientSocket;


    /**
     * Worker constructor.
     */
    public Worker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


    /**
     * Service a request.
     */
    public void serviceRequest() throws Exception {
        try (
            BufferedReader input =
                    new BufferedReader(new InputStreamReader( clientSocket.getInputStream() ));
        ) {
            String inputLine;
            inputLine = input.readLine();

            StringTokenizer tokenizer = new StringTokenizer(inputLine);
            String httpMethod = tokenizer.nextToken();
            String queryString = tokenizer.nextToken();

            // DEBUG
            ////////////////////////////////////////////////////////////////////////////////////////
            System.out.println(inputLine);
            while ( input.ready() ) {
                inputLine = input.readLine();
                System.out.println(inputLine);
            }
            ////////////////////////////////////////////////////////////////////////////////////////


            // response
            ////////////////////////////////////////////////////////////////////////////////////////
            DataOutputStream output = new DataOutputStream( clientSocket.getOutputStream() );

            String Status;
            String Date;
            final String Server = "Jaws (ce325 web server)" + "\r\n";
            String LastModified;
            String Connection;
            String ContentLength;
            String ContentType;

            Status = "HTTP/1.1 ";
            LastModified = "";
            Connection = "";
            ContentLength = "";
            ContentType = "";

            Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(Calendar.getInstance().getTime()) + "\r\n";
            // DEBUG

            if ( httpMethod.equals("GET") || httpMethod.equals("HEAD") ) {
                // TODO
                // serve index.html or index.htm if at least one of them exists,
                // serve dynamically generated html code with the contents of the directory
                // that was requested

                File file = new File( URLDecoder.decode(queryString, "UTF-8") );
                // check if the path exists
                if ( file.exists() ) {
                    Status += "200 OK" + "\r\n";

                    // if the path is a file serve the file
                    if ( file.isFile() ) {
                    // if the path is a directory generate and serve the dynamic html page
                    } else if ( file.isDirectory() ) {
                    }
                } else {
                    Status += "404 Not Found" + "\r\n";
                }

            } else {
                Status += "405 Method Not Allowed" + "\r\n";
            }

            String header = Status + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

            System.out.println(header);

            output.writeBytes(header);

            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run() {
        // DEBUG
        System.out.println();
        System.out.println("new connection: " + clientSocket.getInetAddress() +
                    ":" + clientSocket.getPort());

        try {
            serviceRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
