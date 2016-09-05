package com.kingwaytek.cpami.bykingTablet.app.model.items;

/**
 * Created by vincent.chang on 2016/9/2.
 */
public class ItemsShared {

    public int ID;
    public String DATE;
    public String NAME;
    public int COUNT;

    public ItemsShared(int id, String date, String name, int count) {
        this.ID = id;
        this.DATE = date;
        this.NAME = name;
        this.COUNT = count;
    }
}
