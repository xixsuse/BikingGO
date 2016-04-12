package com.kingwaytek.cpami.bykingTablet.bus;

public class RLineInfo {
	private int id;
	private String name;
	private int category;
	private int period;
	private int price;
	private String Schedule;
	
	public RLineInfo(int id, String name, int nCate, int nPeriod, int nPrice, String nSchd){
		this.id = id;
		this.name = name;
		this.category = nCate;
		this.period = nPeriod;
		this.price = nPrice;
		this.Schedule = nSchd;
	}
	public String getLineName(){
		return name;
	}
	public int getLineId(){
		return id;
	}
	public int getLineCategory(){
		return this.category;
	}
	public int getLinePeriod(){
		return this.period;
	}
	public int getLinePrice(){
		return this.period;
	}
	public String getLineSchedule(){
		return this.Schedule;
	}

}
