package com.kingwaytek.cpami.bykingTablet.app.model;

/**
 * Created by vincent.chang on 2016/5/23.
 */
public interface CommonBundle {

    // Request Codes
    int REQUEST_PLACE_PICKER = 1;
    int REQUEST_PHOTO_FROM_GALLERY = 10;
    int REQUEST_PHOTO_FROM_CAMERA = 20;
    int REQUEST_RELOAD_MARKER = 30;
    int REQUEST_SELECT_LOCATION = 40;

    // Result Code
    int RESULT_DELETE = -2;

    // Bundle
    String BUNDLE_MY_POI_INFO = "MyPoiInfo";
    String BUNDLE_MAP_TO_POI_INFO = "MapToPoiInfo";
    String BUNDLE_DELETE_POI = "DeleteMyPoi";
    String BUNDLE_ENTRY_TYPE = "EntryType";
    String BUNDLE_LOCATION_TITLE = "LocationSelectTitle";
    String BUNDLE_LOCATION_LATLNG = "LocationSelectLatLng";
    String BUNDLE_PLAN_EDIT_INDEX = "PlanEditIndex";

    // Entry Type
    int ENTRY_TYPE_DEFAULT = 0;
    int ENTRY_TYPE_LOCATION_SELECT = 1;

    // Direction Options
    String DIR_MODE_DRIVING = "driving";
    String DIR_MODE_WALKING = "walking";
    String DIR_MODE_BICYCLING = "bicycling";
    String DIR_MODE_TRANSIT = "transit";

    String DIR_AVOID_TOLLS = "tolls";
    String DIR_AVOID_HIGHWAYS = "highways";
    String DIR_AVOID_FERRIES = "ferries";
    String DIR_AVOID_INDOOR = "indoor";
}
