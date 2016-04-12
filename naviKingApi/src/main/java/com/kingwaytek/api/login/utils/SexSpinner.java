package com.kingwaytek.api.login.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.kingwyatek.api.R;

public class SexSpinner extends Spinner implements CustomSpinnerInterface {

	public SexSpinner(Context context) {
		super(context);
		initializeSetting();
	}

	public SexSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeSetting();
	}

	public SexSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeSetting();
	}

	@Override
	public void initializeSetting() {
		setArrayAdapterLayout(android.R.layout.simple_spinner_item);
	}
	
	public void setArrayAdapterLayout(int textViewResoucreId) {
		String male = getResources().getString(R.string.sex_male);
		String female = getResources().getString(R.string.sex_female);
		String other = getResources().getString(R.string.sex_other);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), textViewResoucreId, new String[] {male, female, other });
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.setAdapter(adapter);
	}
}