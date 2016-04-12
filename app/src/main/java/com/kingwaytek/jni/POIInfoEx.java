package com.kingwaytek.jni;

public class POIInfoEx {	
	public int POIID;
	public int POINAMEID;
	public int CITYID;   
	public int TOWNID;
	public String POINAME;
	public String CITYNAME;
	public String TOWNNAME;
	public int ref_distance;
	public int serverPOIId;
	
	public POIInfoEx(int _POIID, int _POINAMEID, int _CITYID, int _TOWNID, 
			String _POINAME, String _CITYNAME, String _TOWNNAME, int _ref_distance)
	{
		POIID = _POIID;
		POINAMEID = _POINAMEID;
		CITYID = _CITYID;
		TOWNID = _TOWNID;
		POINAME = _POINAME;
		CITYNAME = _CITYNAME;
		TOWNNAME = _TOWNNAME;
		ref_distance = _ref_distance;
	}
	public POIInfoEx(){}
}

