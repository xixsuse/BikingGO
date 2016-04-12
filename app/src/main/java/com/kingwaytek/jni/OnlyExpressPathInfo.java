package com.kingwaytek.jni;

public class OnlyExpressPathInfo  implements Comparable {
	public OnlyExpressPathInfo(int _RouteNameID, int _RouteID, int _Type, short _Time, short _Etime, byte _DateTypeID, 

String _TrainNumber)
	{
		RouteNameID = _RouteNameID;
		RouteID = _RouteID;
		Type = _Type;
		Time = _Time; //起點時間
		Etime = _Etime; //終點時間
		DateTypeID = _DateTypeID;
		TrainNumber = _TrainNumber; //目前不使用
	}
	public OnlyExpressPathInfo()
	{}
	public int RouteNameID;
	public int RouteID;
	public int Type;
	public short Time;
	public short Etime;
	public byte DateTypeID;
	public String TrainNumber;
	
	//@Override
	public int compareTo(Object obj) {
		return Time - ((OnlyExpressPathInfo)obj).Time;
	}
}



