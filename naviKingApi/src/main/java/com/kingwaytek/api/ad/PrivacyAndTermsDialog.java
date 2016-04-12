package com.kingwaytek.api.ad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingwaytek.api.model.PrivacyAndTermsData;
import com.kingwyatek.api.R;

public class PrivacyAndTermsDialog extends AlertDialog {
	static final String TAG = "PrivacyAndTermsDialog";
	private Context mContext;
	private PrivacyAndTermsData mPrivacyAndTermsData;
	private TextView mTextViewNegative;
	private TextView mTextViewTitle;
	private TextView mTextViewContent;
	private String mPassCode;
	private boolean mIsMember;
	private LinearLayout mButtonNegative, mButtonPositive;
	private PrivacyAndTermsCallBack mCallback;

	public PrivacyAndTermsDialog(Context ctx, String passCode, boolean isMember, PrivacyAndTermsData privacyAndTermsData, PrivacyAndTermsCallBack callback) {
		super(ctx);
		this.mContext = ctx;
		this.mIsMember = isMember;
		this.mPassCode = passCode;
		this.mPrivacyAndTermsData = privacyAndTermsData;
		this.mCallback = callback;
		Activity activity = (Activity) mContext;
		if (!activity.isFinishing()) {
			show();
			setCancelable(false);
			setContentView(R.layout.privacy_and_terms_dialog);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			initialize();
		}
	}

	private void initialize() {
		findViews();
		setViews();
		setListener();

	}

	private void findViews() {

		mTextViewTitle = (TextView) findViewById(R.id.textview_title);
		mTextViewContent = (TextView) findViewById(R.id.textview_content);
		mTextViewNegative = (TextView) findViewById(R.id.textview_negative);
		mButtonNegative = (LinearLayout) findViewById(R.id.button_negative);
		mButtonPositive = (LinearLayout) findViewById(R.id.button_positive);

	}

	private void setViews() {
		String html = "";
		if (mIsMember) {
			// 會員狀態
			mTextViewNegative.setText(R.string.reject);
			mTextViewTitle.setText(R.string.privacy_and_terms_update_title);
			//html = "提醒您！我們已修改部分條款，為了保障個人權益，請先閱讀並同意最新《<a href=\"" + mPrivacyAndTermsData.getTermsLink() + "\">服務條款</a>》及《<a href=\"" + mPrivacyAndTermsData.getPrivacyLink()
			//		+ "\">隱私權政策</a>》，才能繼續使用各項服務。";
			html = String.format(mContext.getString(R.string.privacy_and_terms_update_content_url), mPrivacyAndTermsData.getTermsLink(), mPrivacyAndTermsData.getPrivacyLink());

		} else {
			// 第一次註冊會員登入
			mTextViewNegative.setText(R.string.cancel);
			mTextViewTitle.setText(R.string.privacy_and_terms_of_use_title);
			//html = "為了保障個人權益，請先閱讀並同意《<a href=\"" + mPrivacyAndTermsData.getTermsLink() + "\">服務條款</a>》及《<a href=\"" + mPrivacyAndTermsData.getPrivacyLink() + "\">隱私權政策</a>》，才能繼續使用各項服務。";
			html = String.format(mContext.getString(R.string.privacy_and_terms_of_use_content_url), mPrivacyAndTermsData.getTermsLink(), mPrivacyAndTermsData.getPrivacyLink());

		}
		mTextViewContent.setText(Html.fromHtml(html));
		mTextViewContent.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void setListener() {
		mButtonNegative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 取消
				AdDebugHelper.debugLog(TAG, "mButtonNegative");
				if (mIsMember) {
					// 會員狀態
					new PrivacyAndTermsRejectDialog(mContext, mPassCode, mIsMember, mPrivacyAndTermsData, mCallback);
					dismiss();
				} else {
					// 第一次註冊會員登入
					mCallback.onResultClose();
					dismiss();
				}
			}
		});

		mButtonPositive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AdDebugHelper.debugLog(TAG, "mButtonPositive mIsMember:" + mIsMember);
				// 同意
				if (mIsMember) {
					// 若是會員直接回傳同意同款更新
					PrivacyAndTermsApi.agreePrivacyAndTermsTask(mContext, mPassCode, mPrivacyAndTermsData, mCallback);
				} else {
					// 若非會員先將條款資訊記錄下來,等註冊成功後才送出同意訊息
					AdDebugHelper.debugLog(TAG, "若非會員先將條款資訊記錄下來,等註冊成功後才送出同意訊息");
					mPrivacyAndTermsData.setNeedSeedAgree(true);
					PrivacyAndTermsApi.setPrivacyAndTermsData(mContext, mPrivacyAndTermsData);
				}
				mCallback.onResultAgree(mPrivacyAndTermsData);
				dismiss();
			}
		});

	}
}
