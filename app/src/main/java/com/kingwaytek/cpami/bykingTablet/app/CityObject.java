package com.kingwaytek.cpami.bykingTablet.app;

public class CityObject {

	private int cityID;
	private String cityCode;
	private String cityName;

	public CityObject() {

	}

	public void setCityID(int cityID) {
		this.cityID = cityID;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getCityID() {
		return this.cityID;
	}

	public String getCityCode() {
		return this.cityCode;
	}

	public String getCityName() {
		return this.cityName;
	}
}
