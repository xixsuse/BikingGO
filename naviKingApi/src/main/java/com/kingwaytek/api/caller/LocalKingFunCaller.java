package com.kingwaytek.api.caller;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.kingwaytek.api.exception.ApiException;
import com.kingwaytek.api.exception.NotInstalledException;
import com.kingwaytek.api.exception.VersionNotSupportException;
import com.kingwaytek.api.model.PackageName;

public class LocalKingFunCaller {
	final static String TAG = "launchAppUtility";

	public static final String LOCALKINGFUN_SCHEME = "kwlocalkingfun";

	public static final String LOCALKINGFUN_COUPON_FUNCTION_NAME = "coupon_verification";
	public static final String ARGUMENT_CPID = "cpid=";

	public static final String LOCALKINGFUN_POI_INFO_FUNCTION_NAME = "poi_info";
	public static final String ARGUMENT_POIID = "poiid=";

	public static final String LOCALKINGFUN_POI_ADD_FUNCTION_NAME = "poi_add";

	public static final String LOCALKINGFUN_POI_REVIEW_FUNCTION_NAME = "poi_review";

	public static final String LOCALKINGFUN_POI_ERROR_REPORT_FUNCTION_NAME = "error_report";

	public static final String ARGUMENT_APP_NAME = "app_name=";

	public static final String ARGUMENT_INFO = "info=";

	public static final String BUNDLE_MANAGER_CPID = "CP_ID";

	public static final String LOCALKINGFUN_COUNINFO_ACTIVITY = "com.kingwaytek.localkingfun.CouponInfoActivity";
	public static final String GOOGLE_PLAY_HTTPS = "https://play.google.com/store/apps/details?id=";

	public static final String GOOGLE_PLAY_MARKET = "market://details?id=";

	public static final String ACTION_CALLER_LOCALKINGFUN = "LOCALKINGFUN_CALLER";

	public static final int LOCALKINGFUN_VERSION_CODE = 31;

	public static void goToCoupon(Activity activity, String cpid) throws ApiException, NotInstalledException, VersionNotSupportException {

		if (!InstallationAppChecker.hasAppInstalled(activity, PackageName.LocalKingFun.LOCALKING_FUN_SETS)) {
			throw new NotInstalledException("Not installed localkingfun");
		}

		if (!InstallationAppChecker.checkVersionCodeLocalKingFun(activity, LocalKingFunCaller.LOCALKINGFUN_VERSION_CODE)) {
			throw new VersionNotSupportException("Version is too low to execute go to coupon.");
		}

		// try {
		// if (cpid != null && cpid.length() > 0) {
		// Intent broadcast = new Intent();
		// Bundle bundle = new Bundle();
		// bundle.putString(BUNDLE_MANAGER_CPID, cpid);
		// broadcast.putExtras(bundle);
		// broadcast.setAction(ACTION_CALLER_LOCALKINGFUN);
		// activity.sendBroadcast(broadcast);
		// }
		// } catch (ActivityNotFoundException e) {
		// e.printStackTrace();
		// throw new ApiException(ApiException.ARGUMENT_NOT_EXIST);
		// }

		String scheme = createCouponPageScheme(LOCALKINGFUN_COUPON_FUNCTION_NAME, cpid);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			intent.setData(Uri.parse(scheme));
			activity.startActivity(intent);
		} catch (ActivityNotFoundException anfe) {
			anfe.printStackTrace();
			throw new ApiException(ApiException.ARGUMENT_NOT_EXIST);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
	}

	public static void goToGooglePlay(Activity activity, int string_id, String packageName) {
		// 未安裝
		Toast.makeText(activity, activity.getString(string_id), Toast.LENGTH_LONG).show();
		Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_HTTPS + packageName));
		web.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(web);
	}

	public static void goToPoiInfo(Activity activity, String poiId) throws ApiException, NotInstalledException, VersionNotSupportException {

		if (!InstallationAppChecker.hasAppInstalled(activity, PackageName.LocalKingFun.LOCALKING_FUN_SETS)) {
			throw new NotInstalledException("Not installed localkingfun");
		}

		if (!InstallationAppChecker.checkVersionCodeLocalKingFun(activity, LocalKingFunCaller.LOCALKINGFUN_VERSION_CODE)) {
			throw new VersionNotSupportException("Version is too low to execute go to poiInfo.");
		}
		String scheme = createPoiPageScheme(LOCALKINGFUN_POI_INFO_FUNCTION_NAME, poiId);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			intent.setData(Uri.parse(scheme));
			activity.startActivity(intent);
		} catch (ActivityNotFoundException anfe) {
			anfe.printStackTrace();

		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}

	}

	// public static final String LOCALKINGFUN_POI_ADD_FUNCTION_NAME =
	// "poi_add";
	/**
	 * 前往樂客玩樂的POI新增頁
	 * 
	 * @param activity
	 * @param poiId
	 * @throws ApiException
	 * @throws NotInstalledException
	 * @throws VersionNotSupportException
	 */
	public static void goToPoiAdd(Activity activity, String poiId) throws ApiException, NotInstalledException, VersionNotSupportException {

		if (!InstallationAppChecker.hasAppInstalled(activity, PackageName.LocalKingFun.LOCALKING_FUN_SETS)) {
			throw new NotInstalledException("Not installed localkingfun");
		}

		if (!InstallationAppChecker.checkVersionCodeLocalKingFun(activity, LocalKingFunCaller.LOCALKINGFUN_VERSION_CODE)) {
			throw new VersionNotSupportException("Version is too low to execute go to poi add.");
		}
		String scheme = createPoiPageScheme(LOCALKINGFUN_POI_ADD_FUNCTION_NAME, poiId);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			intent.setData(Uri.parse(scheme));
			activity.startActivity(intent);
		} catch (ActivityNotFoundException anfe) {
			anfe.printStackTrace();

		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
	}

	// public static final String LOCALKINGFUN_POI_REVIEW_FUNCTION_NAME =
	// "poi_review";
	/**
	 * 前往樂客玩樂的POI評論頁
	 * 
	 * @param activity
	 * @param poiId
	 * @throws ApiException
	 * @throws NotInstalledException
	 * @throws VersionNotSupportException
	 */
	public static void goToPoiReview(Activity activity, String poiId) throws ApiException, NotInstalledException, VersionNotSupportException {

		if (!InstallationAppChecker.hasAppInstalled(activity, PackageName.LocalKingFun.LOCALKING_FUN_SETS)) {
			throw new NotInstalledException("Not installed localkingfun");
		}

		if (!InstallationAppChecker.checkVersionCodeLocalKingFun(activity, LocalKingFunCaller.LOCALKINGFUN_VERSION_CODE)) {
			throw new VersionNotSupportException("Version is too low to execute go to poi add.");
		}
		String scheme = createPoiPageScheme(LOCALKINGFUN_POI_REVIEW_FUNCTION_NAME, poiId);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			intent.setData(Uri.parse(scheme));
			activity.startActivity(intent);
		} catch (ActivityNotFoundException anfe) {
			anfe.printStackTrace();

		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
	}

	public static void goToPoiErrorReport(Activity activity, String poiId) throws ApiException, NotInstalledException, VersionNotSupportException {
		goToPoiErrorReport(activity, poiId, "", "");
	}

	public static void goToPoiErrorReport(Activity activity, String poiId, String appName, String info) throws ApiException, NotInstalledException, VersionNotSupportException {

		if (!InstallationAppChecker.hasAppInstalled(activity, PackageName.LocalKingFun.LOCALKING_FUN_SETS)) {
			throw new NotInstalledException("Not installed localkingfun");
		}

		if (!InstallationAppChecker.checkVersionCodeLocalKingFun(activity, LocalKingFunCaller.LOCALKINGFUN_VERSION_CODE)) {
			throw new VersionNotSupportException("Version is too low to execute go to poi add.");
		}
		String scheme = createErrorReportScheme(LOCALKINGFUN_POI_ERROR_REPORT_FUNCTION_NAME, poiId, appName, info);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		try {
			intent.setData(Uri.parse(scheme));
			activity.startActivity(intent);
		} catch (ActivityNotFoundException anfe) {
			anfe.printStackTrace();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
	}

	public static String createErrorReportScheme(String functionName, String poiId, String appName, String info) throws ApiException {
		String scheme = "";
		if (poiId != null && poiId.length() > 0) {
			scheme = LOCALKINGFUN_SCHEME + "://" + functionName;

			if (poiId == null || poiId.length() == 0 || poiId.equals("null")) {
				throw new ApiException(ApiException.CREATE_SCHEME_POIID_CAN_BE_NULL);
			}
			scheme = scheme + "?" + ARGUMENT_POIID + poiId;
			// TODO 2015.11.24
			// 2.1.110版以前的玩樂無法處理多個參數, 先將導航王此參數先不要帶入,等玩樂支援多個參數功能上架後1個月再將此功能上線
			if (appName != null && appName.length() > 0 &&
			!appName.equals("null")) {
			scheme = scheme + "?" + ARGUMENT_APP_NAME + appName;
			
			}
			if (info != null && info.length() > 0 && !info.equals("null")) {
			scheme = scheme + "?" + ARGUMENT_INFO + info;
			}
		}
		return scheme;
	}

	public static String createCouponPageScheme(String functionName, String cpId) throws ApiException {
		String scheme = "";
		if (cpId != null && cpId.length() > 0) {
			scheme = LOCALKINGFUN_SCHEME + "://" + functionName;

			if (cpId == null || cpId.length() == 0 || cpId.equals("null")) {
				throw new ApiException(ApiException.CREATE_SCHEME_CPID_CAN_BE_NULL);
			}
			scheme = scheme + "?" + ARGUMENT_CPID + cpId;
		}
		return scheme;
	}

	public static String createPoiPageScheme(String functionName, String poiId) throws ApiException {
		String scheme = "";
		if (poiId != null && poiId.length() > 0) {
			scheme = LOCALKINGFUN_SCHEME + "://" + functionName;
			if (poiId == null || poiId.length() == 0 || poiId.equals("null")) {
				throw new ApiException(ApiException.CREATE_SCHEME_POIID_CAN_BE_NULL);
			}
			scheme = scheme + "?" + ARGUMENT_POIID + poiId;
		}
		return scheme;
	}

	public static String parseSchemePoiId(String functionName, String schemeUrl) throws ApiException {
		String poiId = "";
		try {
			// poiId = getArguments(functionName, schemeUrl, ARGUMENT_POIID);
			// 改寫取Arguments,原本只能判斷一組參數
			poiId = getArgumentsMore(functionName, schemeUrl, ARGUMENT_POIID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return poiId;
	}

	public static String parseSchemeAppName(String functionName, String schemeUrl) throws ApiException {
		String appName = "";
		try {
			appName = getArgumentsMore(functionName, schemeUrl, ARGUMENT_APP_NAME);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return appName;
	}

	public static String parseSchemeInfo(String functionName, String schemeUrl) throws ApiException {
		String info = "";
		try {
			info = getArgumentsMore(functionName, schemeUrl, ARGUMENT_INFO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	public static String parseSchemeCpId(String functionName, String schemeUrl) throws ApiException {
		String cpId = "";
		try {
			cpId = getArguments(functionName, schemeUrl, ARGUMENT_CPID);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return cpId;
	}

	public static String getArguments(String functionName, String schemeUrl, String argument) throws ApiException {

		if (schemeUrl == null || schemeUrl.length() == 0) {
			throw new ApiException(ApiException.URL_CANT_BE_NULL_OR_EMPTY);
		}

		schemeUrl = schemeUrl.trim();

		if (!schemeUrl.contains(LOCALKINGFUN_SCHEME) || !schemeUrl.contains(functionName)) {
			throw new ApiException(ApiException.URL_DONESNT_CONTAIN_POIINFO_OR_FUNCTIONNAME_FORMAT);
		}

		if (!schemeUrl.contains(argument)) {
			if (ARGUMENT_POIID.contains(argument))
				throw new ApiException(ApiException.URL_DONESNT_CONTAIN_POIID_ARGUMENT);
			if (ARGUMENT_CPID.contains(argument))
				throw new ApiException(ApiException.URL_DONESNT_CONTAIN_CPID_ARGUMENT);
		}

		if (!schemeUrl.contains("?")) {
			throw new ApiException(ApiException.URL_DONESNT_CONTAIN_SIGN);
		}
		String argumentStr = schemeUrl.replace(LOCALKINGFUN_SCHEME + "://" + functionName + "?" + argument, "");
		return argumentStr;
	}

	public static String getArgumentsMore(String functionName, String schemeUrl, String argument) throws ApiException {

		if (schemeUrl == null || schemeUrl.length() == 0) {
			throw new ApiException(ApiException.URL_CANT_BE_NULL_OR_EMPTY);
		}

		schemeUrl = schemeUrl.trim();

		if (!schemeUrl.contains(LOCALKINGFUN_SCHEME) || !schemeUrl.contains(functionName)) {
			throw new ApiException(ApiException.URL_DONESNT_CONTAIN_POIINFO_OR_FUNCTIONNAME_FORMAT);
		}

		if (!schemeUrl.contains("?")) {
			throw new ApiException(ApiException.URL_DONESNT_CONTAIN_SIGN);
		}
		int indexStart = schemeUrl.indexOf("?" + argument);
		if (indexStart > 0) {
			String temp = schemeUrl.substring(indexStart, schemeUrl.length());
			temp = temp.replace("?" + argument, "");
			int indexEnd = temp.indexOf("?");
			if (indexEnd > 0) {
				return temp.substring(0, indexEnd);
			} else {
				return temp;
			}
		} else {
			return "";
		}

	}

}
