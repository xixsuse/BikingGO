package com.kingwaytek.cpami.bykingTablet.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.kingwaytek.cpami.bykingTablet.app.CoordinateUtil;

/**
 * Geographic point.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class GeoPoint implements Parcelable {

	/**
	 * public CREATOR field that generates instances of this class.
	 */
	public static final Parcelable.Creator<GeoPoint> CREATOR = new Parcelable.Creator<GeoPoint>() {
		public GeoPoint createFromParcel(Parcel in) {
			return new GeoPoint(in);
		}

		public GeoPoint[] newArray(int size) {
			return new GeoPoint[size];
		}
	};

	/** The longitude */
	protected double longitude;

	/** The latitude */
	protected double latitude;

	protected double twd97X;

	protected double twd97Y;

	/**
	 * Create new instance of District.
	 */
	public GeoPoint() {
		longitude = latitude = twd97X = twd97Y = 0.0;
	}

	/**
	 * Create new instance of District.
	 * 
	 * @param parcel
	 *            The instance contains data for initialization.
	 */
	public GeoPoint(Parcel parcel) {
		longitude = parcel.readDouble();
		latitude = parcel.readDouble();
	}

	/**
	 * Create new instance of District.
	 * 
	 * @param longitude
	 *            longitude of data
	 * @param latitude
	 *            latitude of data
	 */
	public GeoPoint(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;

		Double[] result = CoordinateUtil.LonlatToTwd97(this.longitude,
				this.latitude);
		this.twd97X = result[0];
		this.twd97Y = result[1];
	}

	public GeoPoint(double tmX, double tmY, int type) {
		this.twd97X = tmX;
		this.twd97Y = tmY;

		Double[] result = CoordinateUtil.Twd97ToLonlat(twd97X, twd97Y, type);
		if (type == CoordinateUtil.TYPE_RADIAN) {
			this.longitude = result[0];
			this.latitude = result[1];
		}
	}

	/**
	 * Get longitude of the data.
	 * 
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Set longitude of the data.
	 * 
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Get latitude of the data.
	 * 
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Set latitude of the data.
	 * 
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getTmX() {
		return twd97X;
	}

	public double getTmY() {
		return twd97Y;
	}

	public void setTmX(double tmX) {
		this.twd97X = tmX;
	}

	public void setTmY(double tmY) {
		this.twd97Y = tmY;
	}

	public int describeContents() {
		return 0;
	}

	public void LonlatToTm97() {
		if (this.longitude <= 0 || this.latitude <= 0)
			return;
		Double[] result = CoordinateUtil.LonlatToTwd97(this.longitude,
				this.latitude);
		this.twd97X = result[0];
		this.twd97Y = result[1];
	}

	public void Tm97ToLonlat(int type) {
		if (this.twd97X <= 0 || this.twd97Y <= 0)
			return;
		Double[] result = CoordinateUtil.Twd97ToLonlat(twd97X, twd97Y, type);
		if (type == CoordinateUtil.TYPE_RADIAN) {
			this.longitude = result[0];
			this.latitude = result[1];
		}
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
	}

	/**
	 * Should be optimized.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GeoPoint) {
			GeoPoint other = (GeoPoint) obj;

			if (longitude == other.longitude && latitude == other.latitude) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "GeoPoint [" + "longitude=" + longitude + ", latitude="
				+ latitude + "]";
	}
}
