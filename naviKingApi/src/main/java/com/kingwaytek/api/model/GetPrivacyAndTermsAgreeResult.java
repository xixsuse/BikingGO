package com.kingwaytek.api.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetPrivacyAndTermsAgreeResult extends WebResultAbstract {

	public GetPrivacyAndTermsAgreeResult(String jsonData) {
		super(jsonData, false);
	}

	public GetPrivacyAndTermsAgreeResult(JSONObject jsonData) {
		super(jsonData, true);
	}

	@Override
	public void parsingData(JSONObject jsonResult) {

	}

	@Override
	public void parsingData(JSONArray _jsonResult) {

	}

}