package com.kingwaytek.api.model;

import org.json.JSONObject;

import com.kingwaytek.api.utility.JsonApi;

/**
 * LKPushData
 * 
 * @author CalvinHuang
 * 
 */
public class LKPushData {
	private static final String TAG = "LKPushData";

	public int pushtype;
	public int pushid;
	public String url;
	public int topicid;
	public int cpid;

	public LKPushData() {
		super();
	}

	public LKPushData(int _pushtype, int _pushid, String _url, int _topicid, int _cpid) {
		pushtype = _pushtype;
		pushid = _pushid;
		url = _url;
		topicid = _topicid;
		cpid = _cpid;
	}

	public static final String JSON_KEY_LKPush = "LKPush";
	public static final String JSON_KEY_TOPICID = "topicid";
	public static final String JSON_KEY_CPID = "cpid";
	public static final String JSON_KEY_PUSH_ID = "pushid";
	public static final String JSON_KEY_PUSHTYPE = "pushtype";
	public static final String JSON_KEY_URL = "url";

	public static LKPushData parsingData(String _str) {
		try {
			JSONObject jsonObject = new JSONObject(_str);
			if (JsonApi.checkObjNotEmpty(jsonObject, JSON_KEY_LKPush)) {
				LKPushData lKPushData = new LKPushData();
				JSONObject lKPushjsonObject = jsonObject.getJSONObject(JSON_KEY_LKPush);
				if (JsonApi.checkObjNotEmpty(lKPushjsonObject, JSON_KEY_PUSHTYPE)) {
					lKPushData.pushtype = lKPushjsonObject.getInt(JSON_KEY_PUSHTYPE);
				}
				if (JsonApi.checkObjNotEmpty(lKPushjsonObject, JSON_KEY_PUSH_ID)) {
					lKPushData.pushid = lKPushjsonObject.getInt(JSON_KEY_PUSH_ID);
				}
				if (JsonApi.checkObjNotEmpty(lKPushjsonObject, JSON_KEY_TOPICID)) {
					lKPushData.topicid = lKPushjsonObject.getInt(JSON_KEY_TOPICID);
				}
				if (JsonApi.checkObjNotEmpty(lKPushjsonObject, JSON_KEY_CPID)) {
					lKPushData.cpid = lKPushjsonObject.getInt(JSON_KEY_CPID);
				}
				if (JsonApi.checkObjNotEmpty(lKPushjsonObject, JSON_KEY_URL)) {
					lKPushData.url = lKPushjsonObject.getString(JSON_KEY_URL);
				}
				return lKPushData;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}