package com.kingwaytek.cpami.bykingTablet.app.ui.track;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTrackRecord;
import com.kingwaytek.cpami.bykingTablet.app.service.TrackingService;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseMapActivity;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PolyHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.TrackingFileUtil;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.util.ArrayList;

/**
 * 軌跡錄製地圖
 *
 * @author Vincent (2016/7/14)
 */
public class UiTrackMapActivity extends BaseMapActivity {

    private Menu menu;

    private LinearLayout gpsStateLayout;
    private TextView gpsStateText;
    private ProgressBar gpsStateCircle;
    private FrameLayout triggerBtnLayout;

    private ImageButton trackBtn;

    private BroadcastReceiver receiver;

    private Intent trackingServiceIntent;
    private LatLng preLatLng;

    private ItemsTrackRecord trackItem;
    private static final int INVALIDATED_INDEX = -1;
    private boolean isInfoInEditing;

    private Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    @Override
    protected void onMapReady() {
        drawPolylineIfTrackFileContainsData();
        getRecordDataAndDrawLine();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ENTRY_TYPE == ENTRY_TYPE_TRACKING) {
            trackingServiceIntent = new Intent(this, TrackingService.class);
            gettingReceive();
            checkGpsAndServiceState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ENTRY_TYPE == ENTRY_TYPE_TRACKING)
            stopReceiving();
    }

    @Override
    protected int getMapLayout() {
        return R.layout.activity_track_map;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_bike_track);
    }

    @Override
    protected void findViews() {
        mapRootLayout = (RelativeLayout) findViewById(R.id.mapRootLayout);

        gpsStateLayout = (LinearLayout) findViewById(R.id.gpsStateLayout);
        gpsStateText = (TextView) findViewById(R.id.text_gpsState);
        gpsStateCircle = (ProgressBar) findViewById(R.id.gpsStateLoadingCircle);
        trackBtn = (ImageButton) findViewById(R.id.trackButton);
        triggerBtnLayout = (FrameLayout) findViewById(R.id.trackButtonLayout);
    }

    @Override
    protected void setListener() {
        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTrackingRequest();
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        switch (ENTRY_TYPE) {
            case ENTRY_TYPE_TRACKING:
                if (TrackingFileUtil.isTrackingFileContainsData() && !TrackingService.IS_TRACKING_REQUESTED)
                    MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_SAVE);
                break;

            case ENTRY_TYPE_TRACK_VIEWING:
                MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_INFO);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case ACTION_SAVE:
                showSaveDialog();
                break;

            case ACTION_INFO:
                showTrackInfo();
                break;
        }

        return true;
    }

    @Override
    protected void onLocateMyPosition(Location location) {

    }

    @Override
    protected void requestLocationUpdate() {
        // Do Nothing!
        Log.i(TAG, "requestLocationUpdate (Do Nothing)");
    }

    @Override
    protected void removeLocationUpdate() {
        // Do Nothing!
        Log.i(TAG, "removeLocationUpdate (Do Nothing)");
    }

    private void drawPolylineIfTrackFileContainsData() {
        if (ENTRY_TYPE == ENTRY_TYPE_TRACKING && TrackingFileUtil.isTrackingFileContainsData()) {
            ArrayList<LatLng> latLngList = TrackingFileUtil.readTrackingLatLng();

            PolylineOptions polyLine = new PolylineOptions();

            if (notNull(latLngList)) {
                for (LatLng latLng : latLngList) {
                    polyLine.add(latLng);
                }
                polyLine.color(ContextCompat.getColor(appContext(), R.color.md_blue_A700));
                polyLine.width(20);

                map.addPolyline(polyLine);

                moveCameraAndZoom(latLngList.get(0), 16);
            }
        }
    }

    private void getRecordDataAndDrawLine() {
        if (ENTRY_TYPE == ENTRY_TYPE_TRACK_VIEWING) {
            triggerBtnLayout.setVisibility(View.GONE);

            int trackIndex = getIntent().getIntExtra(BUNDLE_TRACK_INDEX, INVALIDATED_INDEX);
            trackItem = JsonParser.getTrackRecord(trackIndex);

            if (notNull(trackItem)) {
                ArrayList<LatLng> latLngList = PolyHelper.decodePolyLine(trackItem.POLY_LINE);

                PolylineOptions polyLine = new PolylineOptions();

                for (LatLng latLng : latLngList) {
                    polyLine.add(latLng);
                }
                polyLine.color(ContextCompat.getColor(appContext(), R.color.md_blue_A700));
                polyLine.width(20);

                map.addPolyline(polyLine);

                moveCameraAndZoom(latLngList.get(0), 16);
            }
        }
    }

    private void checkGpsAndServiceState() {
        if (MyLocationManager.isGpsDisabled()) {
            AppController.getInstance().removeLocationManager();

            if (isTrackingServiceRunning())
                stopService(trackingServiceIntent);

            DialogHelper.showGpsRequestDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
        }
        else
            checkServiceAndSetButton();
    }

    private void checkServiceAndSetButton() {
        if (TrackingService.IS_TRACKING_REQUESTED)
            trackBtn.setImageResource(R.drawable.selector_button_stop);
        else
            trackBtn.setImageResource(R.drawable.selector_button_start);

        if (!isTrackingServiceRunning())
            startService(trackingServiceIntent);
    }

    private void sendTrackingRequest() {
        final Intent intent = new Intent(TRACKING_BROADCAST_FOR_SERVICE);

        if (TrackingService.IS_TRACKING_REQUESTED) {
            sendStopRequest(intent);
        }
        else {
            if (TrackingFileUtil.isTrackingFileEmpty()) {
                sendStartRequest(intent);
            }
            else {
                DialogHelper.showTrackFileOverrideConfirmDialog(this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendStartRequest(intent);
                    }
                });
            }
        }
    }

    private void sendStartRequest(Intent intent) {
        intent.putExtra(TRACKING_REQUEST_STARTING, true);
        trackBtn.setImageResource(R.drawable.selector_button_stop);

        TrackingFileUtil.cleanTrackingFile();
        map.clear();
        menu.clear();

        LocalBroadcastManager.getInstance(appContext()).sendBroadcast(intent);
    }

    private void sendStopRequest(Intent intent) {
        intent.putExtra(TRACKING_REQUEST_STARTING, false);
        trackBtn.setImageResource(R.drawable.selector_button_start);
        showTrackingText(false);

        LocalBroadcastManager.getInstance(appContext()).sendBroadcast(intent);
        TrackingFileUtil.closeWriter();

        if (TrackingFileUtil.isTrackingFileContainsData()) {
            MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_SAVE);
            showSaveDialog();
        }
    }

    private void showSaveDialog() {
        DialogHelper.showTrackSaveDialog(this, new DialogHelper.OnTrackSavedCallBack() {
            @Override
            public void onTrackSaved(String name, int difficulty, String description) {
                if (!TrackingFileUtil.isTrackingFileEmpty()) {
                    ArrayList<LatLng> latLngList = TrackingFileUtil.readTrackingLatLng();

                    FavoriteHelper.addTrack(
                            Utility.getCurrentTimeInFormat(),
                            name, difficulty, description,
                            getEncodedPolyline(latLngList),
                            getTrackDistance(latLngList));

                    TrackingFileUtil.cleanTrackingFile();
                    menu.clear();
                }
            }
        });
    }

    private String getEncodedPolyline(ArrayList<LatLng> latLngList) {
        if (notNull(latLngList)) {
            String encoded = PolyUtil.encode(latLngList);
            Log.i(TAG, "encodedPolyline: " + encoded);
            return encoded;
        }
        return null;
    }

    private String getTrackDistance(ArrayList<LatLng> latLngList) {
        double distance = 0;

        for (int i = 0; i < latLngList.size(); i++) {
            if (i + 1 < latLngList.size())
                distance += Utility.getDistance(latLngList.get(i), latLngList.get(i + 1));
        }
        return Utility.getDistanceText(distance);
    }

    private void gettingReceive() {
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    getIntentAndDoActions(intent);
                }
            };
        }
        LocalBroadcastManager.getInstance(appContext()).registerReceiver(receiver, new IntentFilter(TRACKING_BROADCAST_FOR_ACTIVITY));
    }

    private void stopReceiving() {
        if (notNull(receiver))
            LocalBroadcastManager.getInstance(appContext()).unregisterReceiver(receiver);
    }

    private void getIntentAndDoActions(Intent intent) {
        if (intent.hasExtra(TRACKING_IS_GPS_LOCATED)) {
            if (intent.getBooleanExtra(TRACKING_IS_GPS_LOCATED, false))
                onGpsLocated();
            else
                onGpsLocating();
        }

        if (intent.hasExtra(TRACKING_IS_DOING_RIGHT_NOW)) {
            showTrackingText(true);
            drawingPolyline((LatLng) intent.getParcelableExtra(TRACKING_IS_DOING_RIGHT_NOW));
        }
    }

    public void onGpsLocated() {
        gpsStateCircle.setVisibility(View.GONE);
        gpsStateText.setText(getString(R.string.track_gps_locate_done));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gpsStateLayout.setVisibility(View.GONE);
            }
        }, 3000);
    }

    public void onGpsLocating() {
        gpsStateLayout.setVisibility(View.VISIBLE);
        gpsStateText.setText(getString(R.string.track_gps_locating));
        gpsStateCircle.setVisibility(View.VISIBLE);
        showTrackingText(false);
    }

    private void drawingPolyline(LatLng newLatLng) {
        moveCamera(newLatLng);

        if (preLatLng == null)
            preLatLng = newLatLng;
        else {
            map.addPolyline(new PolylineOptions()
                    .add(preLatLng, newLatLng)
                    .color(ContextCompat.getColor(appContext(), R.color.md_blue_A700))
                    .width(20));

            preLatLng = newLatLng;
        }
    }

    private void showTrackInfo() {
        View view = PopWindowHelper.getTrackInfoPopView(this, mapRootLayout);

        final EditText trackName = (EditText) view.findViewById(R.id.edit_trackName);
        final EditText trackDesc = (EditText) view.findViewById(R.id.edit_trackDescription);
        final RatingBar trackRating = (RatingBar) view.findViewById(R.id.trackRatingBar);
        TextView trackLength = (TextView) view.findViewById(R.id.text_trackLength);
        final TextView closeBtn = (TextView) view.findViewById(R.id.trackClose);
        final TextView editBtn = (TextView) view.findViewById(R.id.trackEdit);

        trackName.setText(trackItem.NAME);
        trackDesc.setText(trackItem.DESCRIPTION);
        trackName.setEnabled(false);
        trackDesc.setEnabled(false);
        trackRating.setProgress(trackItem.DIFFICULTY);
        trackLength.setText(trackItem.DISTANCE);
        trackRating.setIsIndicator(true);

        isInfoInEditing = false;

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopWindowHelper.dismissPopWindow();
                closeBtn.setOnClickListener(null);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInfoInEditing) {
                    isInfoInEditing = false;

                    int trackIndex = getIntent().getIntExtra(BUNDLE_TRACK_INDEX, INVALIDATED_INDEX);
                    FavoriteHelper.updateTrackInfo(trackIndex, trackName.getText().toString(), (int)trackRating.getRating(), trackDesc.getText().toString());

                    trackName.setEnabled(false);
                    trackDesc.setEnabled(false);
                    trackRating.setIsIndicator(true);
                    editBtn.setText(getString(R.string.actionbar_edit));

                    trackItem = JsonParser.getTrackRecord(trackIndex);
                    Utility.toastShort(getString(R.string.update_done));
                }
                else {
                    isInfoInEditing = true;
                    trackName.setEnabled(true);
                    trackDesc.setEnabled(true);
                    trackRating.setIsIndicator(false);

                    trackName.setSelection(trackName.getText().length());
                    trackDesc.setSelection(trackDesc.getText().length());
                    trackName.requestFocus();

                    editBtn.setText(getString(R.string.poi_save));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (TrackingService.IS_TRACKING_REQUESTED)
            finish();
        else {
            if (ENTRY_TYPE == ENTRY_TYPE_TRACKING) {
                stopService(trackingServiceIntent);
                Log.i(TAG, "StopService");
            }
            showTrackingText(false);
            finish();
        }
        Log.i(TAG, "IsServiceRunning: " + isTrackingServiceRunning());
    }
}
