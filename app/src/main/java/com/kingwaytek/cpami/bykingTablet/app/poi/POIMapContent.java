package com.kingwaytek.cpami.bykingTablet.app.poi;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.actionsheet.ActionSheet;
import com.example.actionsheet.ActionSheet.ActionSheetButtonClickListener;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ApplicationGlobal;
import com.kingwaytek.cpami.bykingTablet.app.MapActivity;
import com.kingwaytek.cpami.bykingTablet.app.POI_SMS;
import com.kingwaytek.cpami.bykingTablet.app.PreferenceActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.maps.IMapView;
import com.kingwaytek.cpami.bykingTablet.sql.Favorite;
import com.kingwaytek.cpami.bykingTablet.sql.History;
import com.kingwaytek.cpami.bykingTablet.sql.POI;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.ContentType;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.NaviSetupAction;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.POIMenu;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.sonav;

public class POIMapContent extends Activity {

    private sonav engine;

    private IMapView mapView;
    private RelativeLayout rlMapOption;
    private RelativeLayout rlMapZoom;
    private RelativeLayout rlMap;

    private Intent itenCaller;
    private ActivityCaller myCaller;
    private int itemId;
    private POI thisPOI;

    // to determin set location
    private String naviOption;
    private boolean isNaviNow = false;
    private ImageButton menu_button;
    private ImageButton btn_mylocation;
    private long result = -1;
    private TextView poi_titlebar;

    private ActionSheet actionSheet;
    private ImageView actionsheet_btn;
    private int[][] sub_view; // id, visible

    private ImageButton ibZoomIn;
    private ImageButton ibZoomOut;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        itenCaller = getIntent();
        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.bookmap);
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        // R.layout.title_bar);
        // setTitle(getString(R.string.title_default));
        myCaller = (ActivityCaller) itenCaller.getSerializableExtra("POI_Caller");
        naviOption = itenCaller.getStringExtra("setpoint");
        itemId = (int) itenCaller.getLongExtra("POI_ID", -1);
        // gohome = (Button)findViewById(R.id.go_home);
        // gohome.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // setResult(RESULT_FIRST_USER);
        // finish();
        // return;
        //
        // }
        // });

        menu_button = (ImageButton) findViewById(R.id.menu_button);
        btn_mylocation = (ImageButton) findViewById(R.id.btn_mylocation);
        setActionSheet();
        menu_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                actionSheet.show();
            }
        });
        btn_mylocation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                point_cur_location();
            }
        });

        Log.i("POIContent", "My Caller : " + myCaller + ", POI id : " + itemId);

        poi_titlebar = (TextView) findViewById(R.id.poi_titlebar);
        poi_titlebar.setText("景點位置");
        InitFixedMapView();
        // AddFloatContentView();
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.title_text)).setText(title);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (itemId <= 0)
            return;

        // fetch spoi here.
        thisPOI = new POI(this, itemId);
        if (thisPOI.getID() <= 0) {
            Log.e("POIContent", "poi item is null.");
            return;
        }
        Log.i("Spoi", "this poi id : " + thisPOI.getID() + ", poi name : " + thisPOI.getName());

        // Log.i("POIMapContent", "init:" + engine.toString());
        SetupPOI();
    }

    public void setActionSheet() {
        actionSheet = (ActionSheet) findViewById(R.id.actionSheet1);
        actionSheet.setContext(POIMapContent.this);
        switch (myCaller) {
            case FAVORITE:
            case HISTORY:
                sub_view = new int[7][2];
                sub_view[0][0] = R.id.actionsheet_his_favor01;// 前往
                sub_view[1][0] = R.id.actionsheet_his_favor02;// 設為出發點
                sub_view[2][0] = R.id.actionsheet_his_favor03;// 設為經過點
                sub_view[2][1] = 1;
                sub_view[3][0] = R.id.actionsheet_his_favor04;// 設為目的地
                sub_view[4][0] = R.id.actionsheet_his_favor05;// 分享位置
                sub_view[4][1] = 1;//for pad
                sub_view[5][0] = R.id.actionsheet_his_favor06;// 刪除
                sub_view[6][0] = R.id.actionsheet_his_favor07;// 取消
                set_subview_Visible();
                actionSheet.setActionSheetLayout(R.layout.action_sheet_his_favor, sub_view);
                break;
            case NAVIGATION:
                // break;
            case POI:
            case SPOI:
                sub_view = new int[7][2];
                sub_view[0][0] = R.id.actionsheet_poi01;// 前往
                sub_view[1][0] = R.id.actionsheet_poi02;// 設為出發點
                sub_view[2][0] = R.id.actionsheet_poi03;// 設為經過點
                sub_view[2][1] = 1;
                sub_view[3][0] = R.id.actionsheet_poi04;// 設為目的地
                sub_view[4][0] = R.id.actionsheet_poi05;// 分享位置
                sub_view[4][1] = 1;//for pad
                sub_view[5][0] = R.id.actionsheet_poi06;// 加入我的最愛
                sub_view[6][0] = R.id.actionsheet_poi07;// 取消
                set_subview_Visible();
                actionSheet.setActionSheetLayout(R.layout.action_sheet_poi, sub_view);
                break;
            default:
                sub_view = new int[5][2];
                sub_view[0][0] = R.id.actionsheet_address01;// 前往
                sub_view[1][0] = R.id.actionsheet_address02;// 設為出發點
                sub_view[2][0] = R.id.actionsheet_address03;// 設為經過點
                sub_view[2][1] = 1;
                sub_view[3][0] = R.id.actionsheet_address04;// 設為目的地
                sub_view[4][0] = R.id.actionsheet_address05;// 取消
                set_subview_Visible();
                actionSheet.setActionSheetLayout(R.layout.action_sheet_address, sub_view);
                break;
        }

        actionSheet.setOnActionSheetButtonClickListener(new ActionSheetButtonClickListener() {

            @Override
            public void onButtonClick(ActionSheet actionsheet, int index, int id) {
                int flag = 0;
                switch (id) {
                    case R.id.actionsheet_his_favor05:// 位置分享
                    case R.id.actionsheet_poi05:
                        Log.i("POIContent", "share this poi via SMS.");
                        Intent itenSMS = new Intent(POIMapContent.this, POI_SMS.class);
                        itenSMS.putExtra("POI_Name", thisPOI.getName());
                        itenSMS.putExtra("POI_Lon", String.valueOf(thisPOI.getPOIPoint().getLongitude()));
                        itenSMS.putExtra("POI_Lat", String.valueOf(thisPOI.getPOIPoint().getLatitude()));
                        startActivity(itenSMS);
                        break;
                    case R.id.actionsheet_his_favor06:// 刪除
                        RemoveItem(myCaller);
                        break;
                    case R.id.actionsheet_poi02:
                    case R.id.actionsheet_address02:
                    case R.id.actionsheet_his_favor02:// 設定起點
                        ToggleLocationSetting(NaviSetupAction.SET_ORIGIN);
                        break;

                    case R.id.actionsheet_his_favor01:
                    case R.id.actionsheet_poi01:
                    case R.id.actionsheet_address01:// 立即前往
                        isNaviNow = true;
                        SetLocation(NaviSetupAction.SET_DESTINATION);
                        Log.i("POIContent", "use item as end Point for navigation.");
                        break;
                    case R.id.actionsheet_his_favor04:
                    case R.id.actionsheet_address04:
                    case R.id.actionsheet_poi04:
                        // 設定目的地
                        ToggleLocationSetting(NaviSetupAction.SET_DESTINATION);
                        break;

                    case R.id.actionsheet_poi06:// 加入最愛
                        AddToFavorite();
                        break;
                    case R.id.actionsheet_poi03:
                    case R.id.actionsheet_address03:
                    case R.id.actionsheet_his_favor03:// 設為經過點
                        ToggleLocationSetting(null);
                        break;
                    default:

                        break;
                }
            }
        });
    }

    private void set_subview_Visible() {
        if (naviOption == null || naviOption.equals("")) {
            return;
        } else {
            switch (NaviSetupAction.get(naviOption)) {
                case SET_ORIGIN:
                    sub_view[0][1] = 1;
                    sub_view[2][1] = 1;
                    sub_view[3][1] = 1;
                    sub_view[4][1] = 1;
                    break;
                case SET_DESTINATION:
                    sub_view[0][1] = 1;
                    sub_view[1][1] = 1;
                    sub_view[2][1] = 1;
                    sub_view[4][1] = 1;
                    break;
                case SET_VIA1:
                case SET_VIA2:
                    sub_view[0][1] = 1;
                    sub_view[1][1] = 1;
                    sub_view[2][1] = 0;
                    sub_view[3][1] = 1;
                    sub_view[4][1] = 1;
                    break;
                default:
                    break;
            }
        }
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
        Location cur_loc = getLocation();

        if (cur_loc != null) {
//			GeoPoint poi = mapView.getCenter();

//			double centerLon = (cur_loc.getLongitude() + poi.getLongitude()) / 2;
//			double centerLat= (cur_loc.getLatitude() + poi.getLatitude()) / 2;

            engine.gomap(cur_loc.getLongitude(), cur_loc.getLatitude(), 0);

            engine.setflagpoint(MapView.USER_LOCATION_POINT, cur_loc.getLongitude(), cur_loc.getLatitude());
        } else {
            Toast.makeText(POIMapContent.this, "無法取得目前位置", Toast.LENGTH_SHORT).show();
        }
    }

    private void SetLocation(NaviSetupAction action) {
        int flag = -1;
        // boolean finishActivity = true;
        switch (action) {
            case SET_ORIGIN:
                // itenCaller.putExtra("POI_Action", POIMenu.SET_ORIGIN);
                flag = MapActivity.START_POINT;
                if (naviOption == null || naviOption.equals("")) {
                    // finishActivity = false;
                    // AlertDialogUtil.showMsgWithConfirm(this, "已設定為導航起點",
                    // getString(R.string.dialog_ok_button_text));
                    HasPointSet(flag);
                } else {
                    SetPointAction(flag);
                }
                break;
            case SET_DESTINATION:
                // itenCaller.putExtra("POI_Action", POIMenu.SET_DESTINATION);
                flag = MapActivity.END_POINT;
                if (isNaviNow) {
                    Log.i("POIMapContent.java", "gonow");
                    itenCaller.putExtra("POI_Action", POIMenu.NAVIGATION);
                    SetPointAction(flag);
                    MapActivity.setGoImmediately(true);
                } else if (naviOption == null || naviOption.equals("")) {
                    // itenCaller.putExtra("POI_Action", POIMenu.NAVIGATION);
                    HasPointSet(flag);
                } else {
                    SetPointAction(flag);
                }
                break;
            case SET_VIA1:
                flag = MapActivity.ESS1_POINT;
                SetPointAction(flag);
                break;
            case SET_VIA2:
                flag = MapActivity.ESS2_POINT;
                SetPointAction(flag);
                break;
            default:
                break;
        }
        // itenCaller.putExtra("POI_Name", thisPOI.getName());
        // itenCaller.putExtra("POI_Location", thisPOI.getPOIPoint());

        Log.i("POIMapContent.java", "thisPOI.getName()=" + String.valueOf(thisPOI.getPOIPoint() != null));
        // MapActivity.setPosition(thisPOI.getName(), thisPOI.getPOIPoint(),
        // flag);
        //
        // if (finishActivity) {
        // setResult(RESULT_OK, itenCaller);
        // finish();
        // }
    }

    private void ToggleLocationSetting(NaviSetupAction options) {
        if (null == naviOption) {
            SetLocation(options);
        } else {
            SetLocation(NaviSetupAction.get(naviOption));
        }
    }

    private void HasPointSet(final int flag) {
        String locInfo = MapActivity.getName(flag);
        locInfo += MapActivity.getAddress(flag) == "" ? "" : "\n" + MapActivity.getAddress(flag);
        Log.i("HasPointSet", "flag:" + flag + ", info:" + locInfo);
        // if a point has been set already
        if (!locInfo.equals("")) {

            UtilDialog uit = new UtilDialog(POIMapContent.this) {
                @Override
                public void click_btn_1() {
                    itenCaller.putExtra("POI_Action", POIMenu.NAVIGATION);
                    SetPointAction(flag);
                    super.click_btn_1();
                }
            };
            uit.showDialog_route_plan_choice("已設定:\n" + locInfo + "\n是否取代?", null, "最佳(專用道路優先)", "最短路徑");
        } else {
            itenCaller.putExtra("POI_Action", POIMenu.NAVIGATION);
            SetPointAction(flag);
        }
    }

    private void SetPointAction(int flag) {
        MapActivity.setPosition(thisPOI.getName(), thisPOI.getPOIPoint(), flag, thisPOI.getAddress());
        setResult(RESULT_OK, itenCaller);
        finish();
    }

    /**
     * Add Favorites
     */
    private void AddToFavorite() {

        Favorite poiFavorite = new Favorite(this);

        if (thisPOI.getSubBranch().equals("")) {
            poiFavorite.setName(thisPOI.getName());
        } else {
            poiFavorite.setName(thisPOI.getName() + "(" + thisPOI.getSubBranch() + ")");
        }

        poiFavorite.setType(ContentType.POI.getValue());
        poiFavorite.setItemID(thisPOI.getID());

        String alertMsg = "";
        // check duplication
        try {
            if (poiFavorite.isItemInList()) {
                alertMsg = getString(R.string.favorite_duplicate_poi_msg);
            } else {
                long result = poiFavorite.Add();
                Log.i("POIContent", "add favorite result = " + result);
                if (result <= 0)
                    alertMsg = getString(R.string.favorite_add_fail_msg);
                else
                    alertMsg = getString(R.string.favorite_add_success_msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        UtilDialog uit = new UtilDialog(POIMapContent.this);
        uit.showDialog_route_plan_choice(alertMsg, null, getString(R.string.dialog_ok_button_text), null);
        // AlertDialogUtil.showMsgWithConfirm(this, alertMsg,
        // getString(R.string.dialog_ok_button_text));
    }

    /**
     * Delete Favorites
     */
    private void RemoveItem(ActivityCaller what) {
        // TODO delete completion
        // long result = -1;
        // long sourceID = itenCaller.getLongExtra("item_ID", -1);

        try {
            switch (what) {
                case FAVORITE:
                    deleteItem(391);
                    // result = Favorite.Remove(this, (int) sourceID);
                    // Log.i("POIContent", "delete favorite id:" + sourceID);
                    break;
                case HISTORY:
                    deleteItem(393);
                    // result = History.Remove(this, (int) sourceID);
                    // Log.i("POIContent", "delete history id:" + sourceID);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // AlertDialogUtil.showMsgWithConfirm(this, alertMsg, "確認");

        // itenCaller.putExtra("POI_Action", POIMenu.DELETE);
        // itenCaller.putExtra("Remove_Result", result);
        // setResult(RESULT_OK, itenCaller);
        // finish();
    }

    private void deleteItem(final int caller) {
        final int FAVORITE = 391;
        final int HISTORY = 393;
        // final long result = -1;
        final long sourceID = itenCaller.getLongExtra("item_ID", -1);

        UtilDialog uit = new UtilDialog(POIMapContent.this) {
            @Override
            public void click_btn_1() {
                if (caller == FAVORITE) {
                    result = Favorite.Remove(POIMapContent.this, (int) sourceID);
                    Log.i("POIContent", "delete favorite id:" + sourceID);
                } else if (caller == HISTORY) {
                    result = History.Remove(POIMapContent.this, (int) sourceID);
                    Log.i("POIContent", "delete history id:" + sourceID);
                }

                itenCaller.putExtra("POI_Action", POIMenu.DELETE);
                itenCaller.putExtra("Remove_Result", result);
                setResult(RESULT_OK, itenCaller);
                super.click_btn_1();
                finish();
            }

            @Override
            public void click_btn_2() {
                // TODO Auto-generated method stub
                super.click_btn_2();
            }
        };
        uit.showDialog_route_plan_choice("確定要刪除?", null, "確定", "取消");

    }

    private void InitFixedMapView() {
        mapView = (IMapView) findViewById(R.id.bookmapView);
        mapView.setViewType(MapView.VIEW_2D);

        // mapView.setCenter(new GeoPoint(121.522069004011, 25.0270332995188));

        // rlMapOption = (RelativeLayout) findViewById(R.id.option_view);
        // rlMapZoom = (RelativeLayout) findViewById(R.id.mapView_zoom_layout);
        // rlMap = (RelativeLayout) findViewById(R.id.mapLayout);

        // rlMapOption.setVisibility(RelativeLayout.GONE);
        // rlMapZoom.setVisibility(RelativeLayout.GONE);

        engine = sonav.getInstance();

        int mapstyle = Integer.valueOf(PreferenceActivity.getMapStyle(this));
        if (mapstyle < 6) {
            engine.setmapstyle(0, mapstyle, 1);
        } else {
            mapstyle -= 5;
            engine.setmapstyle(1, 0, mapstyle);
        }
        engine.savenaviparameter();
        // final View emptyView = new View(this);
        // emptyView.setMinimumWidth(engine.getMapWidth());
        // emptyView.setMinimumHeight(engine.getMapHeight());
        // emptyView.setOnTouchListener(new OnTouchListener() {
        //
        // @Override
        // public boolean onTouch(View v, MotionEvent event) {
        // return true;
        // }
        // });
        // rlMap.addView(emptyView);

        // engine.setflagpoint(MapView.SELECTED_POINT, mapView.getCenter()
        // .getLongitude(), mapView.getCenter().getLatitude());
        ibZoomIn = (ImageButton) findViewById(R.id.zoom_in);
        ibZoomOut = (ImageButton) findViewById(R.id.zoom_out);
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
    }

    private void AddFloatContentView() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);
        // layout.removeView(layout.findViewById(RemovedResId));

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View optionView = inflater.inflate(R.layout.map_data_content, null);
        optionView.setId(R.id.poi_content_view);

        layout.addView(optionView);
    }

    private void SetupPOI() {
        mapView.setCenter(thisPOI.getPOIPoint());
        engine.setflagpoint(MapView.SELECTED_POINT, mapView.getCenter().getLongitude(), mapView.getCenter()
                .getLatitude());

        TextView tvName = (TextView) findViewById(R.id.map_data_content_view_name);
        TextView tvAddr = (TextView) findViewById(R.id.map_data_content_view_address);
        TextView tvTel = (TextView) findViewById(R.id.map_data_content_view_tel);

        tvName.setText(thisPOI.getName());
        if (thisPOI.getAddress().equals("")) {
            tvAddr.setVisibility(View.GONE);
        } else {
            tvAddr.setText(thisPOI.getAddress());
        }

        if (thisPOI.getTelNumber().equals("")) {
            tvTel.setVisibility(View.GONE);
        } else {
            tvTel.setText(thisPOI.getTelNumber());
        }

    }

    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // // 是否触发按键为back键
    // if (keyCode == KeyEvent.KEYCODE_BACK) {
    // setResult(RESULT_FIRST_USER);
    // this.finish();
    // return true;
    // }else {
    // return super.onKeyDown(keyCode, event);
    // }
    // }
    @Override
    protected void onDestroy() {

        ((MapView) mapView).GCsss();
        // engine.closescrzz(1);
        super.onDestroy();
    }
}