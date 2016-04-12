package com.kingwaytek.api.ad;

import com.kingwaytek.api.model.PrivacyAndTermsData;

public interface PrivacyAndTermsCallBack {

	/**
	 * 1.在背景回傳使用者同意條款,App前端前往原本對應流程
	 * 2.註冊流程只會回傳同意服務條款更新,前端需要記下PrivacyAndTermsData,等拿到會員GetPass重送一次同意調款
	 */
	public void onResultAgree(PrivacyAndTermsData privacyAndTermsData);

	/**
	 * 第一次註冊使用者不願意同意條款,直接檔下後續登入流程,若非第一次登入則不需要指定後續動作
	 */
	public void onResultClose();

	/**
	 * 拒絕同意條款,App前端清除用戶資料並登出
	 */
	public void onResultReject();

	/**
	 * 向後端詢問條款已經同意過最新版本不須跳出訊問視窗
	 */
	public void onResultByPass();

	/**
	 * 回傳資訊失敗
	 * 
	 * @param result
	 *            server回傳的結果
	 */
	public void onResultFail(String errorMsg);

}
