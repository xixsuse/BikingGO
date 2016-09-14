package com.kingwaytek.cpami.bykingTablet.app.model.items;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/9/13.
 */
public class ItemsTransitOverview {

    public String FARE;
    public LatLng NORTH_EAST_LAT_LNG;
    public LatLng SOUTH_WEST_LAT_LNG;
    public String POLY_LINE;
    public String DISTANCE;
    public String DURATION;
    public ArrayList<ItemsTransitStep> TRANSIT_STEP_ITEM;

    public ItemsTransitOverview(String fare, LatLng northEastLatLng, LatLng southWestLatLng, String polyLine, String distance, String duration,
                                ArrayList<ItemsTransitStep> transitStepItem) {
        this.FARE = fare;
        this.NORTH_EAST_LAT_LNG = northEastLatLng;
        this.SOUTH_WEST_LAT_LNG = southWestLatLng;
        this.POLY_LINE = polyLine;
        this.DISTANCE = distance;
        this.DURATION = duration;
        this.TRANSIT_STEP_ITEM = transitStepItem;
    }
}
