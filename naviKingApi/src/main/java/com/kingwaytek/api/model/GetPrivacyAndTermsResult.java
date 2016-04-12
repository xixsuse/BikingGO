package com.kingwaytek.api.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetPrivacyAndTermsResult extends WebResultAbstract {
	public static final String TAG = "GetPrivacyAndTermsResult";
	private PrivacyAndTermsData mPrivacyAndTermsData;

	// Json keys
	public static final String JSON_KEY_NEED_AGREE = "need_agree";
	public static final String JSON_KEY_PRIVACY_VERSION = "privacy_version";
	public static final String JSON_KEY_PRIVACY_LINK = "privacy_link";
	public static final String JSON_KEY_TERMS_VERSION = "terms_version";
	public static final String JSON_KEY_TERMS_LINK = "terms_link";

	public GetPrivacyAndTermsResult(String jsonData) {
		super(jsonData, false);

	}

	public GetPrivacyAndTermsResult(JSONObject jsonData) {
		super(jsonData, true);

	}

	@Override
	public void parsingData(JSONObject jsonResult) {
		try {
			mPrivacyAndTermsData = new PrivacyAndTermsData();
			if (checkObjNotEmpty(jsonResult, JSON_KEY_NEED_AGREE)) {
				mPrivacyAndTermsData.needAgree = jsonResult.getBoolean(JSON_KEY_NEED_AGREE);
			}
			if (checkObjNotEmpty(jsonResult, JSON_KEY_PRIVACY_VERSION)) {
				mPrivacyAndTermsData.privacyVersion = jsonResult.getString(JSON_KEY_PRIVACY_VERSION);
			}
			if (checkObjNotEmpty(jsonResult, JSON_KEY_PRIVACY_LINK)) {
				mPrivacyAndTermsData.privacyLink = jsonResult.getString(JSON_KEY_PRIVACY_LINK);
			}
			if (checkObjNotEmpty(jsonResult, JSON_KEY_TERMS_VERSION)) {
				mPrivacyAndTermsData.termsVersion = jsonResult.getString(JSON_KEY_TERMS_VERSION);
			}
			if (checkObjNotEmpty(jsonResult, JSON_KEY_TERMS_LINK)) {
				mPrivacyAndTermsData.termsLink = jsonResult.getString(JSON_KEY_TERMS_LINK);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void parsingData(JSONArray jsonResult) {

	}

	public PrivacyAndTermsData getPrivacyAndTermsData() {
		return mPrivacyAndTermsData;
	}

}