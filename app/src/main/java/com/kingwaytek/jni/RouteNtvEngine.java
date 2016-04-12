package com.kingwaytek.jni;

import android.util.Log;

public class RouteNtvEngine {
	// Route type
	static public final int WALK = 0;
	static public final int BUS = 1;
	static public final int SUBWAY = 2;
	static public final int COUNTRY_BUS = 3;
	static public final int TRAIN = 4;
	static public final int HSR = 5;
	static public final int AIRPLANE = 6;
	static public final int SHIP = 7;
	
	// Application msgs
	static public final int MSG_NONE = 0;
	static public final int MSG_START_DUPLICATE = 1;
	static public final int MSG_DEST_DUPLICATE = 2;
	static public final int MSG_START_SUCCESS = 3;
	static public final int MSG_DEST_SUCCESS = 4;
	static public final int MSG_HOME_SUCCESS = 5;
	static public final int MSG_FAV_POS_SUCCESS = 6;
	static public final int MSG_FAV_PATH_SUCCESS = 7;
	static public final int MSG_ROUTING_SUCCESS = 8;
	static public final int MSG_ROUTING_FAIL = 9;
	static public final int MSG_ROUTING_STOP = 10;
	static public final int MSG_CANNOT_WALK = 11;
	static public final int MSG_INSUFFICIENT_MEMORY = 12;

	static public final int STATION_GROUP = 0;
	static public final int STATION = 1;
	static public final int STATION_EXIT = 2;
	static public final int POI = 3;
	static public final int CROSSING = 4;
	static public final int ROAD = 5;
	static public final int GPS = 6;
	static public final int MAP = 7;
	static public final int ROUTE = 8;
	
	static public native int NewRouteEngine(int hBusEng);
	static public native int DestroyRouteEngine(int hRouteEng);
	static public native int SetStartPoint(int hRouteEng, int[] Data);
	static public native int SetEndPoint(int hRouteEng, int[] Data);
	static public native int Run(int hRouteEng);
	
	static public native void ClearEngine(int hRouteEng);
	static public native void ClearResult(int hRouteEng);
	static public native int CurSID(int hRouteEng);
	static public native int CurSType(int hRouteEng);
	static public native int CurSECount(int hRouteEng);
	static public native int CurSEType(int hRouteEng, int EIndex);
	static public native int CurSENameID(int hRouteEng, int EIndex);
	static public native int CurSEIndex(int hRouteEng, int EPOIID);
	static public native int[] CurSEData(int hRouteEng, int EIndex);
	static public native boolean CurSEIsEnd(int hRouteEng, int EIndex);
	static public native int CurSERCount(int hRouteEng, int EIndex);
	static public native int CurSERRouteID(int hRouteEng, int EIndex, int RIndex);
	static public native int CurSERRouteNameID(int hRouteEng, int EIndex, int RIndex);
	static public native int CurSERRouteType(int hRouteEng, int EIndex, int RIndex);
	static public native int ResultSType(int hRouteEng, int SIndex);
	static public native int ResultSNameID(int hRouteEng, int SIndex);
	static public native int ResultSID(int hRouteEng, int SIndex);
	static public native int ResultCount(int hRouteEng);
	static public native boolean IsStart(int hRouteEng, int SIndex);
	static public native float CurSubwayTicket(int hRouteEng, int SID, int EID);
	static public native float CurTicket(int hRouteEng, int EID, int RID);
	static public native short PassStations(int hRouteEng, int EID, int RID);
	static public native int PDistance(int hRouteEng, int EID, int RID);
	static public native short TranTime(int hRouteEng, int EID, int RID);
	static public native short GoESort(int hRouteEng, int EID, int RID);
	static public native void SetDefSIndex(int hRouteEng, int Index);
	static public native int GetDefSIndex(int hRouteEng);
	static public native void SetCurSIndex(int hRouteEng, int Index);
	static public native int GetCurSIndex(int hRouteEng);
	
	static public native CNetwork GetBestPath(int hRouteEng, int Pirority);
	static public native int CalculatingBestPath(int hRouteEng);
	
	static public native String GetMapVersion(String file);
	static public native String GetVersionInfoInText(String file);

	static {
        try
        {   Log.i("whichcalass", "RouteNtvEngine.java");       	
        	Log.i("JNI", "loading GeoBotNtvEngine");
            System.loadLibrary("GeoBotNtvEngine");        	
        }
        catch(UnsatisfiedLinkError ule)
        {
        	Log.e("JNI", "Error: can't load libGeoBotNtvEngine.so");
        }
    }
}
