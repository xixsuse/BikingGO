package com.kingwaytek.cpami.biking.app.model.items;

/**
 * Created by vincent.chang on 2016/8/23.
 */
public class ItemsYouBike {

    public String NAME;
    public int TOTALS;
    public int AVAILABLE_BIKE;
    public int AVAILABLE_SPACE;
    public String AREA;
    public String ADDRESS;
    public double LAT;
    public double LNG;
    public String UPDATE_TIME;
    public int STATUS;

    public ItemsYouBike(String name, int totals, int availableBike, int availableSpace, String area, String address, double lat, double lng, String updateTime, int status) {
        this.NAME = name;
        this.TOTALS = totals;
        this.AVAILABLE_BIKE = availableBike;
        this.AVAILABLE_SPACE = availableSpace;
        this.AREA = area;
        this.ADDRESS = address;
        this.LAT = lat;
        this.LNG = lng;
        this.UPDATE_TIME = updateTime;
        this.STATUS = status;
    }
}
