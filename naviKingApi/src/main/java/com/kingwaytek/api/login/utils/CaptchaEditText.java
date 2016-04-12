package com.kingwaytek.api.login.utils;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

import com.kingwaytek.api.utility.UtilityApi;

public class CaptchaEditText extends EditText implements CustomEditTextInterface {
	private final int maxLength = 4;
	
	public CaptchaEditText(Context context) {
		super(context);
		initializeSetting();
	}
	
	public CaptchaEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeSetting();
	}
	
	public CaptchaEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeSetting();
	}
	
	@Override
	public void initializeSetting() {
		this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
		UtilityApi.setMaxLength(this, maxLength);
	}
	
	// 沒有限制，所以return true
	public boolean isFormat() {
		return true;
	}
	
	public boolean isEmpty() {
		return "".equals(this.getText().toString().trim());
	}
}