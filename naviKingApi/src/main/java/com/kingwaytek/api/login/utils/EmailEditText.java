package com.kingwaytek.api.login.utils;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

public class EmailEditText extends EditText implements CustomEditTextInterface {
	
	public EmailEditText(Context context) {
		super(context);
		initializeSetting();
	}
	
	public EmailEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeSetting();
	}
	
	public EmailEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeSetting();
	}
	
	@Override
	public void initializeSetting() {
		this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
	}
	
	public boolean isFormat() {
		return MemberUtils.isEmailFormat(getContext(), this.getText().toString().trim());
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