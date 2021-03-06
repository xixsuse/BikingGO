package com.kingwaytek.cpami.biking.app.model.items;

import java.io.Serializable;

/**
 * Created by vincent.chang on 2016/5/18.
 */
public class ItemsMyPOI implements Serializable {

    public String TITLE;
    public String ADDRESS;
    public String DESCRIPTION;
    public double LAT;
    public double LNG;
    public String PHOTO_PATH;

    public ItemsMyPOI(String title, String address, String desc, double lat, double lng, String photoPath) {
        this.TITLE = title;
        this.ADDRESS = address;
        this.DESCRIPTION = desc;
        this.LAT = lat;
        this.LNG = lng;
        this.PHOTO_PATH = photoPath;
    }
}
