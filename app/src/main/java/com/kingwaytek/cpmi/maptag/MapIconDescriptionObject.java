package com.kingwaytek.cpmi.maptag;

import android.os.Parcel;
import android.os.Parcelable;

public class MapIconDescriptionObject implements Parcelable {

	private String description;
	private int icon;

	public MapIconDescriptionObject() {
	}

	public MapIconDescriptionObject(Parcel parcel) {
		readFromParcel(parcel);
	}

	public static final Parcelable.Creator<MapIconDescriptionObject> CREATOR = new Parcelable.Creator<MapIconDescriptionObject>() {
		public MapIconDescriptionObject createFromParcel(Parcel in) {
			return new MapIconDescriptionObject(in);
		}

		public MapIconDescriptionObject[] newArray(int size) {
			return new MapIconDescriptionObject[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(description);
		arg0.writeInt(icon);
	}

	private void readFromParcel(Parcel parcel) {
		description = parcel.readString();
		icon = parcel.readInt();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

}
