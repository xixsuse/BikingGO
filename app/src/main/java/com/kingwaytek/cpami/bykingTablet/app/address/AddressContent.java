package com.kingwaytek.cpami.bykingTablet.app.address;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.actionsheet.ActionSheet;
import com.example.actionsheet.ActionSheet.ActionSheetButtonClickListener;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.MapActivity;
import com.kingwaytek.cpami.bykingTablet.app.PreferenceActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.maps.IMapView;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.NaviSetupAction;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.sonav;

public class AddressContent extends Activity {

    private sonav engine;

    private IMapView mapView;
    private RelativeLayout rlMapOption;
    private RelativeLayout rlMapZoom;

    private Intent itenCaller;
    // private ActivityCaller myCaller;
    private String addressRest;
    private GeoPoint addressPoint;

    // to determine set location
    private String naviOption;
    private boolean isNaviNow = false;

    private String Town;

    private ImageButton menu_button;
    private ActionSheet actionSheet;
    private int[][] sub_view;

    private ImageButton ibZoomIn;
    private ImageButton ibZoomOut;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        itenCaller = getIntent();

        setContentView(R.layout.map);

        naviOption = itenCaller.getStringExtra("setpoint");
        addressRest = itenCaller.getStringExtra("addressResult");
        double[] addrPnt = itenCaller.getDoubleArrayExtra("addressLocation");

        if (addrPnt != null)
            addressPoint = new GeoPoint(addrPnt[1], addrPnt[2]);

        Log.i("AddressContent", "addressRest:" + addressRest);
        Log.i("AddressContent", "addressPoint:" + addrPnt[1] + ", " + addrPnt[2]);

        InitFixedMapView();
        AddFloatContentView();
        menu_button = (ImageButton) findViewById(R.id.menu_button);
        setActionSheet();

        menu_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSheet.show();
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.title_text)).setText(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (addressRest == null || addressRest.equals("") || addressPoint == null)
            return;

        SetupAddress();
    }

    public void setActionSheet() {
        actionSheet = (ActionSheet) findViewById(R.id.actionSheet1);
        actionSheet.setContext(AddressContent.this);

        sub_view = new int[5][2];
        sub_view[0][0] = R.id.actionsheet_address01;// 前往
        sub_view[1][0] = R.id.actionsheet_address02;// 設為出發點
        sub_view[2][0] = R.id.actionsheet_address03;// 設為經過點
        sub_view[3][0] = R.id.actionsheet_address04;// 設為目的地
        sub_view[4][0] = R.id.actionsheet_address05;// 取消


        sub_view[0][1] = 1;
        sub_view[1][1] = 1;
        sub_view[2][1] = 1;
        sub_view[3][1] = 1;

        if (naviOption == null || naviOption.equals("")) {
            sub_view[0][1] = 0;
            sub_view[1][1] = 0;
            sub_view[3][1] = 0;

        } else {
            switch (NaviSetupAction.get(naviOption)) {
                case SET_ORIGIN:
                    sub_view[1][1] = 0;
                    break;
                case SET_DESTINATION:
                    sub_view[3][1] = 0;
                    break;
                case SET_VIA1:
                case SET_VIA2:
                    sub_view[2][1] = 0;
                    break;
                default:
                    break;
            }
        }
        actionSheet.setActionSheetLayout(R.layout.action_sheet_address, sub_view);
        actionSheet.setOnActionSheetButtonClickListener(new ActionSheetButtonClickListener() {
            @Override
            public void onButtonClick(ActionSheet actionsheet, int index, int id) {
                switch (id) {
                    case R.id.actionsheet_address02:
                        ToggleLocationSetting(NaviSetupAction.SET_ORIGIN);
                        break;

                    case R.id.actionsheet_address04:
                        ToggleLocationSetting(NaviSetupAction.SET_DESTINATION);
                        break;

                    case R.id.actionsheet_address01: // cause finish
                        isNaviNow = true;
                        SetLocation(NaviSetupAction.SET_DESTINATION);
                        Log.i("AddressContent", "use item as end Point for navigation.");
                        break;

                    case R.id.actionsheet_address03:
                        ToggleLocationSetting(null);
                        isNaviNow = true;
                        break;
                }
            }
        });
    }

    private void SetLocation(NaviSetupAction action) {
        int flag;
        switch (action) {
            case SET_ORIGIN:
                flag = MapActivity.START_POINT;
                if (naviOption == null || naviOption.equals("")) {
                    HasPointSet(flag);
                }
                else {
                    // itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
                    SetPointAction(flag);
                }
                break;
            case SET_DESTINATION:
                // itenCaller.putExtra("POI_Action", POIMenu.SET_DESTINATION);
                flag = MapActivity.END_POINT;
                if (isNaviNow) {
                    itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
                    SetPointAction(flag);
                    MapActivity.setGoImmediately(true);
                }
                else if (naviOption == null || naviOption.equals("")) {
                    // itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
                    HasPointSet(flag);
                }
                else {
                    // itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
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
        }
    }

    private void ToggleLocationSetting(NaviSetupAction options) {
        if (null == naviOption)
            SetLocation(options);
        else
            SetLocation(NaviSetupAction.get(naviOption));
    }

    private void HasPointSet(final int flag) {
        String locInfo = MapActivity.getName(flag);
        locInfo += MapActivity.getAddress(flag).isEmpty() ? "" : "\n" + MapActivity.getAddress(flag);
        Log.i("HasPointSet", "flag:" + flag + ", info:" + locInfo);

        // if a point has been set already
        if (!locInfo.equals("")) {
            UtilDialog uit = new UtilDialog(AddressContent.this) {
                @Override
                public void click_btn_1() {
                    itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
                    SetPointAction(flag);
                    super.click_btn_1();
                }
            };
            uit.showDialog_route_plan_choice("已設定:\n" + locInfo + "\n是否取代?", null, "是", "否");
        }
        else {
            itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
            SetPointAction(flag);
        }
    }

    private void SetPointAction(int flag) {
        MapActivity.setPosition(addressRest, addressPoint, flag);
        setResult(RESULT_OK, itenCaller);
        finish();
    }

    private void InitFixedMapView() {
        mapView = (IMapView) findViewById(R.id.mapView);
        mapView.setViewType(MapView.VIEW_2D);
        // mapView.setCenter(new GeoPoint(121.522069004011, 25.0270332995188));

        rlMapOption = (RelativeLayout) findViewById(R.id.option_view);
        rlMapZoom = (RelativeLayout) findViewById(R.id.mapView_zoom_layout);
//		rlMap = (RelativeLayout) findViewById(R.id.mapLayout);
//
        rlMapOption.setVisibility(RelativeLayout.GONE);
        rlMapZoom.setVisibility(RelativeLayout.GONE);

        engine = sonav.getInstance();
        int mapstyle = Integer.valueOf(PreferenceActivity.getMapStyle(this));

        if (mapstyle < 6)
            engine.setmapstyle(0, mapstyle, 1);
        else {
            mapstyle -= 5;
            engine.setmapstyle(1, 0, mapstyle);
        }
        engine.savenaviparameter();
    }

    private void AddFloatContentView() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View optionView = inflater.inflate(R.layout.addressmenu, null);
        optionView.setId(R.id.poi_content_view);

        layout.addView(optionView);

        ibZoomIn = (ImageButton) optionView.findViewById(R.id.address_zoom_in);
        ibZoomOut = (ImageButton) optionView.findViewById(R.id.address_zoom_out);

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

    private void SetupAddress() {
        mapView.setCenter(addressPoint);
        engine.setflagpoint(MapView.SELECTED_POINT, mapView.getCenter().getLongitude(), mapView.getCenter().getLatitude());

        TextView tvName = (TextView) findViewById(R.id.map_data_content_view_name);

        Town = engine.showcitytownname(mapView.getCenter().getLongitude(), mapView.getCenter().getLatitude());
        tvName.setText(addressRest);
    }
}