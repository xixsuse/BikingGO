package com.kingwaytek.cpami.bykingTablet.app.model.items;

/**
 * Created by vincent.chang on 2016/7/22.
 */
public class ItemsTrackRecord {

    public String TIME;
    public String NAME;
    public int DIFFICULTY;
    public String DESCRIPTION;
    public String POLY_LINE;
    public String DISTANCE;

    public ItemsTrackRecord(String time, String name, int difficulty, String description, String polyLine, String distance) {
        this.TIME = time;
        this.NAME = name;
        this.DIFFICULTY = difficulty;
        this.DESCRIPTION = description;
        this.POLY_LINE = polyLine;
        this.DISTANCE = distance;
    }

    public ItemsTrackRecord(String time, String name, int difficulty, String distance) {
        this.TIME = time;
        this.NAME = name;
        this.DIFFICULTY = difficulty;
        this.DISTANCE = distance;
    }
}
