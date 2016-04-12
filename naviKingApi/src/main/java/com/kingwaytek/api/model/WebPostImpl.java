package com.kingwaytek.api.model;

/**
 * 網路傳送的資料型態使用
 * 
 * @author jeff.lin
 * 
 */
public abstract class WebPostImpl {

	protected String mPassCode = "";

	public abstract String getJSONResult();

	public void setPassCode(String passCode) {
		mPassCode = passCode;
	}

	public boolean hasPassCode() {
		return mPassCode != null && mPassCode.length() > 0;
	}
}