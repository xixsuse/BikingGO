package com.kingwaytek.cpami.bykingTablet.data;

/**
 * Interface of geographic data.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public interface IGeoData {

    /** Doesn't know how the location found. */
    public static final int NO_LOCATE_METHOD = 0;

    /** Location is from stored data. */
    public static final int LOCATE_BY_DATA = 1;

    /**
     * Get longitude of the data.
     * 
     * @return the longitude
     */
    public double getLongitude();

    /**
     * Set longitude of the data.
     * 
     * @param lon
     *            the longitude to set
     */
    public void setLongitude(double lon);

    /**
     * Get latitude of the data.
     * 
     * @return the latitude
     */
    public double getLatitude();

    /**
     * Set latitude of the data.
     * 
     * @param lat
     *            the latitude to set
     */
    public void setLatitude(double lat);

    /**
     * Returns id of data
     * 
     * @return the id
     */
    public int getId();

    /**
     * Set id of data
     * 
     * @param id
     *            the id to set
     */
    public void setId(int id);

    /**
     * Returns name of data
     * 
     * @return the name
     */
    public String getName();

    /**
     * Set name of data
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name);

    /**
     * Returns the method used to get coordinates of this data.
     * 
     * @return NO_LOCATE_METHOD if the method is undefined<br/>
     *         LOCATE_BY_DATA if the location is from stored data<br/>
     *         > 1 if using interpolation, extrapolation or any special methods<br/>
     */
    public int getLocateMethod();

    /**
     * Set the method used to get coordinates of this data.
     * 
     * @param locateMethod
     *            the method used to get coordinates of this data
     */
    public void setLocateMethod(int locateMethod);
}
