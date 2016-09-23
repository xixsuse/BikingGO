package com.kingwaytek.cpami.bykingTablet.app.model;

import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsEvents;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathList;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlans;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsSearchResult;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsShared;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsYouBike;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.TrackingFileUtil;
import com.kingwaytek.cpami.bykingTablet.utilities.Util;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;

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

    private static final String TAG = "DataArray";

    public static SoftReference<ArrayList<ItemsSearchResult>> list_searchResult;
    public static SoftReference<ArrayList<ItemsPathList>> list_pathList;
    public static SoftReference<ArrayList<ItemsEvents>> list_events;

    public interface OnDataGetCallBack {
        void onDataGet();
    }

    public interface OnYouBikeDataGetCallback {
        void onTaipeiYouBikeGet(ArrayList<ItemsYouBike> uBikeItems);
        void onNewTaipeiYouBikeGet(ArrayList<ItemsYouBike> uBikeItems);
        void onDataGetFailed();
    }

    public interface OnAllYouBikeDataGetCallback {
        void onAllYouBikeGet(ArrayList<ItemsYouBike> uBikeItems);
        void onDataGetFailed();
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
                        DialogHelper.dismissDialog();
                        Log.e(TAG, "Geocode_ParseError: " + errorMessage);
                    }
                });
            }

            @Override
            public void onResultFail(String errorMessage) {
                DialogHelper.dismissDialog();
                Log.e(TAG, "Geocode_WebError: " + errorMessage);
            }
        });
    }

    public static ArrayList<ItemsMyPOI> getMyPOI() {
        return JsonParser.parseMyPoiAndGetList();
    }

    public static ArrayList<String[]> getPlanNameAndDateList() {
        return JsonParser.getMyPlanNameAndDateList();
    }

    public static ArrayList<ItemsPlans> getPlansData() {
        return JsonParser.getPlansData();
    }

    /**
     * @return JSONObject's string of the plan
     */
    public static String getPlanObjectString(int index) {
        try {
            JSONArray ja = new JSONArray(Util.readPlanFile());

            return ja.getJSONObject(index).toString();
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return JSONObject's string of the track
     */
    public static String getTrackObjectString(int index) {
        try {
            JSONArray ja = new JSONArray(TrackingFileUtil.readTrackFile());

            return ja.getJSONObject(index).toString();
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<ItemsShared> getSharedList(String jsonString) {
        return JsonParser.parseAndGetSharedItems(jsonString);
    }

    /**
     * @param jsonString Whole JSON String returned from Google Directions.
     * @param namePairList 每一段 steps的起點和終點，內容為：<br>
     *                     String[]{使用者自訂景點1, 使用者自定景點2}，<br>
     *                     將對應為 String[]{ItemsPathList.START_NAME, ItemsPathList.END_NAME}，<br>
     *                     size = (ArrayList<'ItemsPlanItem'>.size() - 1)
     */
    public static void getDirectionPathListData(String jsonString, ArrayList<String[]> namePairList, final OnDataGetCallBack dataGet) {
        JsonParser.parseMultiPointsDirectionData(jsonString, namePairList, new JsonParser.JSONParseResult() {
            @Override
            public void onParseFinished() {
                dataGet.onDataGet();
            }

            @Override
            public void onParseFail(String errorMessage) {
                Utility.toastLong("ParseError: " + errorMessage);
                Log.e(TAG, "Direction_ParseError: " + errorMessage);
            }
        });
    }

    public static void checkAndGetEventsData(final OnDataGetCallBack dataGet) {
        if (list_events == null || list_events.get() == null || list_events.get().isEmpty()) {

            //String apiUrl = MessageFormat.format(API_EVENTS, MD5Util.getMD5Code(MD5Util.SERVICE_NUMBER_EVENTS));

            WebAgent.getStringByUrl(API_EVENTS, new WebAgent.WebResultImplement() {
                @Override
                public void onResultSucceed(String response) {
                    JsonParser.parseEventsDataThenAddToList(response, new JsonParser.JSONParseResult() {
                        @Override
                        public void onParseFinished() {
                            dataGet.onDataGet();
                        }

                        @Override
                        public void onParseFail(String errorMessage) {
                            DialogHelper.dismissDialog();
                            Log.e(TAG, "Events_parseError: " + errorMessage);
                        }
                    });
                }

                @Override
                public void onResultFail(String errorMessage) {
                    DialogHelper.dismissDialog();
                    Log.e(TAG, "Events_webError: " + errorMessage);
                }
            });
        }
        else
            dataGet.onDataGet();
    }

    public static void getYouBikeData(final OnYouBikeDataGetCallback youBikeDataGetCallback) {
        WebAgent.downloadTaipeiYouBikeData(new WebAgent.FileDownloadCallback() {
            @Override
            public void onDownloadFinished() {
                Log.i(TAG, "YouBikeTP download completed!");

                JsonParser.parseTaipeiYouBikeData(new JsonParser.YouBikeParseResult() {
                    @Override
                    public void onParseFinished(ArrayList<ItemsYouBike> uBikeItems) {
                        youBikeDataGetCallback.onTaipeiYouBikeGet(uBikeItems);
                    }

                    @Override
                    public void onParseFail(String errorMessage) {
                        Log.e(TAG, errorMessage);
                        youBikeDataGetCallback.onDataGetFailed();
                    }
                });
            }

            @Override
            public void onDownloadFailed(String errorMessage) {
                Log.e(TAG, errorMessage);
                youBikeDataGetCallback.onDataGetFailed();
            }
        });

        WebAgent.getStringByUrl(API_UBIKE_NEW_TAIPEI, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                Log.i(TAG, "YouBikeNewTP Get!!!");

                JsonParser.parseYouBikeData(response, new JsonParser.YouBikeParseResult() {
                    @Override
                    public void onParseFinished(ArrayList<ItemsYouBike> uBikeItems) {
                        youBikeDataGetCallback.onNewTaipeiYouBikeGet(uBikeItems);
                    }

                    @Override
                    public void onParseFail(String errorMessage) {
                        Log.e(TAG, "NewTaipeiYouBikeParseError: " + errorMessage);
                        youBikeDataGetCallback.onDataGetFailed();
                    }
                });
            }

            @Override
            public void onResultFail(String errorMessage) {
                Log.e(TAG, "NewTaipeiYouBikeWebError: " + errorMessage);
                youBikeDataGetCallback.onDataGetFailed();
            }
        });
    }

    public static void getAllYouBikeData(final OnAllYouBikeDataGetCallback dataGetCallback) {
        WebAgent.getStringByUrl(API_UBIKE_ALL, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                Log.i(TAG, "AllYouBike Get!!!");

                JsonParser.parseYouBikeData(response, new JsonParser.YouBikeParseResult() {
                    @Override
                    public void onParseFinished(ArrayList<ItemsYouBike> uBikeItems) {
                        dataGetCallback.onAllYouBikeGet(uBikeItems);
                    }

                    @Override
                    public void onParseFail(String errorMessage) {
                        Log.e(TAG, "AllYouBikeParseError: " + errorMessage);
                        dataGetCallback.onDataGetFailed();
                    }
                });
            }

            @Override
            public void onResultFail(String errorMessage) {
                Log.e(TAG, "AllYouBikeWebError: " + errorMessage);
                dataGetCallback.onDataGetFailed();
            }
        });
    }
}
