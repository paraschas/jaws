// Run.java


import com.paraschas.ce325.web_server.Server;
import com.paraschas.ce325.web_server.Settings;


/**
 * Run Jaws. For testing and stuff.
 *
 * @author   Dimitrios Paraschas <paraschas@gmail.com>
 * @version  0.0.1
 */
class Run {
    public static void main(String[] args) {
        String configurationFilePath;
        Settings settings = new Settings();

        if (args.length == 0) {
            System.out.println("usage:");
            System.out.println("java Run <CONFIGURATION_FILE.xml>");
            System.exit(0);
        }

        configurationFilePath = args[1];
        System.out.println("configurationFilePath: " + configurationFilePath);

        settings = ParseConfigurationFile(configurationFilePath);
    }
}
