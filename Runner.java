// Runner.java


import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.paraschas.ce325.web_server.Server;
import com.paraschas.ce325.web_server.Settings;


/**
 * Run Jaws. For testing and stuff.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.3.0
 */
class Runner {
    public static void printSettings(Settings settings) {
        System.out.println();
        System.out.println("IP address: " + settings.getIpAddress());
        System.out.println("listen port: " + settings.getListenPort());
        System.out.println("statistics port: " + settings.getStatisticsPort());
        System.out.println("access log path: " + settings.getAccessLogPath());
        System.out.println("error log path: " + settings.getErrorLogPath());
        System.out.println("document root path: " + settings.getDocumentRootPath());
        System.out.println("resources directory path: " + settings.getResourcesDirectoryPath());
        System.out.println("run PHP: " + settings.getRunPhp());
        System.out.println("deny access:");
        List<String> denyAccessIps = settings.getDenyAccessIps();
        for (String ip: denyAccessIps) {
            System.out.println("\tip: " + ip);
        }
        System.out.println();
    }


    /**
     * Parse the configuration file and return a settings object.
     *
     * reference:
     * http://docs.oracle.com/javase/7/docs/api/org/w3c/dom/Document.html
     * http://docs.oracle.com/javase/7/docs/api/org/w3c/dom/Node.html
     */
    public static void parseConfigurationFile(Settings settings, String configurationFilePath) {
        try {
            File xmlFile = new File(configurationFilePath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);

            // get the root node
            Node root = document.getDocumentElement();

            // http://stackoverflow.com/questions/5386991/java-most-efficient-method-to-iterate-over-all-elements-in-a-org-w3c-dom-docume
            NodeList nodeList = document.getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                String nodeName = node.getNodeName();

                // skip the root node
                if (node == root) {
                    continue;
                }

                if (nodeName.equals("listen") && (node.getParentNode() == root)) {
                    Node listenPortAttribute =
                        node.getAttributes().getNamedItem("port");
                    settings.setListenPort(listenPortAttribute.getNodeValue());
                    continue;
                }

                if (nodeName.equals("statistics") && (node.getParentNode() == root)) {
                    Node statisticsPortAttribute =
                        node.getAttributes().getNamedItem("port");
                    settings.setStatisticsPort(statisticsPortAttribute.getNodeValue());
                    continue;
                }

                // skip the log element
                if (nodeName.equals("log") && (node.getParentNode() == root)) {
                    continue;
                }

                if (nodeName.equals("access") && node.getParentNode().getNodeName().equals("log")) {
                    Node accessFilepathAttribute =
                        node.getAttributes().getNamedItem("filepath");
                    settings.setAccessLogPath(accessFilepathAttribute.getNodeValue());
                    continue;
                }

                if (nodeName.equals("error") && node.getParentNode().getNodeName().equals("log")) {
                    Node errorFilepathAttribute =
                        node.getAttributes().getNamedItem("filepath");
                    settings.setErrorLogPath(errorFilepathAttribute.getNodeValue());
                    continue;
                }

                if (nodeName.equals("documentroot") && (node.getParentNode() == root)) {
                    Node documentRootFilepathAttribute =
                        node.getAttributes().getNamedItem("filepath");
                    settings.setDocumentRootPath(documentRootFilepathAttribute.getNodeValue());
                    continue;
                }

                if (nodeName.equals("runphp") && (node.getParentNode() == root)) {
                    settings.setRunPhp(node.getTextContent());
                    continue;
                }

                // skip the denyaccess element
                if (nodeName.equals("denyaccess") && (node.getParentNode() == root)) {
                    continue;
                }

                // add an IP to the deny access list
                if (nodeName.equals("ip") && node.getParentNode().getNodeName().equals("denyaccess")) {
                    settings.addDenyAccessIp(node.getTextContent());
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String configurationFilePath;
        String ipAddress = "localhost";
        String resourcesDirectoryPath = ".";
        Settings settings = new Settings();

        if (args.length == 0) {
            System.out.println("usage:");
            System.out.println("java Runner <CONFIGURATION_FILE.xml>");
            System.exit(0);
        }

        configurationFilePath = args[0];
        // log info
        //System.out.println("configurationFilePath: " + configurationFilePath);

        settings.setIpAddress(ipAddress);
        settings.setResourcesDirectoryPath(resourcesDirectoryPath);
        parseConfigurationFile(settings, configurationFilePath);

        // DEBUG
        //printSettings(settings);

        // create and start the resources server
        Server resourcesServer = new Server(settings, "Resources");
        resourcesServer.start();

        // create and start the statistics server
        Server statisticsServer = new Server(settings, "Statistics");
        statisticsServer.start();
    }
}
