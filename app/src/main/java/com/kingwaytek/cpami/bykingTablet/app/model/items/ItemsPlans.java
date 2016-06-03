package com.kingwaytek.cpami.bykingTablet.app.model.items;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/3.
 */
public class ItemsPlans {

    public String NAME;
    public ArrayList<ItemsPlanItem> PLAN_ITEMS;

    public ItemsPlans(String name, ArrayList<ItemsPlanItem> planItems) {
        this.NAME = name;
        this.PLAN_ITEMS = planItems;
    }
}
