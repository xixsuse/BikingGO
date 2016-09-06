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
    int REQUEST_RELOAD_ALL_MARKER = 35;
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
    String BUNDLE_PLAN_DIRECTION_JSON = "PlanDirectionJsonString";
    String BUNDLE_PLAN_STEP_START_NAME = "PlanStepStartName";
    String BUNDLE_PLAN_STEP_END_NAME = "PlanStepEndName";
    String BUNDLE_PLAN_STEP_DISTANCE = "PlanStepDistance";
    String BUNDLE_PLAN_STEP_DURATION = "PlanStepDuration";
    String BUNDLE_EVENT_DETAIL = "EventDetail";
    String BUNDLE_TRACK_INDEX = "TrackIndex";
    String BUNDLE_DIRECTION_MODE = "DirectionMode";
    String BUNDLE_FRAGMENT_DEFAULT_ARG = "FragmentDefaultArgument";
    String BUNDLE_SHARED_LIST_TYPE = "SharedListType";
    String BUNDLE_SHARED_ITEM_ID = "SharedItemID";
    String BUNDLE_SHARED_ITEM = "SharedItem";

    // Entry Type
    int ENTRY_TYPE_DEFAULT = 0;
    int ENTRY_TYPE_LOCATION_SELECT = 1;
    int ENTRY_TYPE_DIRECTIONS = 2;
    int ENTRY_TYPE_TRACKING = 3;
    int ENTRY_TYPE_TRACK_VIEWING = 4;
    int ENTRY_TYPE_VIEW_SHARED_PLAN = 5;
    int ENTRY_TYPE_VIEW_SHARED_TRACK = 6;

    // Direction Options
    String DIR_MODE_DRIVING = "driving";
    String DIR_MODE_WALKING = "walking";
    String DIR_MODE_BICYCLING = "bicycling";
    String DIR_MODE_TRANSIT = "transit";

    String DIR_AVOID_TOLLS = "tolls";
    String DIR_AVOID_HIGHWAYS = "highways";
    String DIR_AVOID_FERRIES = "ferries";
    String DIR_AVOID_INDOOR = "indoor";

    // Dialog Items Select Type
    int SELECT_TYPE_PHOTO = 1;
    int SELECT_TYPE_POSITION = 2;

    // Broadcast for Tracking
    String TRACKING_BROADCAST_FOR_ACTIVITY = "TrackingBroadcastForActivity";
    String TRACKING_BROADCAST_FOR_SERVICE = "TrackingBroadcastForService";
    String TRACKING_IS_GPS_LOCATED = "IsGpsLocated";
    String TRACKING_REQUEST_STARTING = "IsGpsStarting";
    String TRACKING_IS_DOING_RIGHT_NOW = "IsTrackingRightNow";

    // LruCache bitmap key
    String BITMAP_KEY_MY_POI = "MyPoi";
    String BITMAP_KEY_AROUND_POI = "AroundPoi";
    String BITMAP_KEY_BIKE_RENT_STATION = "BikeRentStation";
    String BITMAP_KEY_YOU_BIKE_NORMAL = "YouBikeNormal";
    String BITMAP_KEY_YOU_BIKE_FULL = "YouBikeFull";
    String BITMAP_KEY_YOU_BIKE_EMPTY = "YouBikeEmpty";
    String BITMAP_KEY_YOU_BIKE_OUT_OF_SERVICE = "YouBikeOutOfService";
    String BITMAP_KEY_SUPPLY_STATION = "SupplyStation";
    String BITMAP_KEY_PIN_PLACE = "PinPlace";
    String BITMAP_KEY_PIN_POINT = "PinPoint";

    // Other keys
    String MARKERS_YOU_BIKE_REFRESH = "YouBikeMarkersRefresh";

    // Post Keys & Values
    String POST_KEY_MODE = "mode";
    String POST_KEY_TYPE = "type";
    String POST_KEY_NAME = "name";
    String POST_KEY_CONTENT = "content";
    String POST_KEY_ID = "id";
    String POST_KEY_STAR = "star";
    String POST_KEY_CITY = "city";

    String POST_VALUE_MODE_UPLOAD = "upload";
    String POST_VALUE_MODE_DOWNLOAD = "download";
    String POST_VALUE_MODE_STAR = "star";
    String POST_VALUE_MODE_LIST = "list";
    String POST_VALUE_MODE_CITY_LIST = "bookcity";
    String POST_VALUE_MODE_CITY_POI = "booklist";
    String POST_VALUE_MODE_CITY_POI_DETAIL = "book";
    String POST_VALUE_TYPE_PLAN = "01";
    String POST_VALUE_TYPE_TRACK = "02";

    // Shared List Type
    int SHARED_LIST_TYPE_PLAN = 1;
    int SHARED_LIST_TYPE_TRACK = 2;
}
