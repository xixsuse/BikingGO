package com.kingwaytek.api.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.kingwaytek.api.utility.DialogAgent;
import com.kingwyatek.api.R;

/**
 * 預設有ProgressDialog的WebAsyncTask，可設定 isCancelable、讀取字串
 * 預設有Network狀況處理的WebAsyncTask
 */
public class WebAsyncTask<E> extends BaseWebAsyncTask<E> {
	ProgressDialog mProgressDialog;

	public WebAsyncTask(Activity activity, WebAgentCallback<E> webAgentCallback) {
		super(activity, webAgentCallback);
		setProgressDialog(false, mActivity.getString(R.string.loading_more));
		setDefaultNetworkHandler();
	}

	public WebAsyncTask(Context context, WebAgentCallback<E> webAgentCallback) {
		super(context, webAgentCallback);
		setProgressDialog(false, context.getString(R.string.loading_more));
		setDefaultNetworkHandler();
	}

	/**
	 * 可自行設定是否可以取消點選讀取中的dialog以及設定顯示的字串
	 * 
	 * @param isCancelable
	 * @param loadingStr
	 * @return
	 */
	public WebAsyncTask<E> setProgressDialog(final boolean isCancelable, final String loadingStr) {
		super.setWebTaskDialogCallback(new WebTaskDialogCallback() {

			@Override
			public void show() {
				try {
					if (mActivity != null && !mActivity.isFinishing()) {
						mProgressDialog = ProgressDialog.show(mActivity, null, loadingStr, true, isCancelable);
					} else {
						if (mContext != null) {
							mProgressDialog = ProgressDialog.show(mContext, null, loadingStr, true, isCancelable);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void dismiss() {
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
			}
		});
		return this;
	}

	/**
	 * 可自行設定是否可以取消點選讀取中的dialog以及設定顯示的字串ResId
	 * 
	 * @param isCancelable
	 * @param loadingStrId
	 * @return
	 */
	public WebAsyncTask<E> setProgressDialog(final boolean isCancelable, final int loadingStrId) {
		return setProgressDialog(isCancelable, mActivity.getString(loadingStrId));
	}

	/**
	 * 不要顯示"讀取中的dialog"
	 * 
	 * @param isShowProgressDialog
	 * @return
	 */
	public WebAsyncTask<E> notShowProgressDialog() {
		return (WebAsyncTask<E>) super.setWebTaskDialogCallback(null);
	}

	/**
	 * 不要顯示"網路連線尚未開啟dialog"
	 * 
	 * @return
	 */
	public WebAsyncTask<E> notShowNetworkDialog() {
		return setWebNetworkCallback(null);
	}

	/**
	 * 預設網路失敗，跳出dialog提示開啟網路
	 */
	private void setDefaultNetworkHandler() {
		setWebNetworkCallback(new WebNetworkCallback() {
			@Override
			public void onNetworkFail() {
				try {
					if (mActivity !=null && !mActivity.isFinishing()) {
						DialogAgent.getNetworkUnavaliableDialog(mActivity).setCancelable(false).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public WebAsyncTask<E> setWebResultCallback(WebResultCallback<E> webResultCallback) {
		return (WebAsyncTask<E>) super.setWebResultCallback(webResultCallback);
	}

	@Override
	public WebAsyncTask<E> setWebNetworkCallback(WebNetworkCallback webNetworkCallback) {
		return (WebAsyncTask<E>) super.setWebNetworkCallback(webNetworkCallback);
	}

}