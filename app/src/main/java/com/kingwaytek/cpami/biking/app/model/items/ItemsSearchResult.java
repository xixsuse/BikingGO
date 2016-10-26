package com.kingwaytek.cpami.biking.app.model.items;

/**
 * Created by Brack on 2016/1/24.
 */
public class ItemsSearchResult {

    public String NAME;
    public String ADMIN_AREA;
    public String COUNTRY_NAME;
    public String ADDRESS;
    public double LAT;
    public double LNG;

    public ItemsSearchResult(String name, String adminArea, String countryName, String address, double lat, double lng) {
        this.NAME = name;
        this.ADMIN_AREA = adminArea;
        this.COUNTRY_NAME = countryName;
        this.ADDRESS = address;
        this.LAT = lat;
        this.LNG = lng;
    }
}
