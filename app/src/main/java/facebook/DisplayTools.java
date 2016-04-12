package facebook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class DisplayTools {

	private final static String TAG = "DisplayTools";
	private final static String NULL_EXCEPTION = "Null Exception";

	/** Prompt **/
	private static final String DIALOG_BTN = "OK";
	private static final String NO_CONNECTION_TITLE = "No Connection";
	private static final String NO_CONNECTION_MESSAGE = "Please try it later";

	/** Device Display **/
	public static final String SCREEN_WIDTH = "SCREEN_WIDTH";
	public static final String SCREEN_HEIGHT = "SCREEN_HEIGHT";
	public static final String DEVICE_INCH = "DEVICE_INCH";
	public static final String DEVICE_DENSITY = "DEVICE_DENSITY";

	/** Toast **/
	public static void showToast(Context context, int textResource) {
		Toast.makeText(context, textResource, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, String textResource) {
		Toast.makeText(context, textResource, Toast.LENGTH_SHORT).show();
	}

	/** Dialog **/
	public static void showSimpleDialog(Context context, int title, int message) {

		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton(DIALOG_BTN, null).show();
	}

	public static void showSimpleDialog(Context context, String title,
			String message) {

		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton(DIALOG_BTN, null).show();
	}

	public static void showSimpleDialog(Context context, int title,
			String message) {

		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton(DIALOG_BTN, null).show();
	}

	public static void showSimpleDialog(Context context, String title,
			int message) {

		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton(DIALOG_BTN, null).show();
	}

	public static void showNoConnettionDialog(Context context) {
		showSimpleDialog(context, NO_CONNECTION_TITLE, NO_CONNECTION_MESSAGE);
	}

	/** Use to judge is tablet or not **/
	public static boolean isTablet(Context context) {

		boolean isTablet = false;

		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		int type = telephony.getPhoneType();

		if (type == TelephonyManager.PHONE_TYPE_NONE) {
			isTablet = true;
		}

		return isTablet;
	}

	/** Screen Width and Screen height **/
	@SuppressLint("NewApi")
	public static DeviceDisplayObject getDeviceDisplayInfomation(
			Activity activity) {

		DeviceDisplayObject object = new DeviceDisplayObject();

		int screenWidth = 0;
		int screenHeight = 0;
		double deviceInches = 0;
		int deviceDensity = 0;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		/* Density */
		deviceDensity = metrics.densityDpi;

		/* Screen size */
		if (Build.VERSION.SDK_INT >= 11) {

			Point size = new Point();

			try {

				activity.getWindowManager().getDefaultDisplay()
						.getRealSize(size);
				screenWidth = size.x;
				screenHeight = size.y;

			} catch (NoSuchMethodError e) {
				Log.i(TAG, printException(e));
			}

		} else {

			screenWidth = metrics.widthPixels;
			screenHeight = metrics.heightPixels;
		}

		/* Inches */
		deviceInches = Math.sqrt(screenWidth + screenHeight);

		object.setWidth(screenWidth);
		object.setHeight(screenHeight);
		object.setInch(deviceInches);
		object.setDensity(deviceDensity);

		return object;
	}

	/** Use to Print Error **/
	private static String printException(Error e) {
		String exception = NULL_EXCEPTION;

		if (e != null) {
			exception = e.getMessage();
		}

		return exception;
	}

}
