package com.kingwaytek.api.ad;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kingwaytek.api.ad.web.WebAgent;
import com.kingwaytek.api.model.GetMsgResult;
import com.kingwaytek.api.model.RequestPushClickLog;
import com.kingwaytek.api.model.RequestTokden;
import com.kingwaytek.api.utility.UtilityApi;
import com.kingwaytek.api.web.WebAgentCallback;
import com.kingwaytek.api.web.WebAsyncTask;
import com.kingwaytek.api.web.WebErrorCode;
import com.kingwaytek.api.web.WebResultCallback;

import java.io.IOException;

/*
 * GoogleCloudMessaging
 */

public class GcmManager {
	static final String TAG = "GcmManager";

	static String regid = null;

	/**
	 * 註冊 Google GCM, SenderId 可用PackageName自動判斷
	 * 
	 * @param context
	 */
	public static void register(Context context) {
		// TODO 可以存手機的SenderId下來
		register(context, SenderId.getId(context));
	}

	/**
	 * 註冊 Google GCM, 需要帶入 SenderId
	 * 
	 * @param context
	 */
	public static void register(Context context, String senderId) {
		boolean bCheckGcmRegisterId = GcmApi.checkGcmRegistrationId(context);
		if (bCheckGcmRegisterId) {
			gcmRegistrationTask(context, senderId);
		}
	}

	static GoogleCloudMessaging mGCM;

	/**
	 * 將使用者的ID回傳後端記錄
	 * 
	 * @param context
	 * @param senderId
	 */
	public static void gcmRegistrationTask(final Context context, final String senderId) {
		if (UtilityApi.isNetworkWorking(context)) {
			new WebAsyncTask<GetMsgResult>(context, new WebAgentCallback<GetMsgResult>() {
				@Override
				public GetMsgResult getWebAgentResult() {
					RequestTokden requestTokden = null;
					try {
						if (mGCM == null) {
							mGCM = GoogleCloudMessaging.getInstance(context);
						}
						// 向Google要該裝置的RegisterId
						regid = mGCM.register(senderId);
						if (UtilityApi.checkStringNotEmpty(regid)) {
							String encodeStr = GcmApi.encodeStrJson(context, regid, false);
							requestTokden = new RequestTokden(encodeStr);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					return WebAgent.sendToken(context, requestTokden);

				}
			}).setWebResultCallback(new WebResultCallback<GetMsgResult>() {

				@Override
				public void onResultSucceed(GetMsgResult result) {
					resultHandler(result);
				}

				private void resultHandler(GetMsgResult result) {
					switch (result.getResultCode()) {
					case WebErrorCode.VERIFY_SUCCESS:
						AdDebugHelper.debugLog(TAG, "gcmRegistrationTask Success");
						GcmApi.setGcmRegistrationId(context, regid);
						break;
					default:
						AdDebugHelper.debugLog(TAG, "gcmRegistrationTask Error");
						break;
					}
				}

				@Override
				public void onResultFail(GetMsgResult result) {

				}

				@Override
				public void onResultFinal() {

				}
			}).notShowProgressDialog().notShowNetworkDialog().execute();
		}
	}

	/**
	 * 送出點擊推播的Log
	 * 
	 * @param context
	 */
	public static void sendPushClickLogTask(final Context context, final int pushId, final double lat, final double lon, final String memberId) {
		if (UtilityApi.isNetworkWorking(context)) {
			new WebAsyncTask<GetMsgResult>(context, new WebAgentCallback<GetMsgResult>() {
				@Override
				public GetMsgResult getWebAgentResult() {
					RequestPushClickLog requestPushClickLog = null;
					String encodeStr = GcmApi.getEncodePushIdClickAndExposureLogStrJson(context, pushId, lat, lon, memberId);
					requestPushClickLog = new RequestPushClickLog(encodeStr);
					return WebAgent.sendPushClickLogTask(context, requestPushClickLog);

				}
			}).setWebResultCallback(new WebResultCallback<GetMsgResult>() {

				@Override
				public void onResultSucceed(GetMsgResult result) {
					resultHandler(result);
				}

				private void resultHandler(GetMsgResult result) {
					switch (result.getResultCode()) {
					case WebErrorCode.VERIFY_SUCCESS:
						AdDebugHelper.debugLog(TAG, "SendPushClickLogTask Success");
						break;
					default:
						AdDebugHelper.debugLog(TAG, "SendPushClickLogTask Error");
						break;
					}
				}

				@Override
				public void onResultFail(GetMsgResult result) {

				}

				@Override
				public void onResultFinal() {

				}
			}).notShowProgressDialog().notShowNetworkDialog().execute();
		}

	}

	/**
	 * 送出收到推播的Log
	 * 
	 * @param context
	 */
	public static void sendPushReceiveLogTask(final Context context, final int pushId, final double lat, final double lon, final String memberId) {
		if (UtilityApi.isNetworkWorking(context)) {
			new WebAsyncTask<GetMsgResult>(context, new WebAgentCallback<GetMsgResult>() {
				@Override
				public GetMsgResult getWebAgentResult() {
					RequestPushClickLog requestPushClickLog = null;
					String encodeStr = GcmApi.getEncodePushIdClickAndExposureLogStrJson(context, pushId, lat, lon, memberId);
					requestPushClickLog = new RequestPushClickLog(encodeStr);
					return WebAgent.sendPushReceiveLogTask(context, requestPushClickLog);

				}
			}).setWebResultCallback(new WebResultCallback<GetMsgResult>() {

				@Override
				public void onResultSucceed(GetMsgResult result) {
					resultHandler(result);
				}

				private void resultHandler(GetMsgResult result) {
					switch (result.getResultCode()) {
					case WebErrorCode.VERIFY_SUCCESS:
						AdDebugHelper.debugLog(TAG, "sendPushReceiveLogTask Success");
						break;
					default:
						AdDebugHelper.debugLog(TAG, "sendPushReceiveLogTask Error");
						break;
					}
				}

				@Override
				public void onResultFail(GetMsgResult result) {

				}

				@Override
				public void onResultFinal() {

				}
			}).notShowProgressDialog().notShowNetworkDialog().execute();
		}

	}
}
