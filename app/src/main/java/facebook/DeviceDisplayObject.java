package facebook;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceDisplayObject implements Parcelable {

	private int width;
	private int height;
	private double inch;
	private int density;

	public DeviceDisplayObject() {
	}

	public DeviceDisplayObject(Parcel in) {
		readFromParcel(in);
	}

	public static final Parcelable.Creator<DeviceDisplayObject> CREATOR = new Parcelable.Creator<DeviceDisplayObject>() {
		public DeviceDisplayObject createFromParcel(Parcel in) {
			return new DeviceDisplayObject(in);
		}

		public DeviceDisplayObject[] newArray(int size) {
			return new DeviceDisplayObject[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(width);
		arg0.writeInt(height);
		arg0.writeDouble(inch);
		arg0.writeInt(density);
	}

	private void readFromParcel(Parcel in) {
		width = in.readInt();
		height = in.readInt();
		inch = in.readDouble();
		density = in.readInt();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double getInch() {
		return inch;
	}

	public void setInch(double inch) {
		this.inch = inch;
	}

	public int getDensity() {
		return density;
	}

	public void setDensity(int density) {
		this.density = density;
	}

}
