package com.kingwaytek.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.kingwaytek.api.caller.Caller;
import com.kingwaytek.api.exception.ApiException;

public class CallerData implements Parcelable{
	
	final static double DEFAULT_LAT_LON_VALUE = 0.0f ;
	final static int DEFAULT_ROAD_ID = -1 ;
	final static String DEFAULT_NAVI_TYPE = Caller.TYPE_NAVI_TO_POINT ;
	
	/**
	 * Latitude,ex 25.13230
	 */
	double mLat = DEFAULT_LAT_LON_VALUE ;
	
	/**
	 * Longitude,ex. 121.73946
	 */
	double mLon = DEFAULT_LAT_LON_VALUE ;
	
	/**
	 * Engine用來更準確地進行定位使用
	 */
	int mRoadId = DEFAULT_ROAD_ID;
	
	/**
	 * APP端顯示名稱使用
	 */
	String mTargetName ;
	
	/**
	 * 導航目的的地址
	 */
	String mAddress ;
	
	public CallerData(double lat,double lon,int roadId,String targetName) throws ApiException{
		if(Checker.checkLatLonUnavaliable(lat,lon)){
			throw new ApiException(ApiException.LAT_CANT_LARGET_THAN_LON);
		}
		
		mLat = lat ;
		mLon = lon ;
		mRoadId = roadId ;
		mTargetName = targetName ;
		mAddress = null ;
	}
	
	public CallerData(double lat,double lon) throws ApiException{
		this(lat,lon,DEFAULT_ROAD_ID," ");
	}
	
	public CallerData(String address,String targetName) throws ApiException{
		if(!Checker.checkAddresssAvaliable(address)){
			throw new ApiException(ApiException.ADDRESS_CAN_NOT_NULL_OR_EMPTY);
		}
		
		mLat = DEFAULT_LAT_LON_VALUE ;
		mLon = DEFAULT_LAT_LON_VALUE ;
		mRoadId = DEFAULT_ROAD_ID ;
		mAddress = address ;
		mTargetName = targetName ;
	}
	
	public CallerData(Parcel in) throws ApiException{
		mLat = in.readDouble() ;
		mLon = in.readDouble() ;
		mRoadId = in.readInt() ;
		mTargetName = in.readString() ;
		mAddress = in.readString() ;
	}

	public double getLat() {
		return mLat;
	}

	public void setLat(double mLat) {
		this.mLat = mLat;
	}

	public double getLon() {
		return mLon;
	}

	public void setLon(double mLon) {
		this.mLon = mLon;
	}

	public int getRoadId() {
		return mRoadId;
	}

	public void setRoadId(int mRoadId) {
		this.mRoadId = mRoadId;
	}

	public String getTargetName() {
		return mTargetName;
	}

	public void setTargetName(String mTargetName) {
		this.mTargetName = mTargetName;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public void clearAddress(){
		mAddress = null ;
	}
	
	/*
	 * 參考自身的成員狀態回傳導航型態
	 * 優先以座標當主要回傳型態
	 */
	public String getNaviType(){
		if (mLat != DEFAULT_LAT_LON_VALUE && mLon != DEFAULT_LAT_LON_VALUE ){
			return DEFAULT_NAVI_TYPE;
		}else if (mAddress != null && mAddress.length() > 0){
			return Caller.TYPE_NAVI_TO_ADDRESS;
		}else{
			return Caller.TYPE_NAVI_TO_ADDRESS;
		}
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeDouble(this.mLat);
		parcel.writeDouble(this.mLon);
		parcel.writeInt(this.mRoadId);
		parcel.writeString(this.mTargetName);
		parcel.writeString(this.mAddress);
	}
	
	public static final Parcelable.Creator<CallerData> CREATOR = new Parcelable.Creator<CallerData>() {
	    public CallerData createFromParcel(Parcel in) {
	        try {
				return new CallerData(in);
			} catch (ApiException e) {				
				e.printStackTrace();
				return null;
			}
	    }

		@Override
		public CallerData[] newArray(int size) {
			return new CallerData[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override 
	public String toString(){
		return "" + mLat + "," + mLon + "," + mTargetName ; 
	}
	
	public String toStringDetail(){
		return "Lat:" + mLat + ",Lon:" + mLon + ",Name:" + mTargetName + ",Address:" + mAddress  ; 
	}
	
	public static class Checker{
		public static boolean checkLatLonUnavaliable(double lat,double lon){
			return lon < lat;
		}
		
		public static boolean checkAddresssAvaliable(String address){
			if(address == null){
				return false;
			}
			
			if(address.length() < 3){
				return false ;
			}
			return true ;
		}
	}	
}