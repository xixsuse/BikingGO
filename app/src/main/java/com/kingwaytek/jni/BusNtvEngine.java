package com.kingwaytek.jni;



import android.util.Log;

public class BusNtvEngine {
	// Constant definitions
	public static final int CATEGORY_ID_CITYBUS = 1101;		// 公車
	public static final int CATEGORY_ID_SUBWAY = 1102;		// 捷運
	public static final int CATEGORY_ID_COUNTRYBUS = 1103;	// 長途客運
	public static final int CATEGORY_ID_TRAIN = 1104;		// 火車
	public static final int CATEGORY_ID_BULLETTRAIN = 1105;	// 高鐵
	public static final int CATEGORY_ID_AIRPLANE = 1106;	// 飛機
	public static final int CATEGORY_ID_SHIP = 1107;		// 船舶	

	public static final int ROUTE_TYPE_ALL = 0;
	public static final int ROUTE_TYPE_CITYBUS = 1;
	public static final int ROUTE_TYPE_SUBWAY = 2;
	public static final int ROUTE_TYPE_COUNTRYBUS = 4;
	public static final int ROUTE_TYPE_TRAIN = 8;
	public static final int ROUTE_TYPE_BULLETTRAIN = 16;
	public static final int ROUTE_TYPE_AIRPLANE = 32;
	public static final int ROUTE_TYPE_SHIP = 64;
	
	public static final int TRAIN_TYPE_ZIQIANG = 1;			// 自強 
	public static final int TRAIN_TYPE_JUGUANG = 2;			// 莒光
	public static final int TRAIN_TYPE_FUSHING = 3;			// 復興/區間
	public static final int TRAIN_TYPE_NORMAL = 4;			// 普通
	public static final int TRAIN_TYPE_OTHER = 0;			// 其他

	public static final int DATE_TYPE_NORMAL = 1;			// 一般時段
	public static final int DATE_TYPE_HOLIDAY = 2;			// 假日時段
	public static final int DATE_TYPE_EVERYDAY = 3;			// 每日行駛
	public static final int DATE_TYPE_MONDAY = 11;			// 星期一
	public static final int DATE_TYPE_TUESDAY = 12;			// 星期二
	public static final int DATE_TYPE_WEDNESDAY = 13;		// 星期三
	public static final int DATE_TYPE_THURSDAY = 14;		// 星期四
	public static final int DATE_TYPE_FRIDAY = 15;			// 星期五
	public static final int DATE_TYPE_SATURDAY = 16;		// 星期六
	public static final int DATE_TYPE_SUNDAY = 17;			// 星期日
	
	static public native int NewBusEngine(String file);
	static public native int DestroyBusEngine(int hBus);
	
	/*站點清單*/

	//是否有返程資料
	static public native boolean HaveReturnPass(int hBus, int RouteID, int CategoryID);

	/* 票價表 */
	// 依據路線和出發/抵達站順序，取得Bus票價
	static public native float GetTicketFee(int hBus, int RouteID, int StartStationSort, int DestStationSort);
	// 依據路線和出發/抵達站ID，取得地鐵票價
	static public native float GetSubwayTicketFee(int hBus, int StartStationID, int EndStationID);
	// 依據路線和出發/抵達站ID，取得火車票價
	static public native float GetTrainTicketFee(int hBus, int StartStationID, int EndStationID, byte Type);
	/// 依據路線和出發/抵達站ID，取得高鐵票價
	static public native float GetHSRTicketFee(int hBus, int StartStationID, int EndStationID);
	// 依據路線和出發/抵達站ID，取得飛機票價
	static public native float GetAirPlaneTicketFee(int hBus, int RouteID, int StartStationID, int EndStationID);
	// 依據路線和出發/抵達站ID，取得船舶票價
	static public native float GetShipTicketFee(int hBus, int RouteID, int StartStationID, int EndStationID);
	// 取得路線估計等待時間
	static public native short GetRouteWaitTimeByRouteID(int hBus, int RouteID);
	//取得POI的經緯度
	static public native Coordinate GetPOILocation(int hBus, int POIID, int POIType);
	
	// 依據站點，取得停靠的路線清單
	static public native int GetParkingRouteCount(int hBus, int POIID, int CategoryID);
	static public native RouteInfo[] GetParkingRouteData(int hBus, int nSize);
	
	// 取得路線資訊
	/*
	RouteType:
	    CityBus = 1,SubWay = 2,CountryBus = 4,Train = 8,BulletTrain = 16,AirPlane = 32,Ship = 64,all = 0
	BusinessID:
		營運業者的id,0為全取
	*/
	static public native int GetRouteCount(int hBus, char[] Keyword, short RouteType, int BusinessID);	
	/*
	RouteType:
		    CityBus = 1,SubWay = 2,CountryBus = 4,Train = 8,BulletTrain = 16,AirPlane = 32,Ship = 64,all = 0
	Area:
		0.北北基 1桃竹苗 2.中彰投 3高高屏 4.其他 
	*/
	static public native int GetRouteCountByArea(int hBus, char[] Keyword, short RouteType, int Area);	
	//讀取資訊
	static public native RouteInfoEx[] ReadRouteData(int hBus, int nSize);
	
	//用RouteID取得路線名稱(除火車外)
	static public native String GetRouteNameByRouteID(int hBus, int RouteID);
	
	//用train的route id取得train的路線名稱(因為火車的名稱有加起終點資訊)
	static public native String GetTrainRouteNameByRouteID(int hBus, int RouteID);
	
	//用RouteNameID取得路線的名稱
	static public native String GetRouteNameByRouteNameID(int hBus, int RouteNameID);
	
	//以RouteID取得去程的站點    /*exceptid:例外的id,輸入0則全取*/	
	static public native int GetFStationCount(int hBus, int RouteID, int RouteType, int exceptid);
	//以RouteID取得返程的站點
	static public native int GetRStationCount(int hBus, int RouteID, int RouteType);
	//讀取資訊
	static public native StationArray[] ReadStationData(int hBus, int nSize);
	
	//取得指定的RouteID,有那幾的站點sort有時刻資訊
	static public native int GetSScheduleSortCount(int hBus, int RouteID, int RouteType);
	static public native int GetSScheduleSortCount(int hBus, int RouteID);
	//讀取資訊	
	static public native short[] ReadSScheduleSortData(int hBus, int nSize);
	
	//以RouteID,StationSort取得時刻資訊
	static public native int GetRSTimeSchedule(int hBus, int RouteID, short StationSort, int RouteType);
	static public native int GetRSTimeSchedule(int hBus, int RouteID, short StationSort);
	//讀取資訊
	static public native TimeTable[] ReadRSTimeScheduleData(int hBus, int nSize);
	
	//以RouteID取得營運業者ID
	static public native int GetBusinessIDCount(int hBus, int RouteID);
	//讀取資訊
	static public native int[] ReadBusinessIDData(int hBus, int nSize);
	//以營運業者id(BusinessID)取得業者名稱
	static public native String GetBusinessName(int hBus, int BusinessID);
	//取得所有營運業者
	static public native int GetBusinessCount(int hBus);
	//讀取資訊
	static public native int[] ReadBusinessData(int hBus, int nSize);
	//以CategoryID取得該類別下的業者資訊,用ReadBusinessData讀取資訊
	static public native int GetTypeBusinessCount(int hBus, short CategoryID);
	
	//指定類別及業者id取得路線資訊
	static public native int GetRouteTypeBusinessCount(int hBus, short CategoryID, int BusinessID);
	//讀取資訊
	static public native RouteInfoEx GetRouteInfo(int hBus, int RouteID);
	
	/* 時刻表 */
	// 取得指定資訊的時刻表    RouteType:同Categoryid:1101:公車 1102:捷運 1103:長途客運 1104:火車 1105:高鐵 1106:飛機 1107:船舶


	static public native int GetTimeScheduleCount(int hBus, int RouteID, byte DateTypeID, int RouteType);
	//讀取資訊
	static public native TimeTableEx[] ReadTimeScheduleData(int hBus, int nSize);
	//火車類型名稱索引表    Index:1.自強 , 2.莒光, 3.復興/區間, 4.普通, 0.其他
	static public native String GetTrainTypeName(int hBus, int Index);
	//日期類型名稱索引表    Index:1.一般時段,2.假日時段,3.每日行駛,11.星期一,12.星期二,13.星期三,14.星期四,15.星期五 ,16.星期六,17.星期日


	static public native String GetDateTypeName(int hBus, byte Index);
	
	//由兩點取得直達的路線資訊(如台北火車站到板橋車站找這兩站的火車星期天的所有車次)
	static public native int GetExpressPathCount(int hBus, short CategoryID, int S_ID, int E_ID, int TrainType, int 

DateType);
	//讀取資訊
	static public native OnlyExpressPathInfo[] ReadExpressPathData(int hBus, int nSize);
	
	static public native int GetPOIbyCategory(int hBus, int CategoryID);
	static public native int GetPOIbyCategoryLocationBound(int hBus, int Category, int Lat, int Lon, int limit_meter);
	static public native int GetPOIbyCategoryCityId(int hBus, int Category, int CityId);
	static public native POIInfoEx[] ReadPOIData(int hBus, int nSize);
	static public native String GetPOINameByPOINameID(int hBus, int POINameID);
	static public native int GetPOIbyID(int hBus, int POIID);
	
	//取得所有city資訊
	static public native int GetAllCity(int hBus);
	//讀取資訊
	static public native CITYInfoEx[] ReadCityData(int hBus, int nSize);
	
	//以經緯度,距離取得即時路況相機的資訊
	static public native int GetCameraLocationBound(int hBus, int Lat, int Lon, int limit_meter);
	//以Camera_ID取得即時路況相機的資訊
	static public native void GetCameraByID(int hBus, int CameraId, CameraInfoEx Data);
	//讀取資訊
	static public native CameraInfoEx[] ReadCameraData(int hBus, int nSize);
	//以HWID取得即時路況相機的資訊
	static public native int GetCameraByHWID(int hBus, int HWID, int Lat, int Lon);
	//以CityId取得即時路況相機的資訊
	static public native int GetCameraByCity(int hBus, int CityId, int Lat, int Lon);
	//以CityId查是否有Camera在該city
	static public native boolean GetCityCameraExist(int hBus, int CityID);
	
	//用RID,CATEGORYID,SORT取得poi名稱
	static public native String GetPOINameByRidCidSort(int hBus, int RouteID, int CategoryID, int Sort);
	//用Routeid取得營運業者名稱
	static public native String GetCompanyNameByRouteID(int hBus, int RouteID);
	
	//SERVER ID
	static public native int GetServerPOIID(int hBus, int LocalPOIID);
	static public native int GetServerRouteID(int hBus, int LocalRID);
	
	//取得所有快速/高速道路的資訊
	static public native int GetAllHighwayInfo(int hBus);
	//讀取資訊
	static public native HWInfoEx[] ReadHWData(int hBus, int nSize);
	
	//取得資料庫版本資訊
	static public native byte[] GetDataBaseVersion(String file);
	
	
	//CCTV------
	static public native int NewCCTVEngine(String file);
	static public native int DestroyCCTVEngine(int hCCTV);
	static public native int CCTVGetAllCity(int hCCTV);	
	static public native CITYInfoEx_CCTV[] CCTVReadCityData(int hCCTV, int nSize);
	static public native int CCTVGetCameraLocationBound(int hCCTV, int Lat, int Lon, int limit_meter);
	static public native void CCTVGetCameraByID(int hCCTV, int CameraId, CameraInfoEx_CCTV Data);
	static public native CameraInfoEx_CCTV[] CCTVReadCameraData(int hCCTV, int nSize);
	static public native int CCTVGetCameraByHWID(int hCCTV, int HWID, int Lat, int Lon);
	static public native int CCTVGetCameraByCity(int hCCTV, int CityId, int Lat, int Lon);
	static public native boolean CCTVGetCityCameraExist(int hCCTV, int CityID);
	static public native int CCTVGetAllHighwayInfo(int hCCTV);
	static public native HWInfoEx_CCTV[] CCTVReadHWData(int hCCTV, int nSize);	
	static public native byte[] CCTVGetDataBaseVersion(String file);
	//CCTV------

	static {
        try
        {        	
        	Log.i("whichcalass", "BusNtvEngine.java");
            System.loadLibrary("GeoBotNtvEngine");        	
        }
        catch(UnsatisfiedLinkError ule)
        {
        	Log.e("JNI", "Error: can't load libGeoBotNtvEngine.so");
        }
    }
}

