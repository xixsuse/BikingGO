package com.kingwaytek.cpami.bykingTablet.app.model.items;

/**
 * Created by vincent.chang on 2016/6/7.
 */
public class ItemsPathStep {

    public String DISTANCE;
    public String DURATION;
    public String INSTRUCTIONS;
    public String GO_ON_PATH;
    public String POLY_LINE;
    public double START_LAT;
    public double START_LNG;
    //public double END_LAT;
    //public double END_LNG;

    public ItemsPathStep(String distance, String instructions, String goOnPath, String polyLine, double startLat, double startLng) {
        this.DISTANCE = distance;
        this.INSTRUCTIONS = instructions;
        this.GO_ON_PATH = goOnPath;
        this.POLY_LINE = polyLine;
        this.START_LAT = startLat;
        this.START_LNG = startLng;
    }

    public ItemsPathStep(String distance, String duration, String instructions, String goOnPath, String polyLine, double startLat, double startLng) {
        this.DISTANCE = distance;
        this.DURATION = duration;
        this.INSTRUCTIONS = instructions;
        this.GO_ON_PATH = goOnPath;
        this.POLY_LINE = polyLine;
        this.START_LAT = startLat;
        this.START_LNG = startLng;
    }
}
