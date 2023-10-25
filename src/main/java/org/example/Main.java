package org.example;
import java.util.*;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.ObjectUtils;
import java.time.LocalTime;
import java.time.Duration;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static class DataSet {
        String tripId;
        String departureTime;
        int routeId;

        public DataSet(String tripId, String departureTime) {
            this.tripId = tripId;
            this.departureTime = departureTime;
            this.routeId = findRouteId("src/main/resources/gtfs/trips.txt", tripId);
        }
    }

    private static int findRouteId(String filePath, String tripId) {
        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));

            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                if (tripId.equals(line[2])) {
                    return Integer.parseInt(line[0]);
                }
            }
            reader.close();
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String findStationName(String filePath, int stationId) {
        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));

            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                int stopId = Integer.parseInt(line[0]);
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

    private static ArrayList<DataSet> findDepartureTimes(String filePath, int stationId) {
        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));
            ArrayList<DataSet> result = new ArrayList<DataSet>();
            String[] line;
            reader.readNext();

            LocalTime parsedTime = null;
            //LocalTime currentTime = LocalTime.now();
            LocalTime currentTime = LocalTime.of(20, 00, 00);
            LocalTime twoHoursFromNow = currentTime.plusHours(2);

            while ((line = reader.readNext()) != null) {
                if (stationId == Integer.parseInt(line[3])) {
                    parsedTime = LocalTime.parse(line[2]);
                    if (parsedTime.isAfter(currentTime) && parsedTime.isBefore(twoHoursFromNow))
                        result.add(new DataSet(line[0], line[2]));
                }
            }
            reader.close();
            return result;
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
        String gtfsDirectory = "src/main/resources/gtfs/stops.txt";
        String stationName;

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

            stationName = findStationName(gtfsDirectory, stationId);

            if (stationName != null) {
                System.out.println("Station Name: " + stationName);
                break;
            } else {
                System.out.println("Station not found.");
            }
        }

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

        ArrayList<DataSet> findDepartureTimes = findDepartureTimes("src/main/resources/gtfs/stop_times.txt", stationId);

        //System.out.println(findDepartureTimes.get(0).tripId + " " + findDepartureTimes.get(0).departureTime);

        Comparator<DataSet> customComparator = new Comparator<DataSet>() {
            @Override
            public int compare(DataSet data1, DataSet data2) {
                int routeIdComparison = Integer.compare(data1.routeId, data2.routeId);
                if (routeIdComparison != 0) {
                    return routeIdComparison;
                }
                return LocalTime.parse(data1.departureTime).compareTo(LocalTime.parse(data2.departureTime));
            }
        };

        findDepartureTimes.sort(customComparator);

        Map<Integer, Integer> routeIdCountMap = new HashMap<>();
        LocalTime currentTime = LocalTime.of(20, 00, 00);

        for (DataSet data : findDepartureTimes) {
            int routeId = data.routeId;
            int count = routeIdCountMap.getOrDefault(routeId, 0);

            String formattedDepartureTime = "";
            if (timeFormat == TimeFormat.RELATIVE) {
                LocalTime departureTime = LocalTime.parse(data.departureTime);
                Duration timeUntilDeparture = Duration.between(currentTime, departureTime);
                int relativeMinutes = (int) timeUntilDeparture.toMinutes();
                formattedDepartureTime = String.format("RouteID: %d %d minutes from now", data.routeId, relativeMinutes);
            } else {
                formattedDepartureTime = String.format("RouteID: %d %s", data.routeId, data.departureTime);
            }

            if (count < numBuses) {
                System.out.println(formattedDepartureTime);
                routeIdCountMap.put(routeId, count + 1);
            }
        }

    }
}