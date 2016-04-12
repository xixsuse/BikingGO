package com.sonavtek.sonav;

import com.kingwaytek.cpami.bykingTablet.data.ICity;
import com.kingwaytek.cpami.bykingTablet.data.IPOI;
import com.kingwaytek.cpami.bykingTablet.data.ITown;

/**
 * This is a subclass of POI that will be create by native method. The
 * initialized values will be only longitude, latitude, ID of category, name,
 * telephone number, mobile phone number, address, note, city name, town name,
 * and distance from specified point of the POI.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class XLIST4 implements IPOI {

    /** The ID of the data */
    protected int id;

    /** The name of the data */
    protected String str;

    /** The longitude of the data */
    protected double x;

    /** The latitude of the data */
    protected double y;

    /** The UB code of the data */
    protected String ubCode;

    /** The category of the data */
    protected int z;

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
    protected int dd;

    /** Which method to get the location coordinate */
    protected int locateMethod;

    /**
     * Create new instance of XLIST4.
     */
    public XLIST4() {
    }

    /**
     * Create new instance of XLIST4.
     * 
     * @param id
     *            ID of POI
     * @param name
     *            name of data
     * @param lon
     *            longitude of data
     * @param lat
     *            latitude of data
     * @param ubCode
     *            the UB code
     * @param categoryId
     *            ID of category
     * @param tel
     *            the contact telephone number
     * @param mobile
     *            the contact mobile phone number
     * @param fax
     *            the FAX number
     * @param addr
     *            the contact address
     * @param note
     *            the note
     * @param locateCity
     *            the city locate in
     * @param locateTown
     *            the town locate in
     * @param distance
     *            the distance from the specified location in meter
     * @param locateMethod
     *            which method to get the location coordinate
     */
    public XLIST4(
	    int id, String name, double lon, double lat, String ubCode, int categoryId,
	    String tel, String mobile, String fax, String addr, String note,
	    ICity locateCity, ITown locateTown, int distance, int locateMethod) {
	this.id = id;
	this.str = name;
	this.x = lon;
	this.y = lat;
	this.ubCode = ubCode;
	this.z = categoryId;
	this.tel = tel;
	this.mobile = mobile;
	this.fax = fax;
	this.addr = addr;
	this.note = note;
	this.locateCity = locateCity;
	this.locateTown = locateTown;
	this.dd = distance;
	this.locateMethod = locateMethod;
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
    public double getLongitude() {
	return x;
    }

    /**
     * {@inheritDoc}
     */
    public void setLongitude(double lon) {
	this.x = lon;
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
    public void setLatitude(double lat) {
	this.y = lat;
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
	String id = String.valueOf(z);

	return id.length() % 2 == 0 ? id : "0" + id;
    }

    /**
     * {@inheritDoc}
     */
    public void setCategory(String id) {
	this.z = id == null ? 0 : Integer.parseInt(id);
    }

    /**
     * {@inheritDoc}
     */
    public ICity getCity() {
	return locateCity;
    }

    /**
     * {@inheritDoc}
     */
    public void setCity(ICity city) {
	this.locateCity = city;
    }

    /**
     * {@inheritDoc}
     */
    public ITown getTown() {
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
	return this.dd;
    }

    /**
     * {@inheritDoc}
     */
    public void setDistance(int distance) {
	this.dd = distance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	return "XLIST4 [id=" + id + ", name=" + str +
	        ", longitude=" + x + ", latitude=" + y +
	        ", ubCode=" + ubCode + ", category=" + z +
	        ", city=" + getCity() + ", town=" + getTown() +
	        ", telephone=" + tel + ", mobilePhone=" + mobile + ", fax=" + fax +
	        ", address=" + addr + ", note=" + note +
	        ", distance=" + dd + ", locateMethod=" + locateMethod + "]";
    }
}
