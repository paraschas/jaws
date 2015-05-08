// StatisticsWorker.java


package com.paraschas.ce325.web_server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;


/**
 * Worker thread that generates and serves the statistics page.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.1
 */
public class StatisticsWorker extends Thread {
    Socket clientSocket;
    Settings settings;


    /**
     * StatisticsWorker constructor.
     */
    public StatisticsWorker(Socket clientSocket, Settings settings) {
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
                        String runningTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(Calendar.getInstance().getTime());

                        StringBuilder statisticsPage = new StringBuilder();
                        statisticsPage.append("<html>");
                        statisticsPage.append("<head>");
                        statisticsPage.append("<title>Jaws (ce325 web server) statistics</title>");
                        statisticsPage.append("</head>");
                        statisticsPage.append("<body>");
                        statisticsPage.append("<p>" + "NOT IMPLEMENTED YET" + "</p>");
                        statisticsPage.append("</body>");
                        statisticsPage.append("</html>");

                        // DEBUG
                        System.out.println( statisticsPage.toString() );
                        output.writeBytes( statisticsPage.toString() );
                    }
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
