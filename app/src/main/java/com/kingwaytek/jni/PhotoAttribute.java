package com.kingwaytek.jni;

public class PhotoAttribute {	
	
	public String date_time;
	public boolean GPS_TAG;
	public double lat;
	public double lon;
	public String filePath;
	
	public PhotoAttribute(String _date_time, boolean _GPS_TAG, double _lat, double _lon)
	{
		date_time = _date_time;
		GPS_TAG = _GPS_TAG;
		lat = _lat;
		lon = _lon;
	}
	public PhotoAttribute() {}
}
