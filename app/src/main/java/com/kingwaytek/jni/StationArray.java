package com.kingwaytek.jni;

public class StationArray {
	public StationArray(int _MStationID, int _StationID, short _StationSort)
	{
		MStationID = _MStationID; //Routeing使用.一般用不到
		StationID = _StationID; //站點的id與poiid同
		StationSort = _StationSort; //在路線中的排序
	}
	public StationArray()
	{}
	public int MStationID;
	public int StationID;
	public short StationSort;
}