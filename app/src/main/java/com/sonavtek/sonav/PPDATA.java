package com.sonavtek.sonav;

import com.kingwaytek.cpami.bykingTablet.data.ICity;
import com.kingwaytek.cpami.bykingTablet.data.IPOI;
import com.kingwaytek.cpami.bykingTablet.data.ITown;

/**
 * This is a subclass of POI that will be create by native method. The
 * initialized values will be only longitude, latitude, ID, name, telephone
 * number, FAX, address, city name, town name, and UB Code of the POI.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class PPDATA implements IPOI {

    /** ID of POI */
    protected int poiid;

    /** The name of POI */
    protected String name;

    /** The longitude of POI */
    protected double x;

    /** The latitude of POI */
    protected double y;

    /** The UB code */
    protected String ubcode;

    /** The ID of category of POI */
    protected String kind;

    /** The contact telephone number of POI */
    protected String tel;

    /** The contact mobile number */
    protected String mobile;

    /** The FAX number of POI */
    protected String fax;

    /** The contact address of POI */
    protected String addr;

    /** The note */
    protected String note;

    /** The instance of city */
    protected ICity locateCity;

    /** The instance of town */
    protected ITown locateTown;

    /** The distance from the specified location in meter */
    protected int distance;

    /** Which method to get the location coordinate */
    protected int locateMethod;

    /**
     * ID of town in the format of ID of city in character + ID of town. ex. the
     * ID of town is 6510, then this value will be "A10"
     */
    protected String ct;

    /**
     * Create new instance of PPDATA.
     */
    public PPDATA() {
    }

    /**
     * Create new instance of PPDATA.
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
    public PPDATA(int id, String name, double lon, double lat, String ubCode, String categoryId,
	    String tel, String mobile, String fax, String addr, String note,
	    ICity locateCity, ITown locateTown, int distance, int locateMethod) {
	this.poiid = id;
	this.name = name;
	this.x = lon;
	this.y = lat;
	this.ubcode = ubCode;
	this.kind = categoryId;
	this.tel = tel;
	this.mobile = mobile;
	this.fax = fax;
	this.addr = addr;
	this.note = note;
	this.locateCity = locateCity;
	this.locateTown = locateTown;
	this.distance = distance;
	this.locateMethod = locateMethod;
    }
    
    /**
     * @author yawhaw ou(yawhaw@kingwaytek.com)
     */
    public PPDATA(PPDATA _ppData) {
    	setId(_ppData.getId());
    	setUB(_ppData.getUB());
    	setName(_ppData.getName());
    	setAddress(_ppData.getAddress());
    	setTelephoneNumber(_ppData.getTelephoneNumber());
    	setFAX(_ppData.getFAX());
    	setLongitude(_ppData.getLongitude());
    	setLatitude(_ppData.getLatitude());
    	setCategory(_ppData.getCategory());
    	ct = _ppData.ct;
    }

    /**
     * {@inheritDoc}
     */
    public int getId() {
	return poiid;
    }

    /**
     * {@inheritDoc}
     */
    public void setId(int id) {
	this.poiid = id;
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
	return ubcode;
    }

    /**
     * {@inheritDoc}
     */
    public void setUB(String code) {
	this.ubcode = code;
    }

    /**
     * {@inheritDoc}
     */
    public String getCategory() {
	return kind;
    }

    /**
     * {@inheritDoc}
     */
    public void setCategory(String id) {
	this.kind = id;
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
	return this.distance;
    }

    /**
     * {@inheritDoc}
     */
    public void setDistance(int distance) {
	this.distance = distance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	return "PPDATA [id=" + poiid + ", name=" + name +
	        ", longitude=" + x + ", latitude=" + y +
	        ", ubCode=" + ubcode + ", category=" + kind +
	        ", city=" + getCity() + ", town=" + getTown() +
	        ", telephone=" + tel + ", mobilePhone=" + mobile + ", fax=" + fax +
	        ", address=" + addr + ", note=" + note +
	        ", distance=" + distance + ", locateMethod=" + locateMethod + "]";
    }
}