package com.kingwaytek.cpami.bykingTablet.utilities;

import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.ItemsSearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/4/18.
 */
public class JsonParser {

    private static final String TAG = "JsonParser";

    static JSONObject JO;
    static JSONArray JA;

    public interface JSONParseResult {
        void onItemGet();
        void onParseFail(String errorMessage);
    }

    private static void releaseObjects() {
        JO = null;
        JA = null;
    }

    public static void parseGoogleGeocodeThenAddToList(String jsonString, JSONParseResult parseResult) {
        try {
            ArrayList<ItemsSearchResult> resultList = new ArrayList<>();

            String name;
            String adminArea = "";
            String countryName = "";
            String lat;
            String lng;

            JO = new JSONObject(jsonString);
            JA = JO.getJSONArray("results");

            JSONObject result;
            JSONObject location;

            for (int i = 0; i < JA.length(); i++) {
                result = JA.getJSONObject(i);
                location = result.getJSONObject("geometry").getJSONObject("location");

                name = result.getString("formatted_address");
                lat = location.getString("lat");
                lng = location.getString("lng");

                resultList.add(new ItemsSearchResult(name, adminArea, countryName, Double.parseDouble(lat), Double.parseDouble(lng)));
            }
            if (DataArray.list_searchResult != null)
                DataArray.list_searchResult.clear();
            DataArray.list_searchResult = new SoftReference<>(resultList);

            parseResult.onItemGet();
        }
        catch (JSONException e) {
            e.printStackTrace();
            parseResult.onParseFail(e.getMessage());
        }
        releaseObjects();
    }
}
