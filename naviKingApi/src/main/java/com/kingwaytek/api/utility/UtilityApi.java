package com.kingwaytek.api.utility;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kingwyatek.api.R;

public class UtilityApi {

	/**
	 * 強制停止整個程式並退出
	 */
	public static void forceCloseTask() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * 設定editText輸入長度
	 * 
	 * @param editText
	 * @param maxLength
	 */
	public static void setMaxLength(EditText editText, int maxLength) {
		editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
	}

	public static boolean isNetworkWorking(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		boolean isWorking = mNetworkInfo != null && mNetworkInfo.isConnected();
		return isWorking;
	}

	public static DisplayMetrics getDisplayMetrics(Activity activity) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics;
	}

	/**
	 * 取得硬體唯一碼,最少14碼, IMEI取不到就改取Wifi mac address
	 * 
	 * @return IMEI or Wifi-Mac
	 */
	public static String getHardwareId(Context context) {
		String hardwareId = "";
		try {
			TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			hardwareId = telMgr.getDeviceId();
			if (hardwareId == null) {
				WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
				hardwareId = wifiInfo.getMacAddress();

				if (hardwareId == null) {
					hardwareId = "";
				} else {
					hardwareId = hardwareId.replaceAll(":", "");
					while (hardwareId.length() < 14) {
						hardwareId += "0";
					}
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return hardwareId;
	}

	public static String getBrandName() {
		return Build.MODEL;
	}

	public static String getMobileIPAddres() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						String str = inetAddress.getHostAddress().toString();
						if (str.startsWith("192.168.") == false && str.startsWith("10.0.") == false) {
							return str;
						}
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static int getMMC(Context ctx) {
		int mcc = 0;
		try {
			String networkOperator = getNetWorkOperator(ctx);
			boolean hasNetworkOperator = networkOperator != null && networkOperator.length() > 0;
			if (hasNetworkOperator) {
				mcc = Integer.parseInt(networkOperator.substring(0, 3));
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return mcc;
	}

	public static int getMNC(Context ctx) {
		int mnc = 0;
		try {
			String networkOperator = getNetWorkOperator(ctx);
			boolean hasNetworkOperator = networkOperator != null && networkOperator.length() > 0;
			if (hasNetworkOperator) {
				mnc = Integer.parseInt(networkOperator.substring(3));
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return mnc;
	}

	public static String getNetWorkOperator(Context ctx) {
		TelephonyManager tel = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String networkOperator = tel.getNetworkOperator();
		return networkOperator;
	}

	public static Intent getInstallFileIntent(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = "application/vnd.android.package-archive";// DownloadManager.getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(file), type);
		return intent;
	}

	public static void setViewsVisibility(int visibilityType, View... views) {
		if (views == null) {
			return;
		}

		for (View view : views) {
			if (view != null) {
				view.setVisibility(visibilityType);
			}
		}
	}

	/**
	 * 取得APP相關資訊
	 */
	public static class AppInfo {

		/**
		 * 回傳當前APP版號，在AndroidManifest.xml，android:versionCode="49"
		 * 
		 * @return Application's version code from the {@code PackageManager}.
		 */
		public static int getAppVersionCode(Context context) {
			try {
				PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				return packageInfo.versionCode;
			} catch (NameNotFoundException e) {
				// should never happen
				throw new RuntimeException("Could not get package name: " + e);
			}
		}

		/**
		 * 回傳當前APP"完整"版號，在AndroidManifest.xml，android:versionName="2.1.49"
		 * 
		 * @param context
		 * @return APP Version
		 */
		public static String getAppVersionName(Context ctx) {
			String mSwVersionName = "";
			try {
				PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
				mSwVersionName = pinfo.versionName; // 1.0.0.0

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			return mSwVersionName;
		}

		/**
		 * 回傳 APP Package Name
		 * 
		 * @param context
		 * @return Package Name
		 */
		public static String getAppPackageName(Context context) {
			String packageName = "";
			try {
				// ---get the package info---
				PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				packageName = pi.packageName;
				if (packageName == null || packageName.length() <= 0) {
					return "";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return packageName;
		}
	}

	/**
	 * 用名稱來找尋id, string id, drawable id
	 */
	public static class Resource {
		public static int getIdByName(Context context, String name) {
			return context.getResources().getIdentifier(name, "id", context.getPackageName());
		}

		public static int getStringByName(Context context, String name) {
			return context.getResources().getIdentifier(name, "string", context.getPackageName());
		}

		// return drawable_id when input img_name
		public static int getDrawableIdByName(Context context, String name) {
			return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		}
	}

	public static class Screen {
		public static Point getScreenSize(Activity activity) {
			DisplayMetrics dm;
			dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			Point point = new Point(dm.widthPixels, dm.heightPixels);
			return point;
		}

		public static int getScreenOreintation(Context context) {
			return context.getResources().getConfiguration().orientation;
		}

		public static Point getScreenSize(Context context) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			Point point = new Point(metrics.widthPixels, metrics.heightPixels);
			return point;
		}

		public static String getScreenSizeStr(Context context) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			if (metrics != null) {
				if (metrics.widthPixels > metrics.heightPixels) {
					return metrics.heightPixels + "x" + metrics.widthPixels;
				} else {
					return metrics.widthPixels + "x" + metrics.heightPixels;
				}
			}
			return "";
		}

		public static int getScreenOreintation(Activity activity) {
			return activity.getResources().getConfiguration().orientation;
		}
	}

	public static LayoutInflater getInflater(Activity activity) {
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater;
	}

	public static final String GOOGLE_PLAY_WEB_SITE = "http://play.google.com/store/apps/details?id=";
	public static final String GOOGLE_PLAY_MARKET = "market://details?id=";

	// 帶入packageName開啟GooglePlay的頁面(打星等評論用)
	public static void goToGooglePlayDetailsPage(Activity activity, String packageName) {
		try {
			String url = "";
			if (!packageName.isEmpty()) {
				boolean googlePlayMarketExists = false;
				try {
					ApplicationInfo info = activity.getPackageManager().getApplicationInfo("com.android.vending", 0);
					if (info.packageName.equals("com.android.vending"))
						googlePlayMarketExists = true;
					else
						googlePlayMarketExists = false;
				} catch (PackageManager.NameNotFoundException e) {
					// application doesn't exist
					googlePlayMarketExists = false;
				}
				if (googlePlayMarketExists) {
					// Log.d(LOG_TAG, "Android Market Installed");
					url = GOOGLE_PLAY_MARKET + packageName;
				} else {
					// Log.d(LOG_TAG, "No Android Market");
					url = GOOGLE_PLAY_WEB_SITE + packageName;
				}
				Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				activity.startActivity(web);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static class SdCard {

		public final static String DEFAULT_INTERNAL_SD_CARD_PATH = Environment.getExternalStorageDirectory().getPath();

		static String[] commonInterSdCardPath = new String[] { "/sdcard", "/storage/sdcard0", "/storage/emulated/0", "/storage/emulated/legacy", };

		@SuppressLint("SdCardPath")
		public static boolean isInternalSdCardPath(String inputPath) {

			boolean isInterSdCard = false;
			for (String sdPath : commonInterSdCardPath) {
				if (inputPath.startsWith(sdPath)) {
					isInterSdCard = true;
				}
			}

			return isInterSdCard;
		}

		@SuppressLint("SdCardPath")
		public static String replaceDefaultSdCardPath(String inputPath) {

			String newPath = inputPath;
			for (String sdPath : commonInterSdCardPath) {
				newPath = newPath.replace(sdPath, DEFAULT_INTERNAL_SD_CARD_PATH);
			}

			return newPath;
		}
	}

	private final static double LON_LOW = 118;
	private final static double LON_HIGH = 124;
	private final static double LAT_LOW = 21;
	private final static double LAT_HIGH = 27;

	private final static double EARTH_RADIUS = 6378137.0;

	/**
	 * 計算兩個座標之間的距離(標準)
	 * 
	 * @param activity
	 * @param latSrc
	 *            "lat":25.079363,
	 * 
	 * @param lonSrc
	 *            "lon":121.578491
	 * @param latDst
	 * @param lonDst
	 * @return
	 */
	public static double calcDist(final Activity activity, double latSrc, double lonSrc, double latDst, double lonDst) {

		if (((LON_LOW < lonDst) && (lonDst < LON_HIGH)) || ((LAT_LOW < latDst) && (latDst < LAT_HIGH))) {
			// double radLat1 = (latSrc * Math.PI / 180.0);
			// double radLat2 = (latDst * Math.PI / 180.0);
			// double a = radLat1 - radLat2;
			// double b = (lonSrc - lonDst) * Math.PI / 180.0;
			// double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
			// Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2),
			// 2)));
			// s = s * EARTH_RADIUS;
			// s = Math.round(s * 10000) / 10000;
			float[] results = new float[1];
			Location.distanceBetween(latSrc, lonSrc, latDst, lonDst, results);
			double s = (double) results[0];
			if (s >= 0) {
				return s;
			}
		}
		return -1;
	}

	/**
	 * 計算兩個座標之間的直線距離 之前只有計算gps座標直接沒有換算成公里(*60*1.852海浬)
	 * 之前只有計算gps座標直接沒有換算成公里(=*111120)
	 * 
	 * @param latSrc
	 *            "lat":25.079363,
	 * 
	 * @param lonSrc
	 *            "lon":121.578491
	 * @param latDst
	 * @param lonDst
	 * @return
	 */
	public static double fastCalcDist(double latSrc, double lonSrc, double latDst, double lonDst) {
		double dist = Math.sqrt(Math.pow((latSrc - latDst), 2) + Math.pow((lonSrc - lonDst), 2)) * 111120;
		return Math.abs(dist);
	}

	/**
	 * 取得目前時間
	 * 
	 * @param dateFormat
	 *            dateFormat = "yyyy/MM/dd HH:mm:ss"
	 * @return
	 */
	public static String getNowTime(String dateFormat) {
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
		return formatter.format(curDate);
	}

	private static final String EMAIL_FORMAT = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	/** Email 格式檢查 */
	public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_FORMAT);

	/**
	 * 檢查該字串是否是EMAIL格式
	 * 
	 * @param context
	 * @param emailFormat
	 *            calvin@gmail.com
	 * @return true=是EMAIL格式 false=不是EMAIL格式
	 */
	public static boolean isEmailFormat(Context context, String emailFormat) {
		return EMAIL_PATTERN.matcher(emailFormat).matches();
	}

	// 判斷手機格式是否正確
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	// 判斷是否全是數字
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;

	}

	public static void showToast(Context context, String str) {
		if (checkStringNotEmpty(str)) {
			Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
		}
	}

	public static void showToast(Context context, int resId) {
		String message = context.getString(resId);
		showToast(context, message);
	}

	public final static String NULL = "null";

	/**
	 * Check if checkStringEmpty is empty
	 */
	public static boolean checkStringNotEmpty(String str) {
		if (str != null && str.length() > 0 && !str.equals(NULL)) {
			return true;
		}
		return false;
	}

	/**
	 * 若字串為空 則回傳""
	 * 
	 * @param str
	 * @return
	 */
	public static String adjustNull(String str) {
		if (checkStringNotEmpty(str)) {
			return str;
		} else {
			return "";
		}
	}

	/**
	 * 使用者回饋信件，開啟email程式並自動帶入字串
	 * 
	 * @param activity
	 */
	public static void sendEmailCustomerService(Activity activity, String name, String tel, String subject, String CUSTOMER_SERVICE, String feedBack) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		String text = String.format(feedBack, name, tel, "");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		String[] strEmailReciver = new String[] { CUSTOMER_SERVICE };
		intent.putExtra(Intent.EXTRA_EMAIL, strEmailReciver);
		activity.startActivity(Intent.createChooser(intent, "Send Email"));
	}

	/**
	 * 使用者回饋信件，開啟email程式並自動帶入字串
	 * 
	 * @param activity
	 */
	public static void sendEmailCustomerService(Activity activity, String name, String tel, String subject, String CUSTOMER_SERVICE) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		String text = String.format(activity.getString(R.string.feed_back_text), name, tel, "");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		String[] strEmailReciver = new String[] { CUSTOMER_SERVICE };
		intent.putExtra(Intent.EXTRA_EMAIL, strEmailReciver);
		activity.startActivity(Intent.createChooser(intent, "Send Email"));
	}

	/**
	 * ListView 下拉更新的 上次更新時間 文字轉換
	 * 
	 * @param activity
	 * @param currentTimeMillis
	 *            EX: long 1427963678335 / System.currentTimeMillis()
	 * @return 59秒內 →X秒前 59分59秒內 →X分前 23小時59分59秒內 →X小時前 大於等於24小時 小於48小時
	 *         →昨天+HH:MM 大於等於48小時 小於7天 →X天前 大於等於7天 小於4周 →X個星期前 大於等於4 小於6個月 →X個月前
	 *         大於等於6個月 →YYYY年MM月DD日 59秒內 →剛剛 59分59秒內 →X分前 23小時59分59秒內 →X小時前
	 *         大於等於24小時 小於等於7天 →X天前 大於7天 → 日期顯示(統一格視為 2014-08-06)
	 */
	public static String adjustCommentTimeFormat(Activity activity, long currentTimeMillis) {

		String commentTime = "";
		Date beginDate = new Date(currentTimeMillis);
		Date endDate = new Date();
		long seconds = 0;
		long days = 0;
		seconds = TimeUnit.MILLISECONDS.toSeconds(endDate.getTime() - beginDate.getTime());
		days = TimeUnit.MILLISECONDS.toDays(endDate.getTime() - beginDate.getTime());
		if (seconds < 0) {
			return commentTime;
		}
		if (seconds >= 0 && seconds < 60) {
			// 剛剛
			commentTime = activity.getResources().getString(R.string.just_ago);
		} else if (seconds >= 60 && seconds < 3600) {
			// 59分59秒內 →X分前
			commentTime = String.valueOf((seconds / 60)) + activity.getResources().getString(R.string.minute_ago);
		} else if (seconds >= 3600 && seconds < 86400) {
			// 23小時59分59秒內 →X小時前
			commentTime = String.valueOf((seconds / 3600)) + activity.getResources().getString(R.string.hour_ago);
		} else if (days >= 1 && days < 7) {
			// 大於等於48小時 小於7天 →X天前
			commentTime = String.valueOf(days) + activity.getResources().getString(R.string.days_ago);
		} else {
			// 直接顯示日期 2013-5-21
			SimpleDateFormat dateFormat = new SimpleDateFormat(activity.getResources().getString(R.string.date_format_dash));
			commentTime = dateFormat.format(beginDate);
		}
		return commentTime;
	}

	public static byte[] shortArrayToByteArray(short[] samples) {
		byte[] bytes = new byte[samples.length * 2];
		ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(samples);
		return bytes;
	}
}