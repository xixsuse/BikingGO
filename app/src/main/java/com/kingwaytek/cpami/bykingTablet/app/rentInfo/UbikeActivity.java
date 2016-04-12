package com.kingwaytek.cpami.bykingTablet.app.rentInfo;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ApplicationGlobal;
import com.kingwaytek.cpami.bykingTablet.app.CreatMD5Code;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.CommunicationBaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.PreferenceActivity;
import com.kingwaytek.cpami.bykingTablet.app.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.maps.IMapView;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.UBikeMapEventHandler;
import com.sonavtek.sonav.UBikeMapEventListen;
import com.sonavtek.sonav.sonav;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class UbikeActivity extends CommunicationBaseActivity implements LocationListener {

    private IMapView mapView;
    private GeoPoint point;
    private RelativeLayout rlMapOption;
    private RelativeLayout rlMapZoom;
    private RelativeLayout rlMap;
    private sonav engine;
    private Intent itenCaller;
    private ImageButton ibZoomIn;
    private ImageButton ibZoomOut;
    private Button btn_mylocation;
    private Button btn_favor;
    private Button btn_list;
    private Button ubike_top_addfavor;
    private TextView top_title;
    private TextView top_dis;
    private TextView top_sbi;
    private TextView top_bemp;

    public static ArrayList<UbikeObject> Ubike_list;
    public static ArrayList<DistenceObject> distence_list;
    public static ArrayList<Integer> UbikeFavorList;
    public static int ubikePoint;

    private Runnable runnable_update;
    private boolean is_mylocWritten;
    private boolean is_firstIn;
    private int cur_sno;
    private Location cur_loc;
    private boolean onDestroy;
    private AlertDialog alertDialog;

    private UtilDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ubike);

        init();
        cur_loc = getLocation();
        getUbike_listByHttp();
        try {
            getUbikeFavor();
        } catch (IOException e) {
        }
        InitFixedMapView();

    }

    @Override
    protected void onResume() {
        int position;
        if (ubikePoint != -1) {
            if (distence_list.size() != 0) {
                for (position = 0; position < Ubike_list.size(); position++) {
                    if (Ubike_list.get(position).getsno() == distence_list.get(ubikePoint).getSno()) {
                        break;
                    }
                }
            } else {
                position = ubikePoint;
            }
            engine.gomap(Ubike_list.get(position).getlng(), Ubike_list.get(position).getlat(), 0);
            engine.redrawmap();
            cur_sno = Ubike_list.get(position).getsno();
            setTopText(cur_sno);

        }
        super.onResume();
    }

    private void init() {
        progressDialog = new UtilDialog(this);
        onDestroy = false;
        is_firstIn = true;
        cur_sno = -1;
        ubikePoint = -1;
        clear_myloc();
        // itenCaller = getIntent();
        Ubike_list = new ArrayList<UbikeObject>();
        distence_list = new ArrayList<DistenceObject>();
        UbikeFavorList = new ArrayList<Integer>();

        // runnable_update = new Runnable() {
        //
        // @Override
        // public void run() {
        // if (!onDestroy) {
        // Log.i("UbikeActivity", "runnable_update:run");
        // getUbike_listByHttp();
        // Handler handler = new Handler();
        // handler.postDelayed(this, 10000);
        // } else {
        // Log.i("UbikeActivity", "runnable_update:stop");
        // }
        // }
        // };
        top_title = (TextView) findViewById(R.id.ubike_top_title);
        top_dis = (TextView) findViewById(R.id.ubike_top_dis);
        top_sbi = (TextView) findViewById(R.id.ubike_top_sbi);
        top_bemp = (TextView) findViewById(R.id.ubike_top_bemp);
        ibZoomIn = (ImageButton) findViewById(R.id.zoom_in);
        ibZoomOut = (ImageButton) findViewById(R.id.zoom_out);
        btn_mylocation = (Button) findViewById(R.id.ubike_btn_myloc);
        btn_favor = (Button) findViewById(R.id.ubike_btn_favor);
        btn_list = (Button) findViewById(R.id.top_right_btn);
        ubike_top_addfavor = (Button) findViewById(R.id.ubike_top_addfavor);
        ibZoomIn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mapView.zoomIn();
            }
        });

        ibZoomOut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mapView.zoomOut();
            }
        });

        btn_favor.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(UbikeActivity.this, UbikeFavorAvtivity.class);
                startActivity(intent);
            }
        });

        ubike_top_addfavor.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean isExist = false;

                if (cur_sno != -1) {
                    if (UbikeFavorList.size() == 0) {
                        UbikeFavorList.add(cur_sno);
                        Toast.makeText(UbikeActivity.this, "成功加入最愛！", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < UbikeFavorList.size(); i++) {
                            if (cur_sno == UbikeFavorList.get(i)) {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            UbikeFavorList.add(cur_sno);
                            Toast.makeText(UbikeActivity.this, "成功加入最愛！", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(UbikeActivity.this, "此點已重複加入", Toast.LENGTH_SHORT).show();

                        }
                    }
                } else {
                    Toast.makeText(UbikeActivity.this, "請先選擇欲加入最愛之景點", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_mylocation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                point_cur_location();
            }
        });
        btn_list.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(UbikeActivity.this, UbkieListActivity.class);
                startActivity(intent);
            }
        });

    }

    private void InitFixedMapView() {
        mapView = (IMapView) findViewById(R.id.ubikemapView);
        mapView.setViewType(MapView.VIEW_2D);

        // point = new GeoPoint(itenCaller.getDoubleExtra("Lon",
        // 121.522069004011), itenCaller.getDoubleExtra("Lat",
        // 25.0270332995188));
        if (cur_loc != null) {
            point = new GeoPoint(cur_loc.getLongitude(), cur_loc.getLatitude());
        } else {
            point = new GeoPoint(121.522069004011, 25.0270332995188);
        }
        mapView.setViewType(Integer.parseInt(PreferenceActivity.getMapViewType(this)));
        mapView.setCenter(point);

        rlMapZoom = (RelativeLayout) findViewById(R.id.Ubike_menu);
        rlMap = (RelativeLayout) findViewById(R.id.mapLayout);
        rlMapZoom.setVisibility(RelativeLayout.VISIBLE);

        engine = sonav.getInstance();
        int mapstyle = Integer.valueOf(PreferenceActivity.getMapStyle(this));
        if (mapstyle < 6) {
            engine.setmapstyle(0, mapstyle, 1);
        } else {
            mapstyle -= 5;
            engine.setmapstyle(1, 0, mapstyle);
        }

        engine.savenaviparameter();
        engine.setresizefont(2);
        engine.setIconSize(1);
        // engine.savenaviparameter();

        UBikeMapEventHandler aaBikeMapEventHandler = engine.getUBikeMapEventHandler();
        aaBikeMapEventHandler.setOnUBikeMapEventListen(new UBikeMapEventListen() {

            @Override
            public void OnUBkieMapEventListen(String sno) {
                Log.i("UbikeActivity", "OnUBkieMapEventListen");
                cur_sno = Integer.valueOf(sno);
                setTopText(cur_sno);
            }
        });
    }

    private void setTopText(int sno) {
        DecimalFormat df = new DecimalFormat("#.##");

        for (int i = 0; i < Ubike_list.size(); i++) {
            if (Ubike_list.get(i).getsno() == Integer.valueOf(sno)) {

                top_title.setText(Ubike_list.get(i).getsna());
                top_sbi.setText("" + Ubike_list.get(i).getsbi());
                top_bemp.setText("" + Ubike_list.get(i).getbemp());

                if (distence_list.size() == 0) {
                    top_dis.setText("--km");
                } else {
                    for (int j = 0; j < distence_list.size(); j++) {
                        if (distence_list.get(j).getSno() == Integer.valueOf(sno)) {
                            top_dis.setText("" + df.format(distence_list.get(j).getDis()) + "km");
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    /* write to myloc */
    private void write2myloc(ArrayList<UbikeObject> Ubike_list) {
        String temp = null;

        File file = new File("/sdcard/BikingData/myloc");
        FileOutputStream fop;
        try {
            fop = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }
            clear_myloc();
            for (int i = 0; i < Ubike_list.size(); i++) {

                if (Ubike_list.get(i).getsna().contains("暫停")) {
                    temp = Ubike_list.get(i).getsno() + "," + Ubike_list.get(i).getlng() + ","
                            + Ubike_list.get(i).getlat() + ",20,,,,\n";
                } else {
                    if (Ubike_list.get(i).getsbi() > 0 && Ubike_list.get(i).getbemp() > 0) {
                        temp = Ubike_list.get(i).getsno() + "," + Ubike_list.get(i).getlng() + ","
                                + Ubike_list.get(i).getlat() + ",21,,,,\n";
                    } else if (Ubike_list.get(i).getsbi() <= 0) {
                        temp = Ubike_list.get(i).getsno() + "," + Ubike_list.get(i).getlng() + ","
                                + Ubike_list.get(i).getlat() + ",22,,,,\n";
                    } else if (Ubike_list.get(i).getbemp() <= 0) {
                        temp = Ubike_list.get(i).getsno() + "," + Ubike_list.get(i).getlng() + ","
                                + Ubike_list.get(i).getlat() + ",23,,,,\n";
                    }
                }
                if (temp != null) {
                    byte[] contentInBytes = temp.getBytes();

                    fop.write(contentInBytes);
                }
            }
            fop.flush();
            fop.close();
            is_mylocWritten = true;
            engine.setmyloc(2);
            engine.reloadmyloc();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* clear myloc */
    private void clear_myloc() {

        File file = new File("/sdcard/BikingData/myloc");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        onDestroy = true;
        clear_myloc();
        engine.reloadmyloc();
        clearUbikeFavor();
        write2myFavor(UbikeFavorList);
        super.onDestroy();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        getDistence_listByHttp(location.getLatitude(), location.getLongitude());
    }

    /* get Location */
    private Location getLocation() {
        Location loc = null;
        if (ApplicationGlobal.gpsListener != null) {
            loc = ApplicationGlobal.gpsListener.getLastLocation();
        }
        return loc;
    }

    /* point current Location */
    private void point_cur_location() {
        cur_loc = getLocation();

        if (cur_loc != null) {
            engine.gomap(cur_loc.getLongitude(), cur_loc.getLatitude(), 0);
            engine.setflagpoint(MapView.USER_LOCATION_POINT, cur_loc.getLongitude(), cur_loc.getLatitude());
        } else {
            Toast.makeText(UbikeActivity.this, "無法取得目前位置", Toast.LENGTH_SHORT).show();
        }
    }

    /* write to myFavor */
    private void write2myFavor(ArrayList<Integer> UbikeFavorList) {
        String temp;

        File file = new File("/sdcard/BikingData/UbikeFavor");
        FileOutputStream fop;
        try {
            fop = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }

            for (int i = 0; i < UbikeFavorList.size(); i++) {
                temp = "" + UbikeFavorList.get(i) + "\n";

                byte[] contentInBytes = temp.getBytes();

                fop.write(contentInBytes);

            }
            fop.flush();
            fop.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* get UbikeFavorList */
    private void getUbikeFavor() throws IOException {
        File file = new File("/sdcard/BikingData/UbikeFavor");
        if (!file.exists()) {
            return;
        }
        FileInputStream fis = new FileInputStream(file);
        BufferedReader bfr = new BufferedReader(new InputStreamReader(fis));
        String line = "";
        int temp;
        while ((line = bfr.readLine()) != null) {
            // do something with the line you just read, e.g.
            temp = Integer.valueOf(line.split(",")[0]);
            UbikeFavorList.add(temp);
        }
        return;
    }

    /* clear UbikeFavor */
    private void clearUbikeFavor() {

        File file = new File("/sdcard/BikingData/UbikeFavor");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getUbike_listByHttp() {
        // 1301 listUbike 顯示所有Ubike站點

        if (is_firstIn) {
            progressDialog.progressDialog("請稍候片刻", "正在取得YouBike資料");
        }
        String listUbike_MD5 = generateMD5(1301);
        startHttpGet("http://biking.cpami.gov.tw/Service/" + "ListUbike?" + "code=" + listUbike_MD5, false, null, null,
                null, null);
    }

    /* getu bikeDistance */
    private void getDistence_listByHttp(double lat, double lon) {
        // 1302 ubikeDistance 計算與站點距離
        String listUbike_MD5 = generateMD5(1302);
        startHttpGet("http://biking.cpami.gov.tw/Service/" + "UbikeDistance?" + "lat=" + lat + "&lon=" + lon + "&code="
                + listUbike_MD5, false, null, null, null, null);
    }

    private String generateMD5(int service_ID) {
        Date date = new Date();
        return CreatMD5Code.getMD5((String.valueOf(((date.getMonth() + 1) + date.getHours())
                * (service_ID + date.getDate())) + "Kingway").getBytes());
    }

    /* HTTP callback */
    @Override
    public void didFinishWithGetRequest(String requestString, String resultString, Header[] respondHeaders) {
        super.didFinishWithGetRequest(requestString, resultString, respondHeaders);
        progressDialog.dismiss();
        if (requestString.contains("ListUbike") && resultString.contains("ar") && resultString.contains("bemp")
                && resultString.contains("lat") && resultString.contains("lng") && resultString.contains("sbi")
                && resultString.contains("sna") && resultString.contains("sno")) {
            String ar;
            int bemp;
            double lat;
            double lng;
            int sbi;
            String sna;
            int sno;
            try {
                JSONArray ja = new JSONArray(resultString);
                Ubike_list.clear();
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo_temp = new JSONObject(ja.get(i).toString());
                    ar = jo_temp.getString("ar");
                    bemp = jo_temp.getInt("bemp");
                    lat = jo_temp.getDouble("lat");
                    lng = jo_temp.getDouble("lng");
                    sbi = jo_temp.getInt("sbi");
                    sna = jo_temp.getString("sna");
                    sno = jo_temp.getInt("sno");
                    UbikeObject ub_temp = new UbikeObject(ar, bemp, lat, lng, sbi, sna, sno);
                    Ubike_list.add(ub_temp);
                    setTopText(cur_sno);
                }
                if (is_firstIn) {
                    // 1.write myloc -> 2.set engine -> 3.get distence_list
                    write2myloc(Ubike_list);
                    if (cur_loc != null)
                        getDistence_listByHttp(cur_loc.getLatitude(), cur_loc.getLongitude());
                    is_firstIn = false;
                    // runnable_update.run();
                }
                // write2UbikeStation(Ubike_list);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                showErrorHttp();
                e.printStackTrace();
            }

        } else if (requestString.contains("UbikeDistance") && resultString.contains("dis")
                && resultString.contains("sno")) {
            double dis;
            int sno;

            try {
                JSONArray ja = new JSONArray(resultString);
                distence_list.clear();
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo_temp = new JSONObject(ja.get(i).toString());
                    dis = jo_temp.getDouble("dis");
                    sno = jo_temp.getInt("sno");
                    DistenceObject temp = new DistenceObject(dis, sno);
                    distence_list.add(temp);
                    if (cur_sno != -1) {
                        setTopText(cur_sno);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            showErrorHttp();
        }
    }

    @Override
    public void didFailWithGetRequest(String requestString, String resultString) {
        super.didFailWithGetRequest(requestString, resultString);
        Log.i("UbikeActivity", "didFailWithGetRequest:" + resultString);
        progressDialog.dismiss();

        if (resultString.contains("Null Exception")) {
            UtilDialog uit = new UtilDialog(UbikeActivity.this) {
                @Override
                public void click_btn_1() {
                    super.click_btn_1();
                    finish();
                }

                @Override
                public void click_btn_2() {
                    super.click_btn_2();
                }
            };
            uit.showDialog_route_plan_choice("查詢失敗", "請檢查您的網路狀態並稍後在試。", "是", null);
        } else {
            if (requestString.contains("ListUbike") && is_firstIn) {
                showErrorHttp();
            }
        }
    }

    private void showErrorHttp() {

        UtilDialog uit = new UtilDialog(UbikeActivity.this) {
            @Override
            public void click_btn_1() {
                getUbike_listByHttp();
                super.click_btn_1();
            }

            @Override
            public void click_btn_2() {
                super.click_btn_2();
            }
        };
        uit.showDialog_route_plan_choice("無法取得資料，\n是否重新搜尋?", null, "是", "否");
    }
}