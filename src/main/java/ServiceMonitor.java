import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceMonitor {

    public static void main(String[] args) {
        // Define the CSV file path
        String csvFilePath = "/home/alexander/services.csv";

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            // Read all data from the CSV file
            String[][] servicesData = reader.readAll().toArray(new String[0][0]);

            // Assuming the first row (index 0) contains headers, start from index 1 in the loop
            for (int i = 1; i < servicesData.length && i < servicesData[0].length; i++) {
                String serviceName = servicesData[2][i];
                String serviceHost = servicesData[3][i];
                int servicePort = Integer.parseInt(servicesData[4][i]);
                int monitoringInterval = Integer.parseInt(servicesData[9][i]);
                String monitoringIntervalUnit = servicesData[10][i];

                // Create a timer to schedule monitoring tasks at defined intervals
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new MonitorTask(serviceName, serviceHost, servicePort),
                        0, convertToMilliseconds(monitoringInterval, monitoringIntervalUnit));
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
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
        private final String serviceName;
        private final String serviceHost;
        private final int servicePort;

        public MonitorTask(String serviceName, String serviceHost, int servicePort) {
            this.serviceName = serviceName;
            this.serviceHost = serviceHost;
            this.servicePort = servicePort;
        }

        @Override
        public void run() {
            boolean isServiceUp = isServiceUp();
            boolean isServerAccessible = isServerAccessible();

            // Log the status with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String serviceStatus = isServiceUp ? "Service is up" : "Service is down";
            String serverStatus = isServerAccessible ? "Server is accessible" : "Server is not accessible";

            System.out.println(timestamp + " - " + serviceName + ": " + serviceStatus + ", " + serverStatus);
        }

        private boolean isServiceUp() {
            try (Socket socket = new Socket(InetAddress.getByName(serviceHost), servicePort)) {
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        private boolean isServerAccessible() {
            try {
                InetAddress address = InetAddress.getByName(serviceHost);
                return address.isReachable(5000); // 5 seconds timeout
            } catch (IOException e) {
                return false;
            }
        }
    }
}
