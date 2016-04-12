package com.kingwaytek.api.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetMsgResult extends WebResultAbstract {

	private String mMsg;

	public static final String JSON_KEY_MSG = "msg";

	public GetMsgResult(String jsonData) {
		super(jsonData, true);
	}

	public GetMsgResult(JSONObject jsonData) {
		super(jsonData, true);
	}

	@Override
	public void parsingData(JSONObject jsonResult) {

		try {
			if (checkObjNotEmpty(jsonResult, JSON_KEY_MSG)) {
				mMsg = jsonResult.getString(JSON_KEY_MSG);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void parsingData(JSONArray _jsonResult) {
		try {
			if (_jsonResult != null && _jsonResult.length() > 0) {
				mMsg = _jsonResult.get(0).toString();
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}
	}

	public String getMsg() {
		return mMsg;
	}

}