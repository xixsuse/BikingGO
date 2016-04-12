package com.sonavtek.sonav;

import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.MapActivity;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.maps.IMapView;

public class SMSMapView extends View implements IMapView{
	
	/** Flag for always using day style */
	public static final int STYLE_DAY = 0;

	/** Flag for always using night style */
	public static final int STYLE_NIGHT = 1;

	/** Flag for switching day and night styles according to the system time */
	public static final int STYLE_AUTO = 2;

	/** Type of map view is 3D (for navigation only) */
	public static final int VIEW_3D = 0;

	/**
	 * Type of map view is 2D and user's direction is always face to the top of
	 * map
	 */
	public static final int VIEW_2D_FIX_DIRECTION = 1;

	/** Type of map view is 2D */
	public static final int VIEW_2D = 2;

	/** Type of map view is 專業速度表 */
	public static final int VIEW_SPEED = 3;

	/** Type of map view is 3D/2D */
	public static final int VIEW_3D_2D = 4;

	/** Type of map view is 2D/3D */
	public static final int VIEW_2D_3D = 5;

	/** Flag to hide the routing path created manually */
	public static final int HIDE_MANUAL_ROUTE = 0;

	/** Flag to show the routing path created manually */
	public static final int SHOW_MANUAL_ROUTE = 1;

	/** Minimum zoom level */
	public static final int MIM_ZOOM_LEVEL = 0;

	/** Maximum zoom level */
	public static final int MAX_ZOOM_LEVEL = 17;

	/** The state of the view is on navigation mode. */
	public static final int STATE_NAVI = 0;

	/** The state of the view is playing navigation emulator. */
	public static final int STATE_EMU = 1;

	/** The state of the view is normal. */
	public static final int STATE_MAP = 2;

	/** The state of the view is playing navigation emulator but paused. */
	public static final int STATE_EMU_PAUSE = 3;

	/** The state of the view is on navigation mode but paused. */
	public static final int STATE_NAVI_PAUSE = 4;

	/** The type of point is the starting point of route */
	public static final int ROUTE_START_POINT = 0;

	/** The type of point is the first essential point to pass through */
	public static final int ROUTE_1ST_ESSENTIAL_POINT = 1;

	/** The type of point is the second essential point to pass through */
	public static final int ROUTE_2ND_ESSENTIAL_POINT = 2;

	/** The type of point is the destination of route */
	public static final int ROUTE_DESTINATION_POINT = 3;

	/** The type of point is the user's location */
	public static final int USER_LOCATION_POINT = 4;

	/** The type of point is the selected point */
	public static final int SELECTED_POINT = 5;

	/** The touch event type of map is touch down */
	public static final int TOUCH_DOWN = 1;

	/** The touch event type of map is touch moving */
	public static final int TOUCH_MOVE = 2;

	/** The touch event type of map is touch up */
	public static final int TOUCH_UP = 3;

	private Paint paint;
	private sonav engine; // the engine provides methods for drawing map
	private int viewType;
	private boolean userLocationVisible;
	private GeoPoint userLocation;
	private boolean choosePointMode; // flag of for user to choose a point
	private static double[] position;
	private boolean PointOnMapFlag;
//	private GestureDetector ges;
	
	/*
	 * @author yawhaw ou(yawhaw@kingwaytek.com)
	 */
	int[] precolors ;
	public static Semaphore smpResizeMap = new Semaphore(1, true);
	static MapActivity mapactivity;
	OnLongClickListener l;
	
	public SMSMapView(Context ctx) {
		super(ctx);

		init(ctx, null);
	}

	public SMSMapView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);

		init(ctx, attrs);
	}

	private void init(Context ctx, AttributeSet attrs) {
		paint = new Paint();
        Log.i("MapView.java","init() of MapView");
		engine = sonav.getInstance();

		if (attrs != null) {
			String namespace = ctx.getString(R.string.app_namespace);

			setViewType(attrs.getAttributeIntValue(namespace, "view_type",
					VIEW_2D));

			setStyle(attrs.getAttributeIntValue(namespace, "view_style",
					STYLE_AUTO));

			setUserLocationVisibility(attrs.getAttributeBooleanValue(namespace,
					"user_location_visible", true));

			setRoutingNodeVisible(ROUTE_START_POINT, attrs
					.getAttributeBooleanValue(namespace, "route_start_visible",
							true));

			setRoutingNodeVisible(ROUTE_1ST_ESSENTIAL_POINT, attrs
					.getAttributeBooleanValue(namespace,
							"route_essential1_visible", true));

			setRoutingNodeVisible(ROUTE_2ND_ESSENTIAL_POINT, attrs
					.getAttributeBooleanValue(namespace,
							"route_essential2_visible", true));

			setRoutingNodeVisible(ROUTE_DESTINATION_POINT, attrs
					.getAttributeBooleanValue(namespace,
							"route_destination_visible", true));

			// set center of map
			GeoPoint point = new GeoPoint();

			point.setLongitude(attrs.getAttributeFloatValue(namespace,
					"center_longitude", 0));

			point.setLatitude(attrs.getAttributeFloatValue(namespace,
					"center_latitude", 0));

			//setCenter(point);
		}

		engine.savenaviparameter();

		// defualt to choosePoint
		this.choosePointMode = true;
//		ges = new GestureDetector(this);
	}

	/**
	 * Set the size of map when getting new size of this view. If the engine is
	 * not initialized, this method will trigger the engine to start.
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// the width of map can not be odd or the map will drawn obliquely
		int mapWidth = (w & 1) == 0 ? w : w + 1;
		int mapHeight = (h & 1) == 0 ? h : h + 1;
		Log.i("MapView.java", "onSizeChanged()");
		// set new size for drawing map
		engine = sonav.getInstance();
  		    
		if (engine.getState() == sonav.STATE_UNINIT) {
			// initialize engine
			engine.setDataPath(((Activity) getContext())
					.getString(R.string.data_dir));

//			engine.getMapEventHandler().setMapView(this);
//			setEngine(engine);

			DisplayMetrics dm = new DisplayMetrics();
			((Activity) getContext()).getWindowManager().getDefaultDisplay()
					.getMetrics(dm);

			engine.setMapSize(mapWidth, mapHeight, dm.widthPixels,
					dm.heightPixels);

			engine.init();
		} else {
			engine.setMapSize(mapWidth, mapHeight, mapWidth, mapHeight);
//			engine.getMapEventHandler().setMapView(this);
			setEngine(engine);
		}
 	
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * You should call invalidate() when receiving AM_PAINT event from engine.
	 * And this method will use the array that stores colors to drawn with width
	 * and height of this instance.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (engine.getState() == sonav.STATE_READY ) {
//			smpResizeMap.acquireUninterruptibly();	
//		 if(MapActivity.onConfigurationChanged == true ){
//			 MapActivity.onConfigurationChanged = false;
//			 int width = engine.getMapWidth();
//			 int height = engine.getMapHeight();
//    	     canvas.drawBitmap(engine.getMapPixelColors() , 0, width, 0, 0, width, height, false,
//						paint);
//			 return;
//		 }else{	  
			Log.i("MapView.java", "onDraw()");
			int[] colors = engine.getMapPixelColors();
			precolors = colors;
			int width = engine.getMapWidth();
			int height = engine.getMapHeight();

			canvas.drawBitmap(colors, 0, width, 0, 0, width, height, false,
					paint);
//      	 }
//		 smpResizeMap.release();
		}	 
	}

	/**
	 * Do following actions opposite to caught events:<br/>
	 * 1. pan the map<br/>
	 * 2. zoom in when double click<br/>
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		Log.i("MapView.java","isSetStEnPointState="+MapActivity.isSetStEnPointState);
//        if(MapActivity.isSetStEnPointState==true){
//        	return false;
//        }
		int action = event.getAction();
		int screenX = (int) event.getX();
		int screenY = (int) event.getY();
        
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.i("MapView.java","ACTION_DOWN");
			engine.putxy(screenX, screenY, 1);

			// 測試點選地圖指定自己的GPS位置
			// double[] lonLat = engine.getmapxy(screenX, screenY);
			// Log.d("MotionEvent.ACTION_DOWN",
			// screenX + "," + screenY + "," + Arrays.toString(lonLat));
			//
			// GPSDATA gpsData = new GPSDATA();
			// gpsData.setSatelliteNumber(3);
			// gpsData.setLongitude(lonLat[0]);
			// gpsData.setLatitude(lonLat[1]);
			// gpsData.setSpeed(5 * 3.6);
			// gpsData.setAltitude(0);
			// gpsData.setBearing(45);
			// gpsData.setTime(System.currentTimeMillis());
			// engine.setgpsvalue(gpsData);
			// engine.setgpsvalue(gpsData);

			break;
		case MotionEvent.ACTION_MOVE:
			Log.i("MapView.java","ACTION_MOVE");
			engine.putxy(screenX, screenY, 2);
			break;
		case MotionEvent.ACTION_UP:
			engine.putxy(screenX, screenY, 3);
//			position = new double[2];
//			double [] positionTemp = engine.getmapxy(screenX, screenY);
			position = engine.getmapxy(screenX, screenY);
//			position[0] = positionTemp[0];
//			position[1] = positionTemp[1];
			Log.i("MapView.java","ACTION_UP");
			Log.i("MapView.java","position[0]="+position[0]);
			Log.i("MapView.java","position[1]="+position[1]);
			break;
		default:
			break;
		}
		return true;
		//return super.onTouchEvent(event);
		//return ges.onTouchEvent(event);
	}

	/**
	 * Returns the engine.
	 * 
	 * @return the engine
	 */
	public sonav getEngine() {
		return engine;
	}

	/**
	 * Set the engine.
	 * 
	 * @param engine
	 *            the engine to set
	 */
	public void setEngine(sonav engine) {
		this.engine = engine;
	}

	public GeoPoint getCenter() {
		double[] lonLat = engine.getmapxy(engine.getMapWidth() / 2, engine
				.getMapHeight() / 2);

		return lonLat != null ? new GeoPoint(lonLat[0], lonLat[1]) : null;
	}

	public void setCenter(GeoPoint point) {
		engine.gomap(point.getLongitude(), point.getLatitude(), isShown() ? 1
				: 0);
	}

	public GeoPoint[] getBounds() {
		double[] westSouth = engine.getmapxy(0, getHeight());
		double[] eastNorth = engine.getmapxy(getWidth(), 0);

		return new GeoPoint[] { new GeoPoint(westSouth[0], westSouth[1]),
				new GeoPoint(eastNorth[0], eastNorth[1]) };
	}

	public void setBounds(double lon1, double lon2, double lat1, double lat2) {
		double leftTopLon = Math.min(lon1, lon2);
		double leftTopLat = Math.max(lat1, lat2);
		double rightBottomLon = Math.max(lon1, lon2);
		double rightBottomLat = Math.min(lat1, lat2);

		engine.zoomrect(leftTopLon, leftTopLat, rightBottomLon, rightBottomLat);
	}

	/**
	 * Returns the current state of the map view.
	 * 
	 * @return The current state of map. Possible values are
	 *         {@link MapView#STATE_NAVI}, {@link MapView#STATE_EMU},
	 *         {@link MapView#STATE_MAP}, {@link MapView#STATE_NAVI_PAUSE}, and
	 *         {@link MapView#STATE_EMU_PAUSE}.
	 */
	public int getControlMode() {
		return engine.getmapmode();
	}

	/**
	 * Set the control mode of the map view. Currently it will switch to
	 * navigation mode or normal map mode.<br/>
	 * <span class="important">Note that if the mode is set to
	 * {@link #STATE_NAVI} and the routing conditions are set, it will do
	 * routing plan automatically.</span>
	 * 
	 * @param mode
	 *            The control mode of map. Possible values are
	 *            {@link #STATE_NAVI} and {@link #STATE_MAP}.
	 */
	public void setControlMode(int mode) {
		if (mode == STATE_NAVI) {
			engine.setgpsmode(1);
			// engine.setgpslock(1);
			// engine.setdogps(1);
			engine.setgpslock(1);
			engine.setdogps(1);
			engine.setnavimode(1);
			engine.savenaviparameter();
		} else if (mode == STATE_MAP) {
			engine.setnavimode(0);
			engine.setdogps(0);
			engine.setgpslock(0);
			engine.setgpsmode(0);
			engine.savenaviparameter();
		} else if (mode == STATE_EMU) {
	        //do nothing
			//engine.setnavimode(1);
			engine.savenaviparameter();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #MAX_ZOOM_LEVEL
	 * @see #MIM_ZOOM_LEVEL
	 */
	public int getMaxZoomLevel() {
		return MAX_ZOOM_LEVEL;
	}

	public int getZoomLevel() {
		return engine.getzoomlevel();
	}

	/**
	 * <span class="important">Do not support.</span>
	 * 
	 * {@inheritDoc}
	 */
	public int setZoomLevel(int level) {
		throw new RuntimeException("Not supported operation");
	}

	public int zoomIn() {
		int level = engine.getzoomlevel();

		if (level > MIM_ZOOM_LEVEL) {
			engine.dozoomin();
			return level - 1;
		} else {
			return level;
		}
	}

	public int zoomOut() {
		int level = engine.getzoomlevel();
		if (level < MAX_ZOOM_LEVEL) {
			engine.dozoomout();
			return level + 1;
		} else {
			return level;
		}
	}

	/**
	 * Returns the view type of the map view.
	 * 
	 * @see #setViewType(int)
	 */
	public int getViewType() {
		return viewType;
	}

	/**
	 * Set the view type of the map view.
	 * 
	 * @param type
	 *            The view type. Possible values are {@link #VIEW_3D},
	 *            {@link #VIEW_2D_FIX_DIRECTION}, {@link #VIEW_2D},
	 *            {@link #VIEW_SPEED}, {@link #VIEW_3D_2D}, and
	 *            {@link #VIEW_2D_3D}.
	 */
	public void setViewType(int type) {
		engine.setdisplaymode(type);
		engine.savenaviparameter();
		this.viewType = type;
	}

	public void setRoutingPathVisible(boolean visible) {
		if (visible) {
			try {
				PathFinder.getInstance().FindPath();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		} else {
			engine.clearshortpath();
		}
	}

	/**
	 * @param nodeType
	 *            The type of node. Possible values are
	 *            {@link PathFinder#START_POINT} ,
	 *            {@link PathFinder#ESSENTIAL_POINT1},
	 *            {@link PathFinder#ESSENTIAL_POINT2},
	 *            {@link PathFinder#AVOID_POINT}, and
	 *            {@link PathFinder#DESTINATION_POINT}.
	 */
	public void setRoutingNodeVisible(int nodeType, boolean visible) {
		if (visible) {
			PathFinder path = PathFinder.getInstance();
			GeoPoint point = null;

			switch (nodeType) {
			case PathFinder.START_POINT:
				point = path.getOrigin();
				break;
			case PathFinder.ESSENTIAL_POINT1:
				point = path.getViaOne();
				break;
			case PathFinder.ESSENTIAL_POINT2:
				point = path.getViaTwo();
				break;
			case PathFinder.DESTINATION_POINT:
				point = path.getDestin();
				break;
			default:
				point = new GeoPoint(0, 0);
				break;
			}

			if (point != null) {
				engine.setflagpoint(nodeType, point.getLongitude(), point
						.getLatitude());
			}
		} else {
			engine.setflagpoint(nodeType, 0, 0);
		}
	}

	public GeoPoint getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(GeoPoint point) {
		// draw on map
		if (userLocationVisible) {
			engine.setflagpoint(USER_LOCATION_POINT, userLocation
					.getLongitude(), userLocation.getLatitude());
		}

		this.userLocation = point;
	}

	public void setUserLocationVisibility(boolean visible) {
		if (visible && userLocation != null) {
			engine.setflagpoint(USER_LOCATION_POINT, userLocation
					.getLongitude(), userLocation.getLatitude());
		} else {
			engine.setflagpoint(USER_LOCATION_POINT, 0, 0);
		}

		userLocationVisible = visible;
	}

	public int getMapHeight() {
		return engine.getMapWidth();
	}

	public int getMapWidth() {
		return engine.getMapHeight();
	}

	public void setMapSize(int width, int height) {
		ViewGroup.LayoutParams params = getLayoutParams();
		params.width = width;
		params.height = height;
	}

	/**
	 * Start to play navigation emulator.
	 * 
	 * @param repeat
	 *            Set true to play navigation emulator repeatedly or false
	 *            instead.
	 */
	public void playNaviEmulator(boolean repeat) {
		engine.doemulatorrepeat(repeat ? sonav.NAVI_EMU_REPEAT
				: sonav.NAVI_EMU_NO_REPEAT);
		engine.doemulator(sonav.NAVI_EMU_START);
	}

	/**
	 * Pause playing navigation emulator.
	 */
	public void pauseNaviEmulator() {
		engine.doemulator(sonav.NAVI_EMU_PAUSE);
	}

	/**
	 * Resume to navigation emulator.
	 */
	public void resumeNaviEmulator() {
		engine.doemulator(sonav.NAVI_EMU_RESUME);
	}

	/**
	 * Close(Stop) playing navigation emulator.
	 */
	public void closeNaviEmulator() {
		engine.doemulator(sonav.NAVI_EMU_CLOSE);
	}

	/**
	 * 設定導航模擬速度(可隨時設定)，預設值為1.
	 * 
	 * @param speed
	 *            <10代表以原道路限速播放速度的幾倍來播放，>10則全線以此速度播放
	 */
	public void setNaviEmulatorSpeed(int speed) {
		engine.setdemospeed(speed, 1);
	}

	/**
	 * Returns whether if the mode is for user choosing a point.
	 * 
	 * @return True if the mode is for user choosing a point.
	 */
	public boolean isChoosePointMode() {
		return choosePointMode;
	}

	/**
	 * Set whether if the mode is for user choosing a point.
	 * 
	 * @param mode
	 *            Set true for user choosing a point or false instead.
	 */
	public void setChoosePointMode(boolean mode) {
		this.choosePointMode = mode;
	}

	/**
	 * Get style of map view.
	 * 
	 * @return The style.
	 */
	public int getStyle() {
		return engine.getmapstyle()[0];
	}

	/**
	 * Set style of map view.
	 * 
	 * @param style
	 *            The style to set. Possible values are {@link #STYLE_DAY} and
	 *            {@link #STYLE_NIGHT}.
	 */
	public void setStyle(int style) {
		engine.setmapstyle(style, 0, 0);
	}

	/**
	 * Make the instance be the target of MapEventHandler to receive events for
	 * map from engine.<br/>
	 * 
	 * The instance of MapView should gain the ownership after it came to front,
	 * since you can create multiple instances of MapView but the
	 * MapEventHandler sends events to only one instance.
	 */
//	public void gainMapEventOwnership() {
//		engine.getMapEventHandler().setMapView(this);
//	}

	public void setSelectionPoint() {
		if (!this.choosePointMode) {
			return;
		}
		Log.i("MapView.java","map position!=null="+String.valueOf(position!=null));
		Log.i("MapView.java","getMapXY()[0] at setSelectionPoint="+getMapXY()[0]);
		Log.i("MapView.java","getMapXY()[1] at setSelectionPoint="+getMapXY()[1]);
		
		engine.setflagpoint(SELECTED_POINT, position[0], position[1]);
		PointOnMapFlag = true;
		Log.i("MapView.java","mapactivity!=null="+String.valueOf(mapactivity!=null));
//		if(MapActivity.pointOnMapMode == true){
//	           mapactivity.wakeUp();
//	           MapActivity.pointOnMapMode=false;           
//	    }
		
	}

	public double[] getMapXY() {
//		Log.i("MapView.java","position[0]at getMapXY="+position[0]);
//		Log.i("MapView.java","position[1]at getMapXY="+position[1]);
		Log.i("MapView.java","getMapXY position ="+String.valueOf(position!=null));
		return position;
	}
	
//	public double getMapLon() {
//		if (null != position) {
//			Log.i("MapHandler.java","get position[0=]"+position[0]);
//			return position[0];
//		}
//		return 0.0;
//	}
//	
//	public double getMapLat() {
//		if (null != position) {
//			Log.i("MapHandler.java","get position[1]="+position[1]);
//			return position[1];
//		}
//		return 0.0;
//	}
	
    public void setMapActivity(MapActivity mapactivity ) {
      this.mapactivity = mapactivity ;
   }

	@Override
	public void setLongClickListener(OnLongClickListener onLongClickListener) {
		// TODO Auto-generated method stub
		setOnLongClickListener(onLongClickListener);
	}
	
	public void wakeup(){
	       mapactivity.wakeUpSetPointView(position);
	       MapActivity.pointOnMapMode=false;           
    }
	
	public boolean getPointOnMapFlag(){
		return  PointOnMapFlag;
	}

}
