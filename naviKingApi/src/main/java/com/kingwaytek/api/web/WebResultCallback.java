package com.kingwaytek.api.web;

/**
 * 將ui以及資料處理的部份拉到app端實作
 */
public interface WebResultCallback<E> {
	
	/**
	 * 回傳資訊成功
	 * @param result server回傳的結果
	 */
	public void onResultSucceed(E result);
	
	/**
	 * 回傳資訊失敗
	 * @param result server回傳的結果
	 */
	public void onResultFail(E result);
	
	/**
	 * 回傳資訊後的後續處理
	 */
	public void onResultFinal();
}

