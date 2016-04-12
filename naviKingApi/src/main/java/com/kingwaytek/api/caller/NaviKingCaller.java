package com.kingwaytek.api.caller;

import android.app.Activity;

import com.kingwaytek.api.exception.ApiException;
import com.kingwaytek.api.exception.VersionNotSupportException;
import com.kingwaytek.api.model.CallerData;
import com.kingwaytek.api.model.PackageName;
import com.kingwaytek.api.utility.DialogAgent;

public class NaviKingCaller {
	public static final String TAG = "NaviKingCaller";
	public static final String NAVIKING3D_SCHEME = "NaviKingCaller";
	public static final String GOOGLE_PLAY_COMMENT = "https://play.google.com/store/apps/details?id=";

	public static void navigationTo(Activity activity, CallerData callerData) throws ApiException, VersionNotSupportException {

		boolean bCheckInstall3D = InstallationAppChecker.hasAppInstalled(activity, PackageName.NaviKingCht3D.ALL_SETS);
		boolean bCheckInstallN3orN5 = InstallationAppChecker.hasAppInstalled(activity, PackageName.NaviKingN3.ALL_SETS);
		boolean bCheckVersionNotSupportN3 = NaviKingN3Caller.checkVersionNotSupport(activity);
		boolean bCheckVersionNotSupport3D = NaviKing3dCaller.checkVersionNotSupport(activity);
		boolean bCheckVersionNotSupportN5 = NaviKingN5Caller.checkVersionNotSupport(activity);

		// Log.v(TAG, "bCheckInstall3D:" + bCheckInstall3D);
		// Log.v(TAG, "bCheckInstallN3orN5:" + bCheckInstallN3orN5);
		// Log.v(TAG, "bCheckVersionNotSupportN3:" + bCheckVersionNotSupportN3);
		// Log.v(TAG, "bCheckVersionNotSupport3D:" + bCheckVersionNotSupport3D);
		// Log.v(TAG, "bCheckVersionNotSupportN5:" + bCheckVersionNotSupportN5);

		if (bCheckInstall3D && bCheckInstallN3orN5) {
			// 3D與N3都有裝
			if (bCheckVersionNotSupport3D) {
				if (bCheckVersionNotSupportN5) {
					if (bCheckVersionNotSupportN3) {
						// 3D與N3都有裝 但版號都不支援
						DialogAgent.openDialogVersionNotSupport(activity);
					} else {
						// 3D與N3都有裝 3D版號不支援 N3版號支援
						NaviKingN3Caller.navigationTo(activity, callerData);
					}
				} else {
					// 3D與N3都有裝, 3D版號不支援, 但N5版號支援
					NaviKing3dCaller.navigationTo(activity, callerData);
				}
			} else {
				// 有裝3D & 3D版號支援
				NaviKing3dCaller.navigationTo(activity, callerData);
			}
		} else if (bCheckInstall3D) {
			if (bCheckVersionNotSupport3D) {
				// 只有裝3D & 3D版號不支援
				DialogAgent.openDialogVersionNotSupport(activity);
			} else {
				// 只有裝3D & 3D版號支援
				NaviKing3dCaller.navigationTo(activity, callerData);
			}
		} else if (bCheckInstallN3orN5) {
			// 有裝N3或N5
			if (bCheckVersionNotSupportN5) {
				// N5版號不支援
				if (bCheckVersionNotSupportN3) {
					// N5版號不支援, N3版號不支援
					DialogAgent.openDialogVersionNotSupport(activity);
				} else {
					NaviKingN3Caller.navigationTo(activity, callerData);
				}
			} else {
				// N5版號支援
				NaviKingN5Caller.navigationTo(activity, callerData);
			}
		} else {
			// 3D與N3都沒有裝
			DialogAgent.openDialogNotInstallNaviKing(activity);
		}
	}
}