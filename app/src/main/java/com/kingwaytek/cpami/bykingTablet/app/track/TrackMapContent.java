package com.kingwaytek.cpami.bykingTablet.app.track;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.actionsheet.ActionSheet;
import com.example.actionsheet.ActionSheet.ActionSheetButtonClickListener;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.AlertDialogUtil;
import com.kingwaytek.cpami.bykingTablet.app.MapActivity;
import com.kingwaytek.cpami.bykingTablet.app.PreferenceActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.maps.IMapView;
import com.kingwaytek.cpami.bykingTablet.sql.Favorite;
import com.kingwaytek.cpami.bykingTablet.sql.History;
import com.kingwaytek.cpami.bykingTablet.sql.Track;
import com.kingwaytek.cpami.bykingTablet.sql.TrackPoint;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.NaviSetupAction;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.TrackMenu;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.sonav;

import facebook.FacebookActivity;

public class TrackMapContent extends Activity implements OnClickListener {

    private sonav engine;

    private IMapView mapView;
    private RelativeLayout rlMapOption;
    private RelativeLayout rlMapZoom;
    private RelativeLayout rlMap;

    private Intent itenCaller;
    private ActivityCaller myCaller;
    private int itemID;
    private Track thisTrack;
    private String itemDistance = "0";
    private String itemTime;

    private boolean isNaviNow = false;

    public static final int DIALOG_PROGRESS = 1;
    public static final int DIALOG_IMPORT_PROGRESS = 2;
    public static final int DIALOG_WRITE_PROGRESS = 3;

    private Button gohome;
    private TextView poi_titlebar;

    private String newFileName;

    private ImageButton btn_share;
    private ImageButton ibZoomIn;
    private ImageButton ibZoomOut;
    private ActionSheet actionSheet;
    private ImageView actionsheet_btn;
    private int[][] sub_view;

    public enum TrackExportExt {
        CSV(0, "csv"), GPX(1, "gpx"), KML(2, "kml");

        private static final Map<Integer, TrackExportExt> extMap = new HashMap<Integer, TrackExportExt>();
        private int value;
        private String ext;

        static {
            for (TrackExportExt ct : EnumSet.allOf(TrackExportExt.class)) {
                extMap.put(ct.getValue(), ct);
            }
        }

        private TrackExportExt(int value, String extension) {
            this.value = value;
            this.ext = extension;
        }

        public int getValue() {
            return this.value;
        }

        public String getExtension() {
            return this.ext;
        }

        public static TrackExportExt get(int value) {
            return extMap.get(value);
        }
    }

    private ProgressDialog exportDialog;

    // to determin set location
    private String naviOption;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        itenCaller = getIntent();
        setContentView(R.layout.map);

        myCaller = (ActivityCaller) itenCaller.getSerializableExtra("Track_Caller");
        naviOption = itenCaller.getStringExtra("setpoint");
        itemID = (int) itenCaller.getLongExtra("Track_ID", -1);
        itemDistance = itenCaller.getStringExtra("Track_Distance");
        Log.i("TrackMapContent.java", "line 122 itemDistance=" + String.valueOf(itemDistance != null));
        itemTime = itenCaller.getStringExtra("Track_Time");
        Log.i("TrackContent", "item id from caller = " + itemID);

        InitFixedMapView();

        // poi_titlebar = (TextView) findViewById(R.id.poi_titlebar);
        // poi_titlebar.setText("查詢紀錄");
        AddFloatContentView();

        setActionSheet();
        btn_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                actionSheet.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (itemID <= 0)
            return;

        try {
            thisTrack = new Track(this, itemID);
        } catch (Exception e) {
            Log.e("TrackContent", "error create track item : " + e);
            e.printStackTrace();
            return;
        }

        // Log.i("POIMapContent", "init:" + engine.toString());
        SetupTrack();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.setChoosePointMode(true);
    }

    public void setActionSheet() {
        actionSheet = (ActionSheet) findViewById(R.id.actionSheet1);
        actionSheet.setContext(TrackMapContent.this);

        sub_view = new int[8][2];
        sub_view[0][0] = R.id.actionsheet_track_01;// Share to Facebook
        sub_view[1][0] = R.id.actionsheet_track_02;// 前往
        sub_view[2][0] = R.id.actionsheet_track_03;// 設為出發點
        sub_view[3][0] = R.id.actionsheet_track_04;// 設為目的地
        sub_view[4][0] = R.id.actionsheet_track_05;// 匯出
        sub_view[5][0] = R.id.actionsheet_track_06;// 刪除
        sub_view[6][0] = R.id.actionsheet_track_07;// 加入我的最愛
        sub_view[7][0] = R.id.actionsheet_track_08;// 取消

        actionSheet.setActionSheetLayout(R.layout.action_sheet_track, sub_view);
        actionSheet.setOnActionSheetButtonClickListener(new ActionSheetButtonClickListener() {

            @Override
            public void onButtonClick(ActionSheet actionsheet, int index, int id) {
                switch (id) {
                    case R.id.actionsheet_track_07:
                        AddToFavorite();
                        break;
                    case R.id.actionsheet_track_06: // cause finish
                        RemoveItem();
                        break;
                    case R.id.actionsheet_track_05:
                        Log.i("TrackContent", "finish activities and save track.");
                        ShowExportSelection();
                        break;
                    case R.id.actionsheet_track_03:
                        ToggleLocationSetting(NaviSetupAction.SET_ORIGIN);
                        break;
                    case R.id.actionsheet_track_04:
                        ToggleLocationSetting(NaviSetupAction.SET_DESTINATION);
                        break;
                    case R.id.actionsheet_track_02: // cause finish
                        isNaviNow = true;
                        SetLocation(NaviSetupAction.SET_DESTINATION);
                        Log.i("POIContent", "use item as end Point for navigation.");
                        break;
                    case R.id.actionsheet_track_01: // cause finish
                        zoomMapAndStore();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_WRITE_PROGRESS:
                exportDialog = new ProgressDialog(this);
                exportDialog.setTitle(TrackMenu.EXPORT.getTitle());
                exportDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                exportDialog.setMessage(getString(R.string.track_export_in_progress_text));
                return exportDialog;
            default:
                break;
        }
        return null;
    }

    /**
     * This Activity will finish after a valid menu action occurred
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Log.i("TrackContent", "Menu item clicked : " + item.getItemId());

        switch (TrackMenu.get(item.getItemId())) {
            case ADD_FAVORITE:
                AddToFavorite();
                return true;
            case DELETE: // cause finish
                RemoveItem();
                return true;
            case EXPORT:
                Log.i("TrackContent", "finish activities and save track.");
                ShowExportSelection();
                return true;
            case SET_ORIGIN:
                ToggleLocationSetting(NaviSetupAction.SET_ORIGIN);
                return true;
            case SET_DESTINATION:
                ToggleLocationSetting(NaviSetupAction.SET_DESTINATION);
                return true;
            case SET_VIA:
                ToggleLocationSetting(null);
                // SetLocation(NaviSetupAction
                // .get(naviOption == null ? NaviSetupAction.SET_ORIGIN
                // .getTitle() : naviOption));
                // Log.i("POIContent", "use item as start Point for navigation.");
                return true;
            case NAVIGATION: // cause finish
                isNaviNow = true;
                SetLocation(NaviSetupAction.SET_DESTINATION);
                Log.i("POIContent", "use item as end Point for navigation.");
                // TODO:finish and set action to start navigation in Map Activity
                return true;
            // case SHARE: // TODO: rewrite for correspond class
            // Log.i("TrackContent", "share this poi via SMS.");
            // Intent itenSMS = new Intent(this, POI_SMS.class);
            // itenSMS.putExtra("POI_Name", thisTrack.getName());
            // itenSMS.putExtra("POI_Lon", String.valueOf(thisTrack
            // .getTrackPoints().get(0).getLongitude()));
            // itenSMS.putExtra("POI_Lat", String.valueOf(thisTrack
            // .getTrackPoints().get(0).getLatitude()));
            // startActivity(itenSMS);
            // return true;
        }

        return super.onMenuItemSelected(featureId, item);
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
                    itenCaller.putExtra("Track_Action", TrackMenu.NAVIGATION);
                    SetPointAction(flag);
                } else if (naviOption == null) {
                    // itenCaller.putExtra("Track_Action", TrackMenu.NAVIGATION);
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
        // MapActivity.setPosition(thisTrack.getName(),
        // thisTrack.getStartPoint(),
        // flag);
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

            UtilDialog uit = new UtilDialog(TrackMapContent.this) {
                @Override
                public void click_btn_1() {
                    itenCaller.putExtra("Track_Action", TrackMenu.NAVIGATION);
                    SetPointAction(flag);
                    super.click_btn_1();
                }
            };
            uit.showDialog_route_plan_choice("已設定:\n" + locInfo + "\n是否取代?", null, "是", "否");

        } else {
            itenCaller.putExtra("Track_Action", TrackMenu.NAVIGATION);
            SetPointAction(flag);
        }
    }

    private void SetPointAction(int flag) {
        MapActivity.setPosition(thisTrack.getName(), thisTrack.getStartPoint(), flag);
        setResult(RESULT_OK, itenCaller);
        finish();
    }

    /**
     * Add Favorites
     */
    private void AddToFavorite() {

        Favorite trackFavorite = new Favorite(this);
        trackFavorite.setName(thisTrack.getName());
        trackFavorite.setType(2);
        trackFavorite.setItemID(thisTrack.getID());

        String alertMsg = "";
        // check duplication
        try {
            if (trackFavorite.isItemInList()) {
                alertMsg = getString(R.string.favorite_duplicate_track_msg);
            } else {
                long result = trackFavorite.Add();
                Log.i("TrackContent", "add favorite result = " + result);
                if (result <= 0)
                    alertMsg = getString(R.string.favorite_add_fail_msg);
                else
                    alertMsg = getString(R.string.favorite_add_success_msg);
            }
        } catch (Exception e) {
            Log.e("TrackContent", "error add to favorite : " + e);
            e.printStackTrace();
        }

        AlertDialogUtil.showMsgWithConfirm(this, alertMsg, getString(R.string.dialog_ok_button_text));
    }

    /**
     * Delete Actions
     */
    private void RemoveItem() {
        // String alertMsg = "";
        long result = -1;
        long sourceID = itenCaller.getLongExtra("item_ID", -1);

        try {
            switch (myCaller) {
                case TRACK:
                    result = Track.Erase(this, thisTrack.getID());
                    Log.i("TrackContent", "delete track result = " + result);
                    break;
                case FAVORITE:
                    result = Favorite.Remove(this, (int) sourceID);
                    Log.i("TrackContent", "delete favorite id:" + sourceID);
                    break;
                case HISTORY:
                    result = History.Remove(this, (int) sourceID);
                    Log.i("TrackContent", "delete history id:" + sourceID);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e("TrackContent", "error delete action : " + e);
            e.printStackTrace();
        }

        // AlertDialogUtil.showMsgWithConfirm(this, alertMsg, "確認");

        itenCaller.putExtra("Track_Action", TrackMenu.DELETE);
        itenCaller.putExtra("Remove_Result", result);
        setResult(RESULT_OK, itenCaller);
        finish();
    }

    private void ExportTrack(TrackExportExt whichExt) {
        Log.i("TrackContent", "selected ext:" + whichExt.getExtension());

        showDialogSafely(DIALOG_WRITE_PROGRESS);
        // exportDialog.setMessage("正在匯出 " + whichExt.toString() + "...");

        switch (whichExt) {
            case CSV:
                final CsvWriter wrCsv = new CsvWriter(this, thisTrack);
                wrCsv.setOnCompletion(new Runnable() {

                    @Override
                    public void run() {
                        dismissDialogSafely(DIALOG_WRITE_PROGRESS);
                        showMessageDialog(wrCsv.getErrorMsg(), wrCsv.wasSuccess());
                    }
                });
                wrCsv.writeTrackAsync();
                break;
            case GPX:
                final GpxWriter wrGpx = new GpxWriter(this, thisTrack);
                wrGpx.setOnCompletion(new Runnable() {

                    @Override
                    public void run() {
                        dismissDialogSafely(DIALOG_WRITE_PROGRESS);
                        showMessageDialog(wrGpx.getErrorMsg(), wrGpx.wasSuccess());
                    }
                });
                wrGpx.writeTrackAsync();
                break;
            case KML:
                final KmlWriter wrKml = new KmlWriter(this, thisTrack);
                wrKml.setOnCompletion(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        dismissDialogSafely(DIALOG_WRITE_PROGRESS);
                        showMessageDialog(wrKml.getErrorMsg(), wrKml.wasSuccess());
                    }
                });
                wrKml.writeTrackAsync();
                break;
            default:
                break;
        }
    }

    private void ShowExportSelection() {
        final String[] options = new String[] { TrackExportExt.CSV.toString(), TrackExportExt.GPX.toString(),
                TrackExportExt.KML.toString() };
        DialogInterface.OnClickListener dlgListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ExportTrack(TrackExportExt.get(which));
            }
        };

        AlertDialogUtil.showContextSelection(this, getString(R.string.track_export_format_tilte), options, dlgListener);
    }

    public void showMessageDialog(final int message, final boolean success) {
        runOnUiThread(new Runnable() {
            public void run() {

                UtilDialog uit = new UtilDialog(TrackMapContent.this) {
                    @Override
                    public void click_btn_1() {

                        super.click_btn_1();
                    }

                    @Override
                    public void click_btn_2() {

                        super.click_btn_2();
                    }
                };
                uit.showDialog_route_plan_choice(success ? getString(R.string.data_export_success_msg)
                                : getString(R.string.data_export_fail_msg), TrackMapContent.this.getString(message),
                        getString(R.string.dialog_close_button_text), null);

                // AlertDialog dialog = null;
                // AlertDialog.Builder builder = new
                // AlertDialog.Builder(TrackMapContent.this);
                // builder.setMessage(TrackMapContent.this.getString(message));
                // builder.setNegativeButton(getString(R.string.dialog_close_button_text),
                // null);
                // // builder.setIcon(success ?
                // android.R.drawable.ic_dialog_info :
                // // android.R.drawable.ic_dialog_alert);
                // builder.setTitle(success ?
                // getString(R.string.data_export_success_msg)
                // : getString(R.string.data_export_fail_msg));
                // dialog = builder.create();
                // dialog.show();
            }
        });
    }

    /**
     * Just like showDialog, but will catch a BadTokenException that sometimes
     * (very rarely) gets thrown. This might happen if the user hits the "back"
     * button immediately after sending tracks to google.
     *
     * @param id
     *            the dialog id
     */
    public void showDialogSafely(final int id) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    showDialog(id);
                } catch (BadTokenException e) {
                    Log.w("TrackContent", "Could not display dialog with id " + id, e);
                } catch (IllegalStateException e) {
                    Log.w("TrackContent", "Could not display dialog with id " + id, e);
                }
            }
        });
    }

    /**
     * Dismisses the progress dialog if it is showing. Executed on the UI
     * thread.
     */
    public void dismissDialogSafely(final int id) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    dismissDialog(id);
                } catch (IllegalArgumentException e) {
                    // This will be thrown if this dialog was not shown before.
                }
            }
        });
    }

    private void InitFixedMapView() {
        mapView = (IMapView) findViewById(R.id.mapView);
        mapView.setViewType(MapView.VIEW_2D);
        mapView.setChoosePointMode(false);

        int aa = mapView.getControlMode();

        // mapView.setCenter(new GeoPoint(121.522069004011, 25.0270332995188));

        rlMapOption = (RelativeLayout) findViewById(R.id.option_view);
        rlMapZoom = (RelativeLayout) findViewById(R.id.mapView_zoom_layout);
        rlMap = (RelativeLayout) findViewById(R.id.mapLayout);

        rlMapOption.setVisibility(RelativeLayout.GONE);
        // rlMapZoom.setVisibility(RelativeLayout.GONE);
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

        engine = sonav.getInstance();
        int mapstyle = Integer.valueOf(PreferenceActivity.getMapStyle(this));
        if (mapstyle < 6) {
            engine.setmapstyle(0, mapstyle, 1);
        } else {
            mapstyle -= 5;
            engine.setmapstyle(1, 0, mapstyle);
        }
        engine.savenaviparameter();
        final View emptyView = new View(this);
        emptyView.setMinimumWidth(engine.getMapWidth());
        emptyView.setMinimumHeight(engine.getMapHeight());
        emptyView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                int screenX = (int) event.getX();
                int screenY = (int) event.getY();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("TrackContent", "touchDown from Empty view.");
                        engine.putxy(screenX, screenY, MapView.TOUCH_DOWN);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("TrackContent", "touchMove from Empty view.");
                        engine.putxy(screenX, screenY, MapView.TOUCH_MOVE);
                        return false;
                    case MotionEvent.ACTION_UP:
                        Log.i("TrackContent", "touchUp from Empty view.");
                        engine.putxy(screenX, screenY, MapView.TOUCH_UP);
                        return false;
                    default:
                        return true;
                }
            }
        });
        rlMap.addView(emptyView);

        // engine.setflagpoint(MapView.SELECTED_POINT, mapView.getCenter()
        // .getLongitude(), mapView.getCenter().getLatitude());
    }

    private void AddFloatContentView() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);
        // layout.removeView(layout.findViewById(RemovedResId));

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View optionView = inflater.inflate(R.layout.map_data_content, null);
        optionView.setId(R.id.poi_content_view);

        btn_share = (ImageButton) optionView.findViewById(R.id.imageButton1);

        layout.addView(optionView);

    }

    private void SetupTrack() {
        engine.newspxy(thisTrack.getTrackPoints().size());
        TrackPoint[] tpoints = new TrackPoint[thisTrack.getTrackPoints().size()];
        thisTrack.getTrackPoints().values().toArray(tpoints);

        for (TrackPoint tp : tpoints) {
            engine.addspxy(tp.getLongitude(), tp.getLatitude());
        }

        mapView.setCenter(thisTrack.getStartPoint());
        engine.drawspxy(MapView.SHOW_MANUAL_ROUTE);

        TextView tvName = (TextView) findViewById(R.id.map_data_content_view_name);
        TextView tvAddr = (TextView) findViewById(R.id.map_data_content_view_address);
        TextView tvTel = (TextView) findViewById(R.id.map_data_content_view_tel);
        Button btnCall = (Button) findViewById(R.id.map_data_content_view_call_button);
        LinearLayout llDifficulty = (LinearLayout) findViewById(R.id.map_content_difficulty_layout);
        TextView distance_TimeTextView = (TextView) findViewById(R.id.map_data_content_distance_time);
        Button btnDetail = (Button) findViewById(R.id.map_data_content_view_detail_btn);

        ImageView[] imgDifficulty = new ImageView[5];
        imgDifficulty[0] = (ImageView) findViewById(R.id.map_content_diff_image1);
        imgDifficulty[1] = (ImageView) findViewById(R.id.map_content_diff_image2);
        imgDifficulty[2] = (ImageView) findViewById(R.id.map_content_diff_image3);
        imgDifficulty[3] = (ImageView) findViewById(R.id.map_content_diff_image4);
        imgDifficulty[4] = (ImageView) findViewById(R.id.map_content_diff_image5);

        for (int i = 0; i < thisTrack.getDifficulty(); i++) {
            imgDifficulty[i].setImageResource(R.drawable.rate_star_big_on);
        }
        Log.i("TrackMapContent.java", "itemDistance=" + String.valueOf(itemDistance != null));
        itemDistance = String.valueOf((int) (Float.parseFloat(itemDistance) / 1000));
        tvName.setText(thisTrack.getName());
        distance_TimeTextView.setText(itemTime + " " + itemDistance + "KM");
        tvAddr.setVisibility(TextView.GONE);
        tvTel.setVisibility(TextView.GONE);
        btnCall.setVisibility(Button.GONE);
        btnDetail.setVisibility(Button.VISIBLE);
        llDifficulty.setVisibility(LinearLayout.VISIBLE);
        distance_TimeTextView.setVisibility(TextView.VISIBLE);

        btnDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                UtilDialog uit = new UtilDialog(TrackMapContent.this);
                uit.showDialog_route_plan_choice(thisTrack.getDescription(), null,
                        getString(R.string.dialog_close_button_text), null);

            }
        });
    }

    public native void savescr2bmp(String filename);

    public native void zoomspextend();

    private void zoomMapAndStore() {
        // Test for snap shot
        File file = Environment.getExternalStoragePublicDirectory("alamo");

        if (!file.exists()) {
            file.mkdir();
        }

        File newFile = new File(file, "alamo4.bmp");

        if (!file.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        newFileName = newFile.toString();

        engine.zoomspextend();

        Handler handler = new Handler();

        handler.postDelayed(run, 500);
    }

    private Runnable run = new Runnable() {
        public void run() {
            engine.savescr2bmp(newFileName);

            Intent intent = new Intent(TrackMapContent.this, FacebookActivity.class);

            intent.putExtra("FILEPATH", newFileName);

            TrackMapContent.this.startActivity(intent);
        }
    };

    @Override
    public void onClick(View arg0) {

        this.zoomMapAndStore();
    }

    @Override
    protected void onDestroy() {

        engine.newspxy(0);
        super.onDestroy();
    }
}