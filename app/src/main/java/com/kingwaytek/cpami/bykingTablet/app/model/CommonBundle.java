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

    // Result Code
    int RESULT_DELETE = -2;

    String BUNDLE_MY_POI_INFO = "MyPoiInfo";
    String BUNDLE_MAP_TO_POI_INFO = "MapToPoiInfo";
    String BUNDLE_DELETE_POI = "DeleteMyPoi";
}
