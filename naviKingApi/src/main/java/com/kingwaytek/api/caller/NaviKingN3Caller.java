package com.kingwaytek.api.caller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.kingwaytek.api.exception.ApiException;
import com.kingwaytek.api.exception.VersionNotSupportException;
import com.kingwaytek.api.model.CallerData;
import com.kingwaytek.api.model.PackageName;

/**
 * 
 * <h2>提供三種導航方式</h2>
 * <ul>
 * <li>
 * 1.直接導航至所給座標點 Navi2Point</li>
 * <li>
 * 2.直接導航至地址 Navi2Address</li>
 * <li>
 * 3.直接導航至關鍵字 Navi2Keyword</li>
 * </ul>
 * 
 * <h2>可以根據參數進行設定</h2>
 * <ul>
 * <li>1.設定路徑規劃方式 {@link Caller.RoutingMethod}</li>
 * <li>2.是否要迴避收費站 {@link Caller.AvoidMode}</li> *
 * </ul>
 * 
 * <h2>接收導航狀態</h2> 而在導航中，透過 {@link GetRoadLocationInfo} 的非同步方式取得目前道路的狀態 <br />
 * 將會需要註冊一個 {@link BrocastReciver} 來接收回傳的資訊,<br />
 * <br />
 * 利用{@link GetRoadInfoDataFromIntent} <br />
 * 可以直接將Inter回傳的Bundle轉換成{@link RoadInfo} 物件取得相關的道路資訊
 * 
 * @author jeff.lin
 * 
 * 
 */

public class NaviKingN3Caller extends Caller {
	/**
	 * A.直接導航至所給座標點 <br />
	 * 
	 * <p>
	 * try { CallerData callerData = new CallerData(25.132301,121.739466);
	 * NaviKingN3Caller.navigationTo(UIDemoActivity.this,callerData); } catch
	 * (ApiException e) { e.printStackTrace(); }
	 * </p>
	 * 
	 * @param activity
	 *            Activity
	 * @param poi_target
	 *            設定目的資訊,格式請參考{@link POI}
	 * @param routingMode
	 *            導航路徑規劃模式,格式請參考 {@link Caller.RoutingMethod}
	 * @param routingAvoidMode
	 *            是否避免收費站,格式請參考 @{link Caller.AvoidMode}
	 * @throws FormatNotMatchException
	 *             格式錯誤或是 null會丟出此Exception
	 */
	public static final int SUPPORT_VERSION = 128;
	public static final String CLASS_NAME_N3_APK_RECEIVER = "com.kingwaytek.receiver.apkReceiver";
	public static final int HAMI_SUPPORT_VERSION = 108;
	public static final int HAMI_PLATFORM_SUPPORT_NUMBER = 10;

	public static void navigationTo(Activity activity, CallerData callerData, int routingMode, int routingAvoidMode) throws ApiException, VersionNotSupportException {

		if (callerData == null) {
			throw new ApiException(ApiException.CALLER_DATA_CANT_BE_NULL);
		}
		if (checkVersionNotSupport(activity)) {
			throw new VersionNotSupportException("Version is too low to execute navigation.");
		}

		String installedPackageName = InstallationAppChecker.getInstalledPackageName(activity, PackageName.NaviKingN3.ALL_SETS);

		Intent intent = new Intent(CALLER_NAME);
		intent.setClassName(installedPackageName, CLASS_NAME_N3_APK_RECEIVER);
		intent.putExtra(CMD_NAME_TYPE, callerData.getNaviType());
		intent.putExtra(CMD_NAME_POINT, callerData.toString());
		setDetailIntent(intent, routingMode, routingAvoidMode);
		activity.startService(intent);
	}

	static boolean checkVersionNotSupport(Activity activity) {
		return !InstallationAppChecker.checkVersionCodeLockingN3(activity, SUPPORT_VERSION, NaviKing3dCaller.SUPPORT_VERSION);
	}

	public static void navigationTo(Activity activity, CallerData callerData) throws ApiException {

		if (callerData == null) {
			throw new ApiException(ApiException.CALLER_DATA_CANT_BE_NULL);
		}

		int routingMode = Caller.RoutingMethod.ROUTE_METHOD_BEST_PATH;
		int routingAvoidMode = Caller.AvoidMode.AVOID_MODE_OPEN_FALSE;

		String installedPackageName = InstallationAppChecker.getInstalledPackageName(activity, PackageName.NaviKingN3.ALL_SETS);
		Log.v(TAG, "installedPackageName:" + installedPackageName);
		Intent intent = new Intent(CALLER_NAME);
		intent.setClassName(installedPackageName, CLASS_NAME_N3_APK_RECEIVER);
		intent.putExtra(CMD_NAME_TYPE, callerData.getNaviType());
		intent.putExtra(CMD_NAME_POINT, callerData.toString());
		setDetailIntent(intent, routingMode, routingAvoidMode);
		activity.startService(intent);
	}

	// public static void navigationToAddr(Activity activity, CallerData
	// callerData)
	// throws ApiException {
	//
	// if (callerData == null) {
	// throw new ApiException(ApiException.CALLER_DATA_CANT_BE_NULL);
	// }
	//
	// int routingMode = Caller.RoutingMethod.ROUTE_METHOD_BEST_PATH;
	// int routingAvoidMode = Caller.AvoidMode.AVOID_MODE_OPEN_FALSE;
	//
	//
	// String installedPackageName =
	// InstallationAppChecker.getInstalledPackageName(activity,PackageName.NaviKingN3.ALL_SETS);
	// Log.v(TAG, "installedPackageName:"+installedPackageName);
	// Intent intent = new Intent(CALLER_NAME);
	// intent.setClassName(installedPackageName,CLASS_NAME_N3_APK_RECEIVER );
	//
	// intent.putExtra(CMD_NAME_TYPE, TYPE_NAVI_TO_ADDRESS);
	// intent.putExtra(CMD_NAME_POINT, callerData.toString());
	// intent.putExtra(CMD_NAME_ADDR, callerData.getAddress());
	//
	// setDetailIntent(intent, routingMode, routingAvoidMode);
	// activity.startService(intent);
	// }

	/**
	 * 檢查是否Hami版本的導航王存在並且需要是108版後的導航王才支援
	 * 
	 */
	private static boolean isHamiVerExistOrSupported(Activity act) {
		try {
			PackageInfo pinfo = act.getPackageManager().getPackageInfo("com.kingwaytek", 0);
			String ver = pinfo.versionName;
			if (ver != null && ver.length() > 0) {
				String[] vers = ver.split("\\.");
				int platformVer = Integer.parseInt(vers[1]);
				int lastVer = Integer.parseInt(vers[3]);
				if (platformVer >= HAMI_PLATFORM_SUPPORT_NUMBER && platformVer < HAMI_PLATFORM_SUPPORT_NUMBER + 10 && lastVer >= HAMI_SUPPORT_VERSION) {
					// if(platformVer >= 0 && platformVer < 10 && lastVer >=
					// HAMI_SUPPORT_VERSION){
					return true;
				} else {
					return false;
				}
			}
			pinfo = null;
			return true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void setDetailIntent(Intent intent, int routingMode, int routingAvoidMode) {
		intent.putExtra(CMD_NAME_ROUTING_MODE, routingMode);
		intent.putExtra(CMD_NAME_ROUTING_AVOID_MODE, routingAvoidMode);
	}
}