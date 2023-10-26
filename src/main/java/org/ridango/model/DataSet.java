package org.ridango.model;

import static org.ridango.service.Helper.findRouteId;

public class DataSet {
    public String tripId;
    public String departureTime;
    public int routeId;

    public DataSet(String tripId, String departureTime) {
        this.tripId = tripId;
        this.departureTime = departureTime;
        this.routeId = findRouteId("src/main/resources/gtfs/trips.txt", tripId);
    }
}
