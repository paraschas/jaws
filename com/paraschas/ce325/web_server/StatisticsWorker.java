// StatisticsWorker.java


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
 * Worker thread that generates and serves the statistics page.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.2
 */
public class StatisticsWorker extends Thread {
    Socket clientSocket;
    Settings settings;
    Statistics statistics;


    /**
     * StatisticsWorker constructor.
     */
    public StatisticsWorker(Socket clientSocket, Settings settings, Statistics statistics) {
        this.clientSocket = clientSocket;
        this.settings = settings;
        this.statistics = statistics;
    }


    /**
     * Generate the statistics HTML page.
     */
    private String generateStatisticsPage() {
        // TODO
        String startedAt = statistics.getStartedAt();
        String runningFor = statistics.getRunningFor();
        String allServicedRequests = Integer.toString( statistics.getAllServicedRequests() );
        String http400Requests = Integer.toString( statistics.getHttp400Requests() );
        String http404Requests = Integer.toString( statistics.getHttp404Requests() );
        String http405Requests = Integer.toString( statistics.getHttp405Requests() );
        String http500Requests = Integer.toString( statistics.getHttp500Requests() );
        String averageServiceTime = statistics.getAverageServiceTime();

        StringBuilder statisticsPage = new StringBuilder();

        statisticsPage.append("<!doctype html>\n");
        statisticsPage.append("<html>\n");
        statisticsPage.append("    <head>\n");
        statisticsPage.append("        <meta charset=\"utf-8\">\n");
        statisticsPage.append("        <title>Jaws (ce325 web server) statistics</title>\n");
        statisticsPage.append("\n");
        statisticsPage.append("        <link rel=\"shortcut icon\" href=\"resources/favicon.ico\">\n");
        statisticsPage.append("\n");
        statisticsPage.append("        <link rel=\"stylesheet\" href=\"resources/normalize.css\">\n");
        statisticsPage.append("        <link rel=\"stylesheet\" href=\"resources/main.css\">\n");
        statisticsPage.append("    </head>\n");
        statisticsPage.append("    <body>\n");
        statisticsPage.append("        <h1>Jaws (ce325 web server) statistics</h1>\n");
        statisticsPage.append("        <table>\n");

        // table header
        statisticsPage.append("            <tr>\n");
        statisticsPage.append("                <th>field</th>\n");
        statisticsPage.append("                <th></th>\n");
        statisticsPage.append("                <th>value</th>\n");
        statisticsPage.append("            </tr>\n");

        // started at
        statisticsPage.append("            <tr>\n");
        statisticsPage.append("                <td>started at</td>\n");
        statisticsPage.append("                <td>&nbsp;:&nbsp;</td>\n");
        statisticsPage.append("                <td>" + startedAt + "</td>\n");
        statisticsPage.append("            </tr>\n");

        // running for
        statisticsPage.append("            <tr>\n");
        statisticsPage.append("                <td>running for</td>\n");
        statisticsPage.append("                <td>&nbsp;:&nbsp;</td>\n");
        statisticsPage.append("                <td>" + runningFor + "</td>\n");
        statisticsPage.append("            </tr>\n");

        // all serviced requests
        statisticsPage.append("            <tr>\n");
        statisticsPage.append("                <td>all serviced requests</td>\n");
        statisticsPage.append("                <td>&nbsp;:&nbsp;</td>\n");
        statisticsPage.append("                <td>" + allServicedRequests + "</td>\n");
        statisticsPage.append("            </tr>\n");

        // HTTP 400 requests
        statisticsPage.append("            <tr>\n");
        statisticsPage.append("                <td>HTTP 400 requests</td>\n");
        statisticsPage.append("                <td>&nbsp;:&nbsp;</td>\n");
        statisticsPage.append("                <td>" + http400Requests + "</td>\n");
        statisticsPage.append("            </tr>\n");

        // HTTP 404 requests
        statisticsPage.append("            <tr>\n");
        statisticsPage.append("                <td>HTTP 404 requests</td>\n");
        statisticsPage.append("                <td>&nbsp;:&nbsp;</td>\n");
        statisticsPage.append("                <td>" + http404Requests + "</td>\n");
        statisticsPage.append("            </tr>\n");

        // HTTP 405 requests
        statisticsPage.append("            <tr>\n");
        statisticsPage.append("                <td>HTTP 405 requests</td>\n");
        statisticsPage.append("                <td>&nbsp;:&nbsp;</td>\n");
        statisticsPage.append("                <td>" + http405Requests + "</td>\n");
        statisticsPage.append("            </tr>\n");

        // HTTP 500 requests
        statisticsPage.append("            <tr>\n");
        statisticsPage.append("                <td>HTTP 500 requests</td>\n");
        statisticsPage.append("                <td>&nbsp;:&nbsp;</td>\n");
        statisticsPage.append("                <td>" + http500Requests + "</td>\n");
        statisticsPage.append("            </tr>\n");

        // average service time
        statisticsPage.append("            <tr>\n");
        statisticsPage.append("                <td>average service time (in ms)</td>\n");
        statisticsPage.append("                <td>&nbsp;:&nbsp;</td>\n");
        statisticsPage.append("                <td>" + averageServiceTime + "</td>\n");
        statisticsPage.append("            </tr>\n");

        statisticsPage.append("        </table>\n");
        statisticsPage.append("    </body>\n");
        statisticsPage.append("</html>\n");

        return statisticsPage.toString();
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
            //System.out.println(inputLine);
            //while ( input.ready() ) {
            //    inputLine = input.readLine();
            //    System.out.println(inputLine);
            //}
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
                // check if the request is valid
                if ( queryString.equals("/") ) {
                    // TODO
                    // generate the statistics page

                    // set the Status Code
                    Status += "200 OK" + "\r\n";

                    LastModified = "Last-Modified: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(Calendar.getInstance().getTime()) + "\r\n";

                    // TODO
                    // get the size of the file
                    //ContentLength = "Content-Length: " + <html-page-size> + "\r\n";

                    // set the mimetype of the file
                    ContentType = "Content-Type: " + "text/html" + "\r\n";

                    String header = Status + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

                    // DEBUG
                    System.out.println(header);
                    output.writeBytes(header);

                    if ( httpMethod.equals("GET") ) {
                        String statisticsPage = generateStatisticsPage();

                        // DEBUG
                        System.out.println(statisticsPage);
                        output.writeBytes(statisticsPage);
                    }
                } else {
                    File path = new File( URLDecoder.decode(settings.getResourcesDirectoryPath() + queryString, "UTF-8") );

                    // check if the path exists and is a file
                    if ( path.exists() && path.isFile() ) {
                        // set the Status Code
                        Status += "200 OK" + "\r\n";

                        LastModified = "Last-Modified: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(path.lastModified()) + "\r\n";

                        // get the size of the file
                        ContentLength = "Content-Length: " + path.length() + "\r\n";
                        // get the mimetype of the file
                        ContentType = "Content-Type: " +
                                Files.probeContentType( path.toPath() ) + "\r\n";

                        String header = Status + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

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
                    } else {
                        Status += "404 Not Found" + "\r\n";

                        String header = Status + Date + Server + LastModified + Connection + ContentLength + ContentType + "\r\n";

                        // DEBUG
                        System.out.println(header);
                        output.writeBytes(header);
                    }
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
