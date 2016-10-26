package com.kingwaytek.cpami.biking.app.model.items;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/9/7.
 */
public class ItemsPoiDetail {

    public String NAME;
    public String ADDRESS;
    public String DESCRIPTION;
    public ArrayList<String> PHOTO_PATH;
    public double LAT;
    public double LNG;

    public ItemsPoiDetail(String name, String address, String description, ArrayList<String> photoPath, double lat, double lng) {
        this.NAME = name;
        this.ADDRESS = address;
        this.DESCRIPTION = description;
        this.PHOTO_PATH = photoPath;
        this.LAT = lat;
        this.LNG = lng;
    }
}
