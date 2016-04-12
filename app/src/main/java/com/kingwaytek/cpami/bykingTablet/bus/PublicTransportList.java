package com.kingwaytek.cpami.bykingTablet.bus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sonavtek.sonav.sonav;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.MapActivity;
import com.kingwaytek.cpami.bykingTablet.app.PreferenceActivity;
import com.kingwaytek.cpami.bykingTablet.view.PublicTransitAdapter;
import com.kingwaytek.cpami.bykingTablet.view.RoadlistAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PublicTransportList extends Activity {

    private TextView param1a;
    private TextView param1b;
    private sonav engine;
    private ListView roadListView;
    private PublicTransitAdapter listitemAdapter;
    private ArrayList<HashMap<String, Object>> listitem;
    private Intent intent;
    private final static int plan1 = 0;
    private final static int plan2 = 1;
    private final static int plan3 = 2;
    private RelativeLayout layout_2;
    private RelativeLayout layout_3;
    private int TotalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        intent = this.getIntent();
        String TrackPoint = intent.getStringExtra("TrackPointString");
        Log.i("PublicTransportList.java", "TrackPoint=" + TrackPoint);
        int plan = intent.getIntExtra("whichPlan", 0);
        Log.i("PublicTransportList.java", "plan=" + plan);
        setContentView(R.layout.public_transport_list);
        layout_2 = (RelativeLayout) findViewById(R.id.param_layout_2);
        roadListView = (ListView) findViewById(R.id.RoadlistItem);
        layout_2.setVisibility(layout_2.GONE);
        param1a = (TextView) findViewById(R.id.param1a);
        param1b = (TextView) findViewById(R.id.param1b);
        engine = sonav.getInstance();
        int mapstyle = Integer.valueOf(PreferenceActivity.getMapStyle(this));
        if (mapstyle < 6) {
            engine.setmapstyle(0, mapstyle, 1);
        }else{
            mapstyle-=5;
            engine.setmapstyle(1, 0, mapstyle);
        }
        engine.savenaviparameter();
        doTransportPlan(TrackPoint, plan);

    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public void doTransportPlan(String str, int whichPlan) {
        StringBuilder strBuilder = new StringBuilder();

        // 取得回傳第一條路徑
        // System.out.println("=> how many routes this source have? "+dp.getRouteSize(source));

        JSONObject jso = getRoutes(str, whichPlan);

        // 取得回傳的第一條路徑的Points, ArrayList
        ArrayList<RPointInfo> PointList = getPointsFromRoute(jso);
        int ArraySize = PointList.size();
        for (int i = 0; i < ArraySize; i++) {
            RPointInfo rrr = PointList.get(i);
            strBuilder.append(rrr.getPointName() + ", ");
            // System.out.println(rrr.getPointName()+", "+rrr.getLon()+", "+rrr.getLat());
        }
        // 取得回傳的第一條路徑的Lines, ArrayList
        ArrayList<RLineInfo> LineList = getLinesFromRoute(jso);
        ArraySize = LineList.size();

        for (int i = 0; i < ArraySize; i++) {
            RLineInfo rli = LineList.get(i);
            strBuilder.append(rli.getLineName() + ", " + rli.getLinePeriod()
                    + "分鐘");
            // System.out.println(rli.getLineName()+", "+rli.getLineCategory()+", "+rli.getLinePeriod());
        }

        setRoadListData(PointList, LineList);

    }

    public ArrayList<PointInfo> getPossiblePoint(String source) {
        // System.out.println("# " + source + " #");
        try {
            ArrayList<PointInfo> rtnList = new ArrayList<PointInfo>();

            JSONArray jaPP = new JSONArray(source);
            String ObjSource = jaPP.get(0).toString();
            JSONObject jo = new JSONObject(ObjSource);

            String rtnResult = jo.getString("result");

            if (("2").equals(rtnResult)) {
                String rtnPPoints = jo.getString("start_points");
                jaPP = new JSONArray(rtnPPoints);

                // how many routes we have?
                int numPP = jaPP.length();

                for (int i = 0; i < numPP; i++) {
                    String rNAME = jaPP.getJSONObject(i).getString("Name");
                    String rTYPE = jaPP.getJSONObject(i).getString("type");
                    String rREG = jaPP.getJSONObject(i).getString("Region");
                    long rLAT = jaPP.getJSONObject(i).getLong("Lat");
                    long rLON = jaPP.getJSONObject(i).getLong("Lon");
                    PointInfo pi = new PointInfo(rNAME, rTYPE, rREG, rLAT, rLON);
                    rtnList.add(pi);
                }

                return rtnList;
            } else {
                return null;
            }
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
    }

    public static int getRouteSize(String source) {
        int rtn = 0;
        try {
            JSONArray jaRoute = new JSONArray(source);
            String ObjSource = jaRoute.get(0).toString();
            JSONObject jo = new JSONObject(ObjSource);
            String rtnResult = jo.getString("result");

            if (("3").equals(rtnResult)) {
                String rtnRoutes = jo.getString("routes");
                jaRoute = new JSONArray(rtnRoutes);

                // how many routes we have?
                rtn = jaRoute.length();
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return rtn;
    }

    public JSONObject getRoutes(String source, int number) {

        try {
            JSONObject rtnObj = new JSONObject("{result:null}");

            JSONArray jaRoute = new JSONArray(source);
            String ObjSource = jaRoute.get(0).toString();
            JSONObject jo = new JSONObject(ObjSource);
            String rtnResult = jo.getString("result");

            if (("3").equals(rtnResult)) {
                String rtnRoutes = jo.getString("routes");
                jaRoute = new JSONArray(rtnRoutes);

                // how many routes we have?
                int numRoute = jaRoute.length();
                // System.out.println("how many routes we have? " + numRoute);

                if (number < numRoute && number > -1) {
                    rtnObj = jaRoute.getJSONObject(number);
                }
                return rtnObj;
            } else {
                // System.out.println(rtnResult);
                return null;
            }
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
    }

    public ArrayList<RPointInfo> getPointsFromRoute(JSONObject RouteObj) {
        try {
            ArrayList<RPointInfo> rtnList = new ArrayList<RPointInfo>();

            String rtnPoints = RouteObj.getString("points");
            JSONArray jaPoints = new JSONArray(rtnPoints);
            // how many points
            int numPoints = jaPoints.length();

            for (int i = 0; i < numPoints; i++) {
                int rID = jaPoints.getJSONObject(i).getInt("id");
                String rNAME = jaPoints.getJSONObject(i).getString("name");
                String rREG = jaPoints.getJSONObject(i).getString("region");
                long rLON = jaPoints.getJSONObject(i).getLong("lon");
                long rLAT = jaPoints.getJSONObject(i).getLong("lat");
                RPointInfo rpi = new RPointInfo(rID, rNAME, rREG, rLAT, rLON);
                rtnList.add(rpi);

            }
            return rtnList;
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
    }

    public ArrayList<RLineInfo> getLinesFromRoute(JSONObject RouteObj) {
        try {
            ArrayList<RLineInfo> rtnList = new ArrayList<RLineInfo>();

            String rtnLines = RouteObj.getString("lines");
            JSONArray jaLines = new JSONArray(rtnLines);
            // how many lines
            int numLines = jaLines.length();

            for (int i = 0; i < numLines; i++) {
                String rNAME = jaLines.getJSONObject(i).getString("name");
                String rSCHD = jaLines.getJSONObject(i).getString("schedule");
                int rCATE = jaLines.getJSONObject(i).getInt("category");
                int rPRICE = jaLines.getJSONObject(i).getInt("price");
                int rRID = jaLines.getJSONObject(i).getInt("RouteID");
                int rPERIOD = jaLines.getJSONObject(i).getInt("period");

                RLineInfo rpi = new RLineInfo(rRID, rNAME, rCATE, rPERIOD,
                        rPRICE, rSCHD);
                rtnList.add(rpi);

            }

            return rtnList;
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
    }

    private int getIconId(int Category) {
        int IconID = 0;
        switch (Category) {
            case 1101:
                IconID = getResources().getIdentifier("transport_bus", "drawable",
                        getPackageName());
                break;
            case 1102:
                IconID = getResources().getIdentifier("ic_transit_mrt", "drawable",
                        getPackageName());
                break;
            case 1104:
                IconID = getResources().getIdentifier("ic_transit_railway",
                        "drawable", getPackageName());
                break;
            case 1105:// 高鐵
                IconID = getResources().getIdentifier("ic_transit_thsrc",
                        "drawable", getPackageName());
                break;
            case 0:
                IconID = getResources().getIdentifier("transport_walk",
                        "drawable", getPackageName());
                break;
            default:
                break;
        }
        return IconID;
    }

    private void setRoadListData(ArrayList<RPointInfo> RPoint,
                                 ArrayList<RLineInfo> RLine) {
        int length = RPoint.size() + RLine.size();
        String[] name = new String[length];
        int[] turn = new int[length];
        String[] citytownname = new String[length];
        ArrayList<HashMap<String, Object>> listitem = new ArrayList<HashMap<String, Object>>();
        String[] Period = new String[length];

        param1b.setText("轉乘次數:" + (RLine.size() - 1) + "次");

        name[0] = "起點";
        citytownname[0] = MapActivity.getName(MapActivity.START_POINT);
        turn[0] = getResources().getIdentifier("transport_start", "drawable",
                getPackageName());

        for (int i = 1; i < RLine.size()+1; i++) {
            name[i] = RLine.get(i-1).getLineName();
            citytownname[i] = RPoint.get(i - 1).getPointName().replace(" ", "") + "-"
                    + RPoint.get(i).getPointName().replace(" ", "");
            int LineCategory = RLine.get(i-1).getLineCategory();
            if(LineCategory != 0){
                name[i] = "搭乘 " + name[i];
            }
            turn[i] = getIconId(RLine.get(i-1).getLineCategory());
            Period[i] = String.valueOf(RLine.get(i-1).getLinePeriod());
            TotalTime = TotalTime + RLine.get(i-1).getLinePeriod();

        }
        param1a.setText("總花費時間:" + TotalTime + "分鐘");
        name[RLine.size()+1] = "終點";
        citytownname[RLine.size()+1] = MapActivity.getName(MapActivity.END_POINT);
        turn[RLine.size()+1] = getResources().getIdentifier("transport_end",
                "drawable", getPackageName());

        for (int i = 0; i < RLine.size() + 2; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", name[i]);
            map.put("Period", Period[i]);
            map.put("citytownname", citytownname[i]);
            listitem.add(map);
        }

        listitemAdapter = new PublicTransitAdapter(this, listitem,
                R.layout.transit_roadlistitem, new String[] { "name", "Period",
                "citytownname" }, new int[] { R.id.roadlist_text1,
                R.id.roadlist_time, R.id.area });
        listitemAdapter.putNameArray(name);
        listitemAdapter.putLengthArray(Period);
        listitemAdapter.putTurnArray(turn);
        listitemAdapter.putCityTowNameArray(citytownname);

        roadListView.setAdapter(listitemAdapter);

    }
}
// 起點
// 步行 9
//
// 福德宮
// B線[捷運頂溪站-六合社區
// 發車班距:15-30分
//
// 永德宮
// 步行 8
//
// 網溪國小
// 241[中和-台北]
// 發車班距:尖峰12-15分 離峰15-20分 例假日20-30分
//
// 南昌路
// 步行 18
//
// 一女中
// 663[新莊-國父紀念館]
// 發車班距:固定班次 例假日停駛

// 裕民國小
// 步行 2
//
// 終點