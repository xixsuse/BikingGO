package com.kingwaytek.cpami.bykingTablet.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.widget.Toast;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vincent.chang on 2015/12/22.
 */
public class Utility {

    public static void forceCloseTask() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void toastShort(String msg) {
        Toast.makeText(AppController.getInstance().getAppContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String msg) {
        Toast.makeText(AppController.getInstance().getAppContext(), msg, Toast.LENGTH_LONG).show();
    }

    public static void logLongInfo(String tagName, String str) {
        if(str.length() > 4000) {
            Log.i(tagName, str.substring(0, 4000));
            logLongInfo(tagName, str.substring(4000));
        }
        else
            Log.i(tagName, str);
    }

    public static boolean hasNoDuplicateInList(ArrayList<String> itemList, String itemName) {
        for (String item : itemList) {
            if (item.equals(itemName))
                return false;
        }
        return true;
    }

    public static Point getScreenInfo(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    public static int getScreenWidth() {
        DisplayMetrics dm = AppController.getInstance().getAppContext().getResources().getDisplayMetrics();

        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics dm = AppController.getInstance().getAppContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static String getLocaleLanguage() {
        Locale locale = Locale.getDefault();
        return String.format("%s-%s", locale.getLanguage(), locale.getCountry());
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static int getNumericCount(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (isNumeric(String.valueOf(str.charAt(i))))
                count++;
        }
        return count;
    }

    public static boolean isDivisibleByTwo(int position, int totalCount) {
        int totalTarget = totalCount / 2;
        for (int i = 1; i < totalTarget; i++) {
            if (position + 1 == 2 * i)
                return true;
        }
        return false;
    }

    public static boolean isDivisibleByTwo(int position) {
        return position % 2 == 0;
    }

    public static int getPixels(int dipValue) {
        Resources res = Resources.getSystem();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, res.getDisplayMetrics());
    }

    /**
     * FragmentHeight = ScreenHeight - height of (TabLayout + Toolbar + StatusBar)
     */
    public static int getFragmentHeight() {
        Context mContext = AppController.getInstance().getAppContext();

        int designWidgetHeight = mContext.getResources().getDimensionPixelSize(R.dimen.actionbar_height_more_higher) +
                mContext.getResources().getDimensionPixelSize(R.dimen.actionbar_height);

        int statusBarHeight = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0)
            statusBarHeight = mContext.getResources().getDimensionPixelSize(resourceId);

        Log.i("StatusBarHeight", "" + statusBarHeight);

        return getScreenHeight() - (designWidgetHeight + statusBarHeight);
    }

    public static BitmapFactory.Options getBitmapOptions(int scale) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = scale;
        return options;
    }

    public static String getShorterHardwareID() {
        String hardwareId;

        hardwareId = getWifiMac();

        if (hardwareId == null)
            hardwareId = getDeviceIMEI();

        return getShorterId(hardwareId);
    }

    public static String getWifiMac() {
        String wifiMac = "";
        try {
            WifiManager wifiMgr = (WifiManager) AppController.getInstance().getAppContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            wifiMac = wifiInfo.getMacAddress();

            if (wifiMac == null) {
                return null;
            }
            else if (getZeroCount(wifiMac) >= 14) {
                return null;
            }
            else {
                wifiMac = wifiMac.replaceAll(":", "");
                while (wifiMac.length() < 14) {
                    wifiMac += "0";
                }
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        return wifiMac;
    }

    public static String getDeviceIMEI() {
        String imei = null;
        try {
            TelephonyManager telMgr = (TelephonyManager) AppController.getInstance().getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
            imei = telMgr.getDeviceId();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        return imei;
    }

    private static int getZeroCount(String wifiMac) {
        int zeroCount = 0;
        for (int i = 0; i < wifiMac.length(); i++) {
            if (String.valueOf(wifiMac.charAt(i)).equals("0") ||
                    String.valueOf(wifiMac.charAt(i)).equals("f"))
                zeroCount++;
        }
        return zeroCount;
    }

    private static String getShorterId(String hardwareId) {
        String result = "";
        try {
            int len = hardwareId.length();
            int start = len - 10;
            int end = len;
            result = hardwareId.substring(start, end);
        }
        catch(ArrayIndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isNetworkEnabled() {
        ConnectivityManager cm = (ConnectivityManager) AppController.getInstance().getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        return ni != null && ni.isAvailable();
    }

    public static void showToastOnNewThread(final String msg) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                new Utility.ToastHandler().obtainMessage(0, msg).sendToTarget();
                Looper.loop();
            }
        }.start();
    }

    private static class ToastHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;

            toastShort(message);
        }
    }
}
