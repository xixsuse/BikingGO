package com.kingwaytek.cpami.bykingTablet.app.model.items;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/7.
 */
public class ItemsPathList {

    public String DISTANCE;
    public String DURATION;
    public String START_NAME;
    public double START_LAT;
    public double START_LNG;
    public String END_NAME;
    public double END_LAT;
    public double END_LNG;
    public ArrayList<ItemsPathStep> PATH_STEPS;

    public ItemsPathList(String distance, String duration, String startName, double startLat, double startLng, String endName,
                         double endLat, double endLng, ArrayList<ItemsPathStep> pathSteps) {
        this.DISTANCE = distance;
        this.DURATION = duration;
        this.START_NAME = startName;
        this.START_LAT = startLat;
        this.START_LNG = startLng;
        this.END_NAME = endName;
        this.END_LAT = endLat;
        this.END_LNG = endLng;
        this.PATH_STEPS = pathSteps;
    }
}
