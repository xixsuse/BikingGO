package com.kingwaytek.jni;

import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.bus.BusNtvEngineManager;

public class RouteInfoEx implements Comparable {
	public RouteInfoEx(int _RouteID, int _RouteNameID, int _CategoryID, int _FixedTicketFee, int _Type)
	{
		RouteID = _RouteID;
		RouteNameID = _RouteNameID;
		CategoryID = _CategoryID;
		FixedTicketFee = _FixedTicketFee; //如果此欄不為零代表本路線為固定票價價錢為此欄內容
		Type = _Type; //同RouteInfo中Type定義
	}
	public RouteInfoEx()
	{}
	public int RouteID;
	public int RouteNameID;
	public int CategoryID;    
	public int FixedTicketFee;
	public int Type;
	public int LikelyValue;
	
	//@Override
	public int compareTo(Object obj) {
		// return getTransName().compareTo(((RouteInfoEx)obj).getTransName());

		RouteInfoEx target = (RouteInfoEx) obj;
		String str1 = this.getRouteName();
		String str2 = target.getRouteName();

		str1 = str1.replace('[', '/');
		str2 = str2.replace('[', '/');

		if (this.LikelyValue == target.LikelyValue)// 同樣的起點出現
		{

			int index = 0;
			int small_length = str1.length();// 先取得最長比對的長度
			if (small_length > str2.length())
				small_length = str2.length();

			char A, B;
			A = str1.charAt(index);
			B = str2.charAt(index);
			while (A == B && index < small_length - 1)// 比較輸入字串後的字元,小的排前面
			{
				index++;
				A = str1.charAt(index);
				B = str2.charAt(index);

			}
			return Character.valueOf(A).compareTo(Character.valueOf(B));

		} else {
			return this.LikelyValue - target.LikelyValue; // 越早出現的排越前面
		}

	}
	
	public String getRouteName() {
		int hBus = BusNtvEngineManager.getBus();
		String routeName = BusNtvEngine.GetRouteNameByRouteID(hBus, RouteID);
		return routeName;
	}
	
	// 路線名稱- 省略中誇號
	public String getTransName() {
		String routeName = getRouteName();
		
		if ((routeName.charAt(0) != '[') && 
				(routeName.contains("[")))
		{
			String transName = routeName.substring(0, routeName.indexOf('[')+1);
			return transName;
		} else {
			return routeName;
		}
	}
}

