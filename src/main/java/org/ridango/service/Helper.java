package org.ridango.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.ridango.model.DataSet;
import org.ridango.model.TimeFormat;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Helper {

    public static int findRouteId(String filePath, String tripId) {
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

    public static String findStationName(String filePath, int stationId) {
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

    public static ArrayList<DataSet> findDepartureTimes(int stationId) {
        String filePath = "src/main/resources/gtfs/stop_times.txt";

        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));
            ArrayList<DataSet> result = new ArrayList<DataSet>();
            String[] line;
            reader.readNext();

            LocalTime parsedTime = null;
            LocalTime currentTime = LocalTime.now();
            //LocalTime currentTime = LocalTime.of(20, 00, 00);
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

    public static ArrayList<DataSet> sortDataSetsByRouteId(ArrayList<DataSet> departureTimes) {
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

        departureTimes.sort(customComparator);
        return departureTimes;
    }

    public static void printData(ArrayList<DataSet> departureTimes, TimeFormat timeFormat, int numBuses) {
        Map<Integer, Integer> routeIdCountMap = new HashMap<>();

        LocalTime currentTime = LocalTime.now();
        //LocalTime currentTime = LocalTime.of(20, 00, 00);

        for (DataSet data : departureTimes) {
            int routeId = data.routeId;
            int count = routeIdCountMap.getOrDefault(routeId, 0);

            String formattedDepartureTime = "";
            if (timeFormat == TimeFormat.RELATIVE) {
                LocalTime departureTime = LocalTime.parse(data.departureTime);
                Duration timeUntilDeparture = Duration.between(currentTime, departureTime);
                int relativeMinutes = (int) timeUntilDeparture.toMinutes();
                formattedDepartureTime = String.format("Bus: %d %dmin", data.routeId, relativeMinutes);
            } else {
                formattedDepartureTime = String.format("Bus: %d %s", data.routeId, data.departureTime);
            }

            if (count < numBuses) {
                System.out.println(formattedDepartureTime);
                routeIdCountMap.put(routeId, count + 1);
            }
        }
    }

}
