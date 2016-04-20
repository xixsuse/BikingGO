package com.kingwaytek.cpami.bykingTablet.app.VersionUpdate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.CreatMD5Code;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.CommunicationBaseActivity;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Update extends CommunicationBaseActivity {

	public static final int REQUEST_CODE = 999;

	private String msg01 = "是否連線檢查有無更新版本?";
	private String msg02 = "軟體與圖資都有新版本，請至單車ing網站下載。";
	private String msg03 = "目前有新版的軟體，是否下載更新？";
	private String msg04 = "目前有新版的圖資，請至單車ing網站下載。";
	private String msg05 = "目前已是最新版本！";
	private String strConnect = "連線檢查";
	private String FileName;

	private int TAG2 = 2;// "apk與圖資都有新版本是否下載";
	private int TAG3 = 3;// "有更新的apk版本是否下載";
	private int TAG4 = 4;// "有更新的圖資版本是否下載";
	private int TAG5 = 5;// "皆無新的版本";

	private int CODE_HAVE_NEW_VERSION = 6;
	private int CODE_NO_NEW_VERSION = 7;
	private int DATA_HAVE_NEW_VERSION = 8;
	private int DATA_NO_NEW_VERSION = 9;

	private String getDataRequest;

	private String getVersionRequest;

	private ProgressDialog progressDialog;

	private int versionCompareCode;

	private int dataCompareCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);

		getDataRequest = this.constructGetDataRequest();

		getVersionRequest = this.constructGetVersionRequest();

		// 詢問是否連線檢查版本
		showDialogForCheck(msg01, strConnect);
	}

	// Get data request
	@SuppressWarnings("deprecation")
	private String constructGetDataRequest() {
		Date date = new Date();

		String MD5Code = CreatMD5Code
				.getMD5((String.valueOf(((date.getMonth() + 1) + date
						.getHours()) * (1106 + date.getDate())) + "Kingway")
						.getBytes());

		String httpURL = "http://biking.cpami.gov.tw/Service/Version?type=data&code="
				+ MD5Code;

		return httpURL;
	}

	// Get version request
	@SuppressWarnings("deprecation")
	private String constructGetVersionRequest() {
		Date date = new Date();

		String MD5Code = CreatMD5Code
				.getMD5((String.valueOf(((date.getMonth() + 1) + date
						.getHours()) * (1106 + date.getDate())) + "Kingway")
						.getBytes());
		String httpURL = "http://biking.cpami.gov.tw/Service/Version?type=software&code="
				+ MD5Code;

		return httpURL;
	}

	// Check update dialog
	private void showDialogForCheck(String mag, String text) {

		new AlertDialog.Builder(Update.this)
				.setMessage(mag)
				.setPositiveButton(text, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						progressDialog = ProgressDialog.show(Update.this,
								"查詢中", "請稍候");

						Update.this.startHttpGet(getVersionRequest, false,
								null, null, null, null);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Update.this.finish();
					}
				}).show();
	}

	/** HTTP result */
	// Success
	@Override
	public void didFinishWithGetRequest(String requestString,
			String resultString, Header[] respondHeaders) {
		super.didFinishWithGetRequest(requestString, resultString,
				respondHeaders);

		if (requestString.contains(getVersionRequest)) {
			this.parseVersionResult(resultString);
		}

		if (requestString.contains(getDataRequest)) {
			this.parseData(resultString);
		}
	}

	// Failed
	@Override
	public void didFailWithGetRequest(String requestString, String resultString) {
		super.didFailWithGetRequest(requestString, resultString);

		this.dismissProogressDialog();

		this.finishActivityDialog("目前無網路連線");
	}

	// Parse version result
	private void parseVersionResult(String json) {

		try {

			JSONArray array = new JSONArray(json);

			JSONObject object = array.getJSONObject(0);

			String version = object.getString("version");

			// Check native version
			PackageInfo pinfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);

			String nativeVersionName = pinfo.versionName;

			FileName = "biking_tablet" + version + ".apk";

			// Check version
			if (version.equals(nativeVersionName)) {

				versionCompareCode = CODE_NO_NEW_VERSION;

			} else {
				versionCompareCode = CODE_HAVE_NEW_VERSION;
			}

			this.startHttpGet(getDataRequest, false, null, null, null, null);

		} catch (JSONException e) {
			e.printStackTrace();

			this.dismissProogressDialog();

			this.finishActivityDialog("查無資料");

		} catch (NameNotFoundException e) {
			e.printStackTrace();

			this.dismissProogressDialog();

			this.finishActivityDialog("查無資料");
		}
	}

	// Parse data result
	private void parseData(String json) {

		try {

			JSONArray array = new JSONArray(json);

			JSONObject object = array.getJSONObject(0);

			String version = object.getString("version");

			String DataVerOnPackage = AppController.getInstance().getDataVersion();

			// Check map version
			if (version.equals(DataVerOnPackage)) {

				dataCompareCode = DATA_NO_NEW_VERSION;

			} else {
				dataCompareCode = DATA_HAVE_NEW_VERSION;
			}

			this.completeWork();

		} catch (JSONException e) {
			e.printStackTrace();

			this.dismissProogressDialog();

			this.finishActivityDialog("查無資料");
		}
	}

	// Check version and data end
	private void completeWork() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		int resultCode = 0;

		if (versionCompareCode == CODE_HAVE_NEW_VERSION) {
			if (dataCompareCode == DATA_HAVE_NEW_VERSION)
				resultCode = TAG2;
			else {
				resultCode = TAG3;
			}
		} else if (versionCompareCode == CODE_NO_NEW_VERSION) {
			if (dataCompareCode == DATA_NO_NEW_VERSION)
				resultCode = TAG5;
			else {
				resultCode = TAG4;
			}
		}

		String message = "";

		switch (resultCode) {
		case 2:
			message = msg02;
			showDialogForUpdata(message);
			break;
		case 3:
			message = msg03;
			showDialogForUpdata(message);
			break;
		case 4:
			message = msg04;
			this.finishActivityDialog(message);
			break;
		case 5:
			message = msg05;
			this.finishActivityDialog(message);
			break;

		default:
			break;
		}
	}

	private void showDialogForUpdata(String mag) {
		new AlertDialog.Builder(Update.this)
				.setMessage(mag)
				.setPositiveButton("下載更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								Log.i("Update.java", "download");

								Intent intent = new Intent(Update.this,
										ConnectWeb.class);
								intent.putExtra("filename", FileName);

								Log.i("Updata.java", "FileName=" + FileName);

								Update.this.startActivity(intent);

								Update.this.finish();
							}
						})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();
	}

	// Parsing error or did failed connect ... etc, finish Activity
	private void finishActivityDialog(String error) {
		new AlertDialog.Builder(this).setMessage(error)
				.setNeutralButton("確定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						Update.this.finish();
					}
				}).show();
	}

	// Dismiss loading progress dialog
	private void dismissProogressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}
