package com.kingwaytek.cpami.bykingTablet.app.track;

import android.os.Parcel;
import android.os.Parcelable;

public class TrackListObject implements Parcelable {

	private String routeID;
	private String routeName;
	private String length;
	private String time;

	public TrackListObject() {
	}

	public TrackListObject(Parcel parcel) {
		readFromParcel(parcel);
	}

	public static final Parcelable.Creator<TrackListObject> CREATOR = new Parcelable.Creator<TrackListObject>() {
		public TrackListObject createFromParcel(Parcel in) {
			return new TrackListObject(in);
		}

		public TrackListObject[] newArray(int size) {
			return new TrackListObject[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(routeID);
		arg0.writeString(routeName);
		arg0.writeString(length);
		arg0.writeString(time);
	}

	private void readFromParcel(Parcel parcel) {
		routeID = parcel.readString();
		routeName = parcel.readString();
		length = parcel.readString();
		time = parcel.readString();
	}

	public String getRouteID() {
		return routeID;
	}

	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
