package com.kingwaytek.api.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.KeyEvent;

import com.kingwaytek.api.caller.NaviKingCaller;
import com.kingwaytek.api.model.PackageName;
import com.kingwyatek.api.R;

public class DialogAgent {
	
	public static AlertDialog.Builder getNetworkUnavaliableDialog(
			final Activity activity) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setTitle(activity.getString(R.string.no_network_title));
		dialog.setMessage(activity.getString(R.string.no_network_content));
		dialog.setPositiveButton(activity.getString(R.string.confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						activity.startActivity(new Intent(
								Settings.ACTION_SETTINGS));
						dialog.cancel();
					}
				});
		dialog.setNeutralButton(activity.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		return dialog;
	}

	public static void openDialogNotInstallNaviKing(final Activity activity) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				activity);
		dialog.setTitle(activity.getResources().getString(
				R.string.dialog_title_not_install_naviking));
		dialog.setMessage(activity.getResources().getString(
				R.string.dialog_message_not_install_naviking));
		dialog.setPositiveButton(activity.getResources()
				.getString(R.string.download),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent web = new Intent(Intent.ACTION_VIEW, Uri
								.parse(NaviKingCaller.GOOGLE_PLAY_COMMENT
										+ PackageName.NaviKingN3.NAVIKING_PRO_1));
						activity.startActivity(web);
					}
				});
		dialog.setNeutralButton(activity.getResources()
				.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();

					}
				});
		dialog.show();
	}
	
	public static void openDialogNotInstallLocalKingFun(Activity activity,DialogInterface.OnClickListener onComfirmClick) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(
				activity);
		dialog.setTitle(activity.getResources().getString(
				R.string.dialog_title_not_install_localkingfun));
		dialog.setMessage(activity.getResources().getString(
				R.string.dialog_message_not_install_localkingfun));
		dialog.setPositiveButton(activity.getResources()
				.getString(R.string.download),onComfirmClick);
		dialog.setNeutralButton(activity.getResources()
				.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();

					}
				});
		dialog.show();
	}
	
	public static void openDialogVersionNotSupport(final Activity activity) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				activity);
		dialog.setTitle(activity.getResources().getString(
				R.string.dialog_title_version_not_support_naviking));
		dialog.setMessage(activity.getResources().getString(
				R.string.dialog_message_version_not_support_naviking));
		dialog.setPositiveButton(activity.getResources()
				.getString(R.string.confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		dialog.show();
	}
	
	public static AlertDialog.Builder getBaseDialog(final Context ctx,int titleResId,int contentResId){
		return getBaseDialog(ctx,titleResId,contentResId,null) ;
	}
	
	public static AlertDialog.Builder getBaseDialog(final Context ctx,int titleResId,int contentResId,OnClickListener onClickListener){		
		String title = titleResId == -1 ? null : ctx.getString(titleResId);
		String message = contentResId == -1 ? null : ctx.getString(contentResId);
		return getBaseDialog(ctx,title,message,onClickListener) ;
	}
	
	public static AlertDialog.Builder getBaseDialog(final Context ctx,String title,String message){		
		return getBaseDialog(ctx,title,message,null) ;
	}
	
	public static AlertDialog.Builder getBaseDialog(final Context ctx,String title,String message,OnClickListener onClickListener){
		AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);		
		if(title != null){
			dialog.setTitle(title);
		}
		if(message != null){
			dialog.setMessage(message);
		}
		if(onClickListener != null){
			dialog.setPositiveButton(R.string.confirm, onClickListener);
		}
		return dialog ;
	}
		
	public static AlertDialog.Builder setDismissListener(AlertDialog.Builder dialog,int textId){
		dialog.setPositiveButton(textId,
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		return dialog ;
	}
	
	public static AlertDialog getSamsungWarnningBuilder(final Activity activity){
		Builder alertDialogBuilder = getBaseDialog(activity, "Samsung Device 驗證失敗", "裝置不正確("+ android.os.Build.MODEL +")，程式即將關閉。");
		alertDialogBuilder.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				
				return true;
			}
			
		});
		alertDialogBuilder.setCancelable(false);		
		alertDialogBuilder.setNegativeButton(R.string.confirm, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				UtilityApi.forceCloseTask();
			}
			
		});
		
		return alertDialogBuilder.create() ; 
	}
}