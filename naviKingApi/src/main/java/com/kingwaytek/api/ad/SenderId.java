package com.kingwaytek.api.ad;

import android.content.Context;

import com.kingwaytek.api.model.PackageName;
import com.kingwaytek.api.utility.UtilityApi;

/**
 * GCM Google後端辨識產品的ID
 * 
 * @author CalvinHuang
 * 
 */
public final class SenderId {

	static String TAG = "SenderId";
	// LocalKingFun (樂客玩樂)
	// Key for server : AIzaSyBeuPj23KzZlXsQoRjudMkSzCKEU2EiE60
	public static final String LOCALKINGFUN = "84519201846";

	// LocalKingRemindMe (樂客生活秘書)
	// Key for server : AIzaSyBti_81YZjJTF8Iz_RrXIay7fIXKxmKwxU
	public static final String LOCALKINGREMINME = "209295289659";

	// LocalKingRider (樂客夥計)
	// Key for server : AIzaSyBPQuaUl3fRNlMRIIwXKf9tO7LwhV5YoNI
	public static final String LOCALKINGRIDER = "496699885237";

	// NaviKing3D Pro (樂客導航王全3D PRO)
	// Key for server : AIzaSyAwHCjra__JaDeBR4oy9CrYpHgJJm8mzWw
	public static final String NAVIKING3D_PRO = "977956470016";

	// NaviKing3D (樂客導航王全3D STD)
	// Key for server : AIzaSyDSDPyqJmP_G9b7CHAajmGnwJDZhXZQR_Q
	public static final String NAVIKING3D_STD = "407403083324";

	// NaviKingN5 Pro (樂客導航王N5 PRO)
	// Key for server : AIzaSyAcJzch0gPlRSKNKVLUTjNoYjCjDHZ_fBg
	public static final String NAVIKINGN5_PRO = "452710033462";

	// NaviKingN5 Lite (樂客導航王N5 Lite版)
	// Key for server : AIzaSyBpLK2fUVjgRZU_oXQVxStlXwo6wy46vM0
	public static final String NAVIKINGN5_LITE = "252270517388";

	// LocalKingTransit Free (樂客轉乘通Free)
	// Key for server : AIzaSyB3xYOontm3QQ1lgDzAQLy9BNw7DXT0C9M
	public static final String LOCALKINGTRANSIT_FREE = "672578587204";

	// LocalKingTransit (樂客轉乘通三都版)
	// Key for server : AIzaSyD9qHZEZ3Mo-CwG9_CD4ES1dp7_SUWpTnU
	public static final String LOCALKINGTRANSIT = "217633566297";

	// LocalKingBaodao (樂客寶島) //大陸手機可能不支援GCM功能 待確認
	// Key for server : AIzaSyARFyPdU7g_C4FIZ3v48AwmB62bWHIhwuU
	public static final String LOCALKINGBAODAO = "457292395703";

	public static String getId(Context ctx) {
		String senderId = "";
		String packageName = UtilityApi.AppInfo.getAppPackageName(ctx);
		if (UtilityApi.checkStringNotEmpty(packageName)) {
			// 樂客玩樂
			for (String name : PackageName.LocalKingFun.LOCALKING_FUN_SETS) {
				if (name.equals(packageName)) {
					AdDebugHelper.debugLog(TAG, "樂客玩樂");
					return LOCALKINGFUN;
				}
			}
			// 樂客生活秘書
			if (PackageName.LOCALKING_LIFEMAN.equals(packageName)) {
				AdDebugHelper.debugLog(TAG, "樂客生活秘書");
				return LOCALKINGREMINME;
			}
			// 樂客夥計
			for (String name : PackageName.LocalKingRider.LOCALKING_RIDER_SETS) {
				if (name.equals(packageName)) {
					AdDebugHelper.debugLog(TAG, "樂客夥計");
					return LOCALKINGRIDER;
				}
			}
			// 樂客導航王全3D PRO
			for (String name : PackageName.NaviKingCht3D.NAVIKING_CHT3D_PRO_SETS) {
				if (name.equals(packageName)) {
					AdDebugHelper.debugLog(TAG, "樂客導航王全3D PRO");
					return NAVIKING3D_PRO;
				}
			}
			// 樂客導航王全3D STD
			for (String name : PackageName.NaviKingCht3D.NAVIKING_CHT3D_STD_SETS) {
				if (name.equals(packageName)) {
					AdDebugHelper.debugLog(TAG, "樂客導航王全3D STD");
					return NAVIKING3D_STD;
				}
			}
			// 樂客導航王N5 PRO
			for (String name : PackageName.NaviKingN3.NAVIKING_N3_PRO_SETS) {
				if (name.equals(packageName)) {
					AdDebugHelper.debugLog(TAG, "樂客導航王N5 PRO");
					return NAVIKINGN5_PRO;
				}
			}
			// 樂客導航王N5 Lite版
			for (String name : PackageName.NaviKingN3.NAVIKING_N3_STD_SETS) {
				if (name.equals(packageName)) {
					AdDebugHelper.debugLog(TAG, "樂客導航王N5 STD");
					return NAVIKINGN5_LITE;
				}
			}

			// 樂客轉乘通FREE
			if (PackageName.GOTCHATRANSIT_FREE.equals(packageName)) {
				AdDebugHelper.debugLog(TAG, "樂客轉乘通FREE");
				return LOCALKINGTRANSIT_FREE;
			}
			// 樂客轉乘通三都版
			if (PackageName.GOTCHATRANSIT_PRO.equals(packageName)) {
				AdDebugHelper.debugLog(TAG, "樂客轉乘通三都版");
				return LOCALKINGTRANSIT;
			}

			// 樂客寶島
			// TODO 樂客寶島的PackageName要確認
			/*
			 * if (PackageName.LOCALKINGBAODAO.equals(packageName)) { return
			 * LOCALKINGBAODAO; }
			 */
		} else {
			AdDebugHelper.debugLog(TAG, "getAppPackageName error");
		}
		return senderId;
	}
}
