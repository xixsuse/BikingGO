package com.kingwaytek.api.model;


import java.lang.reflect.Field;

import com.kingwyatek.api.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;




public class XiaoMiDeviceChecker {

	public static boolean IsXiaoMiDevice(Context context){
		
		boolean isXiaomiDevice = false; 
		boolean isTwBuildDevice = false ;
		boolean isHongmiDevice = false ;
		boolean isXiaomi3Device = false ;
		boolean isXiaomi3WCDMADevice = false ;
		boolean isXiaoBuildCNOrTW = isXiaoBuildCNOrTW();
		boolean isMi4i = false ;
		try {
			Class miuiBuildIInfoClass = Class.forName("miui.os.Build");
			
			
			//Field fieldIsTwBuild = miuiBuildIInfoClass.getField("IS_TW_BUILD");
			//Field fieldIsXiaomi = miuiBuildIInfoClass.getField("IS_XIAOMI");			
			//Field fieldIsHongmi = miuiBuildIInfoClass.getField("IS_HONGMI"); 
			
//			isTwBuildDevice = fieldIsTwBuild.getBoolean(miuiBuildIInfoClass);
//			isXiaomiDevice = fieldIsXiaomi.getBoolean(miuiBuildIInfoClass);			
//			isHongmiDevice = fieldIsHongmi.getBoolean(miuiBuildIInfoClass);
			
			//isTwBuildDevice = checkReflectionField(miuiBuildIInfoClass, "IS_TW_BUILD");
			try {					
				Field fieldIsTwBuild = miuiBuildIInfoClass.getField("IS_TW_BUILD");				
				isTwBuildDevice = fieldIsTwBuild.getBoolean(miuiBuildIInfoClass);								
			} catch (NoSuchFieldException e){
				//e.printStackTrace();
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			}
			
			try{
				String miDeviceName ="";
				String miModelName ="" ;
				try {					
					Field build = miuiBuildIInfoClass.getField("DEVICE");				
					miDeviceName = (String) build.get(miuiBuildIInfoClass);
					Field model = miuiBuildIInfoClass.getField("MODEL");				
					miModelName = (String)model.get(miuiBuildIInfoClass);
				} catch (NoSuchFieldException e){
					//e.printStackTrace();
				} catch (IllegalArgumentException e) {
					//e.printStackTrace();
				} catch (IllegalAccessException e) {
					//e.printStackTrace();
				}
				isMi4i = miDeviceName != null && miDeviceName.contains("ferrari") &&  miModelName != null && miModelName.equals("Mi 4i");
			}catch(Exception e){
				e.printStackTrace();
			}
			
			//isXiaomiDevice = checkReflectionField(miuiBuildIInfoClass, "IS_XIAOMI");
			try {					
				Field fieldIsXiaomi = miuiBuildIInfoClass.getField("IS_XIAOMI");				
				isXiaomiDevice = fieldIsXiaomi.getBoolean(miuiBuildIInfoClass);								
			} catch (NoSuchFieldException e){
				//e.printStackTrace();
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			}
			
			//isHongmiDevice = checkReflectionField(miuiBuildIInfoClass, "IS_HONGMI");
			try {					
				Field fieldIsHongmi = miuiBuildIInfoClass.getField("IS_HONGMI");				
				isHongmiDevice = fieldIsHongmi.getBoolean(miuiBuildIInfoClass);						
			} catch (NoSuchFieldException e){
				//e.printStackTrace();
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			}
			
			try {					
				Field fieldIsXiaomi3 = miuiBuildIInfoClass.getField("IS_MITHREE");//_WCDMA				
				isXiaomi3Device = fieldIsXiaomi3.getBoolean(miuiBuildIInfoClass);						
			} catch (NoSuchFieldException e){
				//e.printStackTrace();
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			}
			
			isXiaomi3WCDMADevice = checkReflectionField(miuiBuildIInfoClass, "IS_MITHREE_WCDMA");
					
//			Log.i(TAG,"Field_bResult1" + isXiaomiDevice);
//			Log.i(TAG,"Field_bResult2" + isTwBuildDevice);
//			Log.i(TAG,"Field_bResult3" + isHongmiDevice);
//			Log.i(TAG,"Field_bResult4(Mi3)" + isXiaomi3Device);
//			Log.i(TAG,"Field_bResult5(Mi3Wcdma)" + isXiaomi3WCDMADevice);
			
		} catch (ClassNotFoundException e) {			
			//e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//e.printStackTrace();
		}
		boolean isPassBuildCNOrTW = isTwBuildDevice || isXiaoBuildCNOrTW;
		boolean isXisoMiDevice = ( isXiaomiDevice || isHongmiDevice || isXiaomi3Device || isXiaomi3WCDMADevice || isMi4i);//isXiaomi3WCDMADevice
		
		return isPassBuildCNOrTW & isXisoMiDevice ; 
	}
	
	public static boolean isXiaoBuildCNOrTW(){
		String region = "default";
		boolean isXiaoBuildCNOrTW = false;
		try{
			region = SystemProperties.get("ro.miui.region", "default");
			boolean isCN = region.equals("CN");
			boolean isTW = region.equals("TW");
			isXiaoBuildCNOrTW = isCN || isTW;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return isXiaoBuildCNOrTW;
	}
	
	static boolean checkReflectionField(Class miuiBuildIInfoClass,String fieldName){
		boolean bResult = false ; 
		try {					
			Field fieldIsXiaomi3WCDMA = miuiBuildIInfoClass.getField("IS_MITHREE_WCDMA");//				
			bResult = fieldIsXiaomi3WCDMA.getBoolean(miuiBuildIInfoClass);						
		} catch (NoSuchFieldException e){
			//e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//e.printStackTrace();
		} catch (IllegalAccessException e) {
			//e.printStackTrace();
		}
		return bResult ;
	}
	
	public static AlertDialog getXiaoWarnningBuilder(final Activity activity){
		Builder alertDialogBuilder = new AlertDialog.Builder(activity) ;
		alertDialogBuilder.setTitle(R.string.alert_xiaomi_title);
		alertDialogBuilder.setMessage(R.string.alert_xiaomi_content); //+ SamsungLicense.GetErrorCode() + ")"); 
		alertDialogBuilder.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				
				return true;
			}
			
		});
		alertDialogBuilder.setCancelable(false);		
		alertDialogBuilder.setNegativeButton(R.string.alert_xiaomi_btn_confirm, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.finish();
			}
			
		});
		
		return alertDialogBuilder.create() ; 
	}
}
