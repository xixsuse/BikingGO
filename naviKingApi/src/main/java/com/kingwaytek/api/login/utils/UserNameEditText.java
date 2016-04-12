package com.kingwaytek.api.login.utils;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

import com.kingwaytek.api.utility.UtilityApi;

public class UserNameEditText extends EditText implements CustomEditTextInterface {
	private final int maxLength = 20;
	
	public UserNameEditText(Context context) {
		super(context);
		initializeSetting();
	}
	
	public UserNameEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeSetting();
	}
	
	public UserNameEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeSetting();
	}
	
	@Override
	public void initializeSetting() {
		this.setInputType(InputType.TYPE_CLASS_TEXT);
		UtilityApi.setMaxLength(this, maxLength);
	}
	
	// 暱稱沒有限制，所以一律回傳true
	public boolean isFormat() {
		return true;
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