package com.kingwaytek.cpami.bykingTablet.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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

import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public static boolean hasNoDuplicateInList(ArrayList<Integer> indexList, int position) {
        for (int index : indexList) {
            if (index == position)
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
        Pattern pattern = Pattern.compile("\\d+");
        Matcher isNum = pattern.matcher(str);
        Log.i("Utility", "isNumeric - String: " + str + " " + isNum.matches());
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


    public static int getActionbarHeight() {
        Context mContext = AppController.getInstance().getAppContext();

        TypedValue tv = new TypedValue();

        int actionBarHeight = 0;
        int statusBarHeight = 0;

        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().getDisplayMetrics());

        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0)
            statusBarHeight = mContext.getResources().getDimensionPixelSize(resourceId);

        Log.i("ActionBarHeight", "ActionBarHeight: " + actionBarHeight + " StatusBarHeight: " + statusBarHeight);

        return actionBarHeight + statusBarHeight;
    }

    public static String getShorterHardwareID() {
        String hardwareId;

        hardwareId = getWifiMac();

        if (hardwareId == null)
            hardwareId = getDeviceIMEI();

        return getShorterId(hardwareId);
    }

    private static String getWifiMac() {
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

    private static String getDeviceIMEI() {
        String imei = null;
        try {
            TelephonyManager telMgr = (TelephonyManager) AppController.getInstance().getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
            imei = telMgr.getDeviceId();
        }
        catch (NullPointerException | SecurityException e) {
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

    static boolean isFileNotExists(String filePath) {
        File file = new File(filePath);
        return !file.exists();
    }

    public static double getDistance(LatLng from, LatLng to) {
        double redLat1 = from.latitude * Math.PI / 180;
        double redLat2 = to.latitude * Math.PI / 180;
        double l = redLat1 - redLat2;
        double p = (from.longitude * Math.PI / 180) - (to.longitude * Math.PI / 180);

        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(l / 2), 2)
                + Math.cos(redLat1) * Math.cos(redLat2)
                * Math.pow(Math.sin(p / 2), 2)));

        distance = distance * 6378137.0;
        distance = Math.round(distance * 10000) / 10000;

        return distance;
    }

    public static String getDistanceText(double distance) {
        if (distance < 1000)
            return AppController.getInstance().getString(R.string.track_distance_meters, String.valueOf((int) distance));
        else
            return AppController.getInstance().getString(R.string.track_distance_kilometers, new DecimalFormat("#.00").format(distance / 1000));
    }

    public static String getDurationText(long duration) {
        double seconds =  (duration / 1000);
        double minutes;

        if (seconds < 60) {
            return AppController.getInstance().getString(R.string.track_duration_text_seconds, (int) seconds);
        }
        else {
            minutes = seconds / 60;

            if (minutes < 60) {
                seconds = seconds % 60;
                return AppController.getInstance().getString(R.string.track_duration_text, (int) minutes, (int) seconds);
            }
            else {
                int hours = (int) (minutes / 60);
                int minutesOfTheHour = (int) (minutes % 60);
                return AppController.getInstance().getString(R.string.track_duration_text_hours, hours, minutesOfTheHour);
            }
        }
    }

    public static String getAverageSpeedText(double distance, long duration) {
        double minutes = (duration / 1000) / 60;
        double kmPerHour = (distance / (minutes / 60)) / 1000;

        //return new DecimalFormat("#.00").format(kmPerHour) + " km/h";
        return AppController.getInstance().getString(R.string.track_average_speed_per_hour, new DecimalFormat("#.00").format(kmPerHour));
    }

    public static String getCurrentTimeInFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.TAIWAN);
        return dateFormat.format(new Date());
    }

    public static int getRoundMinutes(int seconds) {
        return (int) Math.round(((double) seconds) / 60);
    }
}
