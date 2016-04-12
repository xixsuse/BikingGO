package com.kingwaytek.cpami.bykingTablet.bus;

public class PointInfo {
	private String name;
	private String type;
	private String region;
	private long lat;
	private long lon;
	
	public PointInfo(String name, String type, String region, long lat, long lon){
		this.name = name;
		this.type = type;
		this.region = region;
		this.lat = lat;
		this.lon = lon;
	}
	public String getPointName(){
		return name;
	}
	public String getPointType(){
		return type;
	}
	public String getPointRegion(){
		return region;
	}
	public long getLat(){
		return lat;
	}
	public long getLon(){
		return lon;
	}
}
