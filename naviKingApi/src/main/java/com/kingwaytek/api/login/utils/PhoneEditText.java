package com.kingwaytek.api.login.utils;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

import com.kingwaytek.api.utility.UtilityApi;

public class PhoneEditText extends EditText implements CustomEditTextInterface {
	private final int maxLength = 10;
	
	public PhoneEditText(Context context) {
		super(context);
		initializeSetting();
	}
	
	public PhoneEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeSetting();
	}
	
	public PhoneEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeSetting();
	}
	
	@Override
	public void initializeSetting() {
		this.setInputType(InputType.TYPE_CLASS_PHONE);
		UtilityApi.setMaxLength(this, maxLength);
	}
	
	public boolean isFormat() {
		return MemberUtils.isPhoneNumberFormat(this.getText().toString().trim());
	}
	
	public boolean isEmpty() {
		return "".equals(this.getText().toString().trim());
	}
	
	@Override
	public void setError(CharSequence error) {
		this.requestFocus();
		super.setError(error);
	}
}