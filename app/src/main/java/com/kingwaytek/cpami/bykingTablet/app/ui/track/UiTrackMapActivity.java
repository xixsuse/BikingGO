package com.kingwaytek.cpami.bykingTablet.app.ui.track;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTrackRecord;
import com.kingwaytek.cpami.bykingTablet.app.service.TrackingService;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseMapActivity;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.BitmapUtility;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.NotifyHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PolyHelper;
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

    private ScrollView trackInfoLayout;
    private TextView text_trackName;
    private RatingBar trackRating;
    private TextView text_trackDescription;
    private TextView text_trackLength;

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
        triggerBtnLayout = (FrameLayout) findViewById(R.id.trackButtonLayout);
        trackBtn = (ImageButton) findViewById(R.id.trackButton);

        trackInfoLayout = (ScrollView) findViewById(R.id.trackInfoLayout);
        text_trackName = (TextView) findViewById(R.id.text_trackName);
        trackRating = (RatingBar) findViewById(R.id.trackRatingBar);
        text_trackDescription = (TextView) findViewById(R.id.text_trackDescription);
        text_trackLength = (TextView) findViewById(R.id.text_trackLength);

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
                MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_DELETE, ACTION_UPLOAD, ACTION_EDIT);
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

            case ACTION_EDIT:
                showEditDialog();
                break;

            case ACTION_UPLOAD:

                break;

            case ACTION_DELETE:
                deleteTrack();
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

                Bitmap pinBitmap = getBitmapFromMemCache(BITMAP_KEY_PIN_PLACE);

                if (pinBitmap == null) {
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pin_place);
                    pinBitmap = BitmapUtility.convertDrawableToBitmap(drawable, getResources().getDimensionPixelSize(R.dimen.icon_common_size));
                    addBitmapToMemoryCache(BITMAP_KEY_PIN_PLACE, pinBitmap);
                }
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(pinBitmap);

                MarkerOptions marker = new MarkerOptions();

                marker.icon(icon);
                marker.position(latLngList.get(0));
                map.addMarker(marker);

                marker.position(latLngList.get(latLngList.size() - 1));
                map.addMarker(marker);

                moveCameraAndZoom(latLngList.get(0), 16);
            }

            setInfoLayout();
        }
    }

    private void setInfoLayout() {
        trackInfoLayout.setVisibility(ENTRY_TYPE == ENTRY_TYPE_TRACK_VIEWING ? View.VISIBLE : View.GONE);

        text_trackName.setText(trackItem.NAME);
        trackRating.setRating(trackItem.DIFFICULTY);
        text_trackDescription.setText(trackItem.DESCRIPTION);
        text_trackLength.setText(trackItem.DISTANCE);
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
        final ArrayList<LatLng> latLngList = TrackingFileUtil.readTrackingLatLng();
        final String trackLength = getTrackDistance(latLngList);

        DialogHelper.showTrackSaveDialog(this, trackLength, new DialogHelper.OnTrackSavedCallBack() {
            @Override
            public void onTrackSaved(String name, int difficulty, String description) {
                if (!TrackingFileUtil.isTrackingFileEmpty()) {

                    FavoriteHelper.addTrack(
                            Utility.getCurrentTimeInFormat(),
                            name, difficulty, description,
                            getEncodedPolyline(latLngList),
                            trackLength);

                    TrackingFileUtil.cleanTrackingFile();
                    menu.clear();

                    Utility.toastShort(AppController.getInstance().getString(R.string.track_save_done));
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

    private void showEditDialog() {
        DialogHelper.showTrackEditDialog(this, trackItem, new DialogHelper.OnTrackSavedCallBack() {
            @Override
            public void onTrackSaved(String name, int difficulty, String description) {
                int trackIndex = getIntent().getIntExtra(BUNDLE_TRACK_INDEX, INVALIDATED_INDEX);
                FavoriteHelper.updateTrackInfo(trackIndex, name, difficulty, description);
                trackItem = JsonParser.getTrackRecord(trackIndex);
                setInfoLayout();
                Utility.toastShort(getString(R.string.update_done));
            }
        });
    }

    private void deleteTrack() {
        DialogHelper.showDeleteConfirmDialog(this, trackItem.NAME, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int trackIndex = getIntent().getIntExtra(BUNDLE_TRACK_INDEX, INVALIDATED_INDEX);
                FavoriteHelper.removeTrack(trackIndex);
                finish();
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
            NotifyHelper.clearServiceNotification();
            showTrackingText(false);
            finish();
        }
        Log.i(TAG, "IsServiceRunning: " + isTrackingServiceRunning());
    }
}