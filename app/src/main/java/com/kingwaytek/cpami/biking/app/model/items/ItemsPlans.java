package com.kingwaytek.cpami.biking.app.model.items;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/3.
 */
public class ItemsPlans {

    public String NAME;
    public String DATE;
    public ArrayList<ItemsPlanItem> PLAN_ITEMS;

    public ItemsPlans(String name, String date, ArrayList<ItemsPlanItem> planItems) {
        this.NAME = name;
        this.DATE = date;
        this.PLAN_ITEMS = planItems;
    }
}
