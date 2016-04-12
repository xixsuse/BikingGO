package com.kingwaytek.cpami.bykingTablet.app.rentInfo;

public class UbikeObject {
	// {"ar":"忠孝東路\/松仁路(東南側)","bemp":92,"lat":25.04067,"lng":121.56828,"sbi":0,"sna":"捷運市政府站(3號出口)-1","sno":0}
	private String ar;
	private int bemp;
	private double lat;
	private double lng;
	private int sbi;
	private String sna;
	private int sno;

	public UbikeObject() {
	}

	public UbikeObject(String ar, int bemp, double lat, double lng, int sbi, String sna, int sno) {
		this.ar = ar;
		this.bemp = bemp;
		this.lat = lat;
		this.lng = lng;
		this.sbi = sbi;
		this.sna = sna;
		this.sno = sno;
	}

	public void setar(String ar) {
		this.ar = ar;
	}

	public void setbemp(int bemp) {
		this.bemp = bemp;
	}

	public void setlat(double lat) {
		this.lat = lat;
	}

	public void setlng(double lng) {
		this.lng = lng;
	}

	public void setsbi(int sbi) {
		this.sbi = sbi;
	}

	public void setsna(String sna) {
		this.sna = sna;
	}

	public void setsno(int sno) {
		this.sno = sno;
	}

	public String getar() {
		return this.ar;
	}

	public int getbemp() {
		return this.bemp;
	}

	public double getlat() {
		return this.lat;
	}

	public double getlng() {
		return this.lng;
	}

	public int getsbi() {
		return this.sbi;
	}

	public String getsna() {
		return this.sna;
	}

	public int getsno() {
		return this.sno;
	}
}
