package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPointStyle;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.CommonBundle;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsGeoLines;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsYouBike;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>處理 layers疊加到 GoogleMap上的 Handler，
 *<p>
 * 需搭配：<br>
 * 1. HandlerThread (Using its looper.)<br>
 * 2. UI Handler (A new Handler created from UI thread)
 *</p>
 * {1} HandlerThread搭配 this Handler可單線程循序，並且執行緒安全地在背景作業，<br>
 * 背景作業完後需更新 UI時，再交給 {2} UI Handler。
 *
 * 2016/08/29 Updated:
 * YouBike markers 使用 AsyncTask來新增。
 *
 * @author Vincent (2016/7/27)
 */
public class MapLayerHandler extends Handler {

    private static final String TAG = "MapLayerHandler";

    public GeoJsonLayer layer_cyclingLine;
    public GeoJsonLayer layer_cyclingPoints;
    public GeoJsonLayer layer_topTen;
    public GeoJsonLayer layer_recommended;
    public GeoJsonLayer layer_allOfTaiwan;
    public GeoJsonLayer layer_rentStation;

    public static final int LAYER_CYCLING = 100;
    public static final int LAYER_TOP_TEN = 200;
    public static final int LAYER_RECOMMENDED = 300;
    public static final int LAYER_ALL_OF_TAIWAN = 400;
    public static final int LAYER_RENT_STATION = 500;
    private static final int LAYER_GET_PROPERTIES = 600;
    public static final int LAYER_YOU_BIKE = 700;

    private static final String PROP_NAME = "Name";

    private Handler uiHandler;
    private OnLayerChangedCallback layerChangedCallback;

    ArrayList<Polyline> polyLineCyclingList;
    ArrayList<Polyline> polyLineTopTenList;
    ArrayList<Polyline> polyLineRecommendList;
    ArrayList<Polyline> polyLineTaiwanList;

    private Context context;

    private ArrayList<Marker> markerYouBikeList;

    private BitmapDescriptor you_bike_icon_normal;
    private BitmapDescriptor you_bike_icon_empty;
    private BitmapDescriptor you_bike_icon_full;
    private BitmapDescriptor you_bike_icon_unavailable;
    private final int YOU_BIKE_ICON_CODE_NORMAL = 0;
    private final int YOU_BIKE_ICON_CODE_EMPTY = 1;
    private final int YOU_BIKE_ICON_CODE_FULL = 2;
    private final int YOU_BIKE_ICON_CODE_UNAVAILABLE = 3;

    private boolean isLayerChanging;

    interface OnLayerChangedCallback {
        void onPolylinePrepared(int layerCode, PolylineOptions polyLine);
        void onPolylineClick(String name, String location, String description);
        void onLayerAdded(int layerCode);
        void onLayersAllGone();
    }

    MapLayerHandler(Looper looper, Handler uiHandler, OnLayerChangedCallback layerChangedCallback) {
        super(looper);
        this.uiHandler = uiHandler;
        this.layerChangedCallback = layerChangedCallback;
    }

    private Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    void addLayer(final GoogleMap map, @Nullable final Bitmap markerBitmap, final int layerCode) {
        if (isLayerChanging)
            return;

        setIsLayerChanging(true);

        this.post(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (layerCode) {
                        case LAYER_CYCLING:
                            if (layer_cyclingPoints == null)
                                layer_cyclingPoints = new GeoJsonLayer(map, R.raw.layer_cycling_route_point, appContext());

                            BitmapDescriptor supplyPointsIcon = BitmapDescriptorFactory.fromBitmap(markerBitmap);

                            for (GeoJsonFeature feature : layer_cyclingPoints.getFeatures()) {
                                GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();
                                pointStyle.setIcon(supplyPointsIcon);
                                pointStyle.setTitle(feature.getProperty(PROP_NAME));
                                feature.setPointStyle(pointStyle);
                            }

                            JsonParser.parseGeoJsonCoordinates(R.raw.layer_cycling_route_line, true, new JsonParser.GeoJsonParseResult() {
                                @Override
                                public void onParseFinished(ArrayList<ItemsGeoLines> geoLines) {
                                    polyLineCyclingList = new ArrayList<>();

                                    for (int i = 0; i < geoLines.size(); i++) {
                                        PolylineOptions polyLine = new PolylineOptions();

                                        ArrayList<LatLng> latLngList = geoLines.get(i).COORDINATES;
                                        for (int j = 0; j < latLngList.size(); j++) {
                                            polyLine.add(latLngList.get(j));
                                        }
                                        polyLine.color(ContextCompat.getColor(appContext(), R.color.polyline_cycling));
                                        polyLine.width(16);
                                        polyLine.clickable(true);
                                        polyLine.zIndex(LAYER_CYCLING + i);

                                        //obtainMessage(layerCode, polyLine).sendToTarget();
                                        layerChangedCallback.onPolylinePrepared(LAYER_CYCLING, polyLine);
                                    }
                                }

                                @Override
                                public void onParseFail(String errorMessage) {
                                    Utility.showToastOnNewThread(errorMessage);
                                }
                            });

                            break;

                        case LAYER_TOP_TEN:
                            JsonParser.parseGeoJsonCoordinates(R.raw.layer_top10, true, new JsonParser.GeoJsonParseResult() {
                                @Override
                                public void onParseFinished(ArrayList<ItemsGeoLines> geoLines) {
                                    polyLineTopTenList = new ArrayList<>();

                                    for (int i = 0; i < geoLines.size(); i++) {
                                        PolylineOptions polyLine = new PolylineOptions();

                                        ArrayList<LatLng> latLngList = geoLines.get(i).COORDINATES;
                                        for (int j = 0; j < latLngList.size(); j++) {
                                            polyLine.add(latLngList.get(j));
                                        }
                                        polyLine.color(ContextCompat.getColor(appContext(), R.color.polyline_top_ten));
                                        polyLine.width(16);
                                        polyLine.clickable(true);
                                        polyLine.zIndex(LAYER_TOP_TEN + i);

                                        layerChangedCallback.onPolylinePrepared(LAYER_TOP_TEN, polyLine);
                                    }
                                }

                                @Override
                                public void onParseFail(String errorMessage) {
                                    Utility.showToastOnNewThread(errorMessage);
                                }
                            });

                            break;

                        case LAYER_RECOMMENDED:
                            JsonParser.parseGeoJsonCoordinates(R.raw.layer_recommend, true, new JsonParser.GeoJsonParseResult() {
                                @Override
                                public void onParseFinished(ArrayList<ItemsGeoLines> geoLines) {
                                    polyLineRecommendList = new ArrayList<>();

                                    for (int i = 0; i < geoLines.size(); i++) {
                                        PolylineOptions polyLine = new PolylineOptions();

                                        ArrayList<LatLng> latLngList = geoLines.get(i).COORDINATES;
                                        for (int j = 0; j < latLngList.size(); j++) {
                                            polyLine.add(latLngList.get(j));
                                        }
                                        polyLine.color(ContextCompat.getColor(appContext(), R.color.polyline_recommended));
                                        polyLine.width(16);
                                        polyLine.clickable(true);
                                        polyLine.zIndex(LAYER_RECOMMENDED + i);

                                        layerChangedCallback.onPolylinePrepared(LAYER_RECOMMENDED, polyLine);
                                    }
                                }

                                @Override
                                public void onParseFail(String errorMessage) {
                                    Utility.showToastOnNewThread(errorMessage);
                                }
                            });

                            break;

                        case LAYER_ALL_OF_TAIWAN:
                            JsonParser.parseGeoJsonCoordinates(R.raw.layer_biking_route_taiwan, false, new JsonParser.GeoJsonParseResult() {
                                @Override
                                public void onParseFinished(ArrayList<ItemsGeoLines> geoLines) {
                                    polyLineTaiwanList = new ArrayList<>();

                                    for (int i = 0; i < geoLines.size(); i++) {
                                        PolylineOptions polyLine = new PolylineOptions();

                                        ArrayList<LatLng> latLngList = geoLines.get(i).COORDINATES;
                                        for (int j = 0; j < latLngList.size(); j++) {
                                            polyLine.add(latLngList.get(j));
                                        }
                                        polyLine.color(ContextCompat.getColor(appContext(), R.color.polyline_all_of_taiwan));
                                        polyLine.width(15);
                                        polyLine.clickable(true);
                                        polyLine.zIndex(LAYER_ALL_OF_TAIWAN + i);

                                        layerChangedCallback.onPolylinePrepared(LAYER_ALL_OF_TAIWAN, polyLine);
                                    }
                                }

                                @Override
                                public void onParseFail(String errorMessage) {
                                    Utility.showToastOnNewThread(errorMessage);
                                }
                            });

                            break;

                        case LAYER_RENT_STATION:
                            if (layer_rentStation == null)
                                layer_rentStation = new GeoJsonLayer(map, R.raw.layer_station, appContext());

                            BitmapDescriptor rentStationIcon = BitmapDescriptorFactory.fromBitmap(markerBitmap);

                            for (GeoJsonFeature feature : layer_rentStation.getFeatures()) {
                                GeoJsonPointStyle rentPointStyle = new GeoJsonPointStyle();
                                rentPointStyle.setIcon(rentStationIcon);
                                rentPointStyle.setTitle(feature.getProperty(PROP_NAME));
                                feature.setPointStyle(rentPointStyle);
                            }
                            break;
                    }
                    sendEmptyMessage(layerCode);
                }
                catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void getLayerProperties(final int geoJsonData, final int zIndex) {
        this.post(new Runnable() {
            @Override
            public void run() {
                JsonParser.parseGeoJsonProperty(geoJsonData, new JsonParser.GeoJsonParseResult() {
                    @Override
                    public void onParseFinished(ArrayList<ItemsGeoLines> geoLines) {
                        ItemsGeoLines geoItem = geoLines.get(zIndex);
                        obtainMessage(LAYER_GET_PROPERTIES, geoItem).sendToTarget();
                    }

                    @Override
                    public void onParseFail(String errorMessage) {
                        Utility.showToastOnNewThread(errorMessage);
                        Log.e(TAG, errorMessage);
                    }
                });
            }
        });
    }

    @Override
    public void handleMessage(final Message msg) {
        final int layerCode = msg.what;

        final ItemsGeoLines geoItem = (ItemsGeoLines) msg.obj;

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (layerCode) {
                    case LAYER_CYCLING:
                        layer_cyclingPoints.addLayerToMap();

                        Log.i(TAG, "LayerCycling Added!!");

                        break;

                    case LAYER_TOP_TEN:

                        break;

                    case LAYER_RECOMMENDED:

                        break;

                    case LAYER_ALL_OF_TAIWAN:

                        break;

                    case LAYER_RENT_STATION:
                        layer_rentStation.addLayerToMap();
                        break;

                    case LAYER_GET_PROPERTIES:
                        if (geoItem != null)
                            layerChangedCallback.onPolylineClick(geoItem.NAME, geoItem.LOCATION, geoItem.DESCRIPTION);

                        break;
                }
                setIsLayerChanging(false);
                layerChangedCallback.onLayerAdded(layerCode);
            }
        });
    }

    void removeLayer(int layerCode) {
        switch (layerCode) {
            case LAYER_CYCLING:
                if ( layer_cyclingPoints != null) {
                    layer_cyclingPoints.removeLayerFromMap();
                    layer_cyclingPoints = null;
                }

                if (polyLineCyclingList != null) {
                    for (Polyline polyLine : polyLineCyclingList) {
                        polyLine.remove();
                    }
                    polyLineCyclingList.clear();
                    polyLineCyclingList = null;
                }
                break;

            case LAYER_TOP_TEN:
                if (polyLineTopTenList != null) {
                    for (Polyline polyLine : polyLineTopTenList) {
                        polyLine.remove();
                    }
                    polyLineTopTenList.clear();
                    polyLineTopTenList = null;
                }
                break;

            case LAYER_RECOMMENDED:
                if (polyLineRecommendList != null) {
                    for (Polyline polyline : polyLineRecommendList) {
                        polyline.remove();
                    }
                    polyLineRecommendList.clear();
                    polyLineRecommendList = null;
                }
                break;

            case LAYER_ALL_OF_TAIWAN:
                if (polyLineTaiwanList != null) {
                    for (Polyline polyline : polyLineTaiwanList) {
                        polyline.remove();
                    }
                    polyLineTaiwanList.clear();
                    polyLineTaiwanList = null;
                }
                break;

            case LAYER_RENT_STATION:
                if (layer_rentStation != null) {
                    layer_rentStation.removeLayerFromMap();
                    layer_rentStation = null;
                }
                break;

            case LAYER_YOU_BIKE:
                if (markerYouBikeList != null) {
                    for (Marker marker : markerYouBikeList) {
                        marker.remove();
                    }
                    markerYouBikeList.clear();
                    markerYouBikeList = null;

                    context = null;
                    you_bike_icon_normal = null;
                    you_bike_icon_empty = null;
                    you_bike_icon_full = null;
                    you_bike_icon_unavailable = null;
                }
                break;
        }
        if (!isLayerChanging)
            checkAreAllLayerRemoved();
    }

    private void checkAreAllLayerRemoved() {
        if (polyLineCyclingList == null && layer_cyclingPoints == null
                && polyLineTopTenList == null && polyLineRecommendList == null
                && polyLineTaiwanList == null && layer_rentStation == null && markerYouBikeList == null)
        {
            layerChangedCallback.onLayersAllGone();
        }
        System.gc();
    }

    void setIsLayerChanging(boolean isChanging) {
        isLayerChanging = isChanging;
    }

    boolean isLayerChanging() {
        return isLayerChanging;
    }

    class YouBikeMarkerAddTask extends AsyncTask<List<ItemsYouBike>, MarkerOptions, Void> {

        private GoogleMap map;

        YouBikeMarkerAddTask(Context context, GoogleMap map) {
            MapLayerHandler.this.context = context;
            this.map = map;
        }

        @Override
        protected void onPreExecute() {
            markerYouBikeList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(List<ItemsYouBike>... items) {

            for (ItemsYouBike youBikeItem : items[0]) {
                MarkerOptions marker = new MarkerOptions();
                marker.title(youBikeItem.NAME);
                marker.snippet(getYouBikeSnippet(youBikeItem));
                marker.position(new LatLng(youBikeItem.LAT, youBikeItem.LNG));

                switch (getIconCode(youBikeItem)) {
                    case YOU_BIKE_ICON_CODE_NORMAL:
                        marker.icon(you_bike_icon_normal);
                        break;

                    case YOU_BIKE_ICON_CODE_EMPTY:
                        marker.icon(you_bike_icon_empty);
                        break;

                    case YOU_BIKE_ICON_CODE_FULL:
                        marker.icon(you_bike_icon_full);
                        break;

                    case YOU_BIKE_ICON_CODE_UNAVAILABLE:
                        marker.icon(you_bike_icon_unavailable);
                        marker.snippet(appContext().getString(R.string.you_bike_service_unavailable));
                        break;
                }
                publishProgress(marker);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(MarkerOptions... markers) {
            Marker marker = map.addMarker(markers[0]);
            markerYouBikeList.add(marker);
            ((BaseMapActivity) context).setMarkerTypeMap(marker.getPosition().latitude, marker.getPosition().longitude, R.drawable.ic_marker_you_bike_normal);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setIsLayerChanging(false);
            layerChangedCallback.onLayerAdded(LAYER_YOU_BIKE);
        }
    }

    private int getIconCode(ItemsYouBike youBikeItem) {
        if (youBikeItem.STATUS != 1) {
            if (you_bike_icon_unavailable == null) {
                ((BaseActivity) context).checkBitmapCache(CommonBundle.BITMAP_KEY_YOU_BIKE_OUT_OF_SERVICE);
                you_bike_icon_unavailable = BitmapDescriptorFactory.fromBitmap(((BaseActivity) context).getBitmapFromMemCache(CommonBundle.BITMAP_KEY_YOU_BIKE_OUT_OF_SERVICE));
            }
            return YOU_BIKE_ICON_CODE_UNAVAILABLE;
        }
        else if (youBikeItem.AVAILABLE_BIKE == 0) {
            if (you_bike_icon_empty == null) {
                ((BaseActivity) context).checkBitmapCache(CommonBundle.BITMAP_KEY_YOU_BIKE_EMPTY);
                you_bike_icon_empty = BitmapDescriptorFactory.fromBitmap(((BaseActivity) context).getBitmapFromMemCache(CommonBundle.BITMAP_KEY_YOU_BIKE_EMPTY));
            }
            return YOU_BIKE_ICON_CODE_EMPTY;
        }
        else if (youBikeItem.AVAILABLE_SPACE == 0) {
            if (you_bike_icon_full == null) {
                ((BaseActivity) context).checkBitmapCache(CommonBundle.BITMAP_KEY_YOU_BIKE_FULL);
                you_bike_icon_full = BitmapDescriptorFactory.fromBitmap(((BaseActivity) context).getBitmapFromMemCache(CommonBundle.BITMAP_KEY_YOU_BIKE_FULL));
            }
            return YOU_BIKE_ICON_CODE_FULL;
        }
        else {
            if (you_bike_icon_normal == null) {
                ((BaseActivity) context).checkBitmapCache(CommonBundle.BITMAP_KEY_YOU_BIKE_NORMAL);
                you_bike_icon_normal = BitmapDescriptorFactory.fromBitmap(((BaseActivity) context).getBitmapFromMemCache(CommonBundle.BITMAP_KEY_YOU_BIKE_NORMAL));
            }
            return YOU_BIKE_ICON_CODE_NORMAL;
        }
    }

    private String getYouBikeSnippet(ItemsYouBike youBikeItem) {
        return appContext().getString(R.string.you_bike_location, youBikeItem.ADDRESS) +
                appContext().getString(R.string.you_bike_available_bike, youBikeItem.AVAILABLE_BIKE) +
                appContext().getString(R.string.you_bike_available_space, youBikeItem.AVAILABLE_SPACE) +
                appContext().getString(R.string.you_bike_update_time, youBikeItem.UPDATE_TIME);
    }

    void refreshAllYouBikeMarkers(final ArrayList<ItemsYouBike> youBikeItems) {
        if (markerYouBikeList != null) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < markerYouBikeList.size(); i++) {
                        markerYouBikeList.get(i).setSnippet(getYouBikeSnippet(youBikeItems.get(i)));

                        switch(getIconCode(youBikeItems.get(i))) {
                            case YOU_BIKE_ICON_CODE_NORMAL:
                                markerYouBikeList.get(i).setIcon(you_bike_icon_normal);
                                break;

                            case YOU_BIKE_ICON_CODE_EMPTY:
                                markerYouBikeList.get(i).setIcon(you_bike_icon_empty);
                                break;

                            case YOU_BIKE_ICON_CODE_FULL:
                                markerYouBikeList.get(i).setIcon(you_bike_icon_full);
                                break;

                            case YOU_BIKE_ICON_CODE_UNAVAILABLE:
                                markerYouBikeList.get(i).setIcon(you_bike_icon_unavailable);
                                markerYouBikeList.get(i).setSnippet(appContext().getString(R.string.you_bike_service_unavailable));
                                break;
                        }

                        if (markerYouBikeList.get(i).isInfoWindowShown())
                            markerYouBikeList.get(i).showInfoWindow();
                    }
                    setIsLayerChanging(false);
                    layerChangedCallback.onLayerAdded(LAYER_YOU_BIKE);
                }
            });
        }
    }

    boolean isYouBikeMarkerAdded() {
        return markerYouBikeList != null;
    }
}
