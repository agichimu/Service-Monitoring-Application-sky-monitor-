package ServiceMonitoring;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static ServerReachable.ServerReachable.isServerReachable;

public class ServiceMonitoring {

    static class MonitorTask extends TimerTask {
        private final ServiceConfig serviceConfig;

        public MonitorTask(ServiceConfig serviceConfig) {
            this.serviceConfig = serviceConfig;
        }

        @Override
        public void run() {
            // Check if the service is up and if the server is reachable
            boolean isServiceUp = isServiceUp();
            boolean isServerReachable = isServerReachable(serviceConfig.serviceHost(), serviceConfig.servicePort());

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            //service and server status
            String serviceStatus = isServiceUp ? "Service is up" : "Service is down";
            String serverStatus = isServerReachable ? "Server is reachable" : "Server is not reachable";


            System.out.println(timestamp + " - " + serviceConfig.serviceName() + ": " + serviceStatus + ", " + serverStatus);

            // Logging  statuses
            logToFile(timestamp + " - " + serviceStatus + ", " + serverStatus, serviceConfig.serviceName());
        }

        private boolean isServiceUp() {
            try (Socket socket = new Socket(serviceConfig.serviceHost(), serviceConfig.servicePort())) {
                return true;
            } catch (Exception e) {
                System.err.println("Error connecting to the service: " + e.getMessage());
                return false;
            }
        }


        private void logToFile(String logEntry, String serviceName) {
            String logDirectory = "/home/brutal/Sky-monitor-logs";
            String logFileName = logDirectory + File.separator + serviceName + "_log.txt";
            try {
                Files.write(Paths.get(logFileName), (logEntry + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println("Error writing to log file: " + e.getMessage());
            }
        }


        public static void main(String[] args) {
            String xmlFilePath = "configuration/config.xml";

            try {
                ServiceConfigReader configReader = new ServiceConfigReader(xmlFilePath);
                ServiceConfig[] servicesConfig = configReader.readConfig();

                for (ServiceConfig serviceConfig : servicesConfig) {
                    Timer timer = new Timer();
                    // monitoring tasks at fixed intervals
                    timer.scheduleAtFixedRate(new MonitorTask(serviceConfig),
                            0, switch (serviceConfig.monitoringIntervalUnit().toLowerCase()) {
                                case "seconds" -> serviceConfig.monitoringInterval() * 1000L;
                                case "minutes" -> serviceConfig.monitoringInterval() * 60 * 1000L;
                                default ->
                                        throw new IllegalArgumentException("Unsupported time unit: " + serviceConfig.monitoringIntervalUnit());
                            });
                }
            } catch (Exception e) {
                System.err.println("Error reading XML file: " + e.getMessage());
            }
        }

        static class ServiceConfigReader {
            private final String xmlFilePath;

            public ServiceConfigReader(String xmlFilePath) {
                this.xmlFilePath = xmlFilePath;
            }

            public ServiceConfig[] readConfig() throws Exception {
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
                        int monitoringInterval = Integer.parseInt(getTextNodeContent(serviceElement, "monitoringInterval"));
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
}
