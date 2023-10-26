package org.ridango.service;

import org.ridango.model.TimeFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

import static org.ridango.service.Helper.findStationName;

public class InputService {

    public static int inputStationId() {
        Scanner scanner = new Scanner(System.in);
        int stationId;
        String stationName;
        String gtfsStopsDirectory = "src/main/resources/gtfs/stops.txt";

        while(true) {
            while (true) {
                try {
                    System.out.print("Enter the station ID: ");
                    stationId = scanner.nextInt();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.nextLine();
                }
            }
            stationName = findStationName(gtfsStopsDirectory, stationId);
            if (stationName != null) {
                System.out.println("Station Name: " + stationName);
                break;
            } else {
                System.out.println("Station not found.");
            }
        }

        return stationId;
    }

    public static int inputNumBuses() {
        Scanner scanner = new Scanner(System.in);
        int numBuses;

        while (true) {
            try {
                System.out.print("Enter the number of following buses: ");
                numBuses = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }

        return numBuses;
    }

    public static TimeFormat inputTimeFormat() {
        Scanner scanner = new Scanner(System.in);
        TimeFormat timeFormat = null;

        System.out.print("Enter time format (relative/absolute): ");
        String input = scanner.next();
        while (timeFormat == null) {
            try {
                timeFormat = TimeFormat.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.print("Entered time format is not valid, enter relative or absolute: ");
                input = scanner.next();
            }
        }

        return timeFormat;
    }
}
