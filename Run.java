// Run.java


import java.io.File;
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
 * @version  0.0.1
 */
class Run {
    /**
     * Parse the configuration file and return a settings object.
     */
    public static Settings parseConfigurationFile(String configurationFilePath) {
        Settings settings = new Settings();

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
                System.out.println();
                System.out.println("name: " + nodeName);

                // skip the root node
                if (node == root) {
                    continue;
                }

                if (nodeName.equals("listen") && (node.getParentNode() == root)) {
                    Node listenPortAttribute =
                        node.getAttributes().getNamedItem("port");
                    settings.setListenPort(listenPortAttribute.getNodeValue());
                    System.out.println("listen port: " + settings.getListenPort());
                    continue;
                }

                if (nodeName.equals("statistics") && (node.getParentNode() == root)) {
                    Node statisticsPortAttribute =
                        node.getAttributes().getNamedItem("port");
                    settings.setStatisticsPort(statisticsPortAttribute.getNodeValue());
                    System.out.println("statistics port: " + settings.getStatisticsPort());
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
                    System.out.println("access log path: " + settings.getAccessLogPath());
                    continue;
                }

                if (nodeName.equals("error") && node.getParentNode().getNodeName().equals("log")) {
                    Node errorFilepathAttribute =
                        node.getAttributes().getNamedItem("filepath");
                    settings.setErrorLogPath(errorFilepathAttribute.getNodeValue());
                    System.out.println("error log path: " + settings.getErrorLogPath());
                    continue;
                }

                if (nodeName.equals("document-root") && (node.getParentNode() == root)) {
                    Node documentRootFilepathAttribute =
                        node.getAttributes().getNamedItem("filepath");
                    settings.setDocumentRootPath(documentRootFilepathAttribute.getNodeValue());
                    System.out.println("document root path: " + settings.getDocumentRootPath());
                    continue;
                }

                if (nodeName.equals("run-php") && (node.getParentNode() == root)) {
                    settings.setRunPhp(node.getTextContent());
                    System.out.println("run PHP: " + settings.getRunPhp());
                    continue;
                }

                // skip the deny-access element
                if (nodeName.equals("deny-access") && (node.getParentNode() == root)) {
                    continue;
                }

                // add an IP to the deny access list
                if (nodeName.equals("ip") && node.getParentNode().getNodeName().equals("deny-access")) {
                    settings.addDenyAccessIp(node.getTextContent());
                    List<String> denyAccessIps = settings.getDenyAccessIps();
                    for (String ip: denyAccessIps) {
                        System.out.println("\tip: " + ip);
                    }
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return settings;
    }


    public static void main(String[] args) {
        String configurationFilePath;
        Settings settings = new Settings();

        if (args.length == 0) {
            System.out.println("usage:");
            System.out.println("java Run <CONFIGURATION_FILE.xml>");
            System.exit(0);
        }

        configurationFilePath = args[0];
        System.out.println("configurationFilePath: " + configurationFilePath);

        settings = parseConfigurationFile(configurationFilePath);
    }
}
