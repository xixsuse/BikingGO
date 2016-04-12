package com.kingwaytek.api.ad.web;

import android.content.Context;
import android.util.Log;

import com.kingwaytek.api.ad.AdDebugHelper;
import com.kingwaytek.api.model.GetMsgResult;
import com.kingwaytek.api.model.GetPrivacyAndTermsAgreeResult;
import com.kingwaytek.api.model.GetPrivacyAndTermsResult;
import com.kingwaytek.api.model.PrivacyAndTermsRequestAgree;
import com.kingwaytek.api.model.PrivacyAndTermsRequestInfo;
import com.kingwaytek.api.model.RequestPushClickLog;
import com.kingwaytek.api.model.RequestTokden;
import com.kingwaytek.api.model.WebPostImpl;

public class WebAgent {

	private static final String TAG = "WebAgent";

	public static GetMsgResult sendToken(Context context, RequestTokden requestTokden) {
		GetMsgResult getMsgResult = null;
		try {
			getMsgResult = new GetMsgResult(getRequestResultByAction(context, requestTokden, "sendToken"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getMsgResult;
	}

	/**
	 * 送出收到推播的Log
	 * 
	 * @param context
	 * @param requestTokden
	 * @return
	 */
	public static GetMsgResult sendPushReceiveLogTask(Context context, RequestPushClickLog requestTokden) {
		GetMsgResult getMsgResult = null;
		try {
			getMsgResult = new GetMsgResult(getRequestResultByAction(context, requestTokden, "PushReceiveLog"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getMsgResult;
	}

	/**
	 * 送出點擊推播的Log
	 * 
	 * @param context
	 * @param requestTokden
	 * @return
	 */
	public static GetMsgResult sendPushClickLogTask(Context context, RequestPushClickLog requestTokden) {
		GetMsgResult getMsgResult = null;
		try {
			getMsgResult = new GetMsgResult(getRequestResultByAction(context, requestTokden, "PushClickLog"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getMsgResult;
	}

	/**
	 * PrivacyAndTermsCheck確認會員是否需要同意最新隱私權及服務條款
	 * 
	 * @param context
	 * @param privacyAndTermsRequestInfo
	 * @return
	 */
	public static GetPrivacyAndTermsResult checkPrivacyAndTermsTask(Context context, PrivacyAndTermsRequestInfo privacyAndTermsRequestInfo) {
		GetPrivacyAndTermsResult getPrivacyAndTermsResult = null;
		try {
			getPrivacyAndTermsResult = new GetPrivacyAndTermsResult(getRequestResultByAction(context, privacyAndTermsRequestInfo, "PrivacyAndTermsCheck"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getPrivacyAndTermsResult;
	}

	/**
	 * PrivacyAndTermsAgree會員同意隱私權及服務條款
	 * 
	 * @param context
	 * @param privacyAndTermsRequestAgree
	 * @return
	 */
	public static GetPrivacyAndTermsAgreeResult agreePrivacyAndTermsTask(Context context, PrivacyAndTermsRequestAgree privacyAndTermsRequestAgree) {
		GetPrivacyAndTermsAgreeResult getPrivacyAndTermsAgreeResult = null;
		try {
			getPrivacyAndTermsAgreeResult = new GetPrivacyAndTermsAgreeResult(getRequestResultByAction(context, privacyAndTermsRequestAgree, "PrivacyAndTermsAgree"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getPrivacyAndTermsAgreeResult;
	}

	/**
	 * PrivacyAndTermsClearbyid
	 * 
	 * @param context
	 * @param privacyAndTermsRequestAgree
	 * @return
	 */
	public static GetPrivacyAndTermsAgreeResult clearByIdPrivacyAndTermsTask(Context context, PrivacyAndTermsRequestInfo privacyAndTermsRequestInfo) {
		GetPrivacyAndTermsAgreeResult getPrivacyAndTermsAgreeResult = null;
		try {
			getPrivacyAndTermsAgreeResult = new GetPrivacyAndTermsAgreeResult(getRequestResultByAction(context, privacyAndTermsRequestInfo, "PrivacyAndTermsClearbyid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getPrivacyAndTermsAgreeResult;
	}

	/**
	 * PrivacyAndTermsClearAll
	 * 
	 * @param context
	 * @param privacyAndTermsRequestAgree
	 * @return
	 */
	public static GetPrivacyAndTermsAgreeResult clearAllPrivacyAndTermsTask(Context context, PrivacyAndTermsRequestAgree privacyAndTermsRequestAgree) {
		GetPrivacyAndTermsAgreeResult getPrivacyAndTermsAgreeResult = null;
		try {
			getPrivacyAndTermsAgreeResult = new GetPrivacyAndTermsAgreeResult(getRequestResultByAction(context, privacyAndTermsRequestAgree, "PrivacyAndTermsClearAll"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getPrivacyAndTermsAgreeResult;
	}

	static String getRequestResultByAction(Context ctx, WebPostImpl webRequestImpl, String requestActionName) {
		webRequestImpl.setPassCode("");
		return getRequestResultWithoutCheckPassCode(ctx, webRequestImpl, requestActionName);
	}

	static String getRequestResultWithoutCheckPassCode(Context ctx, WebPostImpl webRequestImpl, String requestActionName) {
		WebItem item = getWebItem(requestActionName, webRequestImpl);
		String responseResult = WebService.getResponseByType(item);
		logServiceInfo(item.getRequestUrl(), item.getActionName(), webRequestImpl.getJSONResult(), responseResult);
		return responseResult;
	}

	static WebItem getWebItem(String requestActionName, WebPostImpl webRequestImpl) {
		String url = WebService.BASE_URL_JSON;
		if (AdDebugHelper.IS_USE_WIFI_TEST_SERVICE) {
			url = WebService.TEST_URL_JSON;
		}
		WebItem item = new WebItem(requestActionName, url);
		item.setPostData(webRequestImpl.getJSONResult());
		return item;
	}

	private static void logServiceInfo(String url, String actionName, String request, String response) {
		try {
			if (AdDebugHelper.checkOpen()) {
				Log.i(TAG, "API Url:\n" + (url));
				Log.i(TAG, "Request:\n" + request);
				Log.i(TAG, "Response:\n" + response);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}