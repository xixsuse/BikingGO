package com.kingwaytek.jni;

public class LocationInfo {	
	
	public String GPSDateStamp;  // "yyyy:MM:dd" EX:"2008:09:02"
	public String GPSTimeStamp;  // "HH mm ss " EX:"12 50 03 "
	public double GPSLatitude;
	public double GPSLongitude;
	
	public LocationInfo()
	{
		GPSLatitude = 0;
		GPSLongitude = 0;
	}
}
