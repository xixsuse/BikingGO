package com.sonavtek.sonav;

import java.util.LinkedHashMap;
import java.util.Map;

import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;

/**
 * PathFinder to calculating navigation paths by providing one origin, and 3
 * destinations which includes 2 via points and 1 destination.
 * 
 * @author Andy Chiao
 */
public class PathFinder {

    /** 規畫方式: 系統推薦路徑 */
    public final static int SUGGEST = 10;
    
    /** 規畫方式: 系統推薦路徑+收費道路避走 */
    public final static int SUGGEST_AVOID_TOLL = 11;
    
    /** 規畫方式: 高速公路優先 */
    public final static int METHOD_HIGHWAY = 20;
    
    /** 規畫方式: 高速公路優先+收費道路避走 */
    public final static int HIGHWAY_AVOID_TOLL = 21;
    
    /** 規畫方式: 國道1號優先 */
    public final static int NATIONAL_HIGHWAY1 = 30;
    
    /** 規畫方式: 國道1號優先+收費道路避走 */
    public final static int NATIONAL_HIGHWAY1_AVOID_TOLL = 31;

    /** 規畫方式: 國道3號優先 */
    public final static int NATIONAL_HIGHWAY3 = 40;
    
    /** 規畫方式: 國道3號優先+收費道路避走 */
    public final static int NATIONAL_HIGHWAY3_AVOID_TOLL = 41;
    
    /** 規畫方式: 最短時間優 */
    public final static int SHORTEST_TIME = 50;
    
    /** 規畫方式: 最短時間優先+收費道路避走 */
    public final static int SHORTEST_TIME_AVOID_TOLL = 51;
    
    /** 規畫方式: 最短距離優先 */
    public final static int SHORTEST_DIST = 60;
    
    /** 規畫方式: 最短距離優先+收費道路避走 */
    public final static int SHORTEST_DIST_AVOID_TOLL = 61;

    /** 規畫方式: 一般道路優先 */
    public final static int NORMAL_ROAD = 70;
    
    /** 規畫方式: 一般道路優先+收費道路避走 */
    public final static int NORMAL_ROAD_AVOID_TOLL = 71;

    /** 規畫方式: 行人導航 (單車ing的最短路徑)*/
    public final static int WALKING = 1000;
    
    /** 規畫方式: 自行車導航 */
    public final static int BICYCLE = 2000;
    
    /** 規畫方式: 大型重型機車(Large Heavy Motorcycle)(>=550CC)導航 */
    public final static int LH_MOTORBIKE = 3000;
    
    /** 規畫方式: 機車(<550CC)導航 */
    public final static int MOTORBIKE = 4000;
    
    /** The type of point is the starting point for path planning */
    public static final int START_POINT = 0;

    /**
     * The type of point is the first essential point to pass through for path
     * planning
     */
    public static final int ESSENTIAL_POINT1 = 1;

    /**
     * The type of point is the second essential point to pass through for path
     * planning
     */
    public static final int ESSENTIAL_POINT2 = 2;

    /** The type of point should not be pass through for path planning */
    public static final int AVOID_POINT = 3;

    /** The type of point is the destination for path planning */
    public static final int DESTINATION_POINT = 4;

    private static PathFinder instance; // the only instance.
    private sonav engine; // eeego engine

    private int method; // 規畫方式
    private GeoPoint gptOrigin; // 起點
    private GeoPoint gptViaOne; // 中繼點一
    private GeoPoint gptViaTwo; // 中繼點二
    private GeoPoint gptDestin; // 終點

    private Map<Integer, ROADLISTDATA> mapRoadData; // 路徑規劃結果清單
    private int pathStatus; // 路徑規劃狀態

    // 狀態代碼.0=已初始化, 1=路徑計算中, 2=路徑計算完成
    public final static int PATH_FINDER_READY = 0;
    public final static int PATH_FINDING_INPROGRESS = 1;
    public final static int PATH_FINDING_DONE = 2;

    /**
     * The instance of PathFinder can only has one. Others should call
     * getInstance() method to get the singleton.
     */
    private PathFinder() {
    }

    /**
     * gets current instance of PathFinder, create one if it does not exist.
     * 
     * @return instance of PathFinder
     */
    public static PathFinder getInstance() {
        if (instance == null) {
            synchronized (PathFinder.class) {
                if (instance == null) {
                    instance = new PathFinder();

                    instance.setEngine(sonav.getInstance());
                    instance.getEngine().getPathEventHandler()
                            .setPathEngine(instance);
                    instance.setStatus(PATH_FINDER_READY);
                }
            }
        }
        return instance;
    }

    /**
     * @return eeego_engine
     */
    public sonav getEngine() {
        return engine;
    }

    /**
     * sets eeego_engine.
     * 
     * @param engine
     *            to set
     */
    public void setEngine(sonav engine) {
        this.engine = engine;
    }

    /**
     * Get the methodology for finding path.
     * 
     * @return The methodology for finding path.
     */
    public int getMethod() {
        return method;
    }

    /**
     * Set the methodology for finding path.
     * 
     * @param method The methodology to set.
     */
    public void setMethod(int method) {
        this.method = method;
    }

    /**
     * gets point of Origin.
     * 
     * @return GeoPoint contains Origin
     */
    public GeoPoint getOrigin() {
        if (gptOrigin == null)
            return null;
        if (gptOrigin.getLongitude() == 0 || gptOrigin.getLatitude() == 0) {
            return null;
        }
        return gptOrigin;
    }

    /**
     * gets point of Via Point 1.
     * 
     * @return GeoPoint contains Via Point 1
     */
    public GeoPoint getViaOne() {
        if (gptViaOne == null)
            return null;
        if (gptViaOne.getLongitude() == 0 || gptViaOne.getLatitude() == 0) {
            return null;
        }
        return gptViaOne;
    }

    /**
     * gets point of Via Point 2.
     * 
     * @return GeoPoint contains Via Point 2
     */
    public GeoPoint getViaTwo() {
        if (gptViaTwo == null)
            return null;
        if (gptViaTwo.getLongitude() == 0 || gptViaTwo.getLatitude() == 0) {
            return null;
        }
        return gptViaTwo;
    }

    /**
     * gets point of Destination.
     * 
     * @return GeoPoint contains Destination
     */
    public GeoPoint getDestin() {
        if (gptDestin == null)
            return null;
        if (gptDestin.getLongitude() == 0 || gptDestin.getLatitude() == 0) {
            return null;
        }
        return gptDestin;
    }

    /**
     * sets Origin Point.
     * 
     * @param gptOrigin
     */
    public void setOrigin(GeoPoint gptOrigin) {
        if (gptOrigin == null) {
            throw new IllegalArgumentException("Argument can not be null.");
        }
        if (gptOrigin.getLongitude() == 0 || gptOrigin.getLatitude() == 0) {
            throw new IllegalArgumentException(
                    "Argument contains illegal data.");
        }
        this.gptOrigin = gptOrigin;
    }

    /**
     * sets Via Point 1.
     * 
     * @param gptViaOne
     */
    public void setViaOne(GeoPoint gptViaOne) {
        if (gptViaOne == null) {
            throw new IllegalArgumentException("Argument can not be null.");
        }
        if (gptViaOne.getLongitude() == 0 || gptViaOne.getLatitude() == 0) {
            throw new IllegalArgumentException(
                    "Argument contains illegal data.");
        }
        this.gptViaOne = gptViaOne;
    }

    /**
     * sets Via Point 2.
     * 
     * @param gptViaTwo
     */
    public void setViaTwo(GeoPoint gptViaTwo) {
        if (gptViaTwo == null) {
            throw new IllegalArgumentException("Argument can not be null.");
        }
        if (gptViaTwo.getLongitude() == 0 || gptOrigin.getLatitude() == 0) {
            throw new IllegalArgumentException(
                    "Argument contains illegal data.");
        }
        this.gptViaTwo = gptViaTwo;
    }

    /**
     * sets Destination Point.
     * 
     * @param gptDestin
     */
    public void setDestin(GeoPoint gptDestin) {
        if (gptDestin == null) {
            throw new IllegalArgumentException("Argument can not be null.");
        }
        if (gptDestin.getLongitude() == 0 || gptOrigin.getLatitude() == 0) {
            throw new IllegalArgumentException(
                    "Argument contains illegal data.");
        }
        this.gptDestin = gptDestin;
    }

    /**
     * gets calculated path results in Map form.
     * 
     * @return LinkedHashMap contains path result.
     */
    public Map<Integer, ROADLISTDATA> getPathData() {
        return mapRoadData;
    }

    /**
     * gets PathFinder instance status code.
     * 
     * @return status code of PathFinder
     */
    public int getStatus() {
        return pathStatus;
    }

    /**
     * sets PathFinder instance status code.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.pathStatus = status;
    }

    /**
     * gets calculated path results.
     * 
     * @return array of ROADLISTDATA contains result.
     */
    public ROADLISTDATA[] getPathResult() {
        if (mapRoadData == null)
            return null;
        if (mapRoadData.size() == 0)
            return null;

        return mapRoadData.values().toArray(
                new ROADLISTDATA[mapRoadData.size()]);
    }

    /**
     * loads calculated path data from eeego_engine to a LinkedMap.
     */
    protected void loadFoundPathData() {
        if (!isPathFoundSuccessful())
            return;

        int roadCount = engine.getroadlistnum();
        int currentRoadIndex = engine.getnowroadidx();

        if (roadCount == 0)
            return;

        if (mapRoadData == null)
            mapRoadData = new LinkedHashMap<Integer, ROADLISTDATA>();
        if (this.mapRoadData.size() != 0)
            this.mapRoadData.clear();

        for (int i = currentRoadIndex; i < roadCount; i++) {
            ROADLISTDATA foundPath = new ROADLISTDATA();
            foundPath = engine.getroadlist(foundPath, i);
            this.mapRoadData.put(i, foundPath);
        }

        return;
    }

    /**
     * core method that calls eeego_engine to calculate navigation path. it will
     * call eeego_engine to set origin, 2 via points, and destination then
     * calculate path in a none pointed thread.
     * 
     * @throws Exception
     *             engine has not been initialized exception.
     * 
     */
    public synchronized void FindPath() throws Exception {
        if (engine.getState() != sonav.STATE_READY) {
            Log.w("PathFinder",
                    "eeego has not been initialized or initialization is not done.");
            throw new Exception("engine has not been initialized.");
            // return; // 底層引擎尚未(初始化/完成初始化).
        }

        if (gptOrigin == null) {
            Log.w("PathFinder", "Origin has not set.");
            return; // 沒有起點
        }

        // Collect Destinations
        Map<String, GeoPoint> mapDestinations = new LinkedHashMap<String, GeoPoint>(
                3);

        if (gptDestin != null)
            mapDestinations.put("gptDestin", gptDestin);
        if (gptViaTwo != null)
            mapDestinations.put("gptViaTwo", gptViaTwo);
        if (gptViaOne != null)
            mapDestinations.put("gptViaOne", gptViaOne);

        Object[] gptDestinations = mapDestinations.values().toArray();

        int dCount = gptDestinations.length;
        if (dCount == 0) {
            Log.w("PathFinder", "Destination has not set.");
            return; // 沒有終點
        }

        try {
            Log.i("PathFinder", "Call Native 'setroutemethod'.");
            engine.setroutemethod(method);
        } catch (Exception e) {
            Log.e("PathFinder", "Error setroutemethod: " + e);
            // TODO: handle exception
        }

        // Origin
        try {
            Log.i("PathFinder", "Call Native 'setroutepoint1' to set origin.");
            engine.setroutepoint1(gptOrigin.getLongitude(),
                    gptOrigin.getLatitude(), 0);
        } catch (Exception e) {
            Log.e("PathFinder", "Error setroutepoint1 for origin: " + e);
            // TODO: handle exception
        }

        // Destination
        try {
            Log.i("PathFinder",
                    "Call Native 'setroutepoint1' to set final destination.");
            engine.setroutepoint1(
                    ((GeoPoint) gptDestinations[0]).getLongitude(),
                    ((GeoPoint) gptDestinations[0]).getLatitude(), 4);
        } catch (Exception e) {
            Log.e("PathFinder", "Error setroutepoint1 for destination: " + e);
            // TODO: handle exception
        }

        // Via Points min=0 max=2
        for (int i = 1; i <= dCount - 1; i++) {
            try {
                Log.i("PathFinder",
                        "Call Native 'setroutepoint1' to set via points(" + i
                        + ").");
                engine.setroutepoint1(
                        ((GeoPoint) gptDestinations[i]).getLongitude(),
                        ((GeoPoint) gptDestinations[i]).getLatitude(), i);
            } catch (Exception e) {
                Log.e("PathFinder", "Error setroutepoint1 for via points(" + i
                        + "): " + e);
                // TODO: handle exception
            }
        }

        // FindPath
        Log.i("PathFinder", "Call Native 'runshortpath' main job.");
        Thread pathThread = new Thread(new Runnable() {
            public void run() {
                try {
                    setStatus(PATH_FINDING_INPROGRESS);
                    // String abc = null;
                    engine.runshortpath(2, 1, 1);
                    // abc.toString();
                    Log.i("PathFinder", "FinderThread Initiated.");
                } catch (Exception e) {
                    Log.e("PathFinder", "FinderThread has an Exception : " + e);
                    setStatus(PATH_FINDER_READY);
                }
            }
        });

        pathThread.start();
    }

    /**
     * check if Path been calculated successfully.
     * 
     * @return true if path successfully calculated, false otherwise
     */
    public boolean isPathFoundSuccessful() {
        int routeResult;

        try {
            routeResult = engine.getshortpathok();
        } catch (Exception e) {
            Log.e("PathFinder", "Error getshortpathok: " + e);
            // TODO: handle exception
            routeResult = 0;
        }

        if (routeResult > 0) {
            Log.i("PathFinder", "Path successfully found. magicNumber = "
                    + routeResult);
            return true;
        } else {
            Log.i("PathFinder", "NO Path Found or Error occured.");
            return false;
        }

        // return true;
    }
}
