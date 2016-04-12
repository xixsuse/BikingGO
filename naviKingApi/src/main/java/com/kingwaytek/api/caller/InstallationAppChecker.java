package com.kingwaytek.api.caller;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

import com.kingwaytek.api.model.PackageName;

public class InstallationAppChecker {

	final static String TAG = "CheckInstallLocalKingApp";

	// 檢查有無安裝導航王N3 Std And Pro
	public static boolean checkInstallNaviKingStdAndPro(Context context) {
		return hasAppInstalled(context, PackageName.NaviKingN3.ALL_SETS);
	}

	// 檢查有無安裝導航王N3 Std
	public static boolean checkInstallNaviKingStd(Context context) {
		return hasAppInstalled(context, PackageName.NaviKingN3.NAVIKING_N3_STD_SETS);
	}

	// 檢查有無安裝導航王N3 Pro
	public static boolean checkInstallNaviKingPro(Context context) {
		return hasAppInstalled(context, PackageName.NaviKingN3.NAVIKING_N3_PRO_SETS);
	}

	// 檢查有無安裝導航王全3D
	public static boolean checkInstallLocking3D(Context context) {
		return hasAppInstalled(context, PackageName.NaviKingCht3D.ALL_SETS);
	}

	// 檢查導航王N3的VersionCode
	public static boolean checkVersionCodeLockingN3(Activity activity, int versionCode, int highestVersionCode) {
		return checkVersionSupport(activity, PackageName.NaviKingN3.ALL_SETS, versionCode, highestVersionCode);
	}

	// 檢查導航王N5的VersionCode
	public static boolean checkVersionCodeLockingN5(Activity activity, int versionCode) {
		return checkVersionSupport(activity, PackageName.NaviKingN3.ALL_SETS, versionCode);
	}

	// 檢查導航王全3D的VersionCode TODO rename NaviKing3D
	public static boolean checkVersionCodeLocking3D(Activity activity, int versionCode) {
		return checkVersionSupport(activity, PackageName.NaviKingCht3D.ALL_SETS, versionCode);
	}

	// 檢查樂客玩樂的VersionCode
	public static boolean checkVersionCodeLocalKingFun(Activity activity, int versionCode) {
		return checkVersionSupport(activity, PackageName.LocalKingFun.LOCALKING_FUN_SETS, versionCode);
	}

	static boolean hasResoloveInfoByPackageName(PackageManager manager, String packageName) {
		Intent intent = new Intent().setPackage(packageName);
		List<ResolveInfo> resoloveInfos = manager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
		if (resoloveInfos != null && resoloveInfos.size() > 0) {
			return true;
		}
		return false;
	}

	public static boolean hasAppInstalled(Context context, String[] checkedSets) {
		PackageManager manager = context.getPackageManager();
		boolean bFlag = false;
		for (String packageName : checkedSets) {
			if (hasResoloveInfoByPackageName(manager, packageName)) {
				bFlag = true;
				break;
			}
		}
		return bFlag;
	}

	public static boolean hasAppInstalled(Context context, String checkPackageName) {
		String[] checkSets = new String[] { checkPackageName };
		return hasAppInstalled(context, checkSets);
	}

	public static String getInstalledPackageName(Context context, String[] checkedSets) {
		PackageManager manager = context.getPackageManager();
		for (String packageName : checkedSets) {
			if (hasResoloveInfoByPackageName(manager, packageName)) {
				return packageName;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param activity
	 * @param checkedSets
	 * @param versionCode
	 *            版號支援的底線
	 * @return true:版號支援 false:版號不支援
	 */
	static boolean checkVersionSupport(Activity activity, String[] checkedSets, int versionCode) {
		boolean bFlag = false;
		for (String packageName : checkedSets) {
			PackageInfo packageInfo = getPackageInfo(activity, packageName);
			if (packageInfo != null && packageInfo.versionCode >= versionCode) {
				bFlag = true;
			}
		}
		return bFlag;
	}

	/**
	 * 
	 * @param activity
	 * @param checkedSets
	 * @param versionCode
	 *            版號支援的底線
	 * @param highestVersionCode
	 *            版號支援的最高底線
	 * @return true:版號支援 false:版號不支援
	 */
	static boolean checkVersionSupport(Activity activity, String[] checkedSets, int versionCode, int highestVersionCode) {
		boolean bFlag = false;
		for (String packageName : checkedSets) {
			PackageInfo packageInfo = getPackageInfo(activity, packageName);
			if (packageInfo != null && packageInfo.versionCode >= versionCode && packageInfo.versionCode < highestVersionCode) {
				bFlag = true;
			}
		}
		return bFlag;
	}

	static PackageInfo getPackageInfo(Activity activity, String queryPackageName) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = activity.getPackageManager().getPackageInfo(queryPackageName, PackageManager.GET_CONFIGURATIONS);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return packageInfo;
		}
		return packageInfo;
	}
}