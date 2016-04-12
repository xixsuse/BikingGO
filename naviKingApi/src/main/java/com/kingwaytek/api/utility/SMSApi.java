package com.kingwaytek.api.utility;

public class SMSApi {
	/**
	 * getMessageBody:感謝您註冊樂客會員！您的會員註冊驗證碼是：1402，請在30分內輸入完成驗證
	 * 
	 * @param context
	 * @param sms
	 * @return
	 */
	public static String getSMSVerifyCode(String sms) {
		if (UtilityApi.checkStringNotEmpty(sms) && sms.indexOf("樂客會員") > 0) {
			String startStr = "驗證碼是：";
			String endStr = "，請在30分內";
			int startIndex = sms.indexOf(startStr);
			int endIndex = sms.indexOf(endStr);
			String verifyCode = "";
			// 樂客會員簡訊
			if (startIndex > 0 && endIndex > 0 && (endIndex > (startIndex + startStr.length()))) {
				verifyCode = sms.substring(startIndex + startStr.length(), endIndex);
				if (verifyCode.length() == 4) {
					return verifyCode;
				}
			}
		}
		return "";
	}

}