import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ServiceMonitor {

    private static final String CONFIG_PATH = "configuration/config.xml"; // Specify the path to your configuration file
    private static final String LOG_PREFIX = "/home/alexander/service_log";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");

    public static void main(String[] args) {
        startMonitoring();
    }

    private static void startMonitoring() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkServicesStatus(CONFIG_PATH);
            }
        }, 0, 1000 * 60); // Check every minute (adjust as needed)

        // You can add other commands like starting and stopping the monitoring application here
    }

    private static void checkServicesStatus(String configPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new File(configPath));
            document.getDocumentElement().normalize();

            NodeList serviceList = document.getElementsByTagName("service");

            for (int i = 0; i < serviceList.getLength(); i++) {
                Node serviceNode = serviceList.item(i);

                if (serviceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element serviceElement = (Element) serviceNode;

                    String serviceName = serviceElement.getElementsByTagName("serviceName").item(0).getTextContent();
                    String serviceHost = serviceElement.getElementsByTagName("serviceHost").item(0).getTextContent();
                    int servicePort = Integer.parseInt(serviceElement.getElementsByTagName("servicePort").item(0).getTextContent());

                    boolean isServerReachable = isServerReachable(serviceHost, servicePort);
                    boolean isServiceReachable = isServiceReachable(serviceHost, servicePort, serviceElement.getElementsByTagName("serviceResourceURI").item(0).getTextContent());

                    logServiceStatus(serviceName, isServiceReachable, isServerReachable);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isServerReachable(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isServiceReachable(String host, int port, String resourceURI) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            URI uri = new URI("http", null, host, port, resourceURI, null, null);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200; // Assuming 200 OK means the service is reachable
        } catch (Exception e) {
            return false;
        }
    }

    private static void logServiceStatus(String serviceName, boolean isServiceReachable, boolean isServerReachable) {
        try {
            String logFileName = LOG_PREFIX + "_" + dateFormat.format(new Date()) + ".txt";
            Path logFilePath = Paths.get(logFileName);

            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
            }

            try (FileWriter writer = new FileWriter(logFilePath.toFile(), true)) {
                String logEntry = dateFormat.format(new Date()) + " - Service: " + serviceName +
                        ", Service Reachable: " + isServiceReachable +
                        ", Server Reachable: " + isServerReachable + "\n";
                writer.write(logEntry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
