package com.kingwaytek.cpami.bykingTablet.utilities;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsEvents;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsGeoLines;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathList;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathStep;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanItem;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlans;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsSearchResult;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTrackRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Parse JSON 並建立 Items，搭配 DataArray使用！
 *
 * @author Vincent (2016/4/18)
 */
public class JsonParser {

    private static final String TAG = "JsonParser";

    private static JSONObject JO;
    private static JSONArray JA;

    public interface JSONParseResult {
        void onParseFinished();
        void onParseFail(String errorMessage);
    }

    public interface GeoJsonParseResult {
        void onParseFinished(ArrayList<ItemsGeoLines> geoLines);
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
            double lat;
            double lng;

            JO = new JSONObject(jsonString);
            JA = JO.getJSONArray("results");

            JSONObject result;
            JSONObject location;

            for (int i = 0; i < JA.length(); i++) {
                result = JA.getJSONObject(i);
                location = result.getJSONObject("geometry").getJSONObject("location");

                name = result.getString("formatted_address");
                lat = location.getDouble("lat");
                lng = location.getDouble("lng");

                resultList.add(new ItemsSearchResult(name, adminArea, countryName, name, lat, lng));
            }
            if (DataArray.list_searchResult != null)
                DataArray.list_searchResult.clear();
            DataArray.list_searchResult = new SoftReference<>(resultList);

            parseResult.onParseFinished();
        }
        catch (JSONException e) {
            e.printStackTrace();
            parseResult.onParseFail(e.getMessage());
        }
        releaseObjects();
    }

    public static ArrayList<ItemsMyPOI> parseMyPoiAndGetList() {
        try {
            ArrayList<ItemsMyPOI> myPoiList = new ArrayList<>();

            JA = new JSONArray(Util.readPoiFile());

            String title;
            String address;
            String desc;
            double lat;
            double lng;
            String photoPath;

            JSONObject jo;

            for (int i = 0; i < JA.length(); i++) {
                jo = JA.getJSONObject(i);
                title = jo.getString(FavoriteHelper.POI_TITLE);
                address = jo.getString(FavoriteHelper.POI_ADDRESS);
                desc = jo.getString(FavoriteHelper.POI_DESCRIPTION);
                lat = jo.getDouble(FavoriteHelper.POI_LAT);
                lng = jo.getDouble(FavoriteHelper.POI_LNG);
                photoPath = jo.getString(FavoriteHelper.POI_PHOTO_PATH);

                myPoiList.add(new ItemsMyPOI(title, address, desc, lat, lng, photoPath));
            }
            releaseObjects();
            return myPoiList;
        }
        catch(JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static String getPolyLineOverview(String jsonString) {
        try {
            JO = new JSONObject(jsonString);
            JA = JO.getJSONArray("routes");
            String status = JO.getString("status");

            if (status.equals("OK")) {
                String polyLine = JA.getJSONObject(0).getJSONObject("overview_polyline").getString("points");

                if (!polyLine.isEmpty()) {
                    releaseObjects();
                    return polyLine;
                }
            }
            else if (status.equals("REQUEST_DENIED")) {
                Utility.toastLong(JO.getString("error_message"));
                Log.e(TAG, JO.getString("error_message"));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "getPolyLineError!! " + e.getMessage());
        }
        releaseObjects();
        return null;
    }

    public static ArrayList<String> getMyPlanNameList() {
        try {
            if (Util.isPlanFileNotExistOrEmpty())
                return null;

            JA = new JSONArray(Util.readPlanFile());

            ArrayList<String> planNameList = new ArrayList<>();
            String planName;

            for (int i = 0; i < JA.length(); i++) {
                planName = JA.getJSONObject(i).getString(FavoriteHelper.PLAN_NAME);
                planNameList.add(planName);
            }

            releaseObjects();
            return planNameList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static ArrayList<ItemsPlans> getPlansData() {
        try {
            if (Util.isPlanFileNotExistOrEmpty())
                return null;

            JA = new JSONArray(Util.readPlanFile());

            ArrayList<ItemsPlans> plansList = new ArrayList<>();

            JSONArray ja;
            JSONObject jo;

            String planName;
            ArrayList<ItemsPlanItem> planItems;

            String title;
            double lat;
            double lng;

            for (int i = 0; i < JA.length(); i++) {
                JO = JA.getJSONObject(i);
                planName = JO.getString(FavoriteHelper.PLAN_NAME);

                ja = JO.getJSONArray(FavoriteHelper.PLAN_ITEMS);

                planItems = new ArrayList<>();

                for (int j = 0; j < ja.length(); j++) {
                    jo = ja.getJSONObject(j);

                    title = jo.getString(FavoriteHelper.POI_TITLE);
                    lat = jo.getDouble(FavoriteHelper.POI_LAT);
                    lng = jo.getDouble(FavoriteHelper.POI_LNG);

                    planItems.add(new ItemsPlanItem(title, lat, lng));
                }

                plansList.add(new ItemsPlans(planName, planItems));

                Log.i(TAG, planName + " planItems size: " + planItems.size());
            }

            releaseObjects();
            return plansList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static ArrayList<ItemsPathStep> parseAnGetDirectionItems(String jsonString) {
        try {
            ArrayList<ItemsPathStep> dirItemList = new ArrayList<>();

            String distance;
            String duration;
            String[] instructionAndGoOnPath;
            double startLat;
            double startLng;
            double endLat;
            double endLng;
            String polyLine;

            JO = new JSONObject(jsonString);
            JA = JO.getJSONArray("routes");
            JSONObject route = JA.getJSONObject(0);
            JSONArray stepArr = route.getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

            for (int i = 0; i < stepArr.length(); i++) {
                JSONObject singleStep = stepArr.getJSONObject(i);

                distance = getDistanceString(singleStep.getJSONObject("distance"));
                duration = singleStep.getJSONObject("duration").getString("text");
                instructionAndGoOnPath = getInstructionPath(singleStep.getString("html_instructions"));
                polyLine = singleStep.getJSONObject("polyline").getString("points");
                startLat = singleStep.getJSONObject("start_location").getDouble("lat");
                startLng = singleStep.getJSONObject("start_location").getDouble("lng");
                endLat = singleStep.getJSONObject("end_location").getDouble("lat");
                endLng = singleStep.getJSONObject("end_location").getDouble("lng");

                dirItemList.add(new ItemsPathStep(distance, duration, instructionAndGoOnPath[0], instructionAndGoOnPath[1],
                        polyLine, startLat, startLng, endLat, endLng));
            }

            releaseObjects();

            return dirItemList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static void parseMultiPointsDirectionData(String jsonString, ArrayList<String[]> namePairList, JSONParseResult parseResult) {
        ArrayList<ItemsPathList> pathList = new ArrayList<>();
        ArrayList<ItemsPathStep> stepList;

        try {
            JO = new JSONObject(jsonString);
            JSONObject jo_routes = JO.getJSONArray("routes").getJSONObject(0);

            String status = JO.getString("status");

            if (status.equals("OK"))
            {
                JA = jo_routes.getJSONArray("legs");

                String distance;
                String duration;
                String startName;
                double startLat;
                double startLng;
                String endName;
                double endLat;
                double endLng;

                String step_distance;
                String[] instructionAndGoOnPath;
                String step_polyLine;
                double step_startLat;
                double step_startLng;
                double step_endLat;
                double step_endLng;

                //String polyLineOverview;

                for (int i = 0; i < JA.length(); i++) {
                    JSONObject jo_leg = JA.getJSONObject(i);

                    distance = getDistanceString(jo_leg.getJSONObject("distance"));
                    duration = jo_leg.getJSONObject("duration").getString("text");
                    startName = namePairList.get(i)[0];
                    startLat = jo_leg.getJSONObject("start_location").getDouble("lat");
                    startLng = jo_leg.getJSONObject("start_location").getDouble("lng");
                    endName = namePairList.get(i)[1];
                    endLat = jo_leg.getJSONObject("end_location").getDouble("lat");
                    endLng = jo_leg.getJSONObject("end_location").getDouble("lng");

                    JSONArray ja_steps = jo_leg.getJSONArray("steps");

                    stepList = new ArrayList<>();

                    for (int j = 0; j < ja_steps.length(); j++) {
                        JSONObject singleStep = ja_steps.getJSONObject(j);

                        step_distance = getDistanceString(singleStep.getJSONObject("distance"));
                        instructionAndGoOnPath = getInstructionPath(singleStep.getString("html_instructions"));
                        step_polyLine = singleStep.getJSONObject("polyline").getString("points");
                        step_startLat = singleStep.getJSONObject("start_location").getDouble("lat");
                        step_startLng = singleStep.getJSONObject("start_location").getDouble("lng");
                        step_endLat = singleStep.getJSONObject("end_location").getDouble("lat");
                        step_endLng = singleStep.getJSONObject("end_location").getDouble("lng");

                        stepList.add(new ItemsPathStep(step_distance, instructionAndGoOnPath[0], instructionAndGoOnPath[1],
                                step_polyLine, step_startLat, step_startLng, step_endLat, step_endLng));
                    }

                    pathList.add(new ItemsPathList(distance, duration, startName, startLat, startLng, endName, endLat, endLng, stepList));
                }

                //polyLineOverview = jo_routes.getJSONObject("overview_polyline").getString("points");

                if (DataArray.list_pathList != null)
                    DataArray.list_pathList.clear();
                DataArray.list_pathList = new SoftReference<>(pathList);

                parseResult.onParseFinished();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            parseResult.onParseFail(e.getMessage());
        }
        releaseObjects();
    }

    private static String getDistanceString(JSONObject jo_distance) {
        try {
            int value = Integer.parseInt(jo_distance.getString("value"));

            if (value < 1000)
                return String.valueOf(value) + " 公尺";
            else
                return jo_distance.getString("text");
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param originPath = singleStep.getString("html_instructions")
     * @return String[] {instructions, goOnPath}
     */
    private static String[] getInstructionPath(String originPath) {
        String instruction;
        String goOnPath = "";

        if (originPath.contains("<div")) {
            instruction = originPath.substring(0, originPath.indexOf("<div"));
            goOnPath = originPath.substring(originPath.indexOf("<div"), originPath.length());
            goOnPath = goOnPath.replace("div", "pre");

            Log.i(TAG, "Origin: " + instruction + " goOnPath: " + goOnPath);
        }
        else
            instruction = originPath;

        return new String[]{instruction, goOnPath};
    }

    public static void parseEventsDataThenAddToList(String jsonString, JSONParseResult parseResult) {
        ArrayList<ItemsEvents> eventList = new ArrayList<>();

        try {
            JA = new JSONArray(jsonString);

            String name;
            String description;
            String location;
            String address;
            String organization;
            String startTime;
            String endTime;
            String website;
            String pic1Url;
            String pic1Name;
            String pic2Url;
            String pic2Name;
            String pic3Url;
            String pic3Name;
            double lat;
            double lng;
            String travelInfo;
            String parkingInfo;

            for (int i = 0; i < JA.length(); i++) {
                JO = JA.getJSONObject(i);

                name = JO.getString("name");
                description = JO.getString("description");
                location = JO.getString("location");
                address = JO.getString("add");
                organization = JO.getString("org");
                startTime = JO.getString("start");
                endTime = JO.getString("end");
                website = JO.getString("website");
                pic1Url = JO.getString("picture1");
                pic1Name = JO.getString("picdescribe1");
                pic2Url = JO.getString("picture2");
                pic2Name = JO.getString("picdescribe2");
                pic3Url = JO.getString("picture3");
                pic3Name = JO.getString("picdescribe3");
                lat = JO.getDouble("py");
                lng = JO.getDouble("px");
                travelInfo = JO.getString("travellinginfo");
                parkingInfo = JO.getString("parkinginfo");

                eventList.add((new ItemsEvents(name, description, location, address, organization, startTime, endTime, website,
                        pic1Url, pic1Name, pic2Url, pic2Name, pic3Url, pic3Name, lat, lng, travelInfo, parkingInfo)));
            }

            if (DataArray.list_events != null)
                DataArray.list_events.clear();
            DataArray.list_events = new SoftReference<>(eventList);

            parseResult.onParseFinished();
        }
        catch (JSONException e) {
            e.printStackTrace();
            parseResult.onParseFinished();
        }
        releaseObjects();
    }

    public static ArrayList<ItemsTrackRecord> getTrackList() {
        try {
            String trackJsonString = TrackingFileUtil.readTrackFile();

            if (trackJsonString != null) {
                JA = new JSONArray(trackJsonString);

                ArrayList<ItemsTrackRecord> trackNameList = new ArrayList<>();

                for (int i = 0; i < JA.length(); i++) {
                    JO = JA.getJSONObject(i);
                    trackNameList.add(new ItemsTrackRecord(
                            JO.getString(FavoriteHelper.TRACK_TIME),
                            JO.getString(FavoriteHelper.TRACK_NAME),
                            JO.getInt(FavoriteHelper.TRACK_DIFFICULTY),
                            JO.getString(FavoriteHelper.TRACK_DISTANCE)));
                }
                releaseObjects();

                return trackNameList;
            }
            else
                return null;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static ItemsTrackRecord getTrackRecord(int index) {
        try {
            String trackJsonString = TrackingFileUtil.readTrackFile();

            if (trackJsonString != null) {
                JA = new JSONArray(trackJsonString);

                JO = JA.getJSONObject(index);

                ItemsTrackRecord trackItem = new ItemsTrackRecord(
                        JO.getString(FavoriteHelper.TRACK_TIME),
                        JO.getString(FavoriteHelper.TRACK_NAME),
                        JO.getInt(FavoriteHelper.TRACK_DIFFICULTY),
                        JO.getString(FavoriteHelper.TRACK_DESCRIPTION),
                        JO.getString(FavoriteHelper.TRACK_POLYLINE),
                        JO.getString(FavoriteHelper.TRACK_DISTANCE));

                releaseObjects();

                return trackItem;
            }
            else
                return null;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static void parseGeoJsonCoordinates(int rawGeoJson, boolean nestedCoordinates, GeoJsonParseResult parseResult) {
        InputStream is = AppController.getInstance().getResources().openRawResource(rawGeoJson);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        String eachLine;

        try {
            while ((eachLine = reader.readLine()) != null) {
                sb.append(eachLine);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                ArrayList<ItemsGeoLines> geoLines = new ArrayList<>();

                JO = new JSONObject(sb.toString());
                JA = JO.getJSONArray("features");

                JSONArray coordinates;
                JSONArray coordinateArray;

                ArrayList<LatLng> latLngList;

                for (int i = 0; i < JA.length(); i++) {
                    coordinates = JA.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");

                    latLngList = new ArrayList<>();

                    for (int j = 0; j < coordinates.length(); j++) {

                        coordinateArray = coordinates.getJSONArray(j);

                        if (nestedCoordinates) {
                            for (int k = 0; k < coordinateArray.length(); k++) {
                                latLngList.add(new LatLng(coordinateArray.getJSONArray(k).getDouble(1), coordinateArray.getJSONArray(k).getDouble(0)));
                            }
                        }
                        else
                            latLngList.add(new LatLng(coordinateArray.getDouble(1), coordinateArray.getDouble(0)));
                    }

                    geoLines.add(new ItemsGeoLines(latLngList));
                }
                parseResult.onParseFinished(geoLines);

                is.close();
                reader.close();
                releaseObjects();
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
                parseResult.onParseFail(e.getMessage());
                releaseObjects();
            }
        }
    }

    public static void parseGeoJsonProperty(int rawGeoJson, GeoJsonParseResult parseResult) {
        InputStream is = AppController.getInstance().getResources().openRawResource(rawGeoJson);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        String eachLine;

        try {
            while ((eachLine = reader.readLine()) != null) {
                sb.append(eachLine);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                ArrayList<ItemsGeoLines> geoLines = new ArrayList<>();

                JO = new JSONObject(sb.toString());
                JA = JO.getJSONArray("features");

                JSONObject jo;
                JSONObject jo_properties;

                String name;
                String description;
                String location = "";

                for (int i = 0; i < JA.length(); i++) {
                    jo = JA.getJSONObject(i);
                    jo_properties = jo.getJSONObject("properties");

                    name = jo_properties.getString("Name");

                    description = jo_properties.getString("description");
                    if (description.equals("null"))
                        description = "";

                    if (jo_properties.has("Location"))
                        location = jo_properties.getString("Location");

                    geoLines.add(new ItemsGeoLines(name, description, location));
                }
                parseResult.onParseFinished(geoLines);

                is.close();
                reader.close();
                releaseObjects();
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
                parseResult.onParseFail(e.getMessage());
                releaseObjects();
            }
        }
    }
}
