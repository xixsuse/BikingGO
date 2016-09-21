package com.kingwaytek.cpami.bykingTablet.app.model.items;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vincent.chang on 2016/9/13.
 */
public class ItemsTransitStep {

    public String DISTANCE;
    public int DURATION;
    public String POLY_LINE;
    public String INSTRUCTIONS;
    public LatLng START_LAT_LNG;
    public LatLng END_LAT_LNG;
    public String TRAVEL_MODE;
    public String TRANSIT_DEPARTURE_TIME;
    public String TRANSIT_ARRIVAL_TIME;
    public String TRANSIT_DEPARTURE_STOP;
    public String TRANSIT_ARRIVAL_STOP;
    public String TRANSIT_HEAD_SIGN;
    public int TRANSIT_HEAD_WAY;
    public String TRANSIT_SHORT_NAME;
    public String TRANSIT_VEHICLE_ICON_URL;
    public String TRANSIT_VEHICLE_TYPE;
    public int TRANSIT_NUM_STOPS;

    public ItemsTransitStep(String distance, int duration, String polyLine, String instructions, LatLng startLatLng, LatLng endLatLng,
                            String travelMode, String transitDepartureTime, String transitArrivalTime, String transitDepartureStop, String transitArrivalStop,
                            String transitHeadSign, int transitHeadWay, String transitShortName,
                            String transitVehicleIconUrl, String transitVehicleType, int transitNumStops)
    {
        this.DISTANCE = distance;
        this.DURATION = duration;
        this.POLY_LINE = polyLine;
        this.INSTRUCTIONS = instructions;
        this.START_LAT_LNG = startLatLng;
        this.END_LAT_LNG = endLatLng;
        this.TRAVEL_MODE = travelMode;
        this.TRANSIT_DEPARTURE_TIME = transitDepartureTime;
        this.TRANSIT_ARRIVAL_TIME = transitArrivalTime;
        this.TRANSIT_DEPARTURE_STOP = transitDepartureStop;
        this.TRANSIT_ARRIVAL_STOP = transitArrivalStop;
        this.TRANSIT_HEAD_SIGN = transitHeadSign;
        this.TRANSIT_HEAD_WAY = transitHeadWay;
        this.TRANSIT_SHORT_NAME = transitShortName;
        this.TRANSIT_VEHICLE_ICON_URL = transitVehicleIconUrl;
        this.TRANSIT_VEHICLE_TYPE = transitVehicleType;
        this.TRANSIT_NUM_STOPS = transitNumStops;
    }
}
