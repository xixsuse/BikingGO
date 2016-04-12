package com.kingwaytek.cpami.bykingTablet.app;

import android.content.Context;

public class IsAllowedConnection {

	public static boolean checkConnectionPermission(Context context) {

		String allow = PreferenceActivity.isInternetConfirmEnabled(context);

		if (allow.equals("true")) {
			return true;
		}
		
		return false;
	}
}
