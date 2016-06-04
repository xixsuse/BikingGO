package com.kingwaytek.cpami.bykingTablet.app.model.items;

/**
 * Created by vincent.chang on 2016/6/1.
 */
public class ItemsPlanItem {

    public String TITLE;
    public double LAT;
    public double LNG;

    public ItemsPlanItem(String title, double lat, double lng) {
        this.TITLE = title;
        this.LAT = lat;
        this.LNG = lng;
    }
}
