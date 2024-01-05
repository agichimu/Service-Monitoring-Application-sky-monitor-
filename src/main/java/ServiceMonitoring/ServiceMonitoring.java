package ServiceMonitoring;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static ServerReachable.ServerReachable.isServerReachable;

public class ServiceMonitoring {

    private static final boolean ENABLE_FILE_LOGGING = true;
    private static final int FILE_LOGGING_INTERVAL = 30;
    private static final boolean ENABLE_LOGS_ARCHIVING = true;
    private static final int LOGS_ARCHIVING_INTERVAL = 7;

    static class MonitorTask extends TimerTask {
        private final ServiceConfig serviceConfig;

        public MonitorTask(ServiceConfig serviceConfig) {
            this.serviceConfig = serviceConfig;
        }

        @Override
        public void run() {
            boolean isServiceUp = isServiceUp();
            boolean isServerReachable = isServerReachable(serviceConfig.serviceHost(), serviceConfig.servicePort());

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String serviceStatus = isServiceUp ? "Service is up" : "Service is down";
            String serverStatus = isServerReachable ? "Server is reachable" : "Server is not reachable";

            // printing statuses
            System.out.println(timestamp + " - " + serviceConfig.serviceName() + ": " + serviceStatus + ", " + serverStatus);

            // Logging status to file if file logging is enabled
            if (ENABLE_FILE_LOGGING) {
                try {
                    logToFile(timestamp + " - " + serviceStatus + ", " + serverStatus, serviceConfig.serviceName());
                } catch (IOException e) {
                    System.err.println("Error writing to log file: " + e.getMessage());
                }
            }
        }

        private boolean isServiceUp() {
            try (Socket socket = new Socket(serviceConfig.serviceHost(), serviceConfig.servicePort())) {
                System.out.println(socket);
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        private void logToFile(String logEntry, String serviceName) throws IOException {
            //directory for log files
            String logDirectory = "/home/alexander/Sky-monitor-logs";
            String logFileName = logDirectory + File.separator + serviceName + "_log.txt";

            // Create the log directory if it doesn't exist
            File directory = new File(logDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Write log entry to the specified log file
            try (FileWriter logFileWriter = new FileWriter(logFileName, true)) {
                logFileWriter.write(logEntry + "\n");
                logFileWriter.flush();
            }
        }
    }

    public static void main(String[] args) {
        String xmlFilePath = "configuration/config.xml";

        try {
            ServiceConfigReader configReader = new ServiceConfigReader(xmlFilePath);
            ServiceConfig[] servicesConfig = configReader.readServiceConfig();

            for (ServiceConfig serviceConfig : servicesConfig) {
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new MonitorTask(serviceConfig),
                        0, convertToMilliseconds(serviceConfig.monitoringInterval(), serviceConfig.monitoringIntervalUnit()));
            }
        } catch (Exception e) {
            System.err.println("Error reading XML file: " + e.getMessage());
        }
    }

    private static long convertToMilliseconds(int interval, String unit) {
        return switch (unit.toLowerCase()) {
            case "seconds" -> interval * 1000L;
            case "minutes" -> interval * 60 * 1000L;
            default -> throw new IllegalArgumentException("Unsupported time unit: " + unit);
        };
    }

    static class ServiceConfigReader {
        private final String xmlFilePath;

        public ServiceConfigReader(String xmlFilePath) {
            this.xmlFilePath = xmlFilePath;
        }

        public ServiceConfig[] readServiceConfig() throws Exception {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFilePath));

            NodeList serviceNodes = document.getElementsByTagName("service");
            ServiceConfig[] servicesConfig = new ServiceConfig[serviceNodes.getLength()];

            for (int i = 0; i < serviceNodes.getLength(); i++) {
                Node serviceNode = serviceNodes.item(i);
                if (serviceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element serviceElement = (Element) serviceNode;
                    String serviceName = getTextNodeContent(serviceElement, "serviceName");
                    String serviceHost = getTextNodeContent(serviceElement, "serviceHost");
                    int servicePort = Integer.parseInt(getTextNodeContent(serviceElement, "servicePort"));
                    int monitoringInterval = Integer.parseInt(getTextNodeContent(serviceElement, "monitoringIntervals"));
                    String monitoringIntervalUnit = getTextNodeContent(serviceElement, "monitoringIntervalUnit");

                    servicesConfig[i] = new ServiceConfig(serviceName, serviceHost, servicePort, monitoringInterval, monitoringIntervalUnit);
                }
            }

            return servicesConfig;
        }

        private String getTextNodeContent(Element element, String tagName) {
            NodeList nodeList = element.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                Node node = nodeList.item(0).getFirstChild();
                if (node != null && node.getNodeType() == Node.TEXT_NODE) {
                    return node.getTextContent();
                }
            }
            System.out.println("Text content not found for tag: " + tagName);
            return "";
        }
    }

    static class ServiceConfig {
        private final String serviceName;
        private final String serviceHost;
        private final int servicePort;
        private final int monitoringInterval;
        private final String monitoringIntervalUnit;

        public ServiceConfig(String serviceName, String serviceHost, int servicePort, int monitoringInterval,
                             String monitoringIntervalUnit) {
            this.serviceName = serviceName;
            this.serviceHost = serviceHost;
            this.servicePort = servicePort;
            this.monitoringInterval = monitoringInterval;
            this.monitoringIntervalUnit = monitoringIntervalUnit;
        }

        public String serviceName() {
            return serviceName;
        }

        public String serviceHost() {
            return serviceHost;
        }

        public int servicePort() {
            return servicePort;
        }

        public int monitoringInterval() {
            return monitoringInterval;
        }

        public String monitoringIntervalUnit() {
            return monitoringIntervalUnit;
        }
    }
}
