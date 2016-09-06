package com.kingwaytek.cpami.bykingTablet.app.model.items;

/**
 * Created by vincent.chang on 2016/9/6.
 */
public class ItemsCitiesAndPOI {

    public String CITY_NAME;
    public int POI_ID;
    public String POI_NAME;

    public ItemsCitiesAndPOI(String cityName) {
        this.CITY_NAME = cityName;
    }

    public ItemsCitiesAndPOI(int poiId, String poiName) {
        this.POI_ID = poiId;
        this.POI_NAME = poiName;
    }
}
