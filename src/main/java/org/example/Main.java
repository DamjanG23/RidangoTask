package org.example;
import java.util.Scanner;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;

public class Main {

    private static String findStationName(String filePath, int stationId) {
        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));

            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                int stopId = Integer.parseInt(line[1]);
                if (stopId == stationId) {
                    return line[2];
                }
            }
            reader.close();
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum TimeFormat {
        RELATIVE, ABSOLUTE
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int stationId;
        while (true) {
            try {
                System.out.print("Enter the station ID: ");
                stationId = scanner.nextInt();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }

        String gtfsDirectory = "src/main/resources/gtfs/stops.txt";
        String stationName = findStationName(gtfsDirectory, stationId);

        if (stationName != null) {
            System.out.println("Station Name: " + stationName);
        } else {
            System.out.println("Station not found.");
        }

        int numBuses;
        while (true) {
            try {
                System.out.print("Enter the number of following buses: ");
                numBuses = scanner.nextInt();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }

        System.out.print("Enter time format (relative/absolute): ");
        String input = scanner.next();
        TimeFormat timeFormat = null;

        while (timeFormat == null) {
            try {
                timeFormat = TimeFormat.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.print("Entered time format is not valid, enter relative or absolute: ");
                input = scanner.next();
            }
        }

        scanner.close();

        System.out.println("Station ID: " + stationId);
        System.out.println("Number of Buses: " + numBuses);
        System.out.println("Time Format: " + timeFormat);
    }
}