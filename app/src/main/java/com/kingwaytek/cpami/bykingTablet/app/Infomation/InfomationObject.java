package com.kingwaytek.cpami.bykingTablet.app.Infomation;

import android.os.Parcel;
import android.os.Parcelable;

public class InfomationObject implements Parcelable {

	private String Id;
	private String Name;
	private String Description;
	private String Particpation;
	private String Location;
	private String Add;
	private String Tel;
	private String Org;
	private String Start;
	private String End;
	private String Cycle;
	private String Noncycle;
	private String Website;
	private String Picture1;
	private String Picdescribe1;
	private String Picture2;
	private String Picdescribe2;
	private String Picture3;
	private String Picdescribe3;
	private String Px;
	private String Py;
	private String Class1;
	private String Class2;
	private String ActivityClass;
	private String Map;
	private String Travellinginfo;
	private String Parkinginfo;
	private String Charge;
	private String Remarks;
	private String Region;
	private String Town;

	public InfomationObject() {
	}

	public InfomationObject(Parcel parcel) {
		readFromParcel(parcel);
	}

	public static final Parcelable.Creator<InfomationObject> CREATOR = new Parcelable.Creator<InfomationObject>() {
		public InfomationObject createFromParcel(Parcel in) {
			return new InfomationObject(in);
		}

		public InfomationObject[] newArray(int size) {
			return new InfomationObject[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	private void readFromParcel(Parcel parcel) {
		Id = parcel.readString();
		Name = parcel.readString();
		Description = parcel.readString();
		Particpation = parcel.readString();
		Location = parcel.readString();
		Add = parcel.readString();
		Tel = parcel.readString();
		Org = parcel.readString();
		Start = parcel.readString();
		End = parcel.readString();
		Cycle = parcel.readString();
		Noncycle = parcel.readString();
		Website = parcel.readString();
		Picture1 = parcel.readString();
		Picdescribe1 = parcel.readString();
		Picture2 = parcel.readString();
		Picdescribe2 = parcel.readString();
		Picture3 = parcel.readString();
		Picdescribe3 = parcel.readString();
		Px = parcel.readString();
		Py = parcel.readString();
		Class1 = parcel.readString();
		Class2 = parcel.readString();
		ActivityClass = parcel.readString();
		Map = parcel.readString();
		Travellinginfo = parcel.readString();
		Parkinginfo = parcel.readString();
		Charge = parcel.readString();
		Remarks = parcel.readString();
		Region = parcel.readString();
		Town = parcel.readString();
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(Id);
		arg0.writeString(Name);
		arg0.writeString(Description);
		arg0.writeString(Particpation);
		arg0.writeString(Location);
		arg0.writeString(Add);
		arg0.writeString(Tel);
		arg0.writeString(Org);
		arg0.writeString(Start);
		arg0.writeString(End);
		arg0.writeString(Cycle);
		arg0.writeString(Noncycle);
		arg0.writeString(Website);
		arg0.writeString(Picture1);
		arg0.writeString(Picdescribe1);
		arg0.writeString(Picture2);
		arg0.writeString(Picdescribe2);
		arg0.writeString(Picture3);
		arg0.writeString(Picdescribe3);
		arg0.writeString(Px);
		arg0.writeString(Py);
		arg0.writeString(Class1);
		arg0.writeString(Class2);
		arg0.writeString(ActivityClass);
		arg0.writeString(Map);
		arg0.writeString(Travellinginfo);
		arg0.writeString(Parkinginfo);
		arg0.writeString(Charge);
		arg0.writeString(Remarks);
		arg0.writeString(Region);
		arg0.writeString(Town);
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getParticpation() {
		return Particpation;
	}

	public void setParticpation(String particpation) {
		Particpation = particpation;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getAdd() {
		return Add;
	}

	public void setAdd(String add) {
		Add = add;
	}

	public String getTel() {
		return Tel;
	}

	public void setTel(String tel) {
		Tel = tel;
	}

	public String getOrg() {
		return Org;
	}

	public void setOrg(String org) {
		Org = org;
	}

	public String getStart() {
		return Start;
	}

	public void setStart(String start) {
		Start = start;
	}

	public String getEnd() {
		return End;
	}

	public void setEnd(String end) {
		End = end;
	}

	public String getCycle() {
		return Cycle;
	}

	public void setCycle(String cycle) {
		Cycle = cycle;
	}

	public String getNoncycle() {
		return Noncycle;
	}

	public void setNoncycle(String noncycle) {
		Noncycle = noncycle;
	}

	public String getWebsite() {
		return Website;
	}

	public void setWebsite(String website) {
		Website = website;
	}

	public String getPicture1() {
		return Picture1;
	}

	public void setPicture1(String picture1) {
		Picture1 = picture1;
	}

	public String getPicdescribe1() {
		return Picdescribe1;
	}

	public void setPicdescribe1(String picdescribe1) {
		Picdescribe1 = picdescribe1;
	}

	public String getPicture2() {
		return Picture2;
	}

	public void setPicture2(String picture2) {
		Picture2 = picture2;
	}

	public String getPicdescribe2() {
		return Picdescribe2;
	}

	public void setPicdescribe2(String picdescribe2) {
		Picdescribe2 = picdescribe2;
	}

	public String getPicture3() {
		return Picture3;
	}

	public void setPicture3(String picture3) {
		Picture3 = picture3;
	}

	public String getPicdescribe3() {
		return Picdescribe3;
	}

	public void setPicdescribe3(String picdescribe3) {
		Picdescribe3 = picdescribe3;
	}

	public String getPx() {
		return Px;
	}

	public void setPx(String px) {
		Px = px;
	}

	public String getPy() {
		return Py;
	}

	public void setPy(String py) {
		Py = py;
	}

	public String getClass1() {
		return Class1;
	}

	public void setClass1(String class1) {
		Class1 = class1;
	}

	public String getClass2() {
		return Class2;
	}

	public void setClass2(String class2) {
		Class2 = class2;
	}

	public String getActivityClass() {
		return ActivityClass;
	}

	public void setActivityClass(String activityClass) {
		ActivityClass = activityClass;
	}

	public String getMap() {
		return Map;
	}

	public void setMap(String map) {
		Map = map;
	}

	public String getTravellinginfo() {
		return Travellinginfo;
	}

	public void setTravellinginfo(String travellinginfo) {
		Travellinginfo = travellinginfo;
	}

	public String getParkinginfo() {
		return Parkinginfo;
	}

	public void setParkinginfo(String parkinginfo) {
		Parkinginfo = parkinginfo;
	}

	public String getCharge() {
		return Charge;
	}

	public void setCharge(String charge) {
		Charge = charge;
	}

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

	public String getRegion() {
		return Region;
	}

	public void setRegion(String region) {
		Region = region;
	}

	public String getTown() {
		return Town;
	}

	public void setTown(String town) {
		Town = town;
	}

}
