package csv.create;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVFileGenerator {

    public static void main(String[] args) {
        // Define the CSV file path
        String csvFilePath = "/home/brutal/services.csv";

        // Create records for services
        String[][] servicesData = {
                {"Fields", "Service 1", "Service 2"},
                {"ID", "1", "2"},
                {"Service Name", "Harambee Members Portal", "Sky World Services Portal"},
                {"Service Host", "selfservice.harambeesacco.com", "portal.skyworld.co.ke"},
                {"Service Port", "443", "443"},
                {"Service Resource URI", "/", "/"},
                {"Service Method", "GET", "GET"},
                {"Expected Telnet Response", "Connected to selfservice.harambeesacco.com.", "Connected to portal.skyworld.co.ke."},
                {"Expected Request Response", "Welcome to Harambee Sacco Society Ltd Members' Portal", "Welcome to Sky World Limited Services Hub"},
                {"Monitoring Intervals", "10", "300"},
                {"Monitoring Intervals Time Unit", "Minutes", "Seconds"}
        };

        // Create and write to the CSV file
        writeCSV(csvFilePath, servicesData);
    }

    private static void writeCSV(String csvFilePath, String[][] data) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            // Writing data to the CSV file
            writer.writeAll(List.of(data));

            System.out.println("CSV file created successfully at: " + csvFilePath);
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
