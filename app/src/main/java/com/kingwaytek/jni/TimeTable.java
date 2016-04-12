package com.kingwaytek.jni;

public class TimeTable {
	public TimeTable(short _Time, byte _DateTypeID, String _Note)
	{
		Time = _Time; //以0點為基準往上加(分為單位)如384->6:24分
		DateTypeID = _DateTypeID; //1.一般時段,2.假日時段,3.每日行駛,11.星期一,12.星期二,13.星期三,14.星期四,15.星期五 ,16.星期六,17.星期日  


		Note = _Note; //目前ui用不到
	}
	public TimeTable()
	{}
	public short Time;
	public byte DateTypeID;
	public String Note;
}