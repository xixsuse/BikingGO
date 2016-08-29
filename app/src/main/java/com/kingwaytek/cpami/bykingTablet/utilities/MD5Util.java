package com.kingwaytek.cpami.bykingTablet.utilities;

import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

/**
 * Utility of Generate MD5 code.
 *
 * Rewritten by Vincent on 2016/07/07.
 */
public class MD5Util {

    private static final String TAG = "MD5Util";

    private static Calendar calendar = Calendar.getInstance(Locale.TAIWAN);

    public static final int SERVICE_NUMBER_REPORT = 1208;
    public static final int SERVICE_NUMBER_EVENTS = 1303;

    private static String createMD5(byte[] source) {
        String s = null;

        // 用來將字節轉換成 16 進製表示的字符
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',  'E', 'F'};

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance( "MD5" );
            md.update( source );

            // MD5 的計算結果是一個 128 位的長整數，
            byte tmp[] = md.digest();

            // 用字節表示就是 16 個字節
            char str[] = new char[16 * 2];   // 每個字節用 16 進製表示的話，使用兩個字符
            // 所以表示成 16 進制需要 32 個字符

            // 表示轉換結果中對應的字符位置
            int k = 0;
            for (int i = 0; i < 16; i++) {          // 從第一個字節開始，對 MD5 的每一個字節
                // 轉換成 16 進制字符的轉換
                byte byte0 = tmp[i];                 // 取第 i 個字節

                str[k++] = hexDigits[byte0 >>> 4 & 0xf];  // 取字節中高 4 位的數字轉換,
                // >>> 為邏輯右移，將符號位一起右移
                str[k++] = hexDigits[byte0 & 0xf];        // 取字節中低 4 位的數字轉換
            }
            s = new String(str);    // 換後的結果轉換為字符串
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getMD5Code(int serviceNumber) {
        Log.i(TAG, "Month: " + calendar.get(Calendar.MONTH) + " Hours: " + calendar.get(Calendar.HOUR_OF_DAY) + " Date: " + calendar.get(Calendar.DATE));

        return createMD5(
                (String.valueOf(
                        ((calendar.get(Calendar.MONTH) + 1) + calendar.get(Calendar.HOUR_OF_DAY)) * (serviceNumber + calendar.get(Calendar.DATE))
                ) + "Kingway").getBytes()
        );
    }
}
