package com.kingwaytek.cpami.bykingTablet.app;

import android.os.Environment;

import com.kingwaytek.jni.RouteNtvEngine;

public class GetNaviKingVersion {

	/** 取得圖資版本 */
	public static String GetMapDBVersion() {
		int mapVersion = 0;
		String MAP = "";
		String dir = Environment.getExternalStoragePublicDirectory(MapDownloadActivity.DIR_DATA).getAbsolutePath();
		String strMapDBVer = RouteNtvEngine.GetMapVersion(dir);
		
		if(strMapDBVer == null){
			return MAP;
		}
		try {
			mapVersion = Integer.parseInt(strMapDBVer);

			StringBuilder sb = new StringBuilder(String.valueOf(mapVersion));
			MAP = sb.insert(4, "/").insert(7, "/").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MAP;
	}

}
