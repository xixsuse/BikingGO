package com.kingwaytek.api.caller;

/**
 * Caller argument for N3
 */
public class Caller
{
	/**
	 * 版本
	 */
	final static int VERSION = 20130610 ; 
	
	public final static String TAG 					= "NavikingCaller";
	public final static String CALLER_NAME			= "NAVIKING_N3_CALLER" ;	
	public final static boolean DEBUG	 			= false ; 
	public final static int NOW_VERSION 			= Version.VER_EVP ;
	
	// 以下作為Intent在傳送Extra資料的時候的Command Name
	public final static String CMD_NAME_TYPE				= "CMD_Type";
	public final static String CMD_NAME_POINT				= "STR_Point";
	public final static String CMD_NAME_START_POINT			= "STR_PARAM1";
	public final static String CMD_NAME_MID_POINT1			= "STR_MidPoint1";
	public final static String CMD_NAME_MID_POINT2			= "STR_MidPoint2";
	public final static String CMD_NAME_ADDR				= "STR_Addr";
	public final static String CMD_NAME_KEYWORD				= "STR_Keyword";
	public final static String CMD_NAME_GET_STATUS			= "STR_GetStatus";
	public final static String CMD_NAME_CATEGORY			= "STR_Cate";
	public final static String CMD_NAME_POINAME				= "STR_POIName";
	public final static String CMD_NAME_NAVISTATUS			= "INT_NAVI_STATUS";
	public final static String CMD_NAME_ROUTING_MODE		= "INT_RoutingMode";
	public final static String CMD_NAME_ROUTING_AVOID_MODE	= "INT_RoutingAvoidMode";
	public final static String CMD_NAME_LAUCH_NAVIKING		= "STR_LauchNaviKing" ;
	public final static String CMD_DEBUG_ON					= "BL_Debug";
	//public final static String CMD_NAME_FORCE_CLOSE_APP		= "STR_FORCE_CLOSE_APP";
	public final static String CMD_NAME_INT_LOGIN_STATUS	= "INT_LoginStatus";
	
	public final static String CMD_REVICED_SPEED 			= "INT_SPEED" ;
	public final static String CMD_REVICED_REGION 			= "STR_REGION" ;
	public final static String CMD_REVICED_ROAD_NAME 		= "STR_ROAD_NAME" ;
	public final static String CMD_REVICED_ROAD_CLASS 		= "INT_ROAD_CLASS" ;
		
	// 以下作為Intent在傳送Extra資料的時候的命令類別
	public final static String TYPE_NAVI_TO_POINT		= "Point2Navi";
	public final static String TYPE_NAVI_TO_ADDRESS		= "Navi2Addr";
	public final static String TYPE_NAVI_TO_KEYWORD		= "Navi2Keyword";
	public final static String TYPE_SET_ROUTING_MODE	= "SetRoutingMode";
	public final static String TYPE_SET_DETOUR			= "SetDetour";
	public final static String TYPE_ADD_MIDDLE_DEST		= "AddMidDest";
	public final static String TYPE_GET_ROAD_INFO		= "GetRoadInfo";
	public final static String TYPE_GET_NAVI_STATUS 	= "GetNaviStatus";
	public final static String TYPE_FORCE_CLOSE_APP 	= "ForceCloseApp";
	public final static String TYPE_SET_LOGIN_STATUS 	= "SetLoginStatus";
	
	// Return Type
	public final static int RTYPE_NAVI_TO_POINT		= 1 ;
	public final static int RTYPE_NAVI_TO_ADDRESS	= 2 ;
	public final static int RTYPE_NAVI_TO_KEYWORD	= 3 ;
	public final static int RTYPE_SET_ROUTING_MODE	= 4 ;
	public final static int RTYPE_SET_DETOUR		= 5 ;
	public final static int RTYPE_ADD_MIDDLE_DEST	= 6 ;
	public final static int RTYPE_GET_ROAD_INFO		= 7 ;
	public final static int RTYPE_GET_NAVI_STATUS 	= 8 ;
	
	// Return CMD Type
	public final static String RETURN_CMD_TYPE		= "ReturnCMDType" ;		
	
	// Return value
	public final static int RETURN_GET_ROAD_LOCATION_INFO = 1 ;
	public final static int RETURN_NAVIKING_STATUS 		= 2 ;
	
	public final static String INTENT_NAME ="NAVIKING" ;
	
	/**
	 * 道路迴避模式
	 * 
	 * @author jeff.lin
	 *
	 */
	public static class AvoidMode{
		/** 不進行設定 */
		public final static int DEFAULT 				= -1;
		/** 迴避收費站開啟 */
		public final static int AVOID_MODE_OPEN_TRUE 	= 1;
		/** 迴避收費站關閉 */
		public final static int AVOID_MODE_OPEN_FALSE 	= 0;
	}

	/** 
	 * 設定路徑規劃方式
	 * 
	 * @author jeff.lin
	 *
	 */
	public static class RoutingMethod{
		/** 不進行設定 */
		public final static int DEFAULT							= -1;
		/** 最佳路徑 */
		public final static int ROUTE_METHOD_BEST_PATH         	= 10;
		/** 最短路徑 */		
		public final static int ROUTE_METHOD_SHORTEST_DST     	= 60;
		/** 最短時間 */
		public final static int ROUTE_METHOD_SHORTEST_TIME    	= 50;
		/** 國一優先 */
		public final static int ROUTE_METHOD_HIGHWAY_NO1_PRIOR	= 30;
		/** 國二優先 */
		public final static int ROUTE_METHOD_HIGHWAY_NO2_PRIOR 	= 40;
		/** 避免高速公路 */
		public final static int ROUTE_METHOD_AVOID_HIGHWAY	   	= 80;
		/** 重型機車 */
		public final static int ROUTE_METHOD_MOTOR_550GREATER 	= 3000;
		/** 一般機車(550cc下) */
		public final static int ROUTE_METHOD_MOTOR_550LESS    	= 4000;
	}
	
	/**
	 * 目前道路等級
	 * 
	 * @author jeff.lin
	 *
	 */
	public static class RoadClass{
		/** 高速公路 */
		public final static int ROAD_CLASS_HIGHWAY 				= 1;
		/** 快速道路 */
		public final static int ROAD_CLASS_SPEEDWAY				= 2;
		/** 其他、一般道路 */
		public final static int ROAD_CLASS_OTHERS				= 3;
		
		/* TODO 迅易
		 * #define ROAD_CLASS_0 0 //中山高
			#define ROAD_CLASS_1 1 //二高
			#define ROAD_CLASS_2 2 //其它高
			#define ROAD_CLASS_4 4 //快速道路
			#define ROAD_CLASS_5 5 //交流道
			#define ROAD_CLASS_6 6 //省道
			#define ROAD_CLASS_7 7 //重要道路
			#define ROAD_CLASS_8 8 //縣、鄉道
			#define ROAD_CLASS_9 9 //慢車道
			#define ROAD_CLASS_10 10 //一般道路
			#define ROAD_CLASS_11 11 //巷弄
			#define ROAD_CLASS_12 12 //無名道路
			#define ROAD_CLASS_13 13 //人行、機踏車
			#define ROAD_CLASS_14 14 //計劃道路
		 */
	}
	
	/**
	 * 各種板號,日後各種版號將會有各種不同的功能,藉此來判斷
	 * @author jeff.lin
	 *
	 */
	public class Version{
		public final static int VER_ITS 		= 0 ;
		public final static int VER_EVP 		= 1 ;
		public final static int VER_HAMI 		= 2 ;
		public final static int VER_CHT			= 3 ; // 中華電信數分外包測試使用
	}
	
	/**
	 * 導航王回報目前導航狀態
	 * 
	 * @author jeff.lin
	 *
	 */
	public static class NaviKingStatus{
		/** 異常 */
		public final static int STATUS_ERROR 			= 1 ;
		/** 待機中 */
		public final static int STATUS_ON_CALL 			= 2 ;
		/** 路徑規劃中*/
		public final static int STATUS_ROUTING 			= 3 ;
		/** 導航中 */
		public final static int STATUS_NAVIGATING 		= 4 ;
		/** 模擬導航中 */
		public final static int STATUS_SIMULATING 		= 5 ;
		/** 已到達目的 */
		public final static int STATUS_ARRIVED_END		= 6 ;
		/** 已到經過點1 not use */
		public final static int STATUS_ARRIVED_POINT1	= 7 ;
		/** 已到經過點2 not use */
		public final static int STATUS_ARRIVED_POINT2	= 8 ;
		/** 路徑規劃失敗 */
		public final static int STATUS_ROUTING_FAIL 	= 9 ;
		/** 停止導航 */
		public final static int STATUS_STOP_ROUTING	 	= 10 ;
		/** 播放聲音 not use*/
		public final static int STATUS_SOUND_START	 	= 11 ;
		/** 結束聲音 not use*/
		public final static int STATUS_SOUND_STOP	 	= 12 ;
		/** 關閉導航 */
		public final static int STATUS_CLOSE_NAVI	 	= 13 ;
		
	}
}