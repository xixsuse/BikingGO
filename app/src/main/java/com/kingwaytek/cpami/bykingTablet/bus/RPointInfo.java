package com.kingwaytek.cpami.bykingTablet.bus;

public class RPointInfo {
	private int id;
	private String name;
	private String region;
	private long lat;
	private long lon;
	
	//for test
	public RPointInfo(int id,String name){
		this.name = name;
		this.id = id;
	}
	public RPointInfo(int id, String name, String region, long lat, long lon){
		this.id = id;
		this.name = name;
		this.region = region;
		this.lat = lat;
		this.lon = lon;
	}
	public String getPointName(){
		return name;
	}
	public int getPointId(){
		return id;
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
