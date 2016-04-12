package com.kingwaytek.api.web;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.kingwaytek.api.utility.UtilityApi;

/**
 * webTask框架，使用WebApiInterface、WebAgentInterface兩個callback<br>
 * 因此server api的呼叫以及回傳後的處理都會放在app端<br>
 * 
 * @param <E>
 * 
 * @see WebAsyncTask WebAsyncTask
 */
public class BaseWebAsyncTask<E> extends AsyncTask<Object, Float, E> {
	protected Activity mActivity;
	protected Context mContext;
	protected WebAgentCallback<E> webAgentCallback;
	protected WebResultCallback<E> webResultCallback;
	protected WebTaskDialogCallback webTaskDialogCallback;
	protected WebNetworkCallback webNetWorkCallback;

	public BaseWebAsyncTask(Context context, WebAgentCallback<E> webAgentCallback) {
		this.mContext = context;
		this.webAgentCallback = webAgentCallback;
	}

	public BaseWebAsyncTask(Activity activity, WebAgentCallback<E> webAgentCallback) {
		this.mActivity = activity;
		this.webAgentCallback = webAgentCallback;
	}

	/**
	 * handler response message from server
	 * 因為不一定要處理從server回傳的訊息，因此這個callback是optional的
	 * 
	 * @param webResultCallback
	 * @return
	 */
	public BaseWebAsyncTask<E> setWebResultCallback(WebResultCallback<E> webResultCallback) {
		this.webResultCallback = webResultCallback;
		return this;
	}

	/**
	 * 設定讀取server api時要顯示的dialog
	 * 
	 * @param webResultCallback
	 */
	public BaseWebAsyncTask<E> setWebTaskDialogCallback(WebTaskDialogCallback webTaskDialogCallback) {
		this.webTaskDialogCallback = webTaskDialogCallback;
		return this;
	}

	/**
	 * 設定有關網路狀態相關問題
	 * 
	 * @param webResultCallback
	 */
	public BaseWebAsyncTask<E> setWebNetworkCallback(WebNetworkCallback webNetWorkCallback) {
		this.webNetWorkCallback = webNetWorkCallback;
		return this;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		boolean bIsNetworkWorking = false;
		if (mActivity != null) {
			bIsNetworkWorking = UtilityApi.isNetworkWorking(mActivity);
		} else {
			bIsNetworkWorking = UtilityApi.isNetworkWorking(mContext);
		}

		if (bIsNetworkWorking) {
			if (webTaskDialogCallback != null) {
				webTaskDialogCallback.show();
			}
		} else {
			if (webNetWorkCallback != null) {
				webNetWorkCallback.onNetworkFail();
			}
			cancel(true);
			return;
		}

	}

	protected E doInBackground(Object... params) {
		return webAgentCallback.getWebAgentResult();
	}

	@Override
	protected void onPostExecute(E result) {
		super.onPostExecute(result);
		try {
			if (result != null) {
				if (webResultCallback != null) {
					webResultCallback.onResultSucceed(result);
				}
			} else {
				if (webResultCallback != null) {
					webResultCallback.onResultFail(result);
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			callWebResultFinal();
			dismissDialog();
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		callWebResultFinal();
		dismissDialog();
	}

	void callWebResultFinal() {
		if (webResultCallback != null) {
			webResultCallback.onResultFinal();
		}
	}

	void dismissDialog() {
		if (webTaskDialogCallback != null) {
			try {
				webTaskDialogCallback.dismiss();
			} catch (IllegalArgumentException re) {
				re.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

};