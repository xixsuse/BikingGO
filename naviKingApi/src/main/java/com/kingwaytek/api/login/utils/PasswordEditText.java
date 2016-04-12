package com.kingwaytek.api.login.utils;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

import com.kingwaytek.api.utility.UtilityApi;

public class PasswordEditText extends EditText implements CustomEditTextInterface {
	private final int maxLength = 20;
	
	public PasswordEditText(Context context) {
		super(context);
		initializeSetting();
	}
	
	public PasswordEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeSetting();
	}
	
	public PasswordEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeSetting();
	}
	
	@Override
	public void initializeSetting() {
		this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		UtilityApi.setMaxLength(this, maxLength);
	}
	
	public boolean isFormat() {
		return MemberUtils.isPasswordFormat(this.getText().toString());
	}
	
	public boolean isEmpty() {
		return "".equals(this.getText().toString());
	}
	
	@Override
	public void setError(CharSequence error) {
		this.requestFocus();
		super.setError(error);
	}
}