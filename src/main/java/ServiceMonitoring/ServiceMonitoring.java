package ServiceMonitoring;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceMonitoring {

    public static void main(String[] args) {
        // Define the XML file path
        String xmlFilePath = "configuration/config.xml";

        try {
            // Read services configuration from XML file
            ServiceConfigReader configReader = new ServiceConfigReader(xmlFilePath);
            ServiceConfig[] servicesConfig = configReader.readServiceConfig();

            // Start monitoring for each service
            for (ServiceConfig serviceConfig : servicesConfig) {
                // Create a timer to schedule monitoring tasks at defined intervals
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new MonitorTask(serviceConfig),
                        0, convertToMilliseconds(serviceConfig.getMonitoringInterval(), serviceConfig.getMonitoringIntervalUnit()));
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

    static class MonitorTask extends TimerTask {
        private final ServiceConfig serviceConfig;

        public MonitorTask(ServiceConfig serviceConfig) {
            this.serviceConfig = serviceConfig;
        }

        @Override
        public void run() {
            boolean isServiceUp = isServiceUp();
            boolean isServerAccessible = isServerAccessible();

            // Log the status with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String serviceStatus = isServiceUp ? "Service is up" : "Service is down";
            String serverStatus = isServerAccessible ? "Server is accessible" : "Server is not accessible";

            System.out.println(timestamp + " - " + serviceConfig.getServiceName() + ": " + serviceStatus + ", " + serverStatus);
        }

        private boolean isServiceUp() {
            try (Socket socket = new Socket(InetAddress.getByName(serviceConfig.getServiceHost()), serviceConfig.getServicePort())) {
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean isServerAccessible() {
            try {
                InetAddress address = InetAddress.getByName(serviceConfig.getServiceHost());
                return address.isReachable(5000); // 5 seconds timeout
            } catch (Exception e) {
                return false;
            }
        }
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
                    // Extract service configuration from XML elements
                    String serviceName = serviceElement.getAttribute("name");
                    String serviceHost = getTextNodeContent(serviceElement, "serviceHost");
                    int servicePort = Integer.parseInt(getTextNodeContent(serviceElement, "servicePort"));
                    int monitoringInterval = Integer.parseInt(getTextNodeContent(serviceElement, "monitoringIntervals"));
                    String monitoringIntervalUnit = getTextNodeContent(serviceElement, "monitoringIntervalUnit");

                    // Create a ServiceConfig object for each service
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
            // Log if the text content is not found
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

        public ServiceConfig(String serviceName, String serviceHost, int servicePort, int monitoringInterval, String monitoringIntervalUnit) {
            this.serviceName = serviceName;
            this.serviceHost = serviceHost;
            this.servicePort = servicePort;
            this.monitoringInterval = monitoringInterval;
            this.monitoringIntervalUnit = monitoringIntervalUnit;
        }

        // Getter methods for accessing service configuration parameters
        public String getServiceName() {
            return serviceName;
        }

        public String getServiceHost() {
            return serviceHost;
        }

        public int getServicePort() {
            return servicePort;
        }

        public int getMonitoringInterval() {
            return monitoringInterval;
        }

        public String getMonitoringIntervalUnit() {
            return monitoringIntervalUnit;
        }
    }
}
