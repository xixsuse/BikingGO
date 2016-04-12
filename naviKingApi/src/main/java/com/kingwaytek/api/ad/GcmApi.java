package com.kingwaytek.api.ad;

/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.json.JSONStringer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.kingwaytek.api.utility.UtilityApi;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class GcmApi {

	private static String regid;

	public static final String ENCODE_KEY = "yawgnik";
	public static final String PREFS_NAME_COMMON_SETTINGS = "gcm_common_settings";
	public static final String PREF_GCM_REG_ID = "reg_id";
	public static final String PREF_GCM_APP_VERSION = "app_version";
	public static final String PREF_GCM_ON_SERVER_EXPIRATION_TIME = "on_server_expiration_time";

	static private String KEY_DEVICEID = "DeviceID";
	static private String KEY_MEMBERID = "MemberID";
	static private String KEY_PUSHID = "PushID";
	static private String KEY_LOGTIME = "LogTime";
	static private String KEY_LAT = "Lat";
	static private String KEY_LON = "Lon";
	static private String KEY_APPID = "AppID";
	static private String KEY_VERSION = "Version";
	static private String KEY_DEVICENAME = "DeviceName";
	static private String KEY_SCREENSIZE = "ScreenSize";
	static private String KEY_PLATFORMTYPE = "PlatformType";

	static private String KEY_TOKEN = "token";
	static private String KEY_DEL = "Del";

	static final String TAG = "GcmApi";

	/**
	 * Default lifespan (30 days) of a reservation until it is considered
	 * expired.
	 */
	public static final long REGISTRATION_EXPIRY_TIME_M = 60 * 24 * 30; // 分鐘

	/**
	 * 加密
	 * 
	 * @param context
	 * @param token
	 * @param del
	 * @return
	 */
	static public String encodeStrJson(Context context, String token, Boolean del) {
		String result = null;
		try {
			String json = "";
			try {
				JSONStringer jsonText = new JSONStringer();
				jsonText.object();
				{
					jsonText.key(KEY_PLATFORMTYPE).value(2);
					jsonText.key(KEY_TOKEN).value(token);
					jsonText.key(KEY_DEVICEID).value(UtilityApi.getHardwareId(context));
					jsonText.key(KEY_APPID).value(UtilityApi.AppInfo.getAppPackageName(context));
					jsonText.key(KEY_DEL).value(del);
				}
				jsonText.endObject();
				json = jsonText.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String base64 = Base64.encodeToString(json.toString().getBytes(), Base64.DEFAULT);
			result = ENCODE_KEY + base64;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return result;
	}

	/**
	 * 若需要更新RegistratId回傳true
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean checkGcmRegistrationId(Context context) {
		if (UtilityApi.checkStringNotEmpty(GcmApi.getGcmRegistrationId(context))) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 取得暫存的RegistrationId
	 * 
	 * @param context
	 * @return
	 */
	public static String getGcmRegistrationId(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_COMMON_SETTINGS, Context.MODE_PRIVATE);
		String registrationId = pref.getString(PREF_GCM_REG_ID, "");
		if (!UtilityApi.checkStringNotEmpty(registrationId)) {
			return "";
		}
		int currentVersion = pref.getInt(PREF_GCM_APP_VERSION, Integer.MIN_VALUE);
		int appVersion = UtilityApi.AppInfo.getAppVersionCode(context);
		if (appVersion > currentVersion || isRegistrationExpired(context)) {
			return "";
		}
		return registrationId;
	}

	/**
	 * 
	 * @param context
	 * @param registration_id
	 * 
	 * @param registration_expiry_time_m
	 *            當前時間 /分鐘數
	 */
	public static void setGcmRegistrationId(Context context, String registration_id) {
		SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_COMMON_SETTINGS, Context.MODE_PRIVATE);
		pref.edit().putString(PREF_GCM_REG_ID, registration_id).commit();
		int appVersion = UtilityApi.AppInfo.getAppVersionCode(context);
		pref.edit().putInt(PREF_GCM_APP_VERSION, appVersion).commit();
		pref.edit().putLong(PREF_GCM_ON_SERVER_EXPIRATION_TIME, System.currentTimeMillis() / 60000).commit();
	}

	/**
	 * 取得上次註冊的時間,若回傳false則regid還在時效內
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isRegistrationExpired(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_COMMON_SETTINGS, Context.MODE_PRIVATE);
		long expirationTime = pref.getLong(PREF_GCM_ON_SERVER_EXPIRATION_TIME, 0);
		expirationTime = expirationTime + REGISTRATION_EXPIRY_TIME_M;
		return (System.currentTimeMillis() / 60000) > expirationTime ;
	}

	/**
	 * 加密送出推播LOG
	 * 
	 * @param context
	 * @param pushId
	 * @param lat
	 * @param lon
	 * @param memberId
	 * @return
	 */
	public static String getEncodePushIdClickAndExposureLogStrJson(Context context, int pushId, double lat, double lon, String memberId) {
		String result = null;
		try {
			String json = getPushClickAndExposureLogString(context, pushId, lat, lon, memberId);
			String base64 = Base64.encodeToString(json.toString().getBytes(), Base64.DEFAULT);
			result = ENCODE_KEY + base64;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return result;
	}

	// 產生Push點擊紀錄
	public static String getPushClickAndExposureLogString(Context context, int pushId, double lat, double lon, String memberId) {
		// 硬體裝置os版本
		String jsonString = "";
		try {
			JSONStringer jsonText = new JSONStringer();
			jsonText.object();
			jsonText.key(KEY_DEVICEID).value(UtilityApi.getHardwareId(context));
			jsonText.key(KEY_MEMBERID).value(memberId);
			jsonText.key(KEY_PUSHID).value(pushId);
			jsonText.key(KEY_LOGTIME).value(getLogTime());
			jsonText.key(KEY_LAT).value(lat);
			jsonText.key(KEY_LON).value(lon);
			// PackageName
			jsonText.key(KEY_APPID).value(UtilityApi.AppInfo.getAppPackageName(context));
			jsonText.key(KEY_VERSION).value(android.os.Build.VERSION.RELEASE);
			jsonText.key(KEY_DEVICENAME).value(android.os.Build.MODEL);
			// 螢幕解析度
			jsonText.key(KEY_SCREENSIZE).value(UtilityApi.Screen.getScreenSizeStr(context));
			jsonText.endObject();
			jsonString = jsonText.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	/**
	 * 取得現在時間
	 * 
	 * @return
	 */
	public static String getLogTime() {
		SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// ==GMT標準時間往後加八小時
		nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		// ==取得目前時間
		String sdate = nowdate.format(new java.util.Date());
		return sdate;
	}

}
