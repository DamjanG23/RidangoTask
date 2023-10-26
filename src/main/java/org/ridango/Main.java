package org.ridango;
import java.util.*;

import java.time.LocalTime;
import org.ridango.model.DataSet;
import org.ridango.model.TimeFormat;

import static org.ridango.service.Helper.*;
import static org.ridango.service.InputService.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int stationId = inputStationId();
        int numBuses = inputNumBuses();
        TimeFormat timeFormat = inputTimeFormat();

        scanner.close();

        ArrayList<DataSet> departureTimes = sortDataSetsByRouteId(findDepartureTimes(stationId));

        printData(departureTimes, timeFormat, numBuses);

    }
}