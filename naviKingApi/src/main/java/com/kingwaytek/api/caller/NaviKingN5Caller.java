package com.kingwaytek.api.caller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.kingwaytek.api.exception.ApiException;
import com.kingwaytek.api.exception.VersionNotSupportException;
import com.kingwaytek.api.model.CallerData;

public class NaviKingN5Caller {

	public static final String NAVIKING3D_SCHEME = "kwnavikingn5";
	public static final String NAVIKING3D_NAVI_FUNCTION_NAME = "navigation";
	public static final String ARGUMENT_LAT = "lat=";
	public static final String ARGUMENT_LON = "lon=";
	public static final String ARGUMENT_ROAD_ID = "road_id=";
	public static final String ARGUMENT_TARGET_NAME = "target_name=";
	public static final String ARGUMENT_ADDRESS = "address=";
	public static final int SUPPORT_VERSION = 514;

	public static void navigationTo(Activity activity, CallerData naviking3d) throws ApiException, VersionNotSupportException {
		// 增加支援N5的狀況
		if (checkVersionNotSupport(activity) && NaviKingN5Caller.checkVersionNotSupport(activity)) {
			throw new VersionNotSupportException("Version is too low to execute navigation.");
		}

		String scheme = NaviKing3dCaller.createScheme(naviking3d);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(scheme));
		activity.startActivity(intent);
	}

	static boolean checkVersionNotSupport(Activity activity) {
		return !InstallationAppChecker.checkVersionCodeLockingN5(activity, SUPPORT_VERSION);
	}

}