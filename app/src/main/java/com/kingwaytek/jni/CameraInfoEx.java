package com.kingwaytek.jni;

public class CameraInfoEx {
	public CameraInfoEx(int _Camera_ID, int _Lat, int _Lon, String _POINAME, int _ref_distance)
	{
		Camera_ID = _Camera_ID;
		Lat = _Lat;
		Lon = _Lon;
		POINAME = _POINAME;
		ref_distance = _ref_distance;	
	}
	public CameraInfoEx()
	{}
	public int Camera_ID;
	public int Lat;
	public int Lon;   
	public String POINAME;
	public int ref_distance;//參考距離
}

