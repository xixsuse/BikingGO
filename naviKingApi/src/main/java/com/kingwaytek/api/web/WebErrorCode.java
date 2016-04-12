package com.kingwaytek.api.web;

public class WebErrorCode {
	
	// TODO 如果可以直接用變數名稱明白是什麼 ERROR代表,就不要在加中文註解了
	
	public static final int RETURN_NULL	= -99 ;	//NULL
	public static final int NO_POWER = -6 ; //無權根
	public static final int PARAMETER_LOST_ERROR = -5 ; //缺少必要參數
	public static final int LOGIN_ERROR = -4 ; //程式運行錯誤
	public static final int FUNCTION_ERROR = -3; // 程式運行錯誤
	public static final int PASS_CODE_ERROR = -2;
	public static final int FORMAT_ERROR = -1; // 格式錯誤
	public static final int REGISTEGISTERED_ERROR = -1; // 已註冊 / 此OPENID已註冊

	public static final int VERIFY_FAIL = 0; // 驗證失敗
	public static final int VERIFY_SUCCESS = 1; // 正常
	public static final int LOGIN_ERROR_PHONENOCHECK = -4001; // 電話號碼_已登入_未驗證_未註冊為帳號
	public static final int LOGIN_ERROR_PHONENOREGISTER = -4002; // 電話號碼_已登入_已驗證_未註冊為帳號
	public static final int LOGIN_ERROR_PHONEALLDONE = -4003; // 電話號碼_已登入_已驗證_已註冊為帳號
	public static final int LOGIN_ERROR_ACCOUNT_BLOCKED = -4004; // 帳號已被封鎖
	public static final int LOGIN_ERROR_SEND_MAX = -4005;	//今日的驗證碼發送次數已達上限
	public static final int LOGIN_ERROR_SEND_MMS_FAIL = -4006;	//簡訊發送失敗
	public static final int LOGIN_ERROR_NOT_LOCALKING_MAMBER = -4007;	//無此樂客會員
	public static final int LOGIN_ERROR_CAPTCHA_EXPIRED = -4008;	//此驗證碼已失效
	public static final int LOGIN_ERROR_CAPTCHA_INVAILD = -4009;	//此驗證碼已過期
	public static final int LOGIN_ERROR_CAPTCHA_FAIL = -4010;	//驗證碼錯誤
	public static final int LOGIN_ERROR_MODIFY_PW_FAIL = -4011;	//密碼修改失敗
	public static final int LOGIN_ERROR_PW_ENCODE_FAIL = -4012;	//密碼加密錯誤
	public static final int LOGIN_ERROR_ACCOUNT_HAS_BEEN_CREATED = -4013; // 此帳號已註冊樂客會員
	
	public static final int RETURN_SERVER_FAIL = -5001; // 夥計使用者購買商品後回傳後端失敗,App要做暫存購買資料並重傳的動作
}