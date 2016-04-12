package com.sonavtek.sonav;

import com.kingwaytek.cpami.bykingTablet.data.ICity;
import com.kingwaytek.cpami.bykingTablet.data.IRoad;
import com.kingwaytek.cpami.bykingTablet.data.ITown;

/**
 * 因為定遠的資料型態定義並不明確(多種類型的資料都用XLIST)，因此藉由實作各種介面來提供型態的轉換。
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class XLIST implements ICity, ITown, IRoad {

    /** The longitude of the data */
    protected double x;

    /** The latitude of the data */
    protected double y;

    /** The ID of the data */
    protected int z;

    /** The name of the data */
    protected String str;

    /** Which method to get the location coordinate */
    protected int locateMethod;

    /**
     * Create new instance of XLIST.
     */
    public XLIST() {
    }

    /**
     * Create new instance of XLIST.
     * 
     * @param longitude
     *            longitude of data
     * @param latitude
     *            latitude of data
     * @param id
     *            id of data
     * @param name
     *            name of data
     * @param locateMethod
     *            which method to get the location coordinate
     */
    public XLIST(double longitude, double latitude, int id, String name, int locateMethod) {
	this.x = longitude;
	this.y = latitude;
	this.z = id;
	this.str = name;
	this.locateMethod = locateMethod;
    }

    /**
     * {@inheritDoc}
     */
    public double getLongitude() {
	return x;
    }

    /**
     * {@inheritDoc}
     */
    public void setLongitude(double longitude) {
	this.x = longitude;
    }

    /**
     * {@inheritDoc}
     */
    public double getLatitude() {
	return y;
    }

    /**
     * {@inheritDoc}
     */
    public void setLatitude(double latitude) {
	this.y = latitude;
    }

    /**
     * {@inheritDoc}
     */
    public int getId() {
	return z;
    }

    /**
     * {@inheritDoc}
     */
    public void setId(int id) {
	this.z = id;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
	return str;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
	this.str = name;
    }

    /**
     * {@inheritDoc}
     */
    public int getLocateMethod() {
	return locateMethod;
    }

    /**
     * {@inheritDoc}
     */
    public void setLocateMethod(int locateMethod) {
	this.locateMethod = locateMethod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	return "XLIST [longitude=" + x + ", latitude=" + y +
	        ", id=" + z + ", name=" + str + ", locateMethod=" + locateMethod + "]";
    }
}