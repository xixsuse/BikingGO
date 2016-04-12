package com.kingwaytek.api.caller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.kingwaytek.api.exception.ApiException;
import com.kingwaytek.api.exception.VersionNotSupportException;
import com.kingwaytek.api.model.CallerData;

public class NaviKing3dCaller {

	public static final String NAVIKING3D_SCHEME = "kwnaviking3d";
	public static final String NAVIKING3D_NAVI_FUNCTION_NAME = "navigation";
	public static final String ARGUMENT_LAT = "lat=";
	public static final String ARGUMENT_LON = "lon=";
	public static final String ARGUMENT_ROAD_ID = "road_id=";
	public static final String ARGUMENT_TARGET_NAME = "target_name=";
	public static final String ARGUMENT_ADDRESS = "address=";
	public static final int SUPPORT_VERSION = 514;

	public static void navigationTo(Activity activity, CallerData naviking3d) throws ApiException, VersionNotSupportException {
		// 增加支援N5的狀況
		if (checkVersionNotSupport(activity) && NaviKingN3Caller.checkVersionNotSupport(activity)) {
			throw new VersionNotSupportException("Version is too low to execute navigation.");
		}

		String scheme = createScheme(naviking3d);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(scheme));
		activity.startActivity(intent);
	}

	static boolean checkVersionNotSupport(Activity activity) {
		return !InstallationAppChecker.checkVersionCodeLocking3D(activity, SUPPORT_VERSION);
	}
		
	public static String createScheme(CallerData naviking3d) throws ApiException{
		StringBuilder schemeBuilder = new StringBuilder();
		if(naviking3d != null){
						
			if(naviking3d.getAddress() == null){
				boolean latLonIsZero = naviking3d.getLat() <= 0.0f || naviking3d.getLon() <= 0.0f ;			
				if(latLonIsZero){
					throw new ApiException(ApiException.CREATE_SCHEME_LAT_OR_LON_CAN_BE_ZERO);
				}
			}
			
			schemeBuilder.append(NAVIKING3D_SCHEME);
			schemeBuilder.append("://");
			schemeBuilder.append(NAVIKING3D_NAVI_FUNCTION_NAME);
			schemeBuilder.append("?");
			schemeBuilder.append(ARGUMENT_LAT);
			schemeBuilder.append(naviking3d.getLat());
			schemeBuilder.append("&");
			schemeBuilder.append(ARGUMENT_LON);
			schemeBuilder.append(naviking3d.getLon());
			if (naviking3d.getRoadId() > 0) {
				schemeBuilder.append("&");
				schemeBuilder.append(ARGUMENT_ROAD_ID);
				schemeBuilder.append(naviking3d.getRoadId());
			}
			if (naviking3d.getTargetName() != null) {
				schemeBuilder.append("&");
				schemeBuilder.append(ARGUMENT_TARGET_NAME);
				schemeBuilder.append(naviking3d.getTargetName());
			}
			if (naviking3d.getAddress() != null) {
				schemeBuilder.append("&");
				schemeBuilder.append(ARGUMENT_ADDRESS);
				schemeBuilder.append(naviking3d.getAddress());
			}
		}
		return schemeBuilder.toString();
	}

	// 111台灣台北市士林區, 天母游泳池
	public static CallerData parseSchemeUrl(String schemeUrl) throws ApiException {
		String[] arguments = getArguments(schemeUrl);
		String targetName = "";
		String address = null ;
		double lat = 0.0f;
		double lon = 0.0f;
		int roadId = 0;
		
		try {
			lat = Double.parseDouble(getArgumentByTag(arguments, ARGUMENT_LAT));
			lon = Double.parseDouble(getArgumentByTag(arguments, ARGUMENT_LON));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ApiException(
					ApiException.URL_LAT_LON_FORMAT_CANT_BE_PARSED);
		}

		try {
			roadId = Integer.parseInt(getArgumentByTag(arguments,ARGUMENT_ROAD_ID));
			targetName = getArgumentByTag(arguments, ARGUMENT_TARGET_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			address = getArgumentByTag(arguments, ARGUMENT_ADDRESS);
		} catch (ApiException e) {
			e.printStackTrace();
		}

		CallerData naviking3d = null;		
		if(address == null){
			naviking3d = new CallerData(lat, lon, roadId, targetName);	
		}else{
			naviking3d = new CallerData(address, targetName);
		}
		return naviking3d;
	}

	public static String getArgumentByTag(String[] arguments, String tag)
			throws ApiException {
		String argument = null;
		for (String _argument : arguments) {
			if (_argument.contains(tag)) {
				argument = _argument.replace(tag, "");
			}
		}

		if (argument == null) {
			throw new ApiException(ApiException.ARGUMENT_NOT_EXIST + ",tag="
					+ tag);
		}
		return argument;
	}

	public static String[] getArguments(String schemeUrl) throws ApiException {
		if (schemeUrl == null || schemeUrl.length() == 0) {
			throw new ApiException(ApiException.URL_CANT_BE_NULL_OR_EMPTY);
		}

		schemeUrl = schemeUrl.trim();

		if (!schemeUrl.contains(NAVIKING3D_NAVI_FUNCTION_NAME)
				|| !schemeUrl.contains(NAVIKING3D_SCHEME)) {
			throw new ApiException(
					ApiException.URL_DONESNT_CONTAIN_NAVIGATION_OR_FUNCTIONNAME_FORMAT);
		}
		
//		if (!schemeUrl.contains(ARGUMENT_LAT)) {
//			throw new ApiException(
//					ApiException.URL_DONESNT_CONTAIN_LAT_ARGUMENT);
//		}
//
//		if (!schemeUrl.contains(ARGUMENT_LON)) {
//			throw new ApiException(
//					ApiException.URL_DONESNT_CONTAIN_LON_ARGUMENT);
//		}

		if (!schemeUrl.contains("?") || !schemeUrl.contains("&")) {
			throw new ApiException(ApiException.URL_DONESNT_CONTAIN_SIGN);
		}

		String argument = schemeUrl.replace(NAVIKING3D_SCHEME + "://"
				+ NAVIKING3D_NAVI_FUNCTION_NAME + "?", "");
		String[] arguments = argument.split("&");
		return arguments;
	}	
}