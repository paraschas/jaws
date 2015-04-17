// Run.java


import java.io.File;
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

            NodeList nodeList = document.getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                System.out.println();
                System.out.println("name: " + node.getNodeName());
                // NEXT
                // print the value
                System.out.println("text: " + node.getTextContent());
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
