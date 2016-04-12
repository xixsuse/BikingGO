package com.kingwaytek.api.ad.web;

import java.util.ArrayList;

public class WebItem {
	String mResponseTagName; // GetPOIDetailResult
	String mRequestActionName; // GetPOIDetail
	String mRequestActionUrl; // \"http://www.gotcha.com.tw/WS_CHT_NaviKing/GetPOIDetail\""
								// ;
	String mRequestAPIUrl;
	String mPostData;
	String mPostPath;
	ArrayList<String> mPostArrayPath;

	public WebItem(String actionName, String url) {
		this.mRequestActionName = actionName;
		this.mRequestAPIUrl = url + actionName;
	}

	/** ex. GetPOIInfoResult */
	public String getTagName() {
		return mResponseTagName;
	}

	/** ex. GetPOIInfo */
	public String getActionName() {
		return mRequestActionName;
	}

	public String getRequestUrl() {
		return mRequestAPIUrl;
	}

	public void setPostData(String data) {
		mPostData = data;
	}

	public String getPostData() {
		return mPostData;
	}
}