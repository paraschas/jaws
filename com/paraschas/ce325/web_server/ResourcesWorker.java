// ResourcesWorker.java


package com.paraschas.ce325.web_server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;


/**
 * ResourcesWorker to service requests spawned as a new thread.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.3
 */
public class ResourcesWorker extends Thread {
    Socket clientSocket;
    Settings settings;


    /**
     * ResourcesWorker constructor.
     */
    public ResourcesWorker(Socket clientSocket, Settings settings) {
        this.clientSocket = clientSocket;
        this.settings = settings;
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
            final String Server = "Server: " + "Jaws (ce325 web server)" + "\r\n";
            String LastModified;
            final String Connection = "Connection: " + "close" + "\r\n";
            String ContentLength;
            String ContentType;

            Status = "HTTP/1.1 ";
            LastModified = "";
            ContentLength = "";
            ContentType = "";

            Date = "Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(Calendar.getInstance().getTime()) + "\r\n";

            if ( httpMethod.equals("GET") || httpMethod.equals("HEAD") ) {
                File path = new File( URLDecoder.decode(settings.getDocumentRootPath() + queryString, "UTF-8") );
                // check if the path exists
                if ( path.exists() ) {
                    // if the path is a file, serve the file.

                    if ( path.isDirectory() ) {
                        // serve the index.html file if it exists in the directory
                        if ( new File(path, "index.html").exists() ) {
                            path = new File(path.toPath() + "/" + "index.html");
                        // serve the index.htm file if it exists in the directory
                        } else if ( new File(path, "index.htm").exists() ) {
                            path = new File(path.toPath() + "/" + "index.htm");
                        // generate and serve an html page with the contents of the directory that
                        // was requested
                        } else {
                            // TODO
                        }
                    }

                    // set the Status Code
                    Status += "200 OK" + "\r\n";

                    LastModified = "Last-Modified: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(path.lastModified()) + "\r\n";

                    // get the size of the file
                    ContentLength = "Content-Length: " + path.length() + "\r\n";
                    // get the mimetype of the file
                    ContentType = "Content-Type: " +
                            Files.probeContentType( path.toPath() ) + "\r\n";

                    FileInputStream fin = new FileInputStream(path);

                    String header = Status + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

                    // DEBUG
                    System.out.println(header);

                    output.writeBytes(header);

                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = fin.read(buffer)) != -1 ) {
                        output.write(buffer, 0, bytesRead);
                    }

                    fin.close();
                } else {
                    Status += "404 Not Found" + "\r\n";

                    String header = Status + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

                    // DEBUG
                    System.out.println(header);

                    output.writeBytes(header);
                }
            } else {
                Status += "405 Method Not Allowed" + "\r\n";

                String header = Status + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

                // DEBUG
                System.out.println(header);

                output.writeBytes(header);
            }

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
