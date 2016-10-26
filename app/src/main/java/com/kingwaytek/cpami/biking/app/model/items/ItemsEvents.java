package com.kingwaytek.cpami.biking.app.model.items;

import java.io.Serializable;

/**
 * Created by vincent.chang on 2016/7/7.
 */
public class ItemsEvents implements Serializable {

    public String NAME;
    public String DESCRIPTION;
    public String LOCATION;
    public String ADDRESS;
    public String ORGANIZATION;
    public String START_TIME;
    public String END_TIME;
    public String WEBSITE;
    public String PIC1_URL;
    public String PIC1_NAME;
    public String PIC2_URL;
    public String PIC2_NAME;
    public String PIC3_URL;
    public String PIC3_NAME;
    public double LAT;
    public double LNG;
    public String TRAVEL_INFO;
    public String PARKING_INFO;

    public ItemsEvents(String name, String desc, String location, String address, String organization, String startTime, String endTime, String website,
                       String pic1Url, String pic1Name, String pic2Url, String pic2Name, String pic3Url, String pic3Name, double lat, double lng,
                       String travelInfo, String parkingInfo)
    {
        this.NAME = name;
        this.DESCRIPTION = desc;
        this.LOCATION = location;
        this.ADDRESS = address;
        this.ORGANIZATION = organization;
        this.START_TIME = startTime;
        this.END_TIME = endTime;
        this.WEBSITE = website;
        this.PIC1_URL = pic1Url;
        this.PIC1_NAME = pic1Name;
        this.PIC2_URL = pic2Url;
        this.PIC2_NAME = pic2Name;
        this.PIC3_URL = pic3Url;
        this.PIC3_NAME = pic3Name;
        this.LAT = lat;
        this.LNG = lng;
        this.TRAVEL_INFO = travelInfo;
        this.PARKING_INFO = parkingInfo;
    }
}
