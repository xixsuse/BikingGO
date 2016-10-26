package com.kingwaytek.cpami.biking.app.model.items;

/**
 * Created by vincent.chang on 2016/10/14.
 */

public class ItemsPlanPreview {

    public String NAME;
    public String DATE;
    public int SPOT_COUNTS;

    public ItemsPlanPreview(String name, String date, int spotCounts) {
        this.NAME = name;
        this.DATE = date;
        this.SPOT_COUNTS = spotCounts;
    }
}
