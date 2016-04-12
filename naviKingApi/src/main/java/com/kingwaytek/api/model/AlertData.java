package com.kingwaytek.api.model;

import org.json.JSONObject;

import com.kingwaytek.api.utility.JsonApi;

/**
 * PushData
 * 
 * @author CalvinHuang
 * 
 */
public class AlertData {
	private static final String TAG = "AlertData";

	public String title;
	public String body;

	public AlertData() {
		super();
	}

	public AlertData(String _title, String _body) {
		title = _title;
		body = _body;

	}

	public static final String JSON_KEY_BODY = "body";
	public static final String JSON_KEY_TITLE = "title";

	public static AlertData parsingData(String _str) {
		AlertData alertData = new AlertData();
		try {
			JSONObject jsonObject = new JSONObject(_str);
			if (JsonApi.checkObjNotEmpty(jsonObject, JSON_KEY_BODY)) {
				alertData.body = jsonObject.getString(JSON_KEY_BODY);
			}
			if (JsonApi.checkObjNotEmpty(jsonObject, JSON_KEY_TITLE)) {
				alertData.title = jsonObject.getString(JSON_KEY_TITLE);
			}
			return alertData;
		} catch (Exception e) {
			alertData.title = "";
			alertData.body = _str;
			e.printStackTrace();
		}
		return alertData;
	}
}