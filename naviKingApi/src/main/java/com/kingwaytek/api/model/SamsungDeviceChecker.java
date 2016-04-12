package com.kingwaytek.api.model;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.KeyEvent;

import com.kingwaytek.api.utility.UtilityApi;
import com.kingwyatek.api.R;



public class SamsungDeviceChecker {

	final static private String TAG = "SamsungDeviceChecker" ;	
	public final static String CHECK_BRAND = "sam" + "sung" ;
	static boolean bDeviceVROpen = false ;
	static int mErrorCode = 0; 
	
	public final static int ERROR_CODE_MODEL_NOT_MATCH = 0x00;
	public final static int ERROR_CODE_CHECK2_NOT_MATCH = 0x01;
	
	
	public static boolean check(Context ctx, AlertDialog alertDialog){

		boolean bPass = isSamsungDevice(ctx) && isInstallSamsungApps(ctx) ; 
		if(!bPass){	
			if(alertDialog != null){
				alertDialog.show() ;	
			}
		}
		return bPass ;
	}
	
	
	/*
	 *  SAMSUNG checker 
	 *  
	 *  spend time : 0.003s 
	 */
	private static boolean isSamsungDevice(Context ctx){
		//Log.i(TAG,"Brand:" + android.os.Build.BRAND) ;
		//Log.i(TAG,"MODEL:" + android.os.Build.MODEL) ;		
		//Log.i(TAG,"DEVICE:" + android.os.Build.DEVICE) ;
		
		final String MODEL_NAME = getCorrectModel(android.os.Build.MODEL) ;				// 型號
		final String BRAND_NAME = android.os.Build.BRAND ;				// 品牌
		final boolean IS_SAMSUNG_VER = isOneOfSamsungVersion(ctx) ;	// 版號

		if(IS_SAMSUNG_VER 
			&& BRAND_NAME != null 
			&& BRAND_NAME.length() > 0
			&& BRAND_NAME.equals(CHECK_BRAND)){
			return true ;
		}
		mErrorCode = ERROR_CODE_MODEL_NOT_MATCH ;
		return false ;
	}
	
	/** Remove the letter if last word is non-number */
	public static String getCorrectModel(String src){
		if(src == null || src.length() == 0 ) return "" ; 
		int len = src.length();
		Character chr = src.charAt(len-1) ;
		if(Character.isLetter(chr) || !Character.isDigit(chr)){
			src = src.substring(0, len-1) ;
		}
		return src;
	}
	
	public static boolean getSamsungVROpen(){
		return bDeviceVROpen ; 
	}
	
	public static int GetErrorCode(){
		return mErrorCode ; 
	}

	
	// 檢查是否相對應的app
	private static boolean isInstallSamsungApps(Context ctx){		
		boolean bInstalled = false ;
		try{
			PackageInfo pinfo = ctx.getPackageManager().getPackageInfo("com." + "sec." + "android." + "app.sam" + "sungapps", 0);	
			//pinfo = ctx.getPackageManager().getPackageInfo("com.sec.spp.push", 0);
			//pinfo = ctx.getPackageManager().getPackageInfo("com.osp.app.signin", 0);
			pinfo = null;
			bInstalled = true ;
		}catch (NameNotFoundException e){
			e.printStackTrace();
			bInstalled = false; 
			mErrorCode = ERROR_CODE_CHECK2_NOT_MATCH ;
		}
		return bInstalled ; 
	}
	
	private static boolean isVersion(String checkedPackageName,String packageName,int checkedVersionIndex,int versionIndex){
		boolean isSamePackageName = checkedPackageName.equals(packageName) ; 
		boolean isVersionRange = ( checkedVersionIndex >= versionIndex && checkedVersionIndex < (versionIndex + 10)) ;
		return isSamePackageName && isVersionRange ;
	}
	
	private static boolean isOneOfSamsungVersion(Context ctx){
		String versionName = UtilityApi.AppInfo.getAppVersionName(ctx);
		String packagename = ctx.getPackageName();
		
		String[] str = versionName.split("\\.");
		int ver = Integer.parseInt(str[1]);
		
		if(isVersion(packagename, PackageName.NaviKingCht3D.NAVIKING_3D_SAMSUNG_STD, ver, 120)){
			return true;
		}if(isVersion(packagename, PackageName.NaviKingCht3D.NAVIKING_3D_SAMSUNG_PRO, ver, 120)){
			return true;
		}if(isVersion(packagename, PackageName.NaviKingN3.NAVIKING_PRO_2, ver, 120)){
			return true;
		}else{
			return false;
		}
	}
	
	
	public static AlertDialog getSamsungWarnningBuilder(final Activity activity){
		Builder alertDialogBuilder = new AlertDialog.Builder(activity) ;
		alertDialogBuilder.setTitle(R.string.alert_samsung_title);
		alertDialogBuilder.setMessage(activity.getString(R.string.alert_samsung_content, android.os.Build.MODEL)); //+ SamsungLicense.GetErrorCode() + ")"); 
		alertDialogBuilder.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				
				return true;
			}
			
		});
		alertDialogBuilder.setCancelable(false);		
		alertDialogBuilder.setNegativeButton(R.string.alert_samsung_btn_confirm, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.finish();
			}
			
		});
		
		return alertDialogBuilder.create() ; 
	}
}
