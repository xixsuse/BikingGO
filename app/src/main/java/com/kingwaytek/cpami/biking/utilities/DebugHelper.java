package com.kingwaytek.cpami.biking.utilities;

/**
 * 各種 debug用的 flag都在這裡設！
 *
 * @author Vincent (2016/4/18)
 */
public class DebugHelper {

    private static final boolean DEBUG_OPEN = true;

    public static final boolean SHOW_NETWORK_RETRY = DEBUG_OPEN && true;

    /**
     * If set to true, the source of YouBike's data will using the official open data from Taipei and New Taipei city.
     *
     * If set to false, the source will come from our Biking service, it contains all the counties of YouBike data.
     */
    public static final boolean GET_YOU_BIKE_FROM_OPEN_DATA = false;

    public static final boolean LIMITED_MAP_LAYERS = false;

    public static final boolean SHOW_TUTORIAL_SCREEN = false;
}
