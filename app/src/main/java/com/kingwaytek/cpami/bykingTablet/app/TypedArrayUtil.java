package com.kingwaytek.cpami.bykingTablet.app;

import android.content.res.TypedArray;

/**
 * Utility for fetching values from instance of TypedArray.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class TypedArrayUtil {

    public static int[] getResourceIds(TypedArray arr) {
        int len = arr.length();
        int[] resIds = new int[len];

        for (int i = 0; i < len; i++) {
            resIds[i] = arr.getResourceId(i, 0);
        }

        arr.recycle();

        return resIds;
    }
}
