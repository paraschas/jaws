// ResourcesWorker.java


package com.paraschas.ce325.web_server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;


/**
 * Worker thread that services resources requests.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.1.0
 */
public class ResourcesWorker extends Thread {
    final private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Socket clientSocket;
    private Settings settings;
    private Statistics statistics;
    private Logger logger;


    /**
     * ResourcesWorker constructor.
     */
    public ResourcesWorker(Socket clientSocket, Settings settings, Statistics statistics, Logger logger) {
        this.clientSocket = clientSocket;
        this.settings = settings;
        this.statistics = statistics;
        this.logger = logger;
    }


    /**
     * Generate the directory contents HTML page.
     */
    private String generateDirectoryPage(File directory) throws Exception {
        Path pathAbsolute = Paths.get( directory.getPath() );
        Path pathBase = Paths.get( settings.getDocumentRootPath() );
        Path pathRelative = pathBase.relativize(pathAbsolute);

        String currentDirectory = pathRelative.toString();

        StringBuilder directoryPage = new StringBuilder();

        directoryPage.append("<!doctype html>\n");
        directoryPage.append("<html>\n");
        directoryPage.append("    <head>\n");
        directoryPage.append("        <meta charset=\"utf-8\">\n");
        directoryPage.append("        <title>index of " + "/" + currentDirectory + " - Jaws (ce325 web server)</title>\n");
        directoryPage.append("    </head>\n");
        directoryPage.append("    <body>\n");
        directoryPage.append("        <h1>index of " + "/" + currentDirectory + "</h1>\n");
        directoryPage.append("        <table>\n");

        // table header
        directoryPage.append("            <tr>\n");
        directoryPage.append("                <th>name</th>\n");
        directoryPage.append("                <th>size</th>\n");
        directoryPage.append("                <th>last modified</th>\n");
        directoryPage.append("                <th>mimetype</th>\n");
        directoryPage.append("            </tr>\n");

        for (File file: directory.listFiles()) {
            directoryPage.append("            <tr>\n");

            String queryString;
            if ( currentDirectory.equals("") || currentDirectory.equals("/") ) {
                queryString = currentDirectory + "/" + file.getName();
            } else {
                queryString = (currentDirectory.startsWith("/") ? currentDirectory : "/" + currentDirectory) + "/" + file.getName();
            }
            directoryPage.append("                <td>" + "<a href=\"" + queryString + "\">" + file.getName() + "</a>" + "</td>\n");

            directoryPage.append("                <td align=\"right\">" + file.length() + "</td>\n");
            directoryPage.append("                <td>" + dateTimeFormat.format(file.lastModified()) + "</td>\n");
            directoryPage.append("                <td align=\"right\">" + Files.probeContentType( file.toPath() ) + "</td>\n");
            directoryPage.append("            </tr>\n");
        }

        directoryPage.append("        </table>\n");
        directoryPage.append("        <p>Jaws (ce325 web server) at " + settings.getIpAddress() + ":" + settings.getListenPort() + "</p>\n");
        directoryPage.append("    </body>\n");
        directoryPage.append("</html>\n");

        return directoryPage.toString();
    }


    /**
     * Service a request.
     */
    public void serviceRequest() throws Exception {
        try (
            BufferedReader input =
                    new BufferedReader(new InputStreamReader( clientSocket.getInputStream() ));
        ) {
            long startServicingRequest = System.currentTimeMillis();

            String Request;

            String inputLine;
            inputLine = input.readLine();

            Request = inputLine;

            StringTokenizer tokenizer = new StringTokenizer(inputLine);
            String httpMethod = tokenizer.nextToken();
            String queryString = tokenizer.nextToken();

            // DEBUG
            System.out.println(inputLine);

            // get the User-Agent
            String UserAgent = "";
            while ( input.ready() ) {
                inputLine = input.readLine();

                // DEBUG
                System.out.println(inputLine);

                int index = inputLine.indexOf(' ');
                if ( index > 0 && inputLine.substring(0, index).equals("User-Agent:") ) {
                    UserAgent = inputLine.substring(index + 1);
                }
            }

            StringBuilder logEntry = new StringBuilder();
            //logEntry.append( clientSocket.getInetAddress().toString().substring(1) );
            logEntry.append( clientSocket.getRemoteSocketAddress().toString().substring(1) );
            logEntry.append(" - ");
            logEntry.append("[" + dateTimeFormat.format(new Date(startServicingRequest)) + "]");
            logEntry.append(" - " );
            logEntry.append("\"" + Request + "\"");
            logEntry.append(" -> ");

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

            Status = "";
            LastModified = "";
            ContentLength = "";
            ContentType = "";

            Date = "Date: " + dateTimeFormat.format(Calendar.getInstance().getTime()) + "\r\n";

            if ( httpMethod.equals("GET") || httpMethod.equals("HEAD") ) {
                File path = new File( URLDecoder.decode(settings.getDocumentRootPath() + queryString, "UTF-8") );

                // check if the path exists
                if ( path.exists() ) {
                    boolean isFile = true;

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
                            // generate the directory contents HTML page
                            String directoryPage = "";
                            try {
                                directoryPage = generateDirectoryPage(path);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // set the Status Code
                            Status += "200 OK";

                            LastModified = "Last-Modified: " + dateTimeFormat.format(Calendar.getInstance().getTime()) + "\r\n";

                            // get the size of the page
                            ContentLength = "Content-Length: " + directoryPage.length() + "\r\n";

                            // set the mimetype of the file
                            ContentType = "Content-Type: " + "text/html" + "\r\n";

                            String header = "HTTP/1.1 " + Status + "\r\n" + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

                            // DEBUG
                            System.out.println(header);
                            output.writeBytes(header);

                            if ( httpMethod.equals("GET") ) {
                                // DEBUG
                                //System.out.println(directoryPage);
                                output.writeBytes(directoryPage);
                            }

                            isFile = false;
                        }
                    }

                    // if the path is a file, serve the file.
                    if (isFile) {
                        // set the Status Code
                        Status += "200 OK";

                        LastModified = "Last-Modified: " + dateTimeFormat.format(path.lastModified()) + "\r\n";

                        // get the size of the file
                        ContentLength = "Content-Length: " + path.length() + "\r\n";
                        // get the mimetype of the file
                        ContentType = "Content-Type: " +
                                Files.probeContentType( path.toPath() ) + "\r\n";

                        String header = "HTTP/1.1 " + Status + "\r\n" + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

                        // DEBUG
                        System.out.println(header);
                        output.writeBytes(header);

                        if ( httpMethod.equals("GET") ) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;

                            FileInputStream fin = new FileInputStream(path);

                            while ((bytesRead = fin.read(buffer)) != -1 ) {
                                output.write(buffer, 0, bytesRead);
                            }

                            fin.close();
                        }
                    }
                } else {
                    Status += "404 Not Found";

                    String header = "HTTP/1.1 " + Status + "\r\n" + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

                    // DEBUG
                    System.out.println(header);
                    output.writeBytes(header);

                    statistics.incrementHttp404Requests();
                }
            } else {
                Status += "405 Method Not Allowed";

                String header = "HTTP/1.1 " + Status + "\r\n" + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

                // DEBUG
                System.out.println(header);
                output.writeBytes(header);

                statistics.incrementHttp405Requests();
            }
            output.close();

            logEntry.append(Status);
            logEntry.append(" - ");
            logEntry.append("\"" + UserAgent + "\"");
            logger.addToAccessLog( logEntry.toString() );

            long now = System.currentTimeMillis();
            statistics.addTotalServiceTime(now - startServicingRequest);
            statistics.incrementAllServicedRequests();
        } catch (Exception e) {
            e.printStackTrace();

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);

            logger.addToErrorLog( stringWriter.toString() );

            statistics.incrementHttp500Requests();
        }
    }


    public void run() {
        // DEBUG
        System.out.println();
        System.out.println("new connection: " + clientSocket.getInetAddress().toString().substring(1) + ":" +
                clientSocket.getPort());

        try {
            serviceRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
