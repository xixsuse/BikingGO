package com.kingwaytek.api.model;

import org.json.JSONException;
import org.json.JSONStringer;

public abstract class AbstractPrivacyAndTermsRequestAgree extends WebPostImpl {

	String mPassCode;
	int mLogType;
	String mPrivacyVersion;
	String mTermsVersion;
	String mAppID;

	public AbstractPrivacyAndTermsRequestAgree(String passCode, int logType, String privacyVersion, String termsVersion, String appID) {
		mPassCode = passCode;
		mLogType = logType;
		mPrivacyVersion = privacyVersion;
		mTermsVersion = termsVersion;
		mAppID = appID;
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
					jsonText.key("privacy_version").value(mPrivacyVersion);
					jsonText.key("terms_version").value(mTermsVersion);
					jsonText.key("AppID").value(mAppID);
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