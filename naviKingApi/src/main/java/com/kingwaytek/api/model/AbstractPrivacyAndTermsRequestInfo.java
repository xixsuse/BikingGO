package com.kingwaytek.api.model;

import org.json.JSONException;
import org.json.JSONStringer;

public abstract class AbstractPrivacyAndTermsRequestInfo extends WebPostImpl {

	int mLogType;
	String mPassCode;

	public AbstractPrivacyAndTermsRequestInfo(String passCode, int logType) {
		mPassCode = passCode;
		mLogType = logType;

	}

	@Override
	public String getJSONResult() {
		String requestJSONData = "";
		try {
			JSONStringer jsonText = new JSONStringer();
			jsonText.object();
			{
				jsonText.key("input_code").value(mPassCode);
				jsonText.key("input_datas");
				jsonText.array();
				{
					jsonText.object();
					jsonText.key("LogType").value(mLogType);
					jsonText.endObject();
				}
				jsonText.endArray();

			}
			jsonText.endObject();
			requestJSONData = jsonText.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestJSONData;
	}
}