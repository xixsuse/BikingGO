package com.kingwaytek.api.model;


public class PackageName {
	public final static String GOTCHATRANSIT_FREE = "com.kingwaytek.gotcha.transit";
	public final static String GOTCHATRANSIT_PRO = "com.kingwaytek.gotcha.transit.pro";
	public final static String LOCALKINGFUN_CN = "com.kingwaytek.localkingfun.cn";
	public final static String LOCALKING_LIFEMAN = "com.kingwaytek.lifeman";
	
	public static class NaviKingCht3D{

		public final static String NAVIKING_3D_1 = "com.kingwaytek.naviking3d"; // 官網, Hami Pro ,Hami Pro,Google  Pro, TWN Pro
		public final static String NAVIKING_3D_OFFICIAL_PRO = "com.kingwaytek.naviking3d" ;// 官網
		public final static String NAVIKING_3D_HAMI_PRO = "com.kingwaytek.naviking3d" ;// Hami Pro
		public final static String NAVIKING_3D_GOOGLE_PRO = "com.kingwaytek.naviking3d" ;// Google Pro
		public final static String NAVIKING_3D_SAMSUNG_PRO = "com.kingwaytek.naviking3d"; // Samsung Pro
		public final static String NAVIKING_3D_TWN_PRO = "com.kingwaytek.naviking3d"; // Twn Pro
		
		public final static String NAVIKING_3D_HAMI_ONE_YEAR = "com.kingwaytek.naviking3d.std"; // Hami  One Year
		public final static String NAVIKING_3D_HAMI_SIXTY = "com.kingwaytek.naviking3d.hami.sixty"; // Hami 60 trail,待行銷確認上架
		public final static String NAVIKING_3D_GOOGLE_STD = "com.kingwaytek.naviking3d.google.std"; // Google Std
		public final static String NAVIKING_3D_SAMSUNG_STD = "com.kingwaytek.naviking3d.samsung.std"; // Samsung Std
		
		public static final String[] ALL_SETS = new String[] { 
			NAVIKING_3D_1,
			NAVIKING_3D_HAMI_ONE_YEAR,
			NAVIKING_3D_HAMI_SIXTY,
			NAVIKING_3D_GOOGLE_STD,
			NAVIKING_3D_SAMSUNG_STD
		};
		//全部商城的PRO都一樣
		public static final String[] NAVIKING_CHT3D_PRO_SETS = new String[] { 
			NAVIKING_3D_1
		};
		
		public static final String[] NAVIKING_CHT3D_STD_SETS = new String[] { 
			NAVIKING_3D_HAMI_ONE_YEAR,
			NAVIKING_3D_HAMI_SIXTY,
			NAVIKING_3D_GOOGLE_STD,
			NAVIKING_3D_SAMSUNG_STD
		};
	}
	// N3 等同 N5
	public static class NaviKingN3{
		// N3 std
		public final static String NAVIKING_PLAY_STD = "com.kingwaytek.naviking.std"; // Google Std
		public final static String NAVIKING_HAMI_STD = "com.kingwaytek.hami.std"; // Hami One Year

		// 官網 / Hami Pro / Google Pro / Samsung Pro / Twn Pro / Mio
		public final static String NAVIKING_PRO_3 = "com.kingwaytek"; 
		public final static String NAVIKING_PRO_2 = "com.kingwaytek.samsung"; // Samsung Pro
		public final static String NAVIKING_PRO_1 = "com.kingwaytek.naviking"; // Google Pro

		public static final String[] ALL_SETS = new String[] { 
			NAVIKING_PRO_1,
			NAVIKING_PRO_2, 
			NAVIKING_PRO_3 ,
			NAVIKING_PLAY_STD,
			NAVIKING_HAMI_STD 
		};
		
		public static final String[] NAVIKING_N3_PRO_SETS = new String[] { 
			NAVIKING_PRO_1,
			NAVIKING_PRO_2, 
			NAVIKING_PRO_3
		};
		
		public static final String[] NAVIKING_N3_STD_SETS = new String[] { 
			NAVIKING_PLAY_STD,
			NAVIKING_HAMI_STD
		};
	}
	
	public static class LocalKingFun{
		public static final String LOCALKINGFUN = "com.kingwaytek.localkingfun.tw";

		public static final String[] LOCALKING_FUN_SETS = new String[] { 
			LOCALKINGFUN 
		};
	}
	
	public static class LocalKingRider{
		public static final String LOCALKINGRIDER = "com.kingwaytek.localkingrider";

		public static final String[] LOCALKING_RIDER_SETS = new String[] { 
			LOCALKINGRIDER 
		};
	}
}