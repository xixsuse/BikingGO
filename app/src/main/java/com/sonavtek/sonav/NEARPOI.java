package com.sonavtek.sonav;

import com.kingwaytek.cpami.bykingTablet.data.ICity;
import com.kingwaytek.cpami.bykingTablet.data.IPOI;
import com.kingwaytek.cpami.bykingTablet.data.ITown;

/**
 * This is a subclass of POI that will be create by native method. The
 * initialized values will be only ID, name, distance from specified point of the
 * POI.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class NEARPOI implements IPOI {

    /** The longitude of the data */
    protected double x;

    /** The latitude of the data */
    protected double y;

    /** The ID of the data */
    protected int id;

    /** The name of the data */
    protected String name;

    /** The UB code of the data */
    protected String ubCode;

    /** The category of the data */
    protected String category;

    /** The contact telephone number */
    protected String tel;

    /** The contact mobile number */
    protected String mobile;

    /** The FAX number */
    protected String fax;

    /** The contact address */
    protected String addr;

    /** The note */
    protected String note;

    /** The city name and town name separated by comma. ex. 台北市,中正區 */
    protected String city;

    /** The instance of city */
    protected ICity locateCity;

    /** The instance of town */
    protected ITown locateTown;

    /** The distance from the specified location in meter */
    protected int dist;

    /** Which method to get the location coordinate */
    protected int locateMethod;

    /**
     * Create new instance of NEARPOI.
     */
    public NEARPOI() {
    }

    /**
     * Create new instance of NEARPOI.
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
    public NEARPOI(double longitude, double latitude, int id, String name, int locateMethod) {
	this.x = longitude;
	this.y = latitude;
	this.id = id;
	this.name = name;
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
	return id;
    }

    /**
     * {@inheritDoc}
     */
    public void setId(int id) {
	this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
	return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
	this.name = name;
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
    public String getUB() {
	return ubCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setUB(String code) {
	this.ubCode = code;
    }

    /**
     * {@inheritDoc}
     */
    public String getCategory() {
	return category;
    }

    /**
     * {@inheritDoc}
     */
    public void setCategory(String id) {
	this.category = id;
    }

    /**
     * Get the city where the POI locate in.
     * 
     * @return if the "locateCity" is null and "city" is not null, the
     *         locateCity will be created from value of "city".
     */
    public ICity getCity() {
	if (locateCity == null && city != null) {
	    synchronized (this) {
		String[] names = city.split(",");

		if (locateCity == null) {
		    if (names.length > 0) {
			locateCity = new XLIST();
			locateCity.setName(names[0]);
		    }

		    if (names.length > 1) {
			locateTown = new XLIST();
			locateTown.setName(names[1]);
		    }
		}
	    }
	}

	return locateCity;
    }

    /**
     * {@inheritDoc}
     */
    public void setCity(ICity city) {
	this.locateCity = city;
    }

    /**
     * Get the town where the POI locate in.
     * 
     * @return if the "locateTown" is null and "city" is not null, the
     *         locateTown will be created from value of "city".
     */
    public ITown getTown() {
	if (locateTown == null && city != null) {
	    synchronized (this) {
		String[] names = city.split(",");

		if (locateTown == null) {
		    if (names.length > 1) {
			locateTown = new XLIST();
			locateTown.setName(names[1]);
		    }
		}
	    }
	}

	return locateTown;
    }

    /**
     * {@inheritDoc}
     */
    public void setTown(ITown town) {
	this.locateTown = town;
    }

    /**
     * {@inheritDoc}
     */
    public String getTelephoneNumber() {
	return this.tel;
    }

    /**
     * {@inheritDoc}
     */
    public void setTelephoneNumber(String number) {
	this.tel = number;
    }

    /**
     * {@inheritDoc}
     */
    public String getMobilePhoneNumber() {
	return this.mobile;
    }

    /**
     * {@inheritDoc}
     */
    public void setMobilePhoneNumber(String number) {
	this.mobile = number;
    }

    /**
     * {@inheritDoc}
     */
    public String getFAX() {
	return this.fax;
    }

    /**
     * {@inheritDoc}
     */
    public void setFAX(String fax) {
	this.fax = fax;
    }

    /**
     * {@inheritDoc}
     */
    public String getAddress() {
	return this.addr;
    }

    /**
     * {@inheritDoc}
     */
    public void setAddress(String addr) {
	this.addr = addr;
    }

    /**
     * {@inheritDoc}
     */
    public String getNote() {
	return this.note;
    }

    /**
     * {@inheritDoc}
     */
    public void setNote(String note) {
	this.note = note;
    }

    /**
     * {@inheritDoc}
     */
    public int getDistance() {
	return this.dist;
    }

    /**
     * {@inheritDoc}
     */
    public void setDistance(int distance) {
	this.dist = distance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	return "NEARPOI [id=" + id + ", name=" + name +
	        ", longitude=" + x + ", latitude=" + y +
	        ", ubCode=" + ubCode + ", category=" + category +
	        ", city=" + getCity() + ", town=" + getTown() +
	        ", telephone=" + tel + ", mobilePhone=" + mobile + ", fax=" + fax +
	        ", address=" + addr + ", note=" + note +
	        ", distance=" + dist + ", locateMethod=" + locateMethod + "]";
    }
}
