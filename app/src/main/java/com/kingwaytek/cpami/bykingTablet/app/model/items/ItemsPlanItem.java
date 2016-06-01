package com.kingwaytek.cpami.bykingTablet.app.model.items;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vincent.chang on 2016/6/1.
 */
public class ItemsPlanItem {

    public String TITLE;
    public LatLng LOCATION;
    public int ORDER;

    public ItemsPlanItem(String title, LatLng latLng, int order) {
        this.TITLE = title;
        this.LOCATION = latLng;
        this.ORDER = order;
    }

    public void setOrder(int order) {
        this.ORDER = order;
    }
}
