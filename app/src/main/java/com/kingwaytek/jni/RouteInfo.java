package com.kingwaytek.jni;

public class RouteInfo {
	public RouteInfo(int _RouteID, int _RouteNameID, int _CategoryID, int _Type)
	{
		RouteID = _RouteID;
		RouteNameID = _RouteNameID;
		CategoryID = _CategoryID;
		/*Categoryid 1101:公車 1102:捷運 1103:長途客運 1104:火車 1105:高鐵 1106:飛機 1107:船舶*/
		Type = _Type;
		/*Type:
		For train(1104) case:	1 自強	2 莒光	3.復興/區間	4.普通	0.其他
		For bus(1101) case:  0.北北基 1桃竹苗 2.中彰投 3高高屏 4.其他 
		*/
	}
	public RouteInfo()
	{}
	public int RouteID;
	public int RouteNameID;
	public int CategoryID;    
	public int Type;
}

