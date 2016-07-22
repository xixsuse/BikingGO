package com.kingwaytek.cpami.bykingTablet.app.model.items;

/**
 * Created by vincent.chang on 2016/7/22.
 */
public class ItemsTrackRecord {

    public String NAME;
    public int DIFFICULTY;
    public String DESCRIPTION;
    public String POLY_LINE;

    public ItemsTrackRecord(String name, int difficulty, String description, String polyLine) {
        this.NAME = name;
        this.DIFFICULTY = difficulty;
        this.DESCRIPTION = description;
        this.POLY_LINE = polyLine;
    }
}
