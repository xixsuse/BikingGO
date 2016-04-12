package com.sonavtek.sonav;

/**
 * This class represents one of roads from routing plan. Use getroadlist()
 * method to get the roads.<br/>
 * The content will be like:<br/>
 * <br/>
 * ROADLISTDATA [longitude=121.52466154098511, latitude=25.0608868598938,
 * name=錦州街23巷, clazz=8, type=0, turn=2, length=52.79244212358193]<br/>
 * ROADLISTDATA [longitude=121.5252456665039, latitude=25.060302734375,
 * name=錦州街, clazz=7, type=0, turn=1, length=57.78918132896671]<br/>
 * ROADLISTDATA [longitude=121.52560424804688, latitude=25.060287475585938,
 * name=林森北路, clazz=7, type=0, turn=2, length=36.3073390548482]<br/>
 * ROADLISTDATA [longitude=121.52330780029297, latitude=25.044921875, name=林森南路,
 * clazz=7, type=0, turn=0, length=1580.075224923074]<br/>
 * ROADLISTDATA [longitude=121.52314758300781, latitude=25.04065704345703,
 * name=仁愛路一段23巷, clazz=8, type=0, turn=1, length=20.77586623694091]<br/>
 * ROADLISTDATA [longitude=121.52421283721924, latitude=25.03901958465576,
 * name=杭州南路一段18巷(終點), clazz=8, type=0, turn=0, length=64.59638313704647]<br/>
 */
public class ROADLISTDATA {

    /** 導航指示: 直行 */
    public static final int STRAIGHT = 0;

    /** 導航指示: 左轉 */
    public static final int TURN_LEFT = 1;

    /** 導航指示: 右轉 */
    public static final int TURN_RIGHT = 2;

    /** 導航指示: 靠左 */
    public static final int KEEP_LEFT = 3;

    /** 導航指示: 靠右 */
    public static final int KEEP_RIGHT = 4;

    /** 導航指示: 左迴轉 */
    public static final int LEFT_TURN = 5;

    /** 導航指示: 右迴轉 */
    public static final int RIGHT_TURN = 6;

    /** 導航指示: 三叉路靠左 */
    public static final int THREE_FORK_LEFT = 7;

    /** 導航指示: 三叉路靠中 */
    public static final int THREE_FORK_STRAIGHT = 8;

    /** 導航指示: 三叉路靠右 */
    public static final int THREE_FORK_RIGHT = 9;

    /** 導航指示: 多叉路靠左 */
    public static final int N_FORK_LEFT = 10;

    /** 導航指示: 多叉路靠中左 */
    public static final int N_FORK_CENTER_LEFT = 11;

    /** 導航指示: 多叉路靠中右 */
    public static final int N_FORK_CENTER_RIGHT = 12;

    /** 導航指示: 多叉路靠右 */
    public static final int N_FORK_RIGHT = 13;

    /** 導航指示: 左轉靠右行駛 */
    public static final int TURN_LEFT_KEEP_RIGHT = 14;

    /** 導航指示: 右轉靠左行駛 */
    public static final int TURN_RIGHT_KEEP_LEFT = 15;

    /** 導航指示: 靠左直行 */
    public static final int KEEP_LEFT_GO_STRAIGHT = 16;

    /** 導航指示: 靠右直行 */
    public static final int KEEP_RIGHT_GO_STRAIGHT = 17;

    /** 道路類別: 國道 */
    public static final int NATIONAL_HIGHWAY = 1;

    /** 道路類別: 快速道路 */
    public static final int EXPRESSWAY = 2;

    /** 道路類別: 高架道路 */
    public static final int VIADUCT = 3;

    /** 道路類別: 地下道 */
    public static final int UNDERPASS = 4;

    /** 道路類別: 平面車道 */
    public static final int NORMAL_ROAD = 5;

    /** 道路類別: 橋樑 */
    public static final int BRIDGE = 6;

    /** 道路類別: 隧道 */
    public static final int TUNNEL = 7;

    /** 道路類別: 圓環 */
    public static final int RING_ROAD = 8;

    /** 道路類別: 危險道路 */
    public static final int DANGER_ROAD = 9;

    /** 道路類別: 交流道 */
    public static final int INTERCHANGE = 10;

    /** 道路類別: 迴轉道 */
    public static final int U_TURN = 11;

    /** 道路類別: 系統交流道 */
    public static final int SYSTEM_INTERCHANGE = 12;

    /** 道路類別: 休息站 */
    public static final int REST_STOP = 13;

    /** 道路類別: 收費站 */
    public static final int TOLL = 14;

    /** 道路類別: 服務區 */
    public static final int SERVICE_STOP = 15;

    /** 道路類別: 快車道 */
    public static final int FAST_LANE = 16;

    /** 道路類別: 慢車道 */
    public static final int SLOW_LANE = 17;
    
    /** 圖資道路等級: 國道 */
    public static final int CLS_NATIONAL_HIGHWAY  = 0;
    
    /** 圖資道路等級: 快速道路 */
    public static final int CLS_EXPRESSWAY  = 1;

    /** 圖資道路等級: 交流道 */
    public static final int CLS_INTERCHANGE  = 2;

    /** 圖資道路等級: 省道 */
    public static final int CLS_PROVINCIAL_HIGHWAY  = 3;

    /** 圖資道路等級: 縣道 */
    public static final int CLS_COUNTY_ROAD  = 4;
    
    /** 圖資道路等級: 鄉道 */
    public static final int CLS_TOWNSHIP  = 5;

    /** 圖資道路等級: 重要道路 */
    public static final int CLS_MAJOR_ROAD  = 6;

    /** 圖資道路等級: 一般道路*/
    public static final int CLS_NORMAL_ROAD  = 7;

    /** 圖資道路等級: 巷弄*/
    public static final int CLS_ALLEY  = 8;

    /** 圖資道路等級: 慢車道(對應省道的慢車道) */
    public static final int CLS_PROVINCIAL_SLOW  = 9;

    /** 圖資道路等級: 慢車道(對應縣道的慢車道) */
    public static final int CLS_COUNTY_SLOW = 10;

    /** 圖資道路等級: 慢車道(對應鄉道/重要道路的慢車道) */
    public static final int CLS_TOWNSHIP_MAJOR_SLOW = 11;

    /** 圖資道路等級: 慢車道(對應一般道路的慢車道) */
    public static final int CLS_NORMAL_SLOW = 12;

    /** 圖資道路等級: 無名道路 */
    public static final int CLS_UNKNOWN_ROAD = 13;

    /** 圖資道路等級: 計畫道路 */
    public static final int CLS_PLAN_ROAD = 14;

    private double x; // longitude of the road
    private double y; // latitude of the road
    private String roadname; // name of the road
    private int turn; // turn at the intersection
    private int rdclass; // class of the road
    private int rdstate; // type of the road
    private double len; // length of the road

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return x;
    }

    /**
     * @param lon
     *            the longitude to set
     */
    public void setLongitude(double lon) {
        this.x = lon;
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return y;
    }

    /**
     * @param lat
     *            the latitude to set
     */
    public void setLatitude(double lat) {
        this.y = lat;
    }

    /**
     * @return the name
     */
    public String getName() {
        return roadname;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.roadname = name;
    }

    /**
     * @return the clazz
     */
    public int getClazz() {
        return rdclass;
    }

    /**
     * @param clazz
     *            the clazz to set
     */
    public void setClazz(int clazz) {
        this.rdclass = clazz;
    }

    /**
     * Returns the type of the road.
     * 
     * @return the type
     */
    public int getType() {
        return rdstate;
    }

    /**
     * Set the type of the road.
     * 
     * @param type
     *            The type to set. Possible values are NATIONAL_HIGHWAY,
     *            EXPRESSWAY, VIADUCT, UNDERPASS, NORMAL_ROAD, BRIDGE, TUNNEL,
     *            RING_ROAD, DANGER_ROAD, INTERCHANGE, U_TURN,
     *            SYSTEM_INTERCHANGE, REST_STOP, TOLL, SERVICE_STOP, FAST_LANE,
     *            SLOW_LANE.
     */
    public void setType(int type) {
        this.rdstate = type;
    }

    /**
     * Returns the turn of the road.
     * 
     * @return the turn
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Set the turn of the road.
     * 
     * @param turn
     *            The turn to set. Possible values are STRAIGHT, TURN_LEFT,
     *            TURN_RIGHT, KEEP_LEFT, KEEP_RIGHT, LEFT_TURN, RIGHT_TURN,
     *            THREE_FORK_LEFT, THREE_FORK_STRAIGHT, THREE_FORK_RIGHT,
     *            N_FORK_LEFT, N_FORK_CENTER_LEFT, N_FORK_CENTER_RIGHT,
     *            N_FORK_RIGHT, TURN_LEFT_KEEP_RIGHT, TURN_RIGHT_KEEP_LEFT,
     *            KEEP_LEFT_GO_STRAIGHT, and KEEP_RIGHT_GO_STRAIGHT.
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }

    /**
     * @return the length
     */
    public double getLength() {
        return len;
    }

    /**
     * @param length
     *            the length to set
     */
    public void setLength(double length) {
        this.len = length;
    }

    @Override
    public String toString() {
        return "ROADLISTDATA [longitude=" + x + ", latitude=" + y +
                ", name=" + roadname + ", clazz=" + rdclass +
                ", type=" + rdstate + ", turn=" + turn + ", length=" + len + "]";
    }
}
