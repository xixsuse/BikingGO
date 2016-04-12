package com.kingwaytek.api.ad;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingwaytek.api.model.PrivacyAndTermsData;
import com.kingwyatek.api.R;

public class PrivacyAndTermsWebDialog extends AlertDialog {
	static final String TAG = "PrivacyAndTermsWebDialog";
	private Context mContext;
	private PrivacyAndTermsData mPrivacyAndTermsData;
	private WebView mWebView;
	private TextView mTextViewTitle;
	private LinearLayout mButtonNegative;
	static int PRIVACY_TYPE = 1;
	static int TERMS_TYPE = PRIVACY_TYPE + 1;
	int mType = PRIVACY_TYPE;

	public PrivacyAndTermsWebDialog(Context ctx, PrivacyAndTermsData privacyAndTermsData, int type) {
		super(ctx);
		show();
		setCancelable(false);
		setContentView(R.layout.privacy_and_terms_web_dialog);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		this.mContext = ctx;
		this.mPrivacyAndTermsData = privacyAndTermsData;
		this.mType = type;
		initialize();
	}

	private void initialize() {
		findViews();
		setListener();
		setContent();
	}

	private void setContent() {
		if (mType == PRIVACY_TYPE) {
			
			mWebView.loadUrl(mPrivacyAndTermsData.privacyLink);
		} else {
			
			mWebView.loadUrl(mPrivacyAndTermsData.termsLink);
		}
	}

	private void findViews() {
		mWebView = (WebView) findViewById(R.id.webview);
		WebSettings settings = mWebView.getSettings();
//		settings.setDomStorageEnabled(true);
//		settings.setDatabaseEnabled(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// JavaScript
		settings.setJavaScriptEnabled(true);
		// 設置加載進來的頁面Fit手機寬度
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		// 支援放大縮小
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// mProgressBar.setProgress(newProgress);
				super.onProgressChanged(view, newProgress);
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// mProgressBar.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// mProgressBar.setVisibility(View.GONE);

				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				// updateMenuView();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				try {
					view.loadUrl(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});
		mButtonNegative = (LinearLayout) findViewById(R.id.button_negative);

	}

	private void setListener() {
		mButtonNegative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 取消
				dismiss();
			}
		});

	}

}
