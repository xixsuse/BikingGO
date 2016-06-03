package com.kingwaytek.cpami.bykingTablet.app.model;

import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlans;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsSearchResult;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * 獲取與暫存 array變數的方法都統一放在這裡，<h1>
 *
 * 主要與 WebAgent.getStringByUrl(...) & JsonParser搭配一起服用，<br>
 *
 * static的 ArrayList需設為 SoftReference or WeakReference避免一直佔用記憶體。
 *
 * @author Vincent (2016/4/18)
 */
public class DataArray implements ApiUrls {

    public static SoftReference<ArrayList<ItemsSearchResult>> list_searchResult;
    public static SoftReference<ArrayList<ItemsPlans>> list_plans;

    public interface OnDataGetCallBack {
        void onDataGet();
    }

    public static void getLocationSearchResult(final String locationName, final OnDataGetCallBack dataGet) {
        String apiUrl = MessageFormat.format(API_GOOGLE_GEOCODE, locationName, Utility.getLocaleLanguage());
        Log.i("GoogleGeo", apiUrl);

        WebAgent.getStringByUrl(apiUrl, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                JsonParser.parseGoogleGeocodeThenAddToList(response, new JsonParser.JSONParseResult() {
                    @Override
                    public void onParseFinished() {
                        dataGet.onDataGet();
                    }

                    @Override
                    public void onParseFail(String errorMessage) {
                        Log.e("Geocode_ParseError", errorMessage);
                    }
                });
            }

            @Override
            public void onResultFail(String errorMessage) {
                Log.e("Geocode_WebError", errorMessage);
            }
        });
    }

    public static ArrayList<ItemsMyPOI> getMyPOI() {
        return JsonParser.parseMyPoiAndGetList();
    }

    public static ArrayList<String> getPlanNameList() {
        return JsonParser.getMyPlanNameList();
    }

    public static void getPlansData(final OnDataGetCallBack dataGet) {

    }
}
