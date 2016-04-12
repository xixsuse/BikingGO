package com.kingwaytek.api.model;

public class PrivacyAndTermsRequestAgree extends AbstractPrivacyAndTermsRequestAgree {

	public PrivacyAndTermsRequestAgree(String passCode, int logType, String privacyVersion, String termsVersion, String appID) {
		super(passCode, logType, privacyVersion, termsVersion, appID);
	}
}