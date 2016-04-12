package com.kingwaytek.api.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;

import com.kingwaytek.api.utility.UtilityApi;
import com.kingwaytek.api.web.WebErrorCode;

public abstract class WebResultAbstract {
	public static final String TAG = "WebResultAbstract";
	protected String mErrorMsg;

	protected int mErrorCode = WebErrorCode.PASS_CODE_ERROR;

	public final static String JSON_KEY_OUTPUT_CODE = "output_code";
	public final static String JSON_KEY_OUTPUT_MSG = "output_msg";
	public final static String JSON_KEY_OUTPUT_DATA = "output_data";

	public WebResultAbstract() {

	}

	public WebResultAbstract(String jsonData, boolean isJsonArray) {
		try {
			if (UtilityApi.checkStringNotEmpty(jsonData)) {
				if (isJsonArray) {
					JSONArray jsonResult = getJSONOutputObjectArray(jsonData);
					parsingData(jsonResult);
				} else {
					JSONObject jsonResult = getJSONOutputObject(jsonData);
					parsingData(jsonResult);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public WebResultAbstract(JSONObject jsonData, boolean isJsonArray) {
		try {
			if (isJsonArray) {
				JSONArray jsonResult = getJSONOutputObjectArray(jsonData);
				parsingData(jsonResult);
			} else {
				JSONObject jsonResult = getJSONOutputObject(jsonData);
				parsingData(jsonResult);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public WebResultAbstract(Parcel in) {

	}

	abstract public void parsingData(JSONArray jsonResult);

	abstract public void parsingData(JSONObject jsonResult);

	public JSONObject getJSONOutputObject(String jsonData) throws JSONException {
		JSONObject jsonResult = null;
		JSONObject jsonTop = new JSONObject(jsonData);
		if (jsonTop != null) {
			mErrorCode = jsonTop.getInt(JSON_KEY_OUTPUT_CODE);
			if (checkObjNotEmpty(jsonTop, JSON_KEY_OUTPUT_MSG)) {
				mErrorMsg = jsonTop.getString(JSON_KEY_OUTPUT_MSG);
			}
			if (checkObjNotEmpty(jsonTop, JSON_KEY_OUTPUT_DATA)) {
				if (UtilityApi.checkStringNotEmpty(jsonTop.getString(JSON_KEY_OUTPUT_DATA))) {
					jsonResult = jsonTop.getJSONObject(JSON_KEY_OUTPUT_DATA);
				}
			}
		}
		return jsonResult;
	}

	public JSONObject getJSONOutputObject(JSONObject jsonObjData) throws JSONException {

		JSONObject jsonResult = null;
		if (jsonObjData != null) {
			mErrorCode = jsonObjData.getInt(JSON_KEY_OUTPUT_CODE);
			jsonResult = jsonObjData.getJSONObject(JSON_KEY_OUTPUT_DATA);
		}
		return jsonResult;
	}

	public JSONArray getJSONOutputObjectArray(String jsonData) throws JSONException {
		JSONArray jsonResult = null;
		if (jsonData != null) {
			JSONObject jsonTop = new JSONObject(jsonData);
			if (jsonTop != null) {
				mErrorCode = jsonTop.getInt(JSON_KEY_OUTPUT_CODE);
				if (checkObjNotEmpty(jsonTop, JSON_KEY_OUTPUT_MSG)) {
					mErrorMsg = jsonTop.getString(JSON_KEY_OUTPUT_MSG);
				}
				if (checkObjNotEmpty(jsonTop, JSON_KEY_OUTPUT_DATA)) {
					jsonResult = jsonTop.getJSONArray(JSON_KEY_OUTPUT_DATA);
				}
			}
		}
		return jsonResult;
	}

	public JSONArray getJSONOutputObjectArray(JSONObject jsonObjData) throws JSONException {

		JSONArray jsonResult = null;
		if (jsonObjData != null) {
			mErrorCode = jsonObjData.getInt(JSON_KEY_OUTPUT_CODE);
			jsonResult = jsonObjData.getJSONArray(JSON_KEY_OUTPUT_DATA);
		}
		return jsonResult;
	}

	public int getResultCode() {
		return mErrorCode;
	}

	public String getErrorMsg() {
		return mErrorMsg;
	}

	public static boolean checkObjNotEmpty(JSONObject obj, String key) {
		if (obj.has(key) && !obj.isNull(key)) {
			return true;
		} else {
			return false;
		}
	}
}