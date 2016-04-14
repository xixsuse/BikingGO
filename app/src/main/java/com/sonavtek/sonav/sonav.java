package com.sonavtek.sonav;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.callbacks.OnEngineReadyCallBack;

/**
 * This class provides native methods of native library provided by AnchorPoint.
 * The package and class name can not be modified. (I will call it "engine"
 * somewhere.)<br/>
 * <br/>
 * Notes:<br/>
 * <ul>
 * <li>If you are using native method directly, all parameters can not be null.</li>
 * <li>The engine has to access files in storage, so you should add following
 * permissions in the AndroidManifest.xml:<br/>
 * <blockquote> &lt;uses-permission
 * android:name="android.permission.WRITE_EXTERNAL_STORAGE" /&gt;</blockquote></li>
 * </ul>
 * <br/>
 * You should set directory contains data for the engine before using the
 * engine. See bellow:<br/>
 * <blockquote> setDataPath("/sdcard/anchorpoint_dir/"); <span
 * class="comment">//native method: {@link #setdatapath(String)}</span><br/>
 * </blockquote> <br/>
 * <br/>
 * You must initialize the instance(call {@link #eeeinit()}) and waiting for the
 * state of the engine to become STATE_READY before showing map or doing routing
 * path planning(see PathFinder). To initialize the instance, See bellow:<br/>
 * <blockquote> // set size of map<br/>
 * setMapSize(width, height, maxWidth, maxHeight); <span
 * class="comment">//native method: {@link #setmapsize(int, int)} and void
 * {@link #setmaxmap(int,int)}</span><br/>
 * <br/>
 * init(); <span class="comment">// native method: {@link #eeeinit()}</span><br/>
 * </blockquote> <br/>
 * <b>It will take long time to initialize the engine and draw the map, so maybe
 * we should preinitialize the engine somewhere.</b> <br/>
 * <br/>
 * To use navigation function, you should follow bellow(not verified):<br/>
 * <blockquote> <span class="comment">// 1. register for listening
 * location</span><br/>
 * GPSListener gpsListener = new GPSListener(context, 2000, 1);<br/>
 * gpsListener.setEnabled(true);<br/>
 * <br/>
 * <span class="comment">// enable GPS of the engine</span><br/>
 * engine.setgpsmode(1);<br/>
 * engine.setdogps(1);<br/>
 * <br/>
 * <span class="comment">// 2. do routing plan</span><br/>
 * PathFinder pathFinder = PathFinder.getInstance();<br/>
 * pathFinder.setOrigin(new GeoPoint(121.524668, 25.06078));<br/>
 * pathFinder.setDestin(new GeoPoint(121.523208, 25.05908));<br/>
 * pathFinder.FindPath();<br/>
 * <br/>
 * <span class="comment">// 3. set navigation mode of the map</span><br/>
 * engine.setgpslock(1);<br/>
 * engine.setdisplaymode(2);<br/>
 * engine.setmapmode(1);<br/>
 * engine.setnavimode(1);<br/>
 * <br/>
 * <span class="comment">// or you can play emulator for navigation after step
 * 2</span><br/>
 * engine.doemulatorrepeat(eeego.NAVI_EMU_NO_REPEAT);<br/>
 * engine.{link #savenaviparameter()}; </blockquote> <br/>
 * <br/>
 * <br/>
 * 於地圖上畫線：<br/>
 * <blockquote> 1. 呼叫{@link #newspxy(int)} - 初始化要畫的線所包含的點的數量。<br/>
 * 2. 呼叫{@link #addspxy(double, double)} - 將點一個一個加入<br/>
 * 3. 呼叫{@link #drawspxy(int)} - 將線畫於地圖上<br/>
 * </blockquote> <br/>
 * <span class="important"> Known issue:<br/>
 * <blockquote> 1. Every events from engine will be put into a queue and waiting
 * to be processed. This will cause some problem when size of map view changed
 * and the event handler is passing old colors to draw the map. <br/>
 * <br/>
 * 2. The POI searching by keyword will get wrong results when given only one
 * word. (It can not be solved)</span> </blockquote>
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class sonav extends Handler {

	/** Condition for querying data: query data of all districts */
	public static final int ALL_DISTRICTS = 0;

	/** Do not sort */
	public static final int NO_SORTING = -1;

	/** Sort data by name of city and town */
	public static final int SORT_BY_CITY_TOWN = 0;

	/** Sort data by name of POI */
	public static final int SORT_BY_POI_NAME = 1;

	/** Sort data by distance */
	public static final int SORT_BY_DISTANCE = 2;

	/** Sort data by similarity of name of POI */
	public static final int SORT_BY_SIMILARITY = 3;

	/** Use small icon to show */
	public static final int USE_SMALL_ICON = 0;

	/** Use big icon to show */
	public static final int USE_BIG_ICON = 1;

	/** State of the instance is uninitialized (is a new instance) */
	public static final int STATE_UNINIT = 0;

	/** State of the instance is on initialization process */
	public static final int STATE_INITIALIZING = 1;

	/**
	 * State of the instance is ready for any request (initialization completed)
	 */
	public static final int STATE_READY = 2;

	/** Flag to set navigation emulator play only one time */
	public static final int NAVI_EMU_NO_REPEAT = 0;

	/** Flag to set navigation emulator play repeatedly */
	public static final int NAVI_EMU_REPEAT = 1;

	/** Flag to start playing navigation emulator */
	public static final int NAVI_EMU_START = 0;

	/** Flag to stop(close) navigation emulator */
	public static final int NAVI_EMU_CLOSE = 1;

	/** Flag to pause playing navigation emulator */
	public static final int NAVI_EMU_PAUSE = 2;

	/** Flag to resume playing navigation emulator */
	public static final int NAVI_EMU_RESUME = 3;

	/** Flag for Chinese sound */
	public static final int SOUND_CHINESE = 1;

	/** Flag for Chinese sound */
	public static final int SOUND_TAIWANESE = 2;

	/** Flag for Chinese sound */
	public static final int SOUND_HAKKA = 3;

	/** Flag for Chinese sound */
	public static final int SOUND_ENGLISH = 4;

	/** Flag for Chinese sound */
	public static final int SOUND_TTS = 5;

	/** 在地圖上顯示道路有路況的Icon */
	public static final int MYLOC_TRAFFIC_CONDITION = 1;

	/** 在地圖上顯示 UBIKE 場站的Icon */
	public static final int MYLOC_UBIKE = 2;

	private static sonav instance; // singleton of this class
	private String dataPath; // path which contains data used by the engine
	private MapEventHandler mapEventHandler; // handles events for map
	private AbstractMapInfoHandler mapInfoHandler; // handles events to change
	private UBikeMapEventHandler ubikeMapEventHandler; // handles UBike marker
														// events to change
	// UI on map
	private PathEventHandler pathEventHandler; // handles events for PathFinder
	private int state; // state of engine
	private int iconSize; // size of icon
	private int[] mapPixelColors; // array for storing colors for drawing map
	private int mapWidth; // current width for drawing map
	private int mapHeight; // current height for drawing map
	private int mapMaxWidth; // maximum width for drawing map
	private int mapMaxHeight; // maximum height for drawing map
	private EventSoundPlayer soundPlayer; // plays sounds for events
	private int count_getMapPixelColors;
	private OnSonavEventListener mapEventListener;

    /**
     * 2016/04/14
     * Add callBack interface by Vincent.
     */
    private OnEngineReadyCallBack engineCallBack;

	/**
	 * The instance should be only one. Other classes should call getInstance()
	 * method to get the singleton.
	 */
	private sonav() {
	}

	/**
	 * Returns current instance of eeego, create one if it does not exist.
	 * 
	 * @return instance of eeego.
	 */
	public static sonav getInstance() {
		if (instance == null) {
			synchronized (sonav.class) {
				if (instance == null) {
					// load the native library
					System.loadLibrary("sonav");
					Log.i("eeego", "native library loaded");

					instance = new sonav();

					instance.setMapEventHandler(new MapEventHandler(instance, null));
					instance.setPathEventHandler(new PathEventHandler(instance));
					instance.setUBikeMapEventHandler(new UBikeMapEventHandler(instance));

					// startup player for playing sounds
					instance.soundPlayer = new EventSoundPlayer();
					new Thread(instance.soundPlayer).start();
				}
			}
		}

		return instance;
	}

	/**
	 * Initialize the engine.
	 */
	public synchronized void init() {
		count_getMapPixelColors = 0;
		if (state == STATE_UNINIT) {
			sonavinit();
		}
	}

	/**
	 * Initialize the engine.
     *
     * 2016/04/14
     * Modified by Vincent.
	 * 
	 * @param context The context to get information about the device.
	 * @param dataPath Path contains data for the engine.
     * @param engineCallBack To get the state ready call back when engine was ready.
	 */
	public void init(Context context, String dataPath, OnEngineReadyCallBack engineCallBack) {
		setDataPath(dataPath);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
		setMapSize(dm.widthPixels, dm.heightPixels, dm.widthPixels, dm.heightPixels);
		// setMapSize(300, 300, 300, 300);

        this.engineCallBack = engineCallBack;

		init();
	}

	/**
	 * Returns path which contains data used by the engine.<br/>
	 * This method should be implemented by native library. But the native
	 * library does not provide.
	 * 
	 * @return the path set by calling native method setdatapath().
	 */
	public String getDataPath() {
		return dataPath;
	}

	/**
	 * set the path which contains data used by the engine.<br/>
	 * This method should be call right after the instance created.
	 * 
	 * @param path
	 *            the path contains data used by the engine.
	 */
	public void setDataPath(String path) {
		setdatapath(path);
		this.dataPath = path;
	}

	/**
	 * Returns the handler handles events for the map from the engine.
	 * 
	 * @return the handler
	 */
	public MapEventHandler getMapEventHandler() {
		return mapEventHandler;
	}

	/**
	 * Set the handler handles events for the map from the engine.
	 * 
	 * @param handler
	 *            the handler to set
	 */
	public void setMapEventHandler(MapEventHandler handler) {
		this.mapEventHandler = handler;
	}

	/**
	 * Returns the handler handles events for information about map from the
	 * engine.
	 * 
	 * @return the handler
	 */
	public AbstractMapInfoHandler getMapInfoHandler() {
		return mapInfoHandler;
	}

	/**
	 * Set the handler handles events for information about map from the engine.
	 * 
	 * @param handler
	 *            the handler to set
	 */
	public void setMapInfoHandler(AbstractMapInfoHandler handler) {
		this.mapInfoHandler = handler;
	}

	/**
	 * Returns the handler handles events for PathFinder.
	 * 
	 * @return the handler
	 */
	public PathEventHandler getPathEventHandler() {
		return pathEventHandler;
	}

	/**
	 * Set the handler handles events for PathFinder.
	 * 
	 * @param handler
	 *            the handler to set
	 */
	public void setPathEventHandler(PathEventHandler handler) {
		this.pathEventHandler = handler;
	}

	/**
	 * Returns the handler handles events for the UBIKE from the engine.
	 * 
	 * @return the handler
	 */
	public UBikeMapEventHandler getUBikeMapEventHandler() {
		return ubikeMapEventHandler;
	}

	/**
	 * Set the handler handles events for the UBIKE from the engine.
	 * 
	 * @param handler
	 *            the handler to set
	 */
	public void setUBikeMapEventHandler(UBikeMapEventHandler handler) {
		this.ubikeMapEventHandler = handler;

	}

	/**
	 * Returns the state of the instance.
	 * 
	 * @return the state.
	 * @see #STATE_UNINIT
	 * @see #STATE_INITIALIZING
	 * @see #STATE_READY
	 */
	public int getState() {
		return state;
	}

	/**
	 * Set width and height for drawing map.
	 * 
	 * @param width
	 *            width of map.
	 * @param height
	 *            height of map.
	 * @param maxWidth
	 *            max width of map.
	 * @param maxHeight
	 *            max height of map.
	 */
	public void setMapSize(int width, int height, int maxWidth, int maxHeight) {
		this.mapWidth = width;
		this.mapHeight = height;
		this.mapMaxWidth = maxWidth;
		this.mapMaxHeight = maxHeight;

		if (state == STATE_UNINIT) {
			// create array for storing colors.
			// the size of this array can not be resized, so you have to set the
			// biggest size at the first time. It seems to be a bug of the
			// engine.
			mapPixelColors = new int[maxWidth * maxHeight];

			// set the maximum size as the initial map size, the actual size
			// will be set after AM_CREATE event received
			setmapsize(maxWidth, maxHeight);

			// don't know why, just use the size of long side to be maximum
			// width and maximum height
			int max = Math.max(maxWidth, maxHeight);
			setmaxmap(max, max);
		} else {
			// mapPixelColors = new int[maxWidth * maxHeight*2];//
			// 處理打橫版時畫面拉扯花掉問題,將mappixecolors重新new出來,by
			// // PingYu
			setmapsize(width, height);
			repaintmap();
		}
	}

	/**
	 * Get current width for drawing map.
	 * 
	 * @return current width for drawing map.
	 */
	public int getMapWidth() {
		return this.mapWidth;
	}

	/**
	 * Get current height for drawing map.
	 * 
	 * @return current height for drawing map.
	 */
	public int getMapHeight() {
		return this.mapHeight;
	}

	/**
	 * Returns maximum width of map.
	 * 
	 * @return The maximum width of map.
	 */
	public int getMapMaxWidth() {
		return this.mapMaxWidth;
	}

	/**
	 * Returns maximum height of map.
	 * 
	 * @return The maximum height of map.
	 */
	public int getMapMaxHeight() {
		return this.mapMaxHeight;
	}

	/**
	 * Get pixel colors for drawing map.
	 * 
	 * @return an array for storing color values.
	 */
	public int[] getMapPixelColors() {
		// for (int i = mapPixelColors.length; i > 0; i--) {
		// if (mapPixelColors[i] == -6770488) {
		// mapPixelColors[i] = 0;
		// }
		// }
		getscr(mapPixelColors);
		return mapPixelColors;
	}

	/**
	 * Get size of icon to be drawn.
	 * 
	 * @return size of icon.
	 */
	public int getIconSize() {
		return this.iconSize;
	}

	/**
	 * Set size of icon to be drawn.
	 * 
	 * @param size
	 *            {@link #USE_SMALL_ICON} or {@link #USE_BIG_ICON}.
	 */
	public void setIconSize(int size) {
		setbigbmp(size);
		this.iconSize = size;
	}

	/**
	 * The callback method called by native library to pass events or status of
	 * engine.
	 * 
	 * @param msg
	 *            ID of event
	 * @param arg1
	 *            according to msg
	 * @param arg2
	 *            according to msg
	 * @return always return 0 currently, the meaning is unknown
	 */
	public int proc(int msg, int arg1, int arg2) {
		Log.d(getClass().toString(), "proc: msg=" + msg + ", arg1=" + arg1 + ", arg2=" + arg2);
		if (msg != 25586) {
			Log.i("sonav", "msg!=25586");
			sendMessage(obtainMessage(msg, arg1, arg2));

			// send event for map
			mapEventHandler.sendMessage(mapEventHandler.obtainMessage(msg, arg1, arg2));
			// send event for PathFinder
			pathEventHandler.sendMessage(pathEventHandler.obtainMessage(msg, arg1, arg2));

			// send event for map information
			if (mapInfoHandler != null) {
				mapInfoHandler.sendMessage(mapInfoHandler.obtainMessage(msg, arg1, arg2));
			}
			// send event for map
			if (null != mapEventListener) {
				mapEventListener.handleSonavEvent(obtainMessage(msg, arg1, arg2));
			}

		} else {
			Log.i("sonav", "msg==25586");
			ubikeMapEventHandler.sendMessage(ubikeMapEventHandler.obtainMessage(msg, arg1, arg2));
		}

		return 0;
	}

	/**
	 * Handles events or status received from native library.
	 * 
	 * The native library will call {@link #proc(int, int, int)} method to pass
	 * events or status.
	 * 
	 * @param msg
	 *            instance of Message which contains information about the
	 *            events or status.
	 */
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

        switch (msg.what) {
            case EngineEvent.AM_START: // 25577
                state = STATE_INITIALIZING;
                engineCallBack.onEngineInitializing();
                break;

            case EngineEvent.AM_CREATE: // 25576
                // The STATE_READY will be set in another place
                // state = STATE_READY;
                break;

            case EngineEvent.AM_PAINT: // 25550
                if (state != STATE_READY) {
                    setmapsize(this.mapWidth, this.mapHeight);
                    state = STATE_READY;
                    engineCallBack.onEngineReady();
                    repaintmap();
                }
                break;

            case EngineEvent.AM_PLAYSOUND: // 25583
                String url = getsnd();
                Log.d(getClass().toString(), "handleMessage: url=" + url);
                if (url.length() > 2) {
                    soundPlayer.addNewSound(url);
                }
                break;

            default: // unhandled messages
                break;
        }
    }

    public void callOnEngineInitFailed() {
        engineCallBack.onEngineInitFailed();
    }

	/*
	 * bellow are native methods for using map.
	 */

	/**
	 * Set the path which stores the data used by the engine.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setdatapath(char
	 * *path)</span>
	 * 
	 * @param path
	 *            The data path.
	 */
	public native void setdatapath(String path);

	/**
	 * Call this method to initialize the engine before using map.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void eeego()</span><br/>
	 * 
	 * @return unknown value
	 */
	public native int sonavinit();

	/**
	 * 儲存地圖相關參數設定. 某些設定的方法呼叫後需呼叫此方法才會生效<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void savenaviparameter()</span><br/>
	 */
	public native void savenaviparameter();

	/**
	 * 設定目前地圖畫面顯示的長與寬. <span class="important">This method should be called
	 * before {@link #eeeinit()}.</span>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setmapsize(int w,int
	 * h)</span>
	 * 
	 * @param width
	 *            width of map.
	 * @param height
	 *            height of map.
	 */
	public native void setmapsize(int width, int height);

	/**
	 * 設定地圖畫面最大的長寬.<br/>
	 * <span class="important">This method should be called after
	 * {@link #setmapsize(int, int)} and should be called only one time before
	 * {@link #eeeinit()}. After {@link #eeeinit()} is called, the size(width *
	 * height) given to {@link #setmapsize(int, int)} can not be larger than the
	 * size(maxWidth * maxHeight) geiven to this method.</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setmaxmap(int maxw,int
	 * maxh)</span>
	 * 
	 * @param width
	 *            maximum width.
	 * @param height
	 *            maximum height.
	 */
	public native void setmaxmap(int width, int height);

	/**
	 * <span class="important">Not verified</span><br/>
	 * 取得目前是日間或夜間模式.
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getdn()</span>
	 * 
	 * 
	 * @return 0 for day or 1 for night.
	 */
	public native int getdn();

	/**
	 * <span class="important">Not verified.</span><br/>
	 * 取得日夜模式與圖形顏色樣式.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void getdnmode(int *dn,int
	 * *mc)</span>
	 * 
	 * 
	 * @return Unknown
	 */
	public native int[] getdnmode();

	/**
	 * <span class="important">Not verified</span><br/>
	 * 取得日夜模式與地圖風格設定值.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void getmapstyle(int *dn,int
	 * *mcolor1,int *mcolor2)</span>
	 * 
	 * @return Unknown
	 */
	public native int[] getmapstyle();

	/**
	 * Set style of map (Default is ({@link MapView#STYLE_AUTO}, 0, 0). <span
	 * class="important">This method should be called after AM_CREATE received
	 * and {@link #savenaviparameter()} method should be called after this
	 * method to enabled the setting.</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setmapstyle(int dn,int
	 * mcolor1,int mcolor2)</span>
	 * 
	 * @param dayOrNight
	 *            The flag to set the style. Possible values are
	 *            {@link MapView#STYLE_DAY}, {@link MapView#STYLE_NIGHT}, and
	 *            {@link MapView#STYLE_AUTO}.
	 * @param dayStyle
	 *            The number of styles for day from 0 to 5.
	 * @param nightStyle
	 *            The number of styles for night from 0 to 5.
	 */
	public native void setmapstyle(int dayOrNight, int dayStyle, int nightStyle);

	/**
	 * Set the view type of map. You should call {@link #savenaviparameter()}
	 * after this method.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setdisplaymode(int
	 * dmode)</span>
	 * 
	 * @param type
	 *            The type of map view. Possible values are
	 *            {@link MapView#VIEW_3D}, {@link MapView#VIEW_2D_FIX_DIRECTION}
	 *            , {@link MapView#VIEW_2D}, {@link MapView#VIEW_SPEED},
	 *            {@link MapView#VIEW_3D_2D}, and {@link MapView#VIEW_2D_3D}.
	 */
	public native void setdisplaymode(int type);

	/**
	 * 設定3D導航時的俯視角度 (Don't need to call {@link #savenaviparameter()} and could
	 * be called at any time.)<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setmapdeg(int w)</span>
	 * 
	 * @param degree
	 *            The degree from 0 to 90.
	 */
	public native void setmapdeg(int degree);

	/**
	 * Returns current zoom level of map.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getzoomlevel()</span>
	 * 
	 * @return Current zoom level.
	 */
	public native int getzoomlevel();

	/**
	 * Zoom in by one zoom level. <span class="important">If the limitation
	 * level reached, the engine will crash.</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int dozoomin().</span><br/>
	 * 
	 * @return Always return 1.
	 */
	public native int dozoomin();

	/**
	 * Zoom out by one zoom level. <span class="important">If the limitation
	 * level reached, the engine will crash.</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int dozoomout().</span>
	 * 
	 * @return Always return 1.
	 */
	public native int dozoomout();

	/**
	 * Set center of map. <span class="important">This method should be called
	 * after AM_PAINT event received if "redraw" is set to 1(to redraw).</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void gomap(double x,double y,int
	 * redraw)</span>
	 * 
	 * @param lon
	 *            The longitude of map.
	 * @param lat
	 *            The latitude of map.
	 * @param redraw
	 *            Set 0 for not to redraw the map, or set 1 to redraw. If it is
	 *            set 0, you should call repaint() method to redraw the map.
	 */
	public native void gomap(double lon, double lat, int redraw);

	/**
	 * Get pixel colors for drawing map.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getscr(int *i).</span>
	 * 
	 * @param colors
	 *            The array for the engine to fill with colors.
	 * @return unknown.
	 */
	public native int getscr(int[] colors);

	/**
	 * Redraw the map. <span class="important">This method should be called
	 * after AM_PAINT event received.</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void repaintmap().</span>
	 */
	public native void repaintmap();

	/**
	 * <span class="important">應該是只用在觸碰地圖時，讓定遠的元件取得touch event的位置.</span>>
	 * 
	 * @param x
	 *            The pixel x of map.
	 * @param y
	 *            The pixel y of map.
	 * @param b
	 *            參數的確實意義尚不明確. Possible values are {@link MapView#TOUCH_DOWN},
	 *            {@link MapView#TOUCH_MOVE}, and {@link MapView#TOUCH_UP}.
	 * @return Unknown
	 */
	public native int putxy(int x, int y, int b);

	/**
	 * 設定是否使用自訂的導航語音.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: setvoiceself(int i)</span>
	 * 
	 * @param flag
	 *            set 0 for to use default voice or 1 to use customized voice.
	 */
	public native void setvoiceself(int flag);

	/**
	 * 設定路徑規劃模式.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setroutemethod(int i)</span>
	 * 
	 * @param method
	 *            The method for routing plan. Possible values are
	 *            {@link PathFinder#SUGGEST},
	 *            {@link PathFinder#SUGGEST_AVOID_TOLL},
	 *            {@link PathFinder#METHOD_HIGHWAY},
	 *            {@link PathFinder#HIGHWAY_AVOID_TOLL},
	 *            {@link PathFinder#NATIONAL_HIGHWAY1},
	 *            {@link PathFinder#NATIONAL_HIGHWAY1_AVOID_TOLL},
	 *            {@link PathFinder#NATIONAL_HIGHWAY3},
	 *            {@link PathFinder#NATIONAL_HIGHWAY3_AVOID_TOLL},
	 *            {@link PathFinder#SHORTEST_TIME},
	 *            {@link PathFinder#SHORTEST_TIME_AVOID_TOLL},
	 *            {@link PathFinder#SHORTEST_DIST},
	 *            {@link PathFinder#SHORTEST_DIST_AVOID_TOLL},
	 *            {@link PathFinder#NORMAL_ROAD},
	 *            {@link PathFinder#NORMAL_ROAD_AVOID_TOLL},
	 *            {@link PathFinder#WALKING}, {@link PathFinder#BICYCLE},
	 *            {@link PathFinder#LH_MOTORBIKE}, {@link PathFinder#MOTORBIKE}.
	 */
	public native void setroutemethod(int method);

	/**
	 * 取得設定的路徑規劃模式.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getroutemethod()</span>
	 * 
	 * @return The mode set. See setroutemethod() method for all possible
	 *         values.
	 */
	public native int getroutemethod();

	/**
	 * 取得路徑規劃起迄點.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getroutepoint(int m,double
	 * *x,double *y)</span>
	 * 
	 * @param type
	 *            The type of point. Possible values are
	 *            {@link PathFinder#START_POINT},
	 *            {@link PathFinder#ESSENTIAL_POINT1},
	 *            {@link PathFinder#ESSENTIAL_POINT2},
	 *            {@link PathFinder#AVOID_POINT}, and
	 *            {@link PathFinder#DESTINATION_POINT}.
	 * @return The longitude and latitude of the node.
	 */
	public native double[] getroutepoint(int type);

	/**
	 * <span class="important">< Not verified ></span><br/>
	 * 設定導航起迄點位.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setroutepoint(int
	 * popmenu_x,int popmenu_y,int m)</span>
	 * 
	 * @param popmenu_x
	 *            螢幕座標x.
	 * @param popmenu_y
	 *            螢幕座標y.
	 * @param type
	 *            The type of point. Possible values are
	 *            {@link PathFinder#START_POINT},
	 *            {@link PathFinder#ESSENTIAL_POINT1},
	 *            {@link PathFinder#ESSENTIAL_POINT2},
	 *            {@link PathFinder#AVOID_POINT}, and
	 *            {@link PathFinder#DESTINATION_POINT}.
	 */
	public native void setroutepoint(int popmenu_x, int popmenu_y, int type);

	/**
	 * 設定導航起迄點位.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int setroutepoint1(double
	 * x,double y,int m)</span>
	 * 
	 * @param lon
	 *            The longitude of point.
	 * @param lat
	 *            The latitude of point.
	 * @param type
	 *            The type of point. Possible values are
	 *            {@link PathFinder#START_POINT},
	 *            {@link PathFinder#ESSENTIAL_POINT1},
	 *            {@link PathFinder#ESSENTIAL_POINT2},
	 *            {@link PathFinder#AVOID_POINT}, and
	 *            {@link PathFinder#DESTINATION_POINT}.
	 * @return The ID of line.
	 */
	public native int setroutepoint1(double lon, double lat, int type);

	/**
	 * <span class="important">< Not verified ></span><br/>
	 * 設定導航起迄點位(為環天特別開的方法).<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int setroutepoint2(double
	 * x1,double y1,int m,char *str,int rk)</span>
	 * 
	 * @param lon
	 *            The longitude of point. 螢幕座標x.
	 * @param lat
	 *            The latitude of point.
	 * @param type
	 *            The type of point. Possible values are
	 *            {@link PathFinder#START_POINT},
	 *            {@link PathFinder#ESSENTIAL_POINT1},
	 *            {@link PathFinder#ESSENTIAL_POINT2},
	 *            {@link PathFinder#AVOID_POINT}, and
	 *            {@link PathFinder#DESTINATION_POINT}.
	 * @param str
	 *            近期操作文字
	 * @param rk
	 *            近期操作類別
	 * @return The ID of line.
	 */
	public native int setroutepoint2(double lon, double lat, int type, String str, int rk);

	/**
	 * <span class="important">< Not verified ></span><br/>
	 * 執行路徑規劃.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void runshortpath(int rmode,int
	 * wait,int nomap)</span>
	 * 
	 * @param playSound
	 *            設定路徑規劃語音. 0: 路徑規劃中, 1: 偏移行程路徑重新規劃, 2:不撥語音.
	 * @param wait
	 *            0: 不等待, 1: 等待算完路徑清單
	 * @param nomap
	 *            0: 載入圖的情況, 1:不載入圖
	 */
	public native void runshortpath(int playSound, int wait, int nomap);

	/**
	 * 取得路徑規畫結果的道路ID數量.<br/>
	 * 可用來判斷路徑規劃是否成功<br/>
	 * (若使用非同步方式， 必須在收到{@link EngineEvent#AM_ROUTE}且arg2=2後才能取得)<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getshortpathok()</span>
	 * 
	 * @return the number of ID of roads or 0 if failed.
	 */
	public native int getshortpathok();

	/**
	 * 回傳路徑規劃結果的道路數量.<br/>
	 * (若使用非同步方式，必須在收到{@link EngineEvent#AM_ROUTE}且arg2=2後才能取得)<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getroadlistnum()</span>
	 * 
	 * @return The number of roads in the result of routing plan.
	 */
	public native int getroadlistnum();

	/**
	 * 取得路徑規畫結果中的某條道路資訊.<br/>
	 * (若使用非同步方式，必須在收到{@link EngineEvent#AM_ROUTE}且arg2=2後才能取得)<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getroadlist(int i,double
	 * *len,double *x,double *y,char *roadname,int *turn,int *rdclass,int
	 * *rdstate)</span>
	 * 
	 * @param data
	 *            An instance for storing data.
	 * @param index
	 *            The index of the road in result to get.
	 * @return The given instance of ROADLISTDATA with values set.
	 */
	public native ROADLISTDATA getroadlist(ROADLISTDATA data, int index);

	/**
	 * 清除所有已在路徑規劃中設定的點.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void clearallroutepoint()</span>
	 */
	public native void clearallroutepoint();

	/**
	 * 清除已在路徑規劃中設定的迴避點.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void clearavoidpoint()</span>
	 * 
	 * @param index
	 *            The index of the point.
	 */
	public native void clearavoidpoint(int index);

	/**
	 * 清除已在路徑規劃中設定的任一點.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void clearavoidpoint()</span>
	 * 
	 * @param index
	 *            The index of the point.
	 */
	public native void clearroutepoint(int index);

	/**
	 * 清除路徑規劃結果，地圖上的規劃路線也會消失.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void clearshortpath()</span>
	 */
	public native void clearshortpath();

	/**
	 * 控制地圖導航模擬。(必須先顯示地圖並取得路徑規畫結果).<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void doemulator(int i)</span>
	 * 
	 * @param flag
	 *            The flag to control the navigation emulator. Possible values
	 *            are {@link #NAVI_EMU_START}, {@link #NAVI_EMU_CLOSE},
	 *            {@link #NAVI_EMU_PAUSE}, and {@link #NAVI_EMU_RESUME}.
	 */
	public native void doemulator(int flag);

	/**
	 * Set flag to play navigation emulator repeatedly or not.
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void doemulatorrepeat(int
	 * i)</span>
	 * 
	 * @param flag
	 *            set {@link #NAVI_EMU_NO_REPEAT} or {@link #NAVI_EMU_REPEAT}.
	 */
	public native void doemulatorrepeat(int flag);

	/**
	 * 設定導航模擬速度(可隨時設定)，預設值為1.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setdemospeed(double
	 * speed,double frame)</span>
	 * 
	 * @param speed
	 *            <10代表以原道路限速播放速度的幾倍來播放，>10則全線以此速度播放
	 * @param frame
	 *            No effect now, always set to 1.
	 */
	public native void setdemospeed(double speed, double frame);

	/**
	 * <span class="important">Not verified</span><br/>
	 * Unknown.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getdonavi()</span>
	 * 
	 * @return Unknown
	 */
	public native int getdonavi();

	/**
	 * 啟動/關閉導航模式.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setdogps(int i)</span>
	 * 
	 * @param flag
	 *            set 1 to enable navigation, or 0 to disable navigation.
	 */
	public native void setdogps(int flag);

	/**
	 * 啟用GPS定位當作起點功能(導航時，會隨著GPS定位重新規劃路線).
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setgpsonly(int i)</span>
	 * 
	 * @param flag
	 *            Set 0 to disable or 1 to enable.
	 */
	public native void setgpslock(int flag);

	/**
	 * 執行路徑規劃導航，必須已有路徑規劃的結果後才能直接使用此方法啟動導航.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setnavimode(int i)</span>
	 * 
	 * @param flag
	 *            Set 1 to start navigation by routing plan or 0 to stop the
	 *            navigation by routing plan.
	 */
	public native void setnavimode(int flag);

	/**
	 * <span class="important">Not verified</span><br/>
	 * 取得目前地圖模式. <span class="important">啟動導航模式後，並不會立刻回傳
	 * {@link MapView#STATE_NAVI}，必須經過一定時間(幾毫秒?)後， 才會回傳
	 * {@link MapView#STATE_NAVI}.</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getmapmode()</span>
	 * 
	 * @return The current state of map. Possible values are
	 *         {@link MapView#STATE_NAVI}, {@link MapView#STATE_EMU},
	 *         {@link MapView#STATE_MAP}, {@link MapView#STATE_NAVI_PAUSE}, and
	 *         {@link MapView#STATE_EMU_PAUSE}.
	 */
	public native int getmapmode();

	public native void setmapmode(int flag);

	/**
	 * <span class="important">Not verified</span><br/>
	 * 啟動/關閉地圖操作模式.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setmapmode(int i)</span>
	 * 
	 * @param flag
	 *            Set 1 to enable or 0 to disable. public native void
	 *            setmapmode(int flag);
	 * 
	 *            /** Show location of special marker by given longitude and
	 *            latitude or hide the special marker by setting both longitude
	 *            and latitude to 0 or -1.<br/>
	 * 
	 * <br/>
	 *            <span class="nativeName">native method: setflagpoint(int
	 *            m,double x,double y)</span>
	 * 
	 * @param type
	 *            type of marker. Possible values are
	 *            {@link MapView#ROUTE_START_POINT} ,
	 *            {@link MapView#ROUTE_1ST_ESSENTIAL_POINT},
	 *            {@link MapView#ROUTE_2ND_ESSENTIAL_POINT},
	 *            {@link MapView#ROUTE_DESTINATION_POINT},
	 *            {@link MapView#USER_LOCATION_POINT} , and
	 *            {@link MapView#SELECTED_POINT}.
	 * @param lon
	 *            the longitude of marker
	 * @param lat
	 *            the latitude of marker
	 */
	public native void setflagpoint(int type, double lon, double lat);

	/**
	 * Clear previous routing path created manually(if exist) and create a new
	 * routing path for editing manually. To clear the routing path, you can set
	 * 0 to release the memory and call drawspxy(
	 * {@link MapView#HIDE_MANUAL_ROUTE}) to clear the path drawn.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int newspxy(int maxxy)</span>
	 * 
	 * @param maxPointNum
	 *            The maximum number of points could be added in to the path.
	 *            (Actually, the engine will set maxPointNum + 16 for buffering.
	 *            But don't add points more than maxPointNum please.)
	 * @return 1 for success or -1 for failure.
	 */
	public native int newspxy(int maxPointNum);

	/**
	 * Add a new point into the routing path created manually (by calling
	 * newspxy() method). You should call drawspxy(
	 * {@link MapView#SHOW_MANUAL_ROUTE}) to redraw the modified routing path
	 * after this method called.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int addspxy(double x,double
	 * y)</span>
	 * 
	 * @param lon
	 *            The longitude of the new point.
	 * @param lat
	 *            The latitude of the new point.
	 * @return The index of the point added or -1 if failed.
	 */
	public native int addspxy(double lon, double lat);

	/**
	 * Show or hide the the routing path created manually. Default is hide.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int drawspxy(int i)</span>
	 * 
	 * @param flag
	 *            set {@link MapView#SHOW_MANUAL_ROUTE} to show the path or
	 *            {@link MapView#HIDE_MANUAL_ROUTE} to hide.
	 * @return Always return 1, but no sense.
	 */
	public native int drawspxy(int flag);

	/**
	 * Set the size of icon to show on map or returned from get3egobmp().<br/>
	 * If seticonsize() has never been called, the size of POI icons drawing on
	 * map will be according to this option, otherwise the size of POI icons
	 * will be the given values when calling seticonsize(). <br/>
	 * <br/>
	 * <span class="important">This method should be called before calling
	 * eeeinit() and should not be called more than one time, since the image
	 * size of dynamic flags on map will be not sizable after the engine
	 * initialized. But the size of POI icons could be resized any time.</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setbigbmp(int i)</span>
	 * 
	 * @param size
	 *            USE_BIG_ICON or USE_SMALL_ICON (Default is
	 *            {@link #USE_BIG_ICON})
	 */
	public native void setbigbmp(int size);

	/**
	 * Set size for icons drawing on map. If values are less than 1, then uses
	 * the original width and height of the image.<br/>
	 * <br/>
	 * <span class="important">This method should be called before calling
	 * eeeinit() and should not be called more than one time, since the image
	 * size of dynamic flags on map will be not sizable after the engine
	 * initialized.</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void seticonsize(int iconw,int
	 * iconh)</span>
	 * 
	 * @param width
	 *            width of icon (default is -1)
	 * @param height
	 *            height of icon (default is -1)
	 */
	public native void seticonsize(int width, int height);

	/**
	 * Get bit array data of image file.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: HBITMAP get3egobmp(char
	 * *filename)</span>
	 * 
	 * @param filename
	 *            name of the image file contains extension name. ex. 0101.bmp
	 * @return bit array data of image file. the bit at index 0 is the width of
	 *         the image, the bit at index 1 is the height of the image, and
	 *         bits from index 4 to the end are raw data of image.
	 */
	public native int[] getsonavbmp(String filename);

	/**
	 * Check if the data for drawing map can be read (call getscr() method). The
	 * value will be 1 after the first AM_PAINT event.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getpaintok()</span>
	 * 
	 * @return 0 means can not be read(not yet painted), and 1 means can be
	 *         read.
	 */
	public native int getpaintok();

	/**
	 * 由螢幕座標取得真實座標.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: getmapxy(int popmenu_x,int
	 * popmenu_y,double *x,double *y)</span>
	 * 
	 * @param screenX
	 *            The X coordinate of map in screen.
	 * @param screenY
	 *            The Y coordinate of map in screen.
	 * @return An array contains longitude at index 0 and latitude at index 1 of
	 *         the point.
	 */
	public native double[] getmapxy(int screenX, int screenY);

	/**
	 * 度分秒轉成XY座標值的運算，例如：120°15''20' 轉換為 120.xxxxx.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: double showlatlontoxy(char
	 * *ans)</span>
	 * 
	 * @param ans
	 *            格式為度分秒，例如：120°15''20'
	 * 
	 * @return 座標值.
	 */
	public native double showlatlontoxy(String ans);

	/**
	 * Get all cities.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: XLIST *showlistcity(int
	 * *i)</span>
	 * 
	 * @return instances of cities that contains longitude, latitude, ID and
	 *         name.
	 */
	public native XLIST[] showlistcity();

	/**
	 * Get all towns in a city.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: XLIST *showlisttown(int *i,int
	 * citycode)</span>
	 * 
	 * @param cityId
	 *            ID of city.
	 * @return instances of towns that contains longitude, latitude, ID and
	 *         name.
	 */
	public native XLIST[] showlisttown(int cityId);

	/**
	 * Get list of roads that matches the given address in a city.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: XLIST *findlistroad1(int *i, int
	 * ctcode, char *str, int count)</span>
	 * 
	 * @param cityOrTown
	 *            the ID of city or town(use 0 for all districts or ID of city *
	 *            100 for a city)
	 * @param addr
	 *            從"路"的層級開始的詳細地址，會被用來作為模糊搜尋的開頭字串。譬如：<br/>
	 *            addr = "羅斯"，則會搜尋該縣市或鄉鎮中地址為"羅斯"開頭的地址(如同SQL的LIKE '羅斯%')<br/>
	 * @param num
	 *            maximum number of roads to return.
	 * @return roads that matches the given address in the city or town.
	 */
	public native XLIST[] findlistroad1(int cityOrTown, String addr, int num);

	/**
	 * Get list of roads intersect with the given road.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: XLIST *showlistroad2(int *i,int
	 * ctcode,char *str)</span>
	 * 
	 * @param cityOrTown
	 *            the ID of city or town(use 0 for all districts or ID of city *
	 *            100 for a city)
	 * @param roadName
	 *            name of road.
	 * @return roads that intersect with the given road. (won't be null even if
	 *         the road does not exist)
	 */
	public native XLIST[] showlistroad2(int cityOrTown, String roadName);

	/**
	 * Get location of an address. Use this method when you want to find the
	 * location of an address in a city, town and road.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: double[] showaddrxy(char
	 * *city,char *town,char *rdn,char *retstr,double *ox,double *oy)</span>
	 * 
	 * @param cityName
	 *            name of city.
	 * @param townName
	 *            name of town.
	 * @param roadName
	 *            road name.
	 * @param addr
	 *            the address to be add to the end of road name that maybe
	 *            contains section, lane, alley, etc.
	 * @return the result is an array contains information:<br/>
	 *         index 0: 0 if the location could not be found, 1 if the location
	 *         found from stored data, and > 1 if the location found by
	 *         interpolation or extrapolation. <br/>
	 * <br/>
	 *         index 1: longitude if value at index 0 is bigger than 0. <br/>
	 * <br/>
	 *         index 2: latitude if value at index 0 is bigger than 0.
	 */
	public native double[] showaddrxy(String cityName, String townName, String roadName, String addr);

	/**
	 * Get location of an address by given an string of full address.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int showaddrxy1(char
	 * *addrstr,double *ox,double *oy)</span>
	 * 
	 * @param addr
	 *            address to find the location.
	 * @return the result is an array contains information:<br/>
	 *         index 0: 0 if the location could not be found, 1 if the location
	 *         found from stored data, and > 1 if the location found by
	 *         interpolation or extrapolation. <br/>
	 * <br/>
	 *         index 1: longitude if value at index 0 is bigger than 0. <br/>
	 * <br/>
	 *         index 2: latitude if value at index 0 is bigger than 0.
	 */
	public native double[] showaddrxy1(String addr);

	/**
	 * Get location of an address. Use this method when you want to find the
	 * location of an address in a city(or town) and road.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: double[] showaddrxy2(char
	 * *city,char *town,char *rdn,char *retstr,double *ox,double *oy)</span>
	 * 
	 * @param cityOrTownId
	 *            ID of city or town.
	 * @param roadName
	 *            road name.
	 * @param addr
	 *            the address to be add to the end of road name that maybe
	 *            contains section, lane, alley, etc.
	 * @return the result is an array contains information:<br/>
	 *         index 0: 0 if the location could not be found, 1 if the location
	 *         found from stored data, and > 1 if the location found by
	 *         interpolation or extrapolation. <br/>
	 * <br/>
	 *         index 1: longitude if value at index 0 is bigger than 0. <br/>
	 * <br/>
	 *         index 2: latitude if value at index 0 is bigger than 0.
	 */
	public native double[] showaddrxy2(int cityOrTownId, String roadName, String addr);

	/**
	 * Get central location of a city.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getcityxy(int citycode,double
	 * *x,double *y)</span>
	 * 
	 * @param id
	 *            ID of city.
	 * @return an array which longitude is at index 0 and latitude is at index 1
	 *         or length is zero if the central location can not be found.
	 */
	public native double[] getcityxy(int id);

	/**
	 * Get central location of a town.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int gettownxy(int citycode,double
	 * *x,double *y)</span>
	 * 
	 * @param id
	 *            ID of town.
	 * @return an array which longitude is at index 0 and latitude is at index 1
	 *         or length is zero if the central location can not be found.
	 */
	public native double[] gettownxy(int id);

	/**
	 * Get name of city and town where the given coordinate located.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int showcitytownname(double
	 * x,double y,char *ctname)</span>
	 * 
	 * @param lon
	 *            longitude
	 * @param lat
	 *            latitude
	 * @return name of city and name of town separated by comma or null if not
	 *         found. ex. "台北市,中山區"
	 */
	public native String showcitytownname(double lon, double lat);

	/**
	 * Get name of city and town where the given coordinate located.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int showcitytownnameExt(double
	 * x,double y,char *cityname,char *townname)</span>
	 * 
	 * @param lon
	 *            longitude
	 * @param lat
	 *            latitude
	 * @return an array that puts name of city or null if city not found at
	 *         index 0, and puts name of town or null if town not found at index
	 *         1. ex. ["台北市","中山區"]
	 */
	public native String[] showcitytownnameExt(double lon, double lat);

	/**
	 * Get ID of city and town where the given coordinate located. <br/>
	 * <span class="important">This method should not be called before
	 * {@link EngineEvent#AM_CREATE} event received.</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int showcitytowncode(double
	 * x,double y,int *citycode,int *towncode)</span>
	 * 
	 * @param lon
	 *            longitude
	 * @param lat
	 *            latitude
	 * @return an array that puts ID of city or 0 if city not found at index 0,
	 *         and puts ID of town or 0 if town not found at index 1. ex. [65,
	 *         6510] or [0, 0] if not found
	 */
	public native int[] showcitytowncode(double lon, double lat);

	/**
	 * Get name of city and town of the given ID of town.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void getctname(int ctcode,char
	 * *city,char *town)</span>
	 * 
	 * @param townId
	 *            ID of town
	 * @return an array that puts ID of city or null if city not found at index
	 *         0, and puts ID of town or null if town not found at index 1. ex.
	 *         6503 = ["台北市","中正區"]
	 */
	public native String[] getctname(int townId);

	/**
	 * Returns all classes of POI. ex. 餐廳<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: XLIST *showpoiclass(int *i,int
	 * show,char *pclass)</span>
	 * 
	 * @param max
	 *            maximum number to query.
	 * @param clazz
	 *            不明，目前直接帶入空字串取得所有類別
	 * @return classes of POI.
	 */
	public native XLIST[] showpoiclass(int max, String clazz);

	/**
	 * Get POIs according to the given conditions.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int xynearpoi(int ctcode,char
	 * *kw,char *poiclass,double x,double y,double len,int count,NEARPOI
	 * *np)</span>
	 * 
	 * @param cityOrTown
	 *            the ID of city or town(use 0 for all districts or ID of city *
	 *            100 for a city)
	 * @param keyword
	 *            for searching POIs which part of name matched the keyword
	 *            (like SQL statement '%keyword%'), use empty string for all
	 *            names
	 * @param category
	 *            a string contains ID of POI categories separated by comma, use
	 *            empty string for all categories (ex. "01,02" or "")
	 * @param lon
	 *            the longitude of central point for the searching range
	 * @param lat
	 *            the latitude of central point for the searching range
	 * @param distance
	 *            the distance limitation from the central point in meter, if
	 *            the value is large than 20KM then the engine will use 20KM as
	 *            the limitation
	 * @param num
	 *            the number of POIs to query
	 * @return POIs with ID, name, and distance.
	 */
	public native NEARPOI[] xynearpoi(int cityOrTown, String keyword, String category, double lon, double lat,
			double distance, int num);

	/**
	 * Get POIs according to the given conditions sorted by distance.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int xynearpoi(int ctcode,char
	 * *kw,char *poiclass,double x,double y,double len,int count,NEARPOI
	 * *np)</span>
	 * 
	 * @param cityOrTown
	 *            the ID of city or town(use 0 for all districts or ID of city *
	 *            100 for a city)
	 * @param keyword
	 *            for searching POIs which part of name matched the keyword
	 *            (like SQL statement '%keyword%'), use empty string for all
	 *            names
	 * @param category
	 *            a string contains ID of POI categories separated by comma, use
	 *            empty string for all categories (ex. "01,02" or "")
	 * @param lon
	 *            the longitude of central point for the searching range
	 * @param lat
	 *            the latitude of central point for the searching range
	 * @param distance
	 *            the distance limitation from the central point in meter, if
	 *            the value is large than 20KM then the engine will use 20KM as
	 *            the limitation
	 * @param num
	 *            the number of POIs to query
	 * @return POIs with longitude, latitude, ID of category, name, telephone
	 *         number, mobile phone number, address, note, city name, town name,
	 *         and distance.
	 */
	public native XLIST4[] listnearpoi(int cityOrTown, String keyword, String category, double lon, double lat,
			double distance, int num);

	/**
	 * Get POIs according to the given conditions.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: XLIST4 *findlistpoi1(char *a,int
	 * ctcode,char *poiclass,int outlen,int *iCount,int sort)</span>
	 * 
	 * @param keyword
	 *            for searching POIs which part of name matched the keyword
	 *            (like SQL statement '%keyword%'), use empty string for all
	 *            names
	 * @param cityOrTown
	 *            the ID of city or town(use 0 for all districts or ID of city *
	 *            100 for a city)
	 * @param category
	 *            a string contains ID of POI categories separated by comma, use
	 *            empty string for all categories (ex. "01,02" or "")
	 * @param num
	 *            the number of POIs to query
	 * @param sort
	 *            method for sorting, 0: by city and town, 1: by name of POI, 2:
	 *            by distance, 3: by similarity of POI name, -1: no sorting
	 * @return POIs with longitude, latitude, ID of category, name, telephone
	 *         number, mobile phone number, address, note, city name, town name,
	 *         and distance.
	 */
	public native XLIST4[] findlistpoi1(String keyword, int cityOrTown, String category, int num, int sort);

	/**
	 * Get attributes of a POI.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getptproperty(int
	 * poiid,PPDATA *out)</span>
	 * 
	 * @param data
	 *            an empty instance of PPDATA, the value of members in the
	 *            instance will be set if the POI with the given ID is found
	 * @param id
	 *            ID of POI
	 * @return the given instance of PPDATA.
	 */
	public native PPDATA getptproperty(PPDATA data, int id);

	/**
	 * <span class="important">< Not verified ></span><br/>
	 * Start or stop track record.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setrecordtrack(int i)</span>
	 * 
	 * @param flag
	 *            set 1 to start record or set 0 to stop record.
	 */
	public native void setrecordtrack(int flag);

	/**
	 * <span class="important">< Not verified ></span><br/>
	 * Check whether if track record has been started or not.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getrecordtrack()</span>
	 * 
	 * @return 1 means started and 0 means stopped.
	 */
	public native int getrecordtrack();

	/**
	 * <span class="important">< Not verified ></span><br/>
	 * Pause track recording.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void pauserecordtrack(int
	 * i)</span>
	 * 
	 * @param flag
	 *            set 1 to pause or set 0 to (what?).
	 */
	public native void pauserecordtrack(int flag);

	/**
	 * <span class="important">< Not verified ></span><br/>
	 * Play navigation mode of map for a track record.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int playrecordtrack(char
	 * *trackfile)</span>
	 * 
	 * @param filename
	 *            name of track record file to play.
	 * @return unknown.
	 */
	public native int playrecordtrack(String filename);

	/**
	 * <span class="important">< Not verified ></span><br/>
	 * Check whether if the engine is playing track record or not.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void exitrecordtrack()</span>
	 * 
	 * @return 1 means playing and 0 means not.
	 */
	public native int getplaytrack();

	/**
	 * <span class="important">< Not verified ></span><br/>
	 * Exit tracking (?).<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getplaytrack()</span>
	 */
	public native void exitrecordtrack();

	/**
	 * Enable/disable GPS. Set 0 will be the same as closegps().<br/>
	 * For android, enable/disable GPS means the engine will be enabled/disabled
	 * to receive GPS data from application.<br/>
	 * For Windows Mobile, enable/disable GPS means the the engine will be
	 * enabled/disabled the com port.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setgpsmode(int i)</span><br/>
	 * 
	 * @param flag
	 *            set 1 to enable GPS mode or 0 to disable GPS mode
	 */
	public native void setgpsmode(int flag);

	/**
	 * Disable GPS. The same as setgpsmode(0).<br/>
	 * <span class="important">Unneccessary for Android</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void closegps()</span>
	 * 
	 */
	public native void closegps();

	/**
	 * 設定不啟動地圖而單獨啟用GPS模式.<br/>
	 * <span class="important">Unneccessary for Android</span><br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setgpsonly(int i)</span>
	 * 
	 * @param flag
	 *            Set 1 to allow enabling GPS without map or 0 to not allow.
	 */
	public native void setgpsonly(int flag);

	/**
	 * 取得GPS資料.<br/>
	 * 
	 * <span class="important">< Not verified ></span>
	 * 
	 * @param data
	 *            unknown
	 * @return unknown
	 */
	public native GPSDATA getgpsvalue(GPSDATA data);

	/**
	 * 將收到的位置資訊傳遞給Engine.<br/>
	 * 
	 * <span class="important">< Not verified ></span>
	 * 
	 * @param data
	 *            Instance contains information about location.
	 */
	public native void setgpsvalue(GPSDATA data);

	/**
	 * GPS是否收到有效座標.<br/>
	 * 
	 * <span class="important">< Not verified ></span>
	 * 
	 * @return 1 means got valid GPS data or 0 means not.
	 */
	public native int getgpsok();

	/**
	 * GPS連接是否成功.<br/>
	 * 
	 * <span class="important">< Not verified ></span>
	 * 
	 * @return 1 means success or 0 means failure.
	 */
	public native int getgpscomok();

	/**
	 * unknwon.<br/>
	 * 
	 * <span class="important">< Not verified ></span>
	 * 
	 * @return unknwon
	 */
	public native int getgpserr();

	/**
	 * 取得gps位置當作起點值.<br/>
	 * 
	 * <span class="important">< Not verified ></span>
	 * 
	 * @return unknwon
	 */
	public native int getgpslock();

	/**
	 * 是否自動搜尋GPS comport與bourate.<br/>
	 * <span class="important">< Not verified ></span>
	 * 
	 * @param flag
	 *            set 1 to detect automatically, 0 to detect manually
	 */
	public native void setautogpscom(int flag);

	/**
	 * 設定瀏覽模式是否暫停GPS運作.<br/>
	 * <span class="important">< Not verified ></span>
	 * 
	 * @param flag
	 *            unknown
	 */
	public native void setautopausegps(int flag);

	/**
	 * GPS comport與bourate設定.<br/>
	 * <span class="important">< Not verified ></span>
	 * 
	 * @param i
	 *            unknown
	 * @param port
	 *            unknown ex. 9600
	 */
	public native void setgpscomport(int i, int port);

	/**
	 * 設定GPS靈敏度.<br/>
	 * <span class="important">< Not verified ></span>
	 * 
	 * @param level
	 *            零敏程度1~ 3，預設值是2，數字越小越靈敏，但容易造成飄移。
	 */
	public native void setgpserr(int level);

	/**
	 * 設定GPS的誤差範圍.<br/>
	 * <span class="important">< Not verified ></span>
	 * 
	 * @param tolerence
	 *            誤差值，單位為公尺，預設40公尺。
	 */
	public native void setgpserrbuf(int tolerence);

	/**
	 * 強制暫停或啟動GPS運作.<br/>
	 * <span class="important">< Not verified ></span>
	 * 
	 * @param flag
	 *            unknown
	 */
	public native void setpausegps(int flag);

	/**
	 * Unknown.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: int getlanguage()</span>
	 * 
	 * @return Unknown
	 */
	public native int getlanguage();

	/**
	 * 設定語系.<br/>
	 * <span class="important">< Not verified ></span><br/>
	 * <br/>
	 * <span class="nativeName">native method: void setlanguage(int i)</span>
	 * 
	 * @param lang
	 *            unknown
	 */
	public native void setlanguage(int lang);

	/**
	 * Unknown.<br/>
	 * <br/>
	 * <span class="nativeName">native method: int getlangvoice()</span>
	 * 
	 * @return Unknown
	 */
	public native int getlangvoice();

	/**
	 * 設定語音.<br/>
	 * 
	 * <br/>
	 * <span class="nativeName">native method: void setlanguage(int i)</span>
	 * 
	 * @param lang
	 *            The language of voice to play, possible values are
	 *            {@link #SOUND_CHINESE}, {@link #SOUND_TAIWANESE},
	 *            {@link #SOUND_HAKKA}, {@link #SOUND_ENGLISH}, and
	 *            {@link #SOUND_TTS}.
	 */
	public native void setlangvoice(int lang);

	/**
	 * <span class="important">應該是取得現在要播放的音檔路徑.</span>
	 * 
	 * @return The path of the sound file.
	 */
	public native String getsnd();

	public native void setflag(int i); // 是否顯示起迄點旗幟

	public native int getroadinfo();

	// 取得道路資訊個數

	public native void setroadinfo(int i);

	public native String showworldname(double x, double y);

	// 根據xy座標去取得世界地圖名稱
	// 回傳的名稱

	public native XLIST[] showpoisubclass(int category, int show);

	public native void setidxnum(int i);

	public native PPDATA findtel2poi(PPDATA b, String a);

	// 電話號碼找POI,a是完整電話號碼

	public native PPDATA findubc2poi(PPDATA b, String a);

	// UBCODE找POI,a是UBCODE

	// /////CALLBACK

	// ////////////////////func////////////////////
	public native double getinitmapw();

	// 取得地圖初始圖寬

	public native double getmapw();

	// 取得目前地圖圖寬

	public native double getnextroaddist();

	// 回傳取得目前車輛與下一個路口距離

	public native double[] getgpsloc(int m);

	// 取得m=0目前位置m=1,2經過點m=3目的地位置

	public native double[] getptinfo(int m);

	// 取得m=0目前位置m=1,2經過點m=3目的地位置,lineid為線的ID

	public native double[] getsprange();

	// 回傳路徑範圍

	public native double[] getspxy(int n);

	// 取得路經清單xy的數量

	public native double[] gettargetxy();

	// 取得指標座標

	public native double[] screen2mapxy(int mx, int my);

	// 螢幕座標轉實際座標

	public native int closescr(int sss);

	// 釋放繪圖記憶體

	public native int closescrzz(int sss);

	// 緩衝釋放繪圖記憶體

	public native int dpoint(int sss, int x, int y);

	public native int getbmph(int sss);

	// 取得圖高

	public native int getbmpw(int sss);

	// 取得圖寬

	public native int getcamvoice();

	public native int getcarbmp();

	public native int getfavoritenum(double x, double y, double z, String str);

	// 取得g值

	public native int getinputmethod();

	public native int getnavideg();

	// 取得導航或模擬的車輛方向角

	public native int getnaviinfoshow();

	public native int getnowroadidx();

	// 取得目前道路清單的index

	public native int getnowroadlistid();

	// 回傳取得目前車輛位於第幾筆道路上

	public native int getpixelscale(int d);

	// 取得d公尺的pixel數

	public native int getpoitxt();

	public native int getptvisible();

	// 取的目前POI顯示功能是否有啟動

	public native int getroadkind(int roadidx);

	// 取得導航道路的等級;//roadidx=0是目前道路，=1是下一條，=2是下下條，依此類推
	public native int getshowbuild();

	// 取得是否顯示3d設定狀態

	public native int getspxynum();

	// 取得路經清單xy的數量

	public native int getsystemset();

	// 讀取系統設定參數

	public native int getvolbmp();

	public native int getwarnvoice();

	public native int gotoroad(int ctcode, String str, int redraw);

	// 道路定位,redraw是否重讀與重繪地圖
	// PS.setembed與gomap或gotoroad同一程序使用時，redraw請勿同時都設定1

	public native int initphon();

	// 初始化字庫

	public native int initsppoi();

	// 初始化路徑沿線POI，=0不成功,>0成功

	public native int loadbmp(String filename);

	// 載入圖片

	public native int loadsysbmp(String filename);

	// 載入系統資料夾圖片

	public native int navimalloc(int a);

	// 3ego元件的記憶體宣告

	public native int[] getdospeed();

	// 取得是否撥報超速提示音,i:高速公路,j:快速道路,k:一般道路

	public native int[] getspdistime();

	// dist,hh,mm回傳總距離與時間的小時與分

	public native int[] getwarnspeed();

	// 取得超速容忍值,[0]:高速公路,[1]:快速道路,[2]:一般道路

	public native int[] mapxy2screen(double x, double y);

	// 實際座標轉螢幕座標//僅限2D圖正北模式

	public native IDATA getphonnext(IDATA a, String w);

	public native IDATA getquickinput(IDATA a, String w, int p, int cityid, int townid);

	// 快拼，buf為結果字串,回傳可用的注音符號
	// if ( p==7 || p==11)
	// 找POI+道路名稱的字詞
	// else if (p==0 || p==6 || p==10 )
	// 找道路名稱的字詞
	// else
	// 找POI名稱的字詞

	public native XLIST4[] showfavorite(int k);

	public native XLIST[] showfavoriteclass();

	public native XLIST5[] showrecent();

	public native XLIST[] showtrack();

	// 顯示track file資料清單

	public native XLIST4[] sppoilist(int ii, int viewdeg);

	// 取的路徑清單第ii條線的POI List

	public native String ConvertKindId2Name(String POIClass, String fname);

	// POI代碼轉中文名稱,POIClass是代碼,fname是對照檔檔案名稱,回傳值是中文名稱

	public native String getcallbackstr(int i);

	public native String geteventname(int ec);

	// 由事件代碼取(ec)得事件名稱

	public native String getlocationname(int lc);

	// 由位置代碼(lc)取得位置名稱

	public native String getmapver();

	// 取得地圖版本相關資訊

	public native String getrelateword(String buf);

	// 取得關連字

	public native String getsppoi(int i);

	// 取得第i條線的POI

	public native String getsppoitxt(int ii, int viewdeg);

	// 取得路徑規劃結果第ii條線的POI,viewdeg是車行方向POI的前方搜尋角度,360度是全找

	public native String transphonword(String cmd);

	// 英文轉換注音符號

	public native String xytoroadname1(double x, double y);

	// 地圖xy座標對應到的路名

	public native String xytoroadname2(int scx, int scy);

	// 螢幕上xy座標對應到的路名

	public native void addfavorite(String str, double x, double y, int z, String tel, String mobile, String addr,
			String note);

	public native void addnavivoice(String file);

	// 播放語音

	public native void allocmapmem();

	// 讀回原地圖記憶體

	public native void arc(int x, int y, int w, int h, int b, int e);

	public native void bitblt(int dx, int dy, int sss);

	public native void bitbltalphacolor(int dx, int dy, int sss, int x, int y, int a);

	public native void bitbltalphastr(int dx, int dy, int sss, String sstr, int fs, int fixcolor, int a);

	public native void bitbltalphastr1(int dx, int dy, int sss, String sstr, int fs, int a, int x, int y);

	public native void bitbltcolor(int dx, int dy, int sss, int x, int y);

	public native void bitbltdeg(int dx, int dy, int sss, int deg);

	public native void bitbltstr(int dx, int dy, int sss, String sstr, int fs, int fixcolor);

	public native void bitbltstr1(int dx, int dy, int sss, String sstr, int fs);

	public native void closephon();

	// 關閉字庫

	public native void closesppoi();

	// 關閉路徑沿線POI

	public native void delallfavorite();

	public native void delallrecentpoi();

	public native void delfavorite(int g);

	public native void delrecentpoi(int g);

	public native void editfavorite(int g, String str, double x, double y, int z, String tel, String mobile,
			String addr, String note);

	public native void fillrect(int x, int y, int w, int h);

	public native void fontdrawstr(int fontsize, int xx, int yy, String sstr);

	public native void freemapmem();

	// 釋放地圖記憶體

	public native void line(int x1, int y1, int x2, int y2);

	public native void loadscr();

	public native void navifree(int sss);

	// 3ego元件的記憶體釋放函數

	public native void rect(int x, int y, int w, int h);

	public native void redrawmap();

	public native void redrawnow();

	public native void redrawnowrect(int x, int y, int w, int h);

	public native void reloaddrawmap();

	public native void resizeblt(int dx, int dy, int dw, int dh, int sss);

	public native void resizebltcolor(int dx, int dy, int dw, int dh, int sss, int x, int y);

	public native void savescr();

	public native void set3dbgh(int i);

	// 設定3d模式下，要蓋背景的高度

	public native void setalpha(int a);

	public native void setanti(int a);

	public native void setautobuffer(int i);

	public native void setavoidpoint(double x1, double y1, int m);

	public native void setbkcolorrgb(int r, int g, int b);

	public native void setcamvoice(int i);

	// 測速照相警告 1.警告,2.不警告

	public native void setcarbmp(int i);

	// 預設車輛樣式

	public native void setcolorrgb(int r, int g, int b);

	public native void setdonavi(int i);

	// 預設初始畫面時是否顯示路徑規劃線(不使用)

	public native void setdospeed(int i, int j, int k);

	// 設定是否撥報超速提示音,i:高速公路,j:快速道路,k:一般道路

	public native void setfixroad(int i);

	public native void sethide();

	public native void setinitmapw(double i);

	// 設定初始化圖寬

	public native void setinputmethod(int i);

	public native void setjpgresize(int w, int h);

	public native void setlinewidth(int w);

	public native void setmapright(int i);

	// 地圖向右偏移i個px

	public native void setmapdown(int i);

	// 導航時地圖向下偏移i個px

	public native void setmapstop(int i);

	public native void setmapw(double i);

	// 設定地圖顯示圖寬

	public native void setmovesleep(int i);

	// 設定導航模擬或是導航畫面位移時，停滯幾秒後回復原導航畫面

	public native void setnavibg_h(int i);

	// 設定導航背景圖的圖片高度

	public native void setnaviinfoshow(int i);

	public native void setnavivoice(int i);

	// 動態開關語音撥報功能,i=0關閉,i=1開啟

	public native void setnoredraw(int i);

	// i=1;強制不重繪;i=0;回復重繪

	public native void setonlymainroadtxt(int i);

	public native void setonlyroutelist(double i);

	public native void setothertxt(int i);

	public native void setpoiauto(int i);

	// 設定是否自動依據系統設定比例尺顯示POI類別

	public native void setpoitxt(int i);

	// 圖面是否顯示POI文字

	public native void setpoivid(int poiid);

	// 設定必顯示的POIID

	public native void setpoivisible(String pv6, String pv4, String pv2, int zlevel);

	// 設定POI顯示項目
	// pv6最大1024字元,pv4最大512字元,pv2最大64字元
	// zlevel表示zoomlevel多少時poi的顯示為何,範圍是0~5,zoomlevel>5，不會顯示POI
	// 如果要顯示全部的POI，只要同時將pv6,pv4,pv2都設成空字串即可，例如setpoivisible("","","",0);表示zoomlevel<=0時顯示所有POI

	public native void setpoivstr(String str);

	// 設定必顯示的POI名稱

	public native void setptvisible(int i);

	public native void setquickdraw(int i);

	public native void setresizefont(double i);

	public native void setrgb(int r, int g, int b);

	public native void setshortvoice(int i);

	public native void setshowbuild(int i);

	public native void setspfreemap(int m);

	public native void settargetxy(double x, double y);

	public native void settip(int i);

	public native void settmcok(int i);

	public native void settmcsleep(int i);

	public native void setttsrootpath(String str);

	// 設定tts暫存檔目錄位置

	public native void setttsvolume(int i);

	// 設定TTS的音量,0~1000,預設值750

	public native void setuimode(int i);

	// 設定UI模式,1:橫式,2:直式,3:480X272

	public native void setvolbmp(int i);

	public native void setvolcontrol(int i);

	public native void setwarnspeed(int i, int j, int k);

	// 設定超速容忍值,i:高速公路,j:快速道路,k:一般道路

	public native void setwarnvoice(int i);

	// 超速速限警告 1.警告,2.不警告

	public native void setzoombmp(int i);

	public native void stripstring(String sstr);

	public native void tmcreroute(int i);

	public native void zoomrect(double x1, double y1, double x2, double y2);

	// 左上右下座標來縮放一個適合比例的圖

	public native int initpeninput(int hwrdp);

	// 初始化手寫
	// hwrdp說明：0 // 簡繁體不做變化
	// 1 // 寫繁得簡
	// 2 // 寫簡得繁

	public native String getpeninput(short[] arr);

	// pp 的格式為 x,y,x,y,-1,0,x,y,x,y,-1,-1

	public native int freepeninput();

	// 關閉手寫

	public native int callproc();

	// Free memory created by 3ego engine
	public native void freesonav();

	public native void setusevh(int i);

	// 設定是否考慮NAV_MODE載具欄位資料,0預設，不考慮。1考慮

	public native void setspnoturn(int count, int method, int twice);

	// count:禁轉最多計算次數;
	// method:禁轉拿掉方式,=1全拿掉,=0拿掉第一個;
	// twice:是否計算拿掉前後兩次比較最短數值,1=算兩次，0=算一次就好
	// 預設值：count=10,method=1,twice=1

	public native void setoutwayroute(int sec1, int sec2, int len);// 設定偏移行程路徑規劃參數，
	// len代表偏移距離,預設30,sec1代表距離大於len時且等待秒數預設5秒時,路徑重新規劃。sec2代表距離小於len時且等待秒數預設10秒時,路徑重新規劃

	public native void setmyloc(int i);

	// 選擇在地圖上顯示 MyLoc 檔案中,哪個類別(功能)的Icon

	public native void reloadmyloc();

	// 重讀/重繪我的最愛檔案(放在BikingData資料夾中的myloc檔)

	public native void savescr2bmp(String filename);

	// 將畫面轉存圖檔

	public native void zoomspextend();

	// 縮放到路徑規劃全景範圍

	public void setMapEventListener(OnSonavEventListener listener) {
		this.mapEventListener = listener;
	}
}
