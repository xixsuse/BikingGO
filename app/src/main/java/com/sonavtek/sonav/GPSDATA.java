package com.sonavtek.sonav;

import java.util.Calendar;

import android.location.Location;

/**
 * This is the data structure for storing information about a location. The GPS
 * data need to be set from application to the engine since the engine could not
 * get location by its self.
 */
public class GPSDATA {
    private double x; // longitude ex. 121.524652
    private double y; // latitude ex. 25.060080000000003
    private double high; // altitude in meter ex. 0.0
    private int deg; // bearing from 0 ~ 180 and 0 ~ -180
    private double speed; // speed in km/h ex. 0.0 (會影響預測下一個點的位置)
    private int year; // year ex. 2010
    private int month; // month from 0 to 11 ex. 0 as Jan.
    private int day; // day of month from 1 ~ 31
    private int hour; // hour of the day from 0 to 23
    private int min; // minute of an hour from 0 to 59
    private int sec; // seconds of a minute from 0 to 59
    public int xyok; // always set to 0
    private int xyok1; // the number of satellites used

    /**
     * Create new instance of GPSDATA.
     */
    public GPSDATA() {
    }

    /**
     * Create new instance of GPSDATA.
     * 
     * @param loc
     *            The instance contains information about the location.
     * @param satelliteNum
     *            The number of satellites used.
     */
    public GPSDATA(Location loc, int satelliteNum) {
        this.x = loc.getLongitude();
        this.y = loc.getLatitude();
        this.high = loc.getLongitude();
        this.deg = (int) loc.getBearing();
        this.speed = loc.getSpeed() * 3.6; // the original value is in m/s
        setTime(loc.getTime());
        this.xyok1 = satelliteNum;
    }

    /**
     * Returns the altitude in meter.
     * 
     * @return The altitude in meter.
     */
    public double getAltitude() {
        return high;
    }

    /**
     * Set the altitude.
     * 
     * @param alt
     *            The altitude.
     */
    public void setAltitude(double alt) {
        this.high = alt;
    }

    /**
     * Returns the bearing.
     * 
     * @return The bearing.
     */
    public double getBearing() {
        return deg;
    }

    /**
     * Set the bearing.
     * 
     * @param bearing
     *            The bearing.
     */
    public void setBearing(int bearing) {
        this.deg = bearing;
    }

    /**
     * Returns the longitude.
     * 
     * @return The longitude.
     */
    public double getLongitude() {
        return x;
    }

    /**
     * Set the longitude.
     * 
     * @param lon
     *            The longitude.
     */
    public void setLongitude(double lon) {
        this.x = lon;
    }

    /**
     * Returns the latitude.
     * 
     * @return The latitude.
     */
    public double getLatitude() {
        return y;
    }

    /**
     * Set the latitude.
     * 
     * @param lat
     *            The latitude.
     */
    public void setLatitude(double lat) {
        this.y = lat;
    }

    /**
     * Returns the speed of the device over ground in meters/second.
     * 
     * @return The speed in kilometer per hour.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of this fix, in kilometer per hour. (會影響預測下一個點的位置)
     * 
     * @param speed
     *            The speed in kilometer per hour.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Returns the UTC time of this fix, in milliseconds since January 1, 1970.
     * 
     * @return The position time in milliseconds.
     */
    public long getTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, sec);

        return cal.getTimeInMillis();
    }

    /**
     * Sets the UTC time of this fix, in milliseconds since January 1, 1970.
     * 
     * @param time
     *            The time in milliseconds.
     */
    public void setTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH);
        this.day = cal.get(Calendar.DAY_OF_MONTH);
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.min = cal.get(Calendar.MINUTE);
        this.sec = cal.get(Calendar.SECOND);
    }

    /**
     * Returns number of satellites used.
     * 
     * @return The number of satellites.
     */
    public int getSatelliteNumber() {
        return xyok1;
    }

    /**
     * Set number of satellites used.
     * 
     * @param number
     *            The number of satellites. Set number >= 3 for a valid
     *            position, or set number = 0 for losing position. Set 0 ~ 2 if
     *            the validity is unknown.
     */
    public void setSatelliteNumber(int number) {
        this.xyok1 = number;
    }

    @Override
    public String toString() {
        return "GPSDATA [day=" + day + ", deg=" + deg + ", high=" + high +
                ", hour=" + hour + ", min=" + min + ", month=" + month + ", sec=" + sec +
                ", speed=" + speed + ", x=" + x +
                ", xyok=" + xyok + ", xyok1=" + xyok1 + ", y=" + y + ", year=" + year + "]";
    }
}
