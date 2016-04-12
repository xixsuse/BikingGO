package com.kingwaytek.api.ad;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.kingwaytek.api.model.PrivacyAndTermsData;
import com.kingwyatek.api.R;

/**
 * 再次確認要拒絕條款更新的服務
 * 
 * @author calvinhuang
 * 
 */
public class PrivacyAndTermsRejectDialog extends AlertDialog {
	static final String TAG = "PrivacyAndTermsRejectDialog";
	private Context mContext;
	private PrivacyAndTermsData mPrivacyAndTermsData;
	private String mPassCode;
	private boolean mIsMember;
	private LinearLayout mButtonNegative, mButtonPositive;
	private PrivacyAndTermsCallBack mCallback;

	public PrivacyAndTermsRejectDialog(Context ctx, String passCode, boolean isMember, PrivacyAndTermsData privacyAndTermsData, PrivacyAndTermsCallBack callback) {
		super(ctx);
		show();
		setCancelable(false);
		setContentView(R.layout.privacy_and_terms_reject_dialog);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		this.mContext = ctx;
		this.mIsMember = isMember;
		this.mPassCode = passCode;
		this.mPrivacyAndTermsData = privacyAndTermsData;
		this.mCallback = callback;
		initialize();

	}

	private void initialize() {
		findViews();
		setListener();
	}

	private void findViews() {
		mButtonNegative = (LinearLayout) findViewById(R.id.button_negative);
		mButtonPositive = (LinearLayout) findViewById(R.id.button_positive);
	}

	private void setListener() {
		mButtonNegative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AdDebugHelper.debugLog(TAG, "mButtonPositive 拒絕,返回App端 清除會員資料並登出App");
				// 拒絕,返回App端 清除會員資料並登出App
				mCallback.onResultReject();
				dismiss();
			}
		});

		mButtonPositive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				// 取消
				AdDebugHelper.debugLog(TAG, "mButtonNegative 取消");
				new PrivacyAndTermsDialog(mContext, mPassCode, mIsMember, mPrivacyAndTermsData, mCallback);
				dismiss();
			}
		});

	}
}
