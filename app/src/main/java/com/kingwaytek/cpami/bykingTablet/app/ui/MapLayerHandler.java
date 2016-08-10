package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPointStyle;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsGeoLines;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

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
    private static final int LAYER_PROPERTIES = 600;

    private static final String PROP_NAME = "Name";

    private Handler uiHandler;
    private OnLayerChangedCallback layerChangedCallback;

    public ArrayList<Polyline> polyLineCyclingList;
    public ArrayList<Polyline> polyLineTopTenList;
    public ArrayList<Polyline> polyLineRecommendList;
    public ArrayList<Polyline> polyLineTaiwanList;

    private boolean isLayerChanging;

    public interface OnLayerChangedCallback {
        void onPolylinePrepared(int layerCode, PolylineOptions polyLine);
        void onPolylineClick(String name, String location, String description);
        void onLayerAdded(int layerCode);
        void onLayersAllGone();
    }

    public MapLayerHandler(Looper looper, Handler uiHandler, OnLayerChangedCallback layerChangedCallback) {
        super(looper);
        this.uiHandler = uiHandler;
        this.layerChangedCallback = layerChangedCallback;
    }

    private Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    public void addLayer(final GoogleMap map, final int layerCode) {
        this.post(new Runnable() {
            @Override
            public void run() {
                isLayerChanging = true;

                try {
                    switch (layerCode) {
                        case LAYER_CYCLING:
                            if (layer_cyclingPoints == null)
                                layer_cyclingPoints = new GeoJsonLayer(map, R.raw.layer_cycling_route_point, appContext());

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
                                        polyLine.color(ContextCompat.getColor(appContext(), R.color.md_brown_700));
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
                                        polyLine.color(ContextCompat.getColor(appContext(), R.color.md_deep_orange_900));
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
                                        polyLine.color(ContextCompat.getColor(appContext(), R.color.md_deep_purple_A400));
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
                                        polyLine.color(ContextCompat.getColor(appContext(), R.color.md_black_1000));
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

                            for (GeoJsonFeature feature : layer_rentStation.getFeatures()) {
                                GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();
                                pointStyle.setTitle(feature.getProperty(PROP_NAME));
                                feature.setPointStyle(pointStyle);
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

    public void getLayerProperties(final int geoJsonData, final int zIndex) {
        this.post(new Runnable() {
            @Override
            public void run() {
                JsonParser.parseGeoJsonProperty(geoJsonData, new JsonParser.GeoJsonParseResult() {
                    @Override
                    public void onParseFinished(ArrayList<ItemsGeoLines> geoLines) {
                        ItemsGeoLines geoItem = geoLines.get(zIndex);
                        obtainMessage(LAYER_PROPERTIES, geoItem).sendToTarget();
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
                        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

                        for (GeoJsonFeature feature : layer_cyclingPoints.getFeatures()) {
                            GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();
                            pointStyle.setTitle(feature.getProperty(PROP_NAME));
                            pointStyle.setIcon(icon);

                            feature.setPointStyle(pointStyle);
                        }

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

                    case LAYER_PROPERTIES:
                        if (geoItem != null) {
                            layerChangedCallback.onPolylineClick(geoItem.NAME, geoItem.LOCATION, geoItem.DESCRIPTION);
                        }
                        break;
                }
                isLayerChanging = false;
                layerChangedCallback.onLayerAdded(layerCode);
            }
        });
    }

    public void removeLayer(int layerCode) {
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
        }
        if (!isLayerChanging)
            checkAreAllLayerRemoved();
    }

    private void checkAreAllLayerRemoved() {
        if (polyLineCyclingList == null && layer_cyclingPoints == null
                && polyLineTopTenList == null && polyLineRecommendList == null
                && polyLineTaiwanList == null && layer_rentStation == null)
        {
            layerChangedCallback.onLayersAllGone();
        }
        System.gc();
    }
}
