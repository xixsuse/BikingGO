package com.sonavtek.sonav;

import android.os.Message;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.app.MapActivity;

/**
 * The event handler which process events for the map
 */
public class MapEventHandler extends EngineEventHandler {

    protected MapView mapView;
    protected sonav engine;
//    protected MapActivity mapactivity;

    /**
     * @param engine
     * @param mapView
     */
    public MapEventHandler(sonav engine, MapView mapView) {
        super(engine);
        this.engine = engine;
        this.mapView = mapView;
    }
    /**
     * Handles events or status received from native library. The native library
     * will call proc(int msg, int arg1, int arg2) method to pass events or
     * status.
     * 
     * @param msg
     *            instance of Message which contains information about the
     *            events or status.
     */
    @Override
    public void handleMessage(Message msg) {
        //Log.d(getClass().toString(), "handleMessage: " + msg);

        super.handleMessage(msg);

        switch (msg.what) {
            case EngineEvent.AM_PAINT: // 25550
                // Log.d(getClass().toString(), "AM_PAINT: " + gpsData);

                if (mapView != null) {
 //               	if(MapActivity.onConfigurationChanged == true ){
 //               		MapActivity.onConfigurationChanged =false;
                	
 //               		return;
 //               	}else{
//                	engine.getscr(mapView.precolors);
//                	mapView.isMapResizing = false;
                      mapView.postInvalidate();
                   	Log.i("MapHandler.java","MapActivity.pointOnMapMode="+MapActivity.pointOnMapMode);
                	Log.i("MapHandler.java","MapView.getPointOnMapFlag()="+mapView.getPointOnMapFlag());
                      if(MapActivity.pointOnMapMode == true && mapView.getPointOnMapFlag() == true ){
                    	  mapView.wakeup();
                      }
 //               	}
                }

                break;
            case EngineEvent.AM_TIP://25570
                   mapView.setSelectionPoint();
            	Log.i("MapHandler.java","mapView!=null="+String.valueOf(mapView!=null));
            	Log.i("MapHandler.java","engine!=null="+String.valueOf(engine!=null));
//            	if (null == engine) {
//					engine = eeego.getInstance();
//				}
            	//engine.setflagpoint(5, mapView.getMapXY()[0], mapView.getMapXY()[1]);
            	
                  
                break;
        }
    }

    /**
     * @return the mapView
     */
    public MapView getMapView() {
        return mapView;
    }

    /**
     * @param mapView
     *            the mapView to set
     */
    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }
    

}
