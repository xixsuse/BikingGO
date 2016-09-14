package com.kingwaytek.cpami.bykingTablet.utilities;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsCitiesAndPOI;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsEvents;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsGeoLines;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathList;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathStep;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanItem;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlans;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPoiDetail;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsSearchResult;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsShared;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTrackRecord;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTransitOverview;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTransitStep;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsYouBike;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

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

    public interface YouBikeParseResult {
        void onParseFinished(ArrayList<ItemsYouBike> uBikeItems);
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

            String poiJsonString = Util.readPoiFile();

            if (poiJsonString == null)
                return null;

            JA = new JSONArray(poiJsonString);

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
        catch (JSONException e) {
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

    public static ArrayList<String[]> getMyPlanNameAndDateList() {
        try {
            if (Util.isPlanFileNotExistOrEmpty())
                return null;

            JA = new JSONArray(Util.readPlanFile());

            ArrayList<String[]> planPairList = new ArrayList<>();

            for (int i = 0; i < JA.length(); i++) {
                String[] pair = new String[] {
                        JA.getJSONObject(i).getString(FavoriteHelper.PLAN_NAME),
                        JA.getJSONObject(i).getString(FavoriteHelper.PLAN_DATE)
                };
                planPairList.add(pair);
            }

            releaseObjects();
            return planPairList;
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
            String planDate;
            ArrayList<ItemsPlanItem> planItems;

            String title;
            double lat;
            double lng;

            for (int i = 0; i < JA.length(); i++) {
                JO = JA.getJSONObject(i);
                planName = JO.getString(FavoriteHelper.PLAN_NAME);
                planDate = JO.getString(FavoriteHelper.PLAN_DATE);

                ja = JO.getJSONArray(FavoriteHelper.PLAN_ITEMS);

                planItems = new ArrayList<>();

                for (int j = 0; j < ja.length(); j++) {
                    jo = ja.getJSONObject(j);

                    title = jo.getString(FavoriteHelper.POI_TITLE);
                    lat = jo.getDouble(FavoriteHelper.POI_LAT);
                    lng = jo.getDouble(FavoriteHelper.POI_LNG);

                    planItems.add(new ItemsPlanItem(title, lat, lng));
                }

                plansList.add(new ItemsPlans(planName, planDate, planItems));

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
            JO = new JSONObject(jsonString);
            JA = JO.getJSONObject("Infos").getJSONArray("Info");

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
                JSONObject jo = JA.getJSONObject(i);

                name = jo.getString("Name");
                description = jo.getString("Description");
                location = jo.getString("Location");
                address = jo.getString("Add");
                organization = jo.getString("Org");
                startTime = jo.getString("Start");
                endTime = jo.getString("End");
                website = jo.getString("Website");
                pic1Url = jo.getString("Picture1");
                pic1Name = jo.getString("Picdescribe1");
                pic2Url = jo.getString("Picture2");
                pic2Name = jo.getString("Picdescribe2");
                pic3Url = jo.getString("Picture3");
                pic3Name = jo.getString("Picdescribe3");
                lat = jo.getDouble("Py");
                lng = jo.getDouble("Px");
                travelInfo = jo.getString("Travellinginfo");
                parkingInfo = jo.getString("Parkinginfo");

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
            parseResult.onParseFail(e.getMessage());
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
                            JO.getString(FavoriteHelper.TRACK_DATE),
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
                        JO.getString(FavoriteHelper.TRACK_DATE),
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

    public static void parseTaipeiYouBikeData(YouBikeParseResult parseResult) {
        try {
            ArrayList<ItemsYouBike> uBikeItems = new ArrayList<>();

            JSONObject jo = new JSONObject(Util.readYouBikeTPData());

            JSONObject jo_stations = jo.getJSONObject("retVal");
            JSONObject jo_eachStation;

            String name;
            int totals;
            int availableBike;
            int availableSpace;
            String area;
            String address;
            double lat;
            double lng;
            String updateTime;
            int status;

            SimpleDateFormat rawDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.TAIWAN);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.TAIWAN);

            int index = 0;

            for (int i = 0; i < jo_stations.length(); i++) {
                String sno;
                do {
                    index++;
                    sno = String.format(Locale.TAIWAN, "%04d", index);
                }
                while (!jo_stations.has(sno));

                jo_eachStation = jo_stations.getJSONObject(sno);

                name = jo_eachStation.getString("sna");
                totals = jo_eachStation.getInt("tot");
                availableBike = jo_eachStation.getInt("sbi");
                availableSpace = jo_eachStation.getInt("bemp");
                area = jo_eachStation.getString("sarea");
                address = jo_eachStation.getString("ar");
                lat = jo_eachStation.getDouble("lat");
                lng = jo_eachStation.getDouble("lng");
                updateTime = jo_eachStation.getString("mday");
                status = jo_eachStation.getInt("act");

                updateTime = dateFormat.format(rawDateFormat.parse(updateTime));

                uBikeItems.add(new ItemsYouBike(name, totals, availableBike, availableSpace, area, address, lat, lng, updateTime, status));
            }
            parseResult.onParseFinished(uBikeItems);
        }
        catch (JSONException | ParseException e) {
            e.printStackTrace();
            parseResult.onParseFail(e.getMessage());
        }
    }

    public static void parseNewTaipeiYouBikeData(String jsonString, YouBikeParseResult parseResult) {
        try {
            ArrayList<ItemsYouBike> uBikeItems = new ArrayList<>();

            JSONArray ja = new JSONArray(jsonString);

            String name;
            int totals;
            int availableBike;
            int availableSpace;
            String area;
            String address;
            double lat;
            double lng;
            String updateTime;
            int status;

            SimpleDateFormat rawDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.TAIWAN);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.TAIWAN);

            JSONObject jo_eachStation;

            for (int i = 0; i < ja.length(); i++) {
                jo_eachStation = ja.getJSONObject(i);

                name = jo_eachStation.getString("sna");
                totals = jo_eachStation.getInt("tot");
                availableBike = jo_eachStation.getInt("sbi");
                availableSpace = jo_eachStation.getInt("bemp");
                area = jo_eachStation.getString("sarea");
                address = jo_eachStation.getString("ar");
                lat = jo_eachStation.getDouble("lat");
                lng = jo_eachStation.getDouble("lng");
                updateTime = jo_eachStation.getString("mday");
                status = jo_eachStation.getInt("act");

                updateTime = dateFormat.format(rawDateFormat.parse(updateTime));

                uBikeItems.add(new ItemsYouBike(name, totals, availableBike, availableSpace, area, address, lat, lng, updateTime, status));
            }
            parseResult.onParseFinished(uBikeItems);
        }
        catch (JSONException | ParseException e) {
            e.printStackTrace();
            parseResult.onParseFail(e.getMessage());
        }
    }

    public static ArrayList<ItemsShared> parseAndGetSharedItems(String jsonString) {
        try {
            ArrayList<ItemsShared> sharedItemList = new ArrayList<>();

            JA = new JSONArray(jsonString);

            int id;
            String date;
            String name;
            int count;

            for (int i = 0; i < JA.length(); i++) {
                JO = JA.getJSONObject(i);

                id = JO.getInt("id");
                date = JO.getString("date");
                name = JO.getString("name");
                count = JO.getInt("count");

                sharedItemList.add(new ItemsShared(id, date, name, count));
            }

            releaseObjects();
            return sharedItemList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static ItemsPlans parseAndGetSharedPlan(String jsonString) {
        try {
            String contentString = new JSONObject(jsonString).getString("content");
            JO = new JSONObject(contentString);

            String planName;
            String planDate;
            ArrayList<ItemsPlanItem> planItems;

            String title;
            double lat;
            double lng;

            planName = JO.getString(FavoriteHelper.PLAN_NAME);
            planDate = JO.getString(FavoriteHelper.PLAN_DATE);

            JA = JO.getJSONArray(FavoriteHelper.PLAN_ITEMS);

            planItems = new ArrayList<>();

            for (int j = 0; j < JA.length(); j++) {
                JSONObject jo = JA.getJSONObject(j);

                title = jo.getString(FavoriteHelper.POI_TITLE);
                lat = jo.getDouble(FavoriteHelper.POI_LAT);
                lng = jo.getDouble(FavoriteHelper.POI_LNG);

                planItems.add(new ItemsPlanItem(title, lat, lng));
            }

            releaseObjects();
            return new ItemsPlans(planName, planDate, planItems);
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static ItemsTrackRecord parseAndGetSharedTrack(String jsonString) {
        try {
            String contentString = new JSONObject(jsonString).getString("content");
            JO = new JSONObject(contentString);

            ItemsTrackRecord trackItem = new ItemsTrackRecord(
                    JO.getString(FavoriteHelper.TRACK_DATE),
                    JO.getString(FavoriteHelper.TRACK_NAME),
                    JO.getInt(FavoriteHelper.TRACK_DIFFICULTY),
                    JO.getString(FavoriteHelper.TRACK_DESCRIPTION),
                    JO.getString(FavoriteHelper.TRACK_POLYLINE),
                    JO.getString(FavoriteHelper.TRACK_DISTANCE)
            );

            releaseObjects();
            return trackItem;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static ArrayList<ItemsCitiesAndPOI> parseAndGetCityList(String jsonString) {
        try {
            JA = new JSONArray(jsonString);

            ArrayList<ItemsCitiesAndPOI> cityList = new ArrayList<>();

            for (int i = 0; i < JA.length(); i++) {
                JO = JA.getJSONObject(i);
                cityList.add(new ItemsCitiesAndPOI(JO.getString("City")));
            }

            releaseObjects();
            return cityList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static ArrayList<ItemsCitiesAndPOI> parseAndGetCityPoiList(String jsonString) {
        try {
            JA = new JSONArray(jsonString);

            ArrayList<ItemsCitiesAndPOI> cityList = new ArrayList<>();

            for (int i = 0; i < JA.length(); i++) {
                JO = JA.getJSONObject(i);
                cityList.add(new ItemsCitiesAndPOI(JO.getInt("Id"), JO.getString("Name")));
            }

            releaseObjects();
            return cityList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static ItemsPoiDetail parseAndGetPoiDetail(String jsonString) {
        try {
            JO = new JSONArray(jsonString).getJSONObject(0);

            String name = JO.getString("Name");
            String address = JO.getString("Address");
            String description = JO.getString("Introduction");
            double lat = JO.getDouble("Coly");
            double lng = JO.getDouble("Colx");
            ArrayList<String> photoPath = new ArrayList<>();

            String photo_1 = JO.getString("Photo_1");
            String photo_2 = JO.getString("Photo_2");
            String photo_3 = JO.getString("Photo_3");


            if (!photo_1.isEmpty())
                photoPath.add(photo_1);
            if (!photo_2.isEmpty())
                photoPath.add(photo_2);
            if (!photo_3.isEmpty())
                photoPath.add(photo_3);

            releaseObjects();
            return new ItemsPoiDetail(name, address, description, photoPath, lat, lng);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemsTransitOverview parseAndGetTransitData(String jsonString) {
        try {
            JSONObject jo_routes = new JSONObject(jsonString).getJSONArray("routes").getJSONObject(0);

            JSONObject jo_bounds = jo_routes.getJSONObject("bounds");

            String fare = jo_routes.getJSONObject("fare").getString("text");
            LatLng northLatLng = new LatLng(jo_bounds.getJSONObject("northeast").getDouble("lat"), jo_bounds.getJSONObject("northeast").getDouble("lng"));
            LatLng southLatLng = new LatLng(jo_bounds.getJSONObject("southwest").getDouble("lat"), jo_bounds.getJSONObject("southwest").getDouble("lng"));
            String polyLineOverview = jo_routes.getJSONObject("overview_polyline").getString("points");

            JSONObject jo_legs = jo_routes.getJSONArray("legs").getJSONObject(0);

            String totalDistance = jo_legs.getJSONObject("distance").getString("text");
            String totalDuration = jo_legs.getJSONObject("duration").getString("text");

            JA = jo_legs.getJSONArray("steps");

            ArrayList<ItemsTransitStep> stepItems = new ArrayList<>();

            String distance;
            String duration;
            String polyline;
            String instructions;
            LatLng startLatLng;
            LatLng endLatLng;
            String travelMode;

            for (int i = 0; i < JA.length(); i++) {
                JO = JA.getJSONObject(i);

                distance = JO.getJSONObject("distance").getString("text");
                duration = JO.getJSONObject("duration").getString("text");
                polyline = JO.getJSONObject("polyline").getString("points");
                instructions = JO.getString("html_instructions");
                startLatLng = new LatLng(JO.getJSONObject("start_location").getDouble("lat"), JO.getJSONObject("start_location").getDouble("lng"));
                endLatLng = new LatLng(JO.getJSONObject("end_location").getDouble("lat"), JO.getJSONObject("end_location").getDouble("lng"));
                travelMode = JO.getString("travel_mode");

                String transitDepartureStop = "";
                String transitArrivalStop = "";
                String transitHeadSign = "";
                int transitHeadWay = 0;
                String transitShortName = "";
                String transitVehicleType = "";
                int transitNumStops = 0;

                if (JO.has("transit_details")) {
                    JSONObject jo_transit = JO.getJSONObject("transit_details");

                    transitDepartureStop = jo_transit.getJSONObject("departure_stop").getString("name");
                    transitArrivalStop = jo_transit.getJSONObject("arrival_stop").getString("name");
                    transitHeadSign = jo_transit.getString("headsign");
                    transitHeadWay = jo_transit.getInt("headway");
                    transitShortName = jo_transit.getJSONObject("line").getString("short_name");
                    transitVehicleType = jo_transit.getJSONObject("line").getJSONObject("vehicle").getString("type");
                    transitNumStops = jo_transit.getInt("num_stops");
                }

                stepItems.add(new ItemsTransitStep(distance, duration, polyline, instructions, startLatLng, endLatLng, travelMode,
                        transitDepartureStop, transitArrivalStop, transitHeadSign, transitHeadWay, transitShortName, transitVehicleType, transitNumStops));
            }

            releaseObjects();
            return new ItemsTransitOverview(fare, northLatLng, southLatLng, polyLineOverview, totalDistance, totalDuration, stepItems);

        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }
}
