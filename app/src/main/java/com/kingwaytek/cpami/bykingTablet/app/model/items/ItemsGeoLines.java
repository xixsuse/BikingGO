package com.kingwaytek.cpami.bykingTablet.app.model.items;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/8/8.
 */
public class ItemsGeoLines {

    public String NAME;
    public String DESCRIPTION;
    public String LOCATION;
    public ArrayList<LatLng> COORDINATES;

    public ItemsGeoLines(String name, String description, String location) {
        this.NAME = name;
        this.DESCRIPTION = description;
        this.LOCATION = location;
    }

    public ItemsGeoLines(ArrayList<LatLng> coordinates) {
        this.COORDINATES = coordinates;
    }
}
