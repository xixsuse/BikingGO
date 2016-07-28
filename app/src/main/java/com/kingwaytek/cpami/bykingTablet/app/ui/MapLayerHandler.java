package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPointStyle;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by vincent.chang on 2016/7/27.
 */
public class MapLayerHandler extends Handler {

    private static final String TAG = "MapLayerHandler";

    private GeoJsonLayer layer_cyclingLine;
    private GeoJsonLayer layer_cyclingPoints;
    private GeoJsonLayer layer_topTen;
    private GeoJsonLayer layer_recommended;
    private GeoJsonLayer layer_allOfTaiwan;
    private GeoJsonLayer layer_rentStation;

    public static final int LAYER_CYCLING = 1;
    public static final int LAYER_TOP_TEN = 2;
    public static final int LAYER_RECOMMENDED = 3;
    public static final int LAYER_ALL_OF_TAIWAN = 4;
    public static final int LAYER_RENT_STATION = 5;

    private static final String PROP_NAME = "Name";

    private Handler uiHandler;
    private OnLayerChangedCallback layerChangedCallback;

    public interface OnLayerChangedCallback {
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
                try {
                    switch (layerCode) {
                        case LAYER_CYCLING:
                            if (layer_cyclingLine == null)
                                layer_cyclingLine = new GeoJsonLayer(map, R.raw.layer_cycling_route_line, appContext());

                            if (layer_cyclingPoints == null)
                                layer_cyclingPoints = new GeoJsonLayer(map, R.raw.layer_cycling_route_point, appContext());

                            layer_cyclingLine.getDefaultLineStringStyle().setColor(ContextCompat.getColor(appContext(), R.color.md_brown_800));
                            layer_cyclingLine.getDefaultLineStringStyle().setWidth(18);

                            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

                            for (GeoJsonFeature feature : layer_cyclingPoints.getFeatures()) {
                                GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();
                                pointStyle.setTitle(feature.getProperty(PROP_NAME));
                                pointStyle.setIcon(icon);

                                feature.setPointStyle(pointStyle);
                            }

                            break;

                        case LAYER_TOP_TEN:
                            if (layer_topTen == null)
                                layer_topTen = new GeoJsonLayer(map, R.raw.layer_top10, appContext());

                            break;

                        case LAYER_RECOMMENDED:
                            if (layer_recommended == null)
                                layer_recommended = new GeoJsonLayer(map, R.raw.layer_recommend, appContext());

                            break;

                        case LAYER_ALL_OF_TAIWAN:
                            if (layer_allOfTaiwan == null)
                                layer_allOfTaiwan = new GeoJsonLayer(map, R.raw.layer_biking_route_taiwan, appContext());

                            break;

                        case LAYER_RENT_STATION:
                            if (layer_rentStation == null)
                                layer_rentStation = new GeoJsonLayer(map, R.raw.layer_station, appContext());

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

    @Override
    public void handleMessage(Message msg) {
        final int layerCode = msg.what;

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (layerCode) {
                    case LAYER_CYCLING:
                        layer_cyclingLine.addLayerToMap();
                        layer_cyclingPoints.addLayerToMap();
                        break;

                    case LAYER_TOP_TEN:
                        layer_topTen.addLayerToMap();
                        break;

                    case LAYER_RECOMMENDED:
                        layer_recommended.addLayerToMap();
                        break;

                    case LAYER_ALL_OF_TAIWAN:
                        layer_allOfTaiwan.addLayerToMap();
                        break;

                    case LAYER_RENT_STATION:
                        layer_rentStation.addLayerToMap();
                        break;
                }
                layerChangedCallback.onLayerAdded(layerCode);
            }
        });
    }

    public void removeLayer(int layerCode) {
        switch (layerCode) {
            case LAYER_CYCLING:
                if (layer_cyclingLine != null && layer_cyclingPoints != null) {
                    layer_cyclingLine.removeLayerFromMap();
                    layer_cyclingPoints.removeLayerFromMap();
                    layer_cyclingLine = null;
                    layer_cyclingPoints = null;
                }
                break;

            case LAYER_TOP_TEN:
                if (layer_topTen != null) {
                    layer_topTen.removeLayerFromMap();
                    layer_topTen = null;
                }
                break;

            case LAYER_RECOMMENDED:
                if (layer_recommended != null) {
                    layer_recommended.removeLayerFromMap();
                    layer_recommended = null;
                }
                break;

            case LAYER_ALL_OF_TAIWAN:
                if (layer_allOfTaiwan != null) {
                    layer_allOfTaiwan.removeLayerFromMap();
                    layer_allOfTaiwan = null;
                }
                break;

            case LAYER_RENT_STATION:
                if (layer_rentStation != null) {
                    layer_rentStation.removeLayerFromMap();
                    layer_rentStation = null;
                }
                break;
        }
        checkIsAllLayerRemoved();
    }

    private void checkIsAllLayerRemoved() {
        if (layer_cyclingLine == null && layer_cyclingPoints == null
                && layer_topTen == null && layer_recommended == null
                && layer_allOfTaiwan == null && layer_rentStation == null)
        {
            layerChangedCallback.onLayersAllGone();
        }
        System.gc();
    }
}
