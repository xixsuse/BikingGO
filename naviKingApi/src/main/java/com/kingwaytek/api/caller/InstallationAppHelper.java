package com.kingwaytek.api.caller;

import com.kingwaytek.api.utility.UtilityApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

//http://developer.android.com/distribute/tools/promote/linking.html
public class InstallationAppHelper {
	
	public static void showAppOnGooglePlay(Context context,String packageName){		
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
		context.startActivity(intent);
	}
	
	public static void showAppOnGooglePlay(Context context){
		String packageName = UtilityApi.AppInfo.getAppPackageName(context);
		if(packageName == null){
			return ;
		}
		showAppOnGooglePlay(context, packageName);
	}	
}