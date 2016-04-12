package com.sonavtek.sonav;

/**
 * This class represents events from instance of eeego(the engine).
 */
public interface EngineEvent {

    /**
     * Native event: A new map image has been created and could be read by
     * calling getscr() method.
     */
    public static final int AM_PAINT = 25550;

    /**
     * Native event: 目前道路名稱 (導航時).<br/>
     * Parameter arg1 is the 路名標示，需使用getcallbackstr(arg2)取得。 ex. (泰林路/XX路) 或
     * (桃園)<br/>
     * Parameter arg2 is the 路名，需使用getcallbackstr(arg2)取得.
     */
    public static final int AM_NOWROADNAME = 25551;

    /**
     * Native event: 目前門牌號碼與道路等級 (導航).<br/>
     * Parameter arg1 為道路等級<br/>
     * 1. 路徑規劃後的地圖，依據路網class分級(參考{@link ROADLISTDATA}的圖資道路等級) <br/>
     * 2. 無路徑規劃的地圖(free navi，沒有目的地的導航)，定義為<br/>
     * 14: 國道<br/>
     * 13: 快速道路<br/>
     * 12: 交流道<br/>
     * 11: 省道<br/>
     * 10: 縣道 <br/>
     * 9: 鄉道<br/>
     * 8: 重要與一般道路<br/>
     * 7: 計劃與慢速道路<br/>
     * 6: 自行車道<br/>
     * 5: 巷弄與無名道路<br/>
     * <br/>
     * Parameter arg2 為門牌號碼代碼，需使用getcallbackstr(arg2)取得。
     */
    public static final int AM_NOWROADADDRNO = 25552;

    /**
     * Native event: The center of map is located in a new district.<br/>
     * Parameter arg1 is the ID of district. ex. 6502 Parameter arg2 is the name
     * of the district (name of city and town). ex. 台北市,中山區
     */
    public static final int AM_NOWCITYTOWN = 25553;

    /**
     * Native event: 到達目的地預估時間 (導航開始時).<br/>
     * Parameter arg2 is the minutes remaining. ex. 0 or 30
     */
    public static final int AM_NAVITIME = 25554;

    /**
     * Native event: 目的地方向 (導航開始時).<br/>
     * Parameter arg2 is the (what??). ex. 176 or -78
     */
    public static final int AM_NAVIDISTDIR = 25555;

    /**
     * Native event: 到達目的地預估距離 (導航開始時).<br/>
     * Parameter arg2 is the distance from user location to the destination in
     * meter. ex. 0 or 66
     */
    public static final int AM_NAVIDIST = 25556;

    /**
     * Native event: 指北針方向 (導航開始時).<br/>
     * Parameter arg2 is the (what??). ex. 0 or ??
     */
    public static final int AM_NAVIDIR = 25557;

    /**
     * Native event: 路口實境道路轉向.<br/>
     * Parameter arg2 is the ID of turn. Possible values are
     * {@link ROADLISTDATA#STRAIGHT}, {@link ROADLISTDATA#TURN_LEFT},
     * {@link ROADLISTDATA#TURN_RIGHT}, {@link ROADLISTDATA#KEEP_LEFT},
     * {@link ROADLISTDATA#KEEP_RIGHT}, {@link ROADLISTDATA#LEFT_TURN},
     * {@link ROADLISTDATA#RIGHT_TURN}, {@link ROADLISTDATA#THREE_FORK_LEFT},
     * {@link ROADLISTDATA#THREE_FORK_STRAIGHT},
     * {@link ROADLISTDATA#THREE_FORK_RIGHT}, {@link ROADLISTDATA#N_FORK_LEFT},
     * {@link ROADLISTDATA#N_FORK_CENTER_LEFT},
     * {@link ROADLISTDATA#N_FORK_CENTER_RIGHT},
     * {@link ROADLISTDATA#N_FORK_RIGHT},
     * {@link ROADLISTDATA#TURN_LEFT_KEEP_RIGHT},
     * {@link ROADLISTDATA#TURN_RIGHT_KEEP_LEFT},
     * {@link ROADLISTDATA#KEEP_LEFT_GO_STRAIGHT}, and
     * {@link ROADLISTDATA#KEEP_RIGHT_GO_STRAIGHT}.
     */
    public static final int AM_VRTURNBMP = 25558;

    /**
     * Native event: 路口實境道路名稱<br/>
     * Parameter arg2 is the name of road. 需使用getcallbackstr(arg2)取得 ex.
     * 新生高架道路(長安路口) or 圓山交流道(國1 路口)
     */
    public static final int AM_VRROAD = 25559;

    /**
     * <span class="important">Not verified.</span><br/>
     * 路口實境圖片<br/>
     * Parameter arg2 is (what??) ex. 1775952
     */
    public static final int AM_VRPIC = 25560;

    /**
     * Native event: 距路口實境距離.<br/>
     * Parameter arg2: 測速照相儀器距離(m) ex. 139
     */
    public static final int AM_VRDIST = 25561;

    /**
     * Native event: 下一條路的路標轉向.<br/>
     * Parameter arg2 is the (what??). 需使用getcallbackstr(arg2)取得 ex. c4
     */
    public static final int AM_NEXTTURN1 = 25562;

    /**
     * Native event: 以逗號分隔下n條路轉向 (導航時).<br/>
     * Parameter arg1 is the (what?? or 0). Possible values are:<br/>
     * 1: 橋樑<br/>
     * 2: 圓環<br/>
     * 3: 進匝道<br/>
     * 4: 出匝道<br/>
     * 5: 隧道<br/>
     * 6: 地下道<br/>
     * 7: 高架道路<br/>
     * <br/>
     * Parameter arg2 is the 轉向圖示，需使用getcallbackstr(arg2)取得以逗號(,)分隔代號的字串.
     * Possible values are {@link ROADLISTDATA#STRAIGHT},
     * {@link ROADLISTDATA#TURN_LEFT}, {@link ROADLISTDATA#TURN_RIGHT},
     * {@link ROADLISTDATA#KEEP_LEFT}, {@link ROADLISTDATA#KEEP_RIGHT},
     * {@link ROADLISTDATA#LEFT_TURN}, {@link ROADLISTDATA#RIGHT_TURN},
     * {@link ROADLISTDATA#THREE_FORK_LEFT},
     * {@link ROADLISTDATA#THREE_FORK_STRAIGHT},
     * {@link ROADLISTDATA#THREE_FORK_RIGHT}, {@link ROADLISTDATA#N_FORK_LEFT},
     * {@link ROADLISTDATA#N_FORK_CENTER_LEFT},
     * {@link ROADLISTDATA#N_FORK_CENTER_RIGHT},
     * {@link ROADLISTDATA#N_FORK_RIGHT},
     * {@link ROADLISTDATA#TURN_LEFT_KEEP_RIGHT},
     * {@link ROADLISTDATA#TURN_RIGHT_KEEP_LEFT},
     * {@link ROADLISTDATA#KEEP_LEFT_GO_STRAIGHT}, and
     * {@link ROADLISTDATA#KEEP_RIGHT_GO_STRAIGHT}.
     */
    public static final int AM_NEXTTURN = 25563;

    /**
     * Native event: 下n條路路名 (導航時).<br/>
     * Parameter arg1 is the 以逗號分隔下n個路名標示，需以getcallbackstr(arg1)取得. ex. (目的地) or
     * (北 國1)<br/>
     * Parameter arg2 is the 以逗號分隔下n個路名，需以getcallbackstr(arg2)取得. ex. 中山北路二段
     */
    public static final int AM_NEXTROAD = 25564;

    /**
     * Native event: 下n條路距離 (導航時).<br/>
     * Parameter arg2 is the 以逗號分隔下n個距離，需以getcallbackstr(arg2)取得字串(what??). ex.
     * ?? or a negative value
     */
    public static final int AM_NEXTDIST = 25565;

    /**
     * Native event: 測速照相.<br/>
     * Parameter arg1: 道路限速(km/h) ex. 50 Parameter arg2: 測速照相儀器距離(m) ex. 139
     */
    public static final int AM_CAMERA = 25567;

    /**
     * Native event: 超速警示.<br/>
     * Parameter arg1: 道路限速(km/h) ex. 30 Parameter arg2: 目前車速(km/h) ex. 40
     */
    public static final int AM_LIMITSPEED = 25568;

    /**
     * Native event: 路徑規劃事件.<br/>
     * Parameter arg2 is the action. Possible values are
     * {@link #START_ROUTING_PATH_PLANING}, {@link #ROUTING_PATH_GERENRATING},
     * and {@link #ROUTING_PATH_GENERATED}.<br/>
     * <br/>
     * 正常的觸發順序為 {@link #START_ROUTING_PATH_PLANING} ->
     * {@link #ROUTING_PATH_GERENRATING} -> {@link #ROUTING_PATH_GENERATED}.
     */
    public static final int AM_ROUTE = 25569;

    /**
     * Native event: 導航(或模擬)時，使用者觸碰地圖時會發生的事件.<br/>
     * Parameter arg2: 1:開始移圖, 0:回復導航狀態
     */
    public static final int AM_MOVE = 25571;

    /**
     * Native event: The instance has been initialized.
     */
    public static final int AM_CREATE = 25576;

    /**
     * Native event: The instance is going to initializing.
     */
    public static final int AM_START = 25577;

    /**
     * Native event: 導航到達目的地.
     */
    public static final int AM_DESTINATION = 25579;

    /**
     * Native event: Sound file is ready to be played. Using getsnd() to get the
     * name of sound file.
     */
    public static final int AM_PLAYSOUND = 25583;

    /**
     * <span class="important">Not verified.</span><br/>
     * TMC<br/>
     * char *aaa;<br/>
     * aaa=(char*)arg2;<br/>
     * 此為逗號分隔字串，分別是: 事件代碼,位置代碼,距離可由此代碼利用geteventname或getlocationname取得名稱
     */
    public static final int AM_TMCMSG = 25566;

    /**
     * <span class="important">Not verified.</span><br/>
     * 目前TIP資料<br/>
     * char *aaa;<br/>
     * int bb;<br/>
     * aaa=(char*)arg2; //tip資訊，逗號分隔各資訊<br/>
     * bb=arg1; //tip類別,0=道路,1=POI,2=行政區
     */
    public static final int AM_TIP = 25570;

    /**
     * <span class="important">Not verified.</span><br/>
     * 語音檔<br/>
     * char *aaa;<br/>
     * aaa=(char*)arg2;
     */
    public static final int AM_VOICE = 25572;

    /**
     * <span class="important">Not verified.</span><br/>
     * 交流道<br/>
     * char *aaa;<br/>
     * int bb;<br/>
     * aaa=(char*)arg2; //名稱<br/>
     * bb=arg1; //距離
     */
    public static final int AM_SLIP = 25573;

    /**
     * <span class="important">Not verified.</span><br/>
     * 休息區<br/>
     * char *aaa;<br/>
     * int bb;<br/>
     * aaa=(char*)arg2; //名稱<br/>
     * bb=arg1; //距離
     */
    public static final int AM_REST = 25574;

    /**
     * <span class="important">Not verified.</span><br/>
     * 休息區<br/>
     * int aa;<br/>
     * int bb;<br/>
     * aa=arg2; //第幾個機過點<br/>
     * bb=arg1; //距離
     */
    public static final int AM_PASS = 25575;

    /**
     * <span class="important">Not verified.</span><br/>
     * 收費站<br/>
     * char *aaa;<br/>
     * int bb;<br/>
     * aaa=(char*)arg2; //名稱<br/>
     * bb=arg1; //距離
     */
    public static final int AM_TOLL = 25578;

    /**
     * <span class="important">Not verified.</span><br/>
     */
    public static final int AM_PROCESS = 25580;

    /**
     * <span class="important">Not verified.</span><br/>
     * GPS狀態
     */
    public static final int AM_GPSSTATUS = 25581;
    
    /**
     * For UBIKE 使用 2013/11/27 yawhaw
     * 點選UBIKE場站Marker時取的該點ID
     */
    public static final int AM_NEWTIP = 25586;  

    /**
     * <span class="important">Not verified.</span><br/>
     * Shortpath記憶體狀態<br/>
     * int aa;<br/>
     * int bb;<br/>
     * aa=arg2; //記憶體狀態,0~4(數字越小越良好)。0,1,2良好，3,4不足<br/>
     * bb=arg1; //是否啟用釋放圖資記憶體,0啟用,1不啟用
     */
    public static final int AM_SPSTATUS = 25582;
    
    public static final int  AM_NOWROADLAYER =25584;

    /** The engine is starting to plan routing path */
    public static final int START_ROUTING_PATH_PLANING = 1;

    /** Routing path planing is done, The engine is going to generate results */
    public static final int ROUTING_PATH_GERENRATING = 0;

    /** The engine is starting to plan routing path */
    public static final int ROUTING_PATH_GENERATED = 2;
    
   }
