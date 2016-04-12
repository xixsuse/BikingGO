package com.kingwaytek.api.ad;

import android.content.Context;
import android.content.SharedPreferences;

import com.kingwaytek.api.ad.web.WebAgent;
import com.kingwaytek.api.model.GetPrivacyAndTermsAgreeResult;
import com.kingwaytek.api.model.GetPrivacyAndTermsResult;
import com.kingwaytek.api.model.PackageName;
import com.kingwaytek.api.model.PrivacyAndTermsData;
import com.kingwaytek.api.model.PrivacyAndTermsRequestAgree;
import com.kingwaytek.api.model.PrivacyAndTermsRequestInfo;
import com.kingwaytek.api.utility.UtilityApi;
import com.kingwaytek.api.web.WebAgentCallback;
import com.kingwaytek.api.web.WebAsyncTask;
import com.kingwaytek.api.web.WebErrorCode;
import com.kingwaytek.api.web.WebResultCallback;

public final class PrivacyAndTermsApi {

	static final String TAG = "PrivacyAndTermsApi";

	public static final String PREFS_NAME_COMMON_SETTINGS = "privacy_and_terms_common_settings";
	public static final String PREF_NEED_SEED_AGREE = "need_seed_agree";
	public static final String PREF_NEED_AGREE = "need_Agree";
	public static final String PREF_PRIVACY_VERSION = "privacy_version";
	public static final String PREF_PRIVACY_LINK = "privacy_link";
	public static final String PREF_TERMS_VERSION = "terms_version";
	public static final String PREF_TERMS_LINK = "terms_link";

	public static final int NAVIKING3D = 1;
	public static final int LOCALKINGFUN = 3;
	public static final int LOCALKINGREMINME = 4;
	public static final int LOCALKINGWEB = 5;
	public static final int BUBUTRIP = 6;
	public static final int NAVIKINGN5 = 7;
	public static final int LOCALKINGRIDER = 8;

	public static final String PRIVACY_LINK = "http://www.localking.com.tw/about/privacy.aspx?app=1";
	public static final String TERMS_LINK = "http://www.localking.com.tw/about/service.aspx?app=1";

	// 只跳出同意條款,第一次註冊的使用者
	public static final int NON_MEMBER_TYPE = 1;// 非樂客會員,只要取得服務條款URL,不用跳出視窗
	public static final int MEMBER_TYPE = NON_MEMBER_TYPE + 1;// 樂客會員,檢查有無更新條款,跳出視窗
	public static final int REGISTER_TYPE = MEMBER_TYPE + 1;// 非會員並第一次註冊的使用者

	/**
	 * 取得隱私條款的URL
	 * 
	 * @param context
	 * @return
	 */
	public static String getPrivacyLink(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_COMMON_SETTINGS, Context.MODE_PRIVATE);
		String privacyLink = pref.getString(PREF_PRIVACY_LINK, "");

		if (UtilityApi.checkStringNotEmpty(privacyLink)) {
			return privacyLink;
		} else {
			return PRIVACY_LINK;
		}

	}

	/**
	 * 取得服務條款的URL
	 * 
	 * @param context
	 * @return
	 */
	public static String getTermsLink(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_COMMON_SETTINGS, Context.MODE_PRIVATE);
		String termsLink = pref.getString(PREF_TERMS_LINK, "");

		if (UtilityApi.checkStringNotEmpty(termsLink)) {
			return termsLink;
		} else {
			return TERMS_LINK;
		}

	}

	public static PrivacyAndTermsData getPrivacyAndTermsData(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_COMMON_SETTINGS, Context.MODE_PRIVATE);
		boolean needSeedAgree = pref.getBoolean(PREF_NEED_SEED_AGREE, false);
		boolean needAgree = pref.getBoolean(PREF_NEED_AGREE, false);
		String privacyVersion = pref.getString(PREF_PRIVACY_VERSION, "");
		String privacyLink = pref.getString(PREF_PRIVACY_LINK, "");
		String termsVersion = pref.getString(PREF_TERMS_VERSION, "");
		String termsLink = pref.getString(PREF_TERMS_LINK, "");
		PrivacyAndTermsData privacyAndTermsData = new PrivacyAndTermsData(needSeedAgree, needAgree, privacyVersion, privacyLink, termsVersion, termsLink);
		return privacyAndTermsData;
	}

	public static void clearPrivacyAndTermsData(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_COMMON_SETTINGS, Context.MODE_PRIVATE);
		pref.edit().putBoolean(PREF_NEED_SEED_AGREE, false).commit();
		pref.edit().putBoolean(PREF_NEED_AGREE, false).commit();
	}

	public static void setPrivacyAndTermsData(Context context, PrivacyAndTermsData privacyAndTermsData) {
		SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_COMMON_SETTINGS, Context.MODE_PRIVATE);
		pref.edit().putBoolean(PREF_NEED_SEED_AGREE, privacyAndTermsData.getNeedSeedAgree()).commit();
		pref.edit().putBoolean(PREF_NEED_AGREE, privacyAndTermsData.getNeedAgree()).commit();
		pref.edit().putString(PREF_PRIVACY_VERSION, privacyAndTermsData.getPrivacyVersion()).commit();
		pref.edit().putString(PREF_PRIVACY_LINK, privacyAndTermsData.getPrivacyLink()).commit();
		pref.edit().putString(PREF_TERMS_VERSION, privacyAndTermsData.getTermsVersion()).commit();
		pref.edit().putString(PREF_TERMS_LINK, privacyAndTermsData.getTermsLink()).commit();
	}

	/**
	 * 檢查服務條款是否有更新
	 * 
	 * @param context
	 * @param passCode
	 * @param isMember
	 * @param logType
	 * @param callback
	 */
	public static void checkPrivacyAndTermsTask(final Context context, final String passCode, final int type, final int logType, final PrivacyAndTermsCallBack callback) {
		if (UtilityApi.checkStringNotEmpty(passCode)) {
			if (type == MEMBER_TYPE) {
				// 是樂客會員
				new WebAsyncTask<GetPrivacyAndTermsResult>(context, new WebAgentCallback<GetPrivacyAndTermsResult>() {
					@Override
					public GetPrivacyAndTermsResult getWebAgentResult() {
						PrivacyAndTermsRequestInfo privacyAndTermsRequestInfo = null;
						privacyAndTermsRequestInfo = new PrivacyAndTermsRequestInfo(passCode, logType);
						return WebAgent.checkPrivacyAndTermsTask(context, privacyAndTermsRequestInfo);

					}
				}).setWebResultCallback(new WebResultCallback<GetPrivacyAndTermsResult>() {

					@Override
					public void onResultSucceed(GetPrivacyAndTermsResult result) {
						resultHandler(result);
					}

					private void resultHandler(GetPrivacyAndTermsResult result) {
						switch (result.getResultCode()) {
						case WebErrorCode.VERIFY_SUCCESS:
							if (result != null && result.getPrivacyAndTermsData() != null) {
								if (result.getPrivacyAndTermsData().needAgree) {
									new PrivacyAndTermsDialog(context, passCode, true, result.getPrivacyAndTermsData(), callback);
								} else {
									PrivacyAndTermsData privacyAndTermsData = result.getPrivacyAndTermsData();
									privacyAndTermsData.setNeedSeedAgree(false);
									PrivacyAndTermsApi.setPrivacyAndTermsData(context, privacyAndTermsData);
									callback.onResultByPass();
								}
							}
							AdDebugHelper.debugLog(TAG, "checkPrivacyAndTermsTask VERIFY SUCCESS");
							break;
						default:
							callback.onResultFail(result.getErrorMsg());
							break;
						}
					}

					@Override
					public void onResultFinal() {

					}

					@Override
					public void onResultFail(GetPrivacyAndTermsResult result) {
						// TODO Auto-generated method stub

					}
				}).notShowProgressDialog().notShowNetworkDialog().execute();
			} else if (type == REGISTER_TYPE) {
				// 只跳出同意條款,第一次註冊的使用者
				new WebAsyncTask<GetPrivacyAndTermsResult>(context, new WebAgentCallback<GetPrivacyAndTermsResult>() {
					@Override
					public GetPrivacyAndTermsResult getWebAgentResult() {
						PrivacyAndTermsRequestInfo privacyAndTermsRequestInfo = null;
						privacyAndTermsRequestInfo = new PrivacyAndTermsRequestInfo(passCode, logType);
						return WebAgent.checkPrivacyAndTermsTask(context, privacyAndTermsRequestInfo);

					}
				}).setWebResultCallback(new WebResultCallback<GetPrivacyAndTermsResult>() {

					@Override
					public void onResultSucceed(GetPrivacyAndTermsResult result) {
						resultHandler(result);
					}

					private void resultHandler(GetPrivacyAndTermsResult result) {
						switch (result.getResultCode()) {
						case WebErrorCode.VERIFY_SUCCESS:
							if (result != null && result.getPrivacyAndTermsData() != null) {
								// if (AdDebugHelper.ByPassAgreePrivacyAndTerms)
								// {
								// result.getPrivacyAndTermsData().needAgree =
								// true;
								// }
								if (result.getPrivacyAndTermsData().needAgree) {
									new PrivacyAndTermsDialog(context, passCode, false, result.getPrivacyAndTermsData(), callback);
								} else {
									// 已經同意過最新的服務條款,不需要跳出服務條款更新同意
									// 存PrivacyAndTermsData
									PrivacyAndTermsData privacyAndTermsData = result.getPrivacyAndTermsData();
									privacyAndTermsData.setNeedSeedAgree(false);
									PrivacyAndTermsApi.setPrivacyAndTermsData(context, privacyAndTermsData);

									callback.onResultByPass();
								}

							}
							AdDebugHelper.debugLog(TAG, "checkPrivacyAndTermsTask VERIFY SUCCESS");
							break;
						default:
							callback.onResultFail(result.getErrorMsg());
							break;
						}
					}

					@Override
					public void onResultFinal() {

					}

					@Override
					public void onResultFail(GetPrivacyAndTermsResult result) {
						callback.onResultFail(result.getErrorMsg());
					}
				}).execute();
			} else if (type == NON_MEMBER_TYPE) {
				// 非樂客會員,只要取服務條款URL
				new WebAsyncTask<GetPrivacyAndTermsResult>(context, new WebAgentCallback<GetPrivacyAndTermsResult>() {
					@Override
					public GetPrivacyAndTermsResult getWebAgentResult() {
						PrivacyAndTermsRequestInfo privacyAndTermsRequestInfo = null;
						privacyAndTermsRequestInfo = new PrivacyAndTermsRequestInfo(passCode, logType);
						return WebAgent.checkPrivacyAndTermsTask(context, privacyAndTermsRequestInfo);

					}
				}).setWebResultCallback(new WebResultCallback<GetPrivacyAndTermsResult>() {

					@Override
					public void onResultSucceed(GetPrivacyAndTermsResult result) {
						resultHandler(result);
					}

					private void resultHandler(GetPrivacyAndTermsResult result) {
						switch (result.getResultCode()) {
						case WebErrorCode.VERIFY_SUCCESS:
							if (result != null && result.getPrivacyAndTermsData() != null) {
								// 取得最新的服務條款,不需要跳出服務條款更新同意
								// 存PrivacyAndTermsData
								PrivacyAndTermsData privacyAndTermsData = result.getPrivacyAndTermsData();
								privacyAndTermsData.setNeedSeedAgree(false);
								PrivacyAndTermsApi.setPrivacyAndTermsData(context, privacyAndTermsData);
								callback.onResultByPass();
							}
							AdDebugHelper.debugLog(TAG, "checkPrivacyAndTermsTask VERIFY SUCCESS");
							break;
						default:
							callback.onResultFail(result.getErrorMsg());
							break;
						}
					}

					@Override
					public void onResultFinal() {
					}

					@Override
					public void onResultFail(GetPrivacyAndTermsResult result) {
						callback.onResultFail(result.getErrorMsg());
					}
				}).notShowProgressDialog().notShowNetworkDialog().execute();
			}
		} else {
			callback.onResultFail("passCode is null.");
		}

	}

	/**
	 * 內網清除全部人的同意條款記錄
	 * 
	 * @param context
	 */
	public static void clearAllPrivacyAndTermsTask(final Context context) {

		new WebAsyncTask<GetPrivacyAndTermsAgreeResult>(context, new WebAgentCallback<GetPrivacyAndTermsAgreeResult>() {
			@Override
			public GetPrivacyAndTermsAgreeResult getWebAgentResult() {
				PrivacyAndTermsRequestAgree privacyAndTermsRequestAgree = new PrivacyAndTermsRequestAgree("", 0, "", "", "");
				return WebAgent.clearAllPrivacyAndTermsTask(context, privacyAndTermsRequestAgree);

			}
		}).setWebResultCallback(new WebResultCallback<GetPrivacyAndTermsAgreeResult>() {

			@Override
			public void onResultSucceed(GetPrivacyAndTermsAgreeResult result) {
				AdDebugHelper.debugLog(TAG, "onResultSucceed");
				resultHandler(result);
			}

			private void resultHandler(GetPrivacyAndTermsAgreeResult result) {
				switch (result.getResultCode()) {
				case WebErrorCode.VERIFY_SUCCESS:
					if (AdDebugHelper.checkOpen()) {
						UtilityApi.showToast(context, "已清除同意條款記錄");
					}
					break;
				default:
					if (AdDebugHelper.checkOpen()) {
						UtilityApi.showToast(context, "失敗->清除同意條款記錄");
					}
					break;
				}
			}

			@Override
			public void onResultFinal() {

			}

			@Override
			public void onResultFail(GetPrivacyAndTermsAgreeResult result) {
				if (AdDebugHelper.checkOpen()) {
					UtilityApi.showToast(context, "失敗:清除同意條款記錄");
				}
			}
		}).execute();
	}

	/**
	 * 清除指定人的同意條款記錄
	 * 
	 * @param context
	 */
	public static void clearByIdPrivacyAndTermsTask(final Context context, final String passCode) {

		new WebAsyncTask<GetPrivacyAndTermsAgreeResult>(context, new WebAgentCallback<GetPrivacyAndTermsAgreeResult>() {
			@Override
			public GetPrivacyAndTermsAgreeResult getWebAgentResult() {
				PrivacyAndTermsRequestInfo privacyAndTermsRequestInfo = null;
				privacyAndTermsRequestInfo = new PrivacyAndTermsRequestInfo(passCode, getLogType(context));
				return WebAgent.clearByIdPrivacyAndTermsTask(context, privacyAndTermsRequestInfo);

			}
		}).setWebResultCallback(new WebResultCallback<GetPrivacyAndTermsAgreeResult>() {

			@Override
			public void onResultSucceed(GetPrivacyAndTermsAgreeResult result) {
				AdDebugHelper.debugLog(TAG, "onResultSucceed");
				resultHandler(result);
			}

			private void resultHandler(GetPrivacyAndTermsAgreeResult result) {
				switch (result.getResultCode()) {
				case WebErrorCode.VERIFY_SUCCESS:
					if (AdDebugHelper.checkOpen()) {
						UtilityApi.showToast(context, "已清除同意條款記錄");
					}
					break;
				default:
					if (AdDebugHelper.checkOpen()) {
						UtilityApi.showToast(context, "失敗->清除同意條款記錄");
					}
					break;
				}
			}

			@Override
			public void onResultFinal() {

			}

			@Override
			public void onResultFail(GetPrivacyAndTermsAgreeResult result) {
				if (AdDebugHelper.checkOpen()) {
					UtilityApi.showToast(context, "失敗:清除同意條款記錄");
				}
			}
		}).execute();
	}

	/**
	 * 檢查條款有無需要更新,並送出同意條款至後端
	 * 
	 * @param context
	 * @param passCode
	 * @param isMember
	 * @param callback
	 */
	public static void checkPrivacyAndTerms(Context context, String passCode, int type, PrivacyAndTermsCallBack callback) {
		checkPrivacyAndTerms(context, passCode, type, getLogType(context), callback);
	}

	/**
	 * 檢查條款有無需要更新
	 * 
	 * @param context
	 * @param passCode
	 * @param isMember
	 * @param logType
	 * @param callback
	 */
	public static void checkPrivacyAndTerms(Context context, String passCode, int type, int logType, PrivacyAndTermsCallBack callback) {
		if (logType == NAVIKING3D || logType == LOCALKINGFUN || logType == LOCALKINGREMINME || logType == NAVIKINGN5 || logType == LOCALKINGRIDER) {
			PrivacyAndTermsApi.checkPrivacyAndTermsTask(context, passCode, type, logType, callback);
		} else {
			AdDebugHelper.debugLog(TAG, "未定義的LogType");
		}
	}

	/**
	 * 取得登入的App對應的LogType(後端定義的)
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getLogType(Context context) {
		String packageName = UtilityApi.AppInfo.getAppPackageName(context);
		if (UtilityApi.checkStringNotEmpty(packageName)) {
			// 樂客玩樂
			for (String name : PackageName.LocalKingFun.LOCALKING_FUN_SETS) {
				if (name.equals(packageName)) {
					AdDebugHelper.debugLog(TAG, "樂客玩樂");
					return LOCALKINGFUN;
				}
			}
			// 樂客生活秘書
			if (PackageName.LOCALKING_LIFEMAN.equals(packageName)) {
				AdDebugHelper.debugLog(TAG, "樂客生活秘書");
				return LOCALKINGREMINME;
			}
			// 樂客夥計
			for (String name : PackageName.LocalKingRider.LOCALKING_RIDER_SETS) {
				if (name.equals(packageName)) {
					AdDebugHelper.debugLog(TAG, "樂客夥計");
					return LOCALKINGRIDER;
				}
			}
			// 樂客導航王全3D
			for (String name : PackageName.NaviKingCht3D.ALL_SETS) {
				if (name.equals(packageName)) {
					AdDebugHelper.debugLog(TAG, "樂客導航王全3D");
					return NAVIKING3D;
				}
			}
			// 樂客導航王N5
			for (String name : PackageName.NaviKingN3.ALL_SETS) {
				if (name.equals(packageName)) {
					AdDebugHelper.debugLog(TAG, "樂客導航王N5");
					return NAVIKINGN5;
				}
			}
			// 樂客轉乘通FREE
			// if (PackageName.GOTCHATRANSIT_FREE.equals(packageName)) {
			// GcmDebugHelper.debugLog(TAG, "樂客轉乘通FREE");
			// // return LOCALKINGTRANSIT_FREE;
			// }
			// // 樂客轉乘通三都版
			// if (PackageName.GOTCHATRANSIT_PRO.equals(packageName)) {
			// GcmDebugHelper.debugLog(TAG, "樂客轉乘通三都版");
			// // return LOCALKINGTRANSIT;
			// }
			// 樂客寶島
			// TODO 樂客寶島的PackageName要確認
			/*
			 * if (PackageName.LOCALKINGBAODAO.equals(packageName)) { return
			 * LOCALKINGBAODAO; }
			 */
		} else {
			AdDebugHelper.debugLog(TAG, "getAppPackageName error");
		}
		return -1;
	}

	/**
	 * 在背景後送同意服務條款
	 * 
	 * @param context
	 * @param passCode
	 * @param callback
	 */
	public static void agreePrivacyAndTerms(final Context context, final String passCode, final PrivacyAndTermsCallBack callback) {
		agreePrivacyAndTermsTask(context, passCode, callback);
	}

	/**
	 * 使用在註冊流程,取之前暫存同意的條款記錄背景後送同意服務條款
	 * 
	 * @param context
	 * @param passCode
	 * @param callback
	 */
	public static void agreePrivacyAndTermsTask(final Context context, final String passCode, final PrivacyAndTermsCallBack callback) {
		if (UtilityApi.checkStringNotEmpty(passCode)) {
			new WebAsyncTask<GetPrivacyAndTermsAgreeResult>(context, new WebAgentCallback<GetPrivacyAndTermsAgreeResult>() {
				@Override
				public GetPrivacyAndTermsAgreeResult getWebAgentResult() {

					PrivacyAndTermsData privacyAndTermsData = PrivacyAndTermsApi.getPrivacyAndTermsData(context);
					if (privacyAndTermsData != null && privacyAndTermsData.getNeedSeedAgree()) {
						int logType = getLogType(context);
						String privacyVersion = privacyAndTermsData.privacyVersion;
						String termsVersion = privacyAndTermsData.termsVersion;
						String appID = UtilityApi.AppInfo.getAppPackageName(context);
						PrivacyAndTermsRequestAgree privacyAndTermsRequestAgree = null;
						privacyAndTermsRequestAgree = new PrivacyAndTermsRequestAgree(passCode, logType, privacyVersion, termsVersion, appID);
						return WebAgent.agreePrivacyAndTermsTask(context, privacyAndTermsRequestAgree);
					} else {
						callback.onResultFail("PrivacyAndTermsData is null.");
						return null;
					}

				}
			}).setWebResultCallback(new WebResultCallback<GetPrivacyAndTermsAgreeResult>() {

				@Override
				public void onResultSucceed(GetPrivacyAndTermsAgreeResult result) {
					resultHandler(result);
				}

				private void resultHandler(GetPrivacyAndTermsAgreeResult result) {
					switch (result.getResultCode()) {
					case WebErrorCode.VERIFY_SUCCESS:
						if (AdDebugHelper.checkOpen()) {
							AdDebugHelper.debugToast(context, "同意條款更新-成功");
						}
						PrivacyAndTermsApi.clearPrivacyAndTermsData(context);
						callback.onResultByPass();
						break;
					default:
						callback.onResultFail("agreePrivacyAndTermsTask Error");
						break;
					}
				}

				@Override
				public void onResultFinal() {

				}

				@Override
				public void onResultFail(GetPrivacyAndTermsAgreeResult result) {
					callback.onResultFail("agreePrivacyAndTermsTask onResultFail");

				}
			}).notShowProgressDialog().notShowNetworkDialog().execute();
		} else {
			callback.onResultFail("passCode is null");
		}
	}

	public static void agreePrivacyAndTermsTask(final Context context, final String passCode, final PrivacyAndTermsData privacyAndTermsData, final PrivacyAndTermsCallBack callback) {

		new WebAsyncTask<GetPrivacyAndTermsAgreeResult>(context, new WebAgentCallback<GetPrivacyAndTermsAgreeResult>() {
			@Override
			public GetPrivacyAndTermsAgreeResult getWebAgentResult() {
				if (privacyAndTermsData != null) {
					int logType = getLogType(context);
					String privacyVersion = privacyAndTermsData.privacyVersion;
					String termsVersion = privacyAndTermsData.termsVersion;
					String appID = UtilityApi.AppInfo.getAppPackageName(context);
					AdDebugHelper.debugLog(TAG, "agreePrivacyAndTermsTask logType:" + logType);
					AdDebugHelper.debugLog(TAG, "agreePrivacyAndTermsTask privacyVersion:" + privacyVersion);
					AdDebugHelper.debugLog(TAG, "agreePrivacyAndTermsTask termsVersion:" + termsVersion);
					AdDebugHelper.debugLog(TAG, "agreePrivacyAndTermsTask appID:" + appID);
					PrivacyAndTermsRequestAgree privacyAndTermsRequestAgree = null;
					privacyAndTermsRequestAgree = new PrivacyAndTermsRequestAgree(passCode, logType, privacyVersion, termsVersion, appID);
					return WebAgent.agreePrivacyAndTermsTask(context, privacyAndTermsRequestAgree);
				} else {
					return null;
				}

			}
		}).setWebResultCallback(new WebResultCallback<GetPrivacyAndTermsAgreeResult>() {

			@Override
			public void onResultSucceed(GetPrivacyAndTermsAgreeResult result) {
				AdDebugHelper.debugLog(TAG, "onResultSucceed");
				if (result != null) {
					resultHandler(result);
				}
			}

			private void resultHandler(GetPrivacyAndTermsAgreeResult result) {

				switch (result.getResultCode()) {
				case WebErrorCode.VERIFY_SUCCESS:
					callback.onResultByPass();
					if (AdDebugHelper.checkOpen()) {
						AdDebugHelper.debugToast(context, "同意條款更新-成功");
					}
					break;
				default:
					callback.onResultFail("agreePrivacyAndTermsTask Error");
					AdDebugHelper.debugLog(TAG, "agreePrivacyAndTermsTask Error");
					break;
				}
			}

			@Override
			public void onResultFinal() {

			}

			@Override
			public void onResultFail(GetPrivacyAndTermsAgreeResult result) {
				callback.onResultFail("agreePrivacyAndTermsTask onResultFail");
			}
		}).notShowProgressDialog().notShowNetworkDialog().execute();
	}
}
