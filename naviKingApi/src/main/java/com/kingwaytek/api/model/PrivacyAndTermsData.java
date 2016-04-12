package com.kingwaytek.api.model;

import com.kingwaytek.api.utility.UtilityApi;

public class PrivacyAndTermsData {
	private static final String TAG = "PrivacyAndTermsData";
	public boolean needSeedAgree;
	public boolean needAgree;
	public String privacyVersion;
	public String privacyLink;
	public String termsVersion;
	public String termsLink;

	public PrivacyAndTermsData() {
		super();
	}

	public PrivacyAndTermsData(boolean _needSeedAgree, boolean _needAgree, String _privacyVersion, String _privacyLink, String _termsVersion, String _termsLink) {
		needSeedAgree = _needSeedAgree;
		needAgree = _needAgree;
		privacyVersion = _privacyVersion;
		privacyLink = _privacyLink;
		termsVersion = _termsVersion;
		termsLink = _termsLink;
	}
	
	public PrivacyAndTermsData(boolean _needAgree, String _privacyVersion, String _privacyLink, String _termsVersion, String _termsLink) {
		needAgree = _needAgree;
		privacyVersion = _privacyVersion;
		privacyLink = _privacyLink;
		termsVersion = _termsVersion;
		termsLink = _termsLink;
	}
	public void setNeedSeedAgree(Boolean _needSeedAgree) {
		needSeedAgree = _needSeedAgree;
	}
	
	public boolean getNeedSeedAgree() {
		return needSeedAgree;
	}
	
	public boolean getNeedAgree() {
		return needAgree;
	}

	public String getPrivacyVersion() {
		return UtilityApi.adjustNull(privacyVersion);
	}

	public String getPrivacyLink() {
		return UtilityApi.adjustNull(privacyLink);
	}

	public String getTermsVersion() {
		return UtilityApi.adjustNull(termsVersion);
	}

	public String getTermsLink() {
		return UtilityApi.adjustNull(termsLink);
	}

}