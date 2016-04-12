package com.kingwaytek.cpami.bykingTablet.app.track;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager.BadTokenException;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.AlertDialogUtil;
import com.kingwaytek.cpami.bykingTablet.app.POI_SMS;
import com.kingwaytek.cpami.bykingTablet.app.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.sql.Favorite;
import com.kingwaytek.cpami.bykingTablet.sql.History;
import com.kingwaytek.cpami.bykingTablet.sql.Track;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.TrackMenu;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class TrackContent extends Activity {

    private Intent itenCaller;
    private ActivityCaller myCaller;
    private int itemID;
    private Track thisTrack;

    public static final int DIALOG_PROGRESS = 1;
    public static final int DIALOG_IMPORT_PROGRESS = 2;
    public static final int DIALOG_WRITE_PROGRESS = 3;

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

    //	private ProgressDialog exportDialog;
    private UtilDialog progressDialog;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        itenCaller = getIntent();
        setContentView(R.layout.track_content);
        progressDialog = new UtilDialog(this);
        myCaller = (ActivityCaller) itenCaller.getSerializableExtra("Track_Caller");
        itemID = (int) itenCaller.getLongExtra("Track_ID", -1);

        Log.i("TrackContent", "item id from caller = " + itemID);
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

        SetTrackUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // getMenuInflater().inflate(R.layout.menu_poi_favorite, menu);

        menu.add(0, TrackMenu.SHOW.getId(), 0, TrackMenu.SHOW.getTitle());
        switch (myCaller) {
            case FAVORITE:
            case HISTORY:

                break;
            case TRACK:
                menu.add(0, TrackMenu.ADD_FAVORITE.getId(), 0, TrackMenu.ADD_FAVORITE.getTitle());
                break;
            default:
                break;
        }
        menu.add(0, TrackMenu.DELETE.getId(), 0, TrackMenu.DELETE.getTitle());
        menu.add(0, TrackMenu.SET_LOCATION.getId(), 0, TrackMenu.SET_LOCATION.getTitle());
        menu.add(0, TrackMenu.SHARE.getId(), 0, TrackMenu.SHARE.getTitle());
        menu.add(0, TrackMenu.EXPORT.getId(), 0, TrackMenu.EXPORT.getTitle());

        return true;
    }

//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch (id) {
//		case DIALOG_WRITE_PROGRESS:
//			exportDialog = new ProgressDialog(this);
//			exportDialog.setTitle(TrackMenu.EXPORT.getTitle());
//			exportDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			exportDialog.setMessage(getString(R.string.track_export_in_progress_text));
//			return exportDialog;
//		default:
//			break;
//		}
//		return null;
//	}

    /**
     * This Activity will finish after a valid menu action occurred
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Log.i("TrackContent", "Menu item clicked : " + item.getItemId());

        switch (TrackMenu.get(item.getItemId())) {
            case SHOW: // cause finish
                DrawTrack();
                break;
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
            case SET_LOCATION: // cause finish
                SetLocation();
                Log.i("TrackContent", "use item as Point for navigation.");
                return true;
            case SHARE: // TODO: rewrite for correspond class
                Log.i("TrackContent", "share this poi via SMS.");
                Intent itenSMS = new Intent(this, POI_SMS.class);
                itenSMS.putExtra("POI_Name", thisTrack.getName());
                itenSMS.putExtra("POI_Lon", String.valueOf(thisTrack.getTrackPoints().get(0).getLongitude()));
                itenSMS.putExtra("POI_Lat", String.valueOf(thisTrack.getTrackPoints().get(0).getLatitude()));
                startActivity(itenSMS);
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
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
        UtilDialog uit = new UtilDialog(TrackContent.this);
        uit.showDialog_route_plan_choice(alertMsg, null, getString(R.string.dialog_ok_button_text), null);
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

    private void DrawTrack() {
        itenCaller.putExtra("Track_Action", TrackMenu.SHOW);
        itenCaller.putExtra("Track_Name", thisTrack.getName());
        itenCaller.putExtra("Track_Location", thisTrack.getID());
        setResult(RESULT_OK, itenCaller);
        finish();
    }

    private void SetLocation() {
        itenCaller.putExtra("Track_Action", TrackMenu.SET_LOCATION);
        itenCaller.putExtra("Track_Name", thisTrack.getName());
        GeoPoint point = new GeoPoint(thisTrack.getTrackPoints().get(0).getLongitude(), thisTrack.getTrackPoints()
                .get(0).getLongitude());
        itenCaller.putExtra("Track_Start_Location", point);
        int last = thisTrack.getTrackPoints().size() - 1;
        point = new GeoPoint(thisTrack.getTrackPoints().get(last).getLongitude(), thisTrack.getTrackPoints().get(last)
                .getLongitude());
        itenCaller.putExtra("Track_End_Location", point);
        setResult(RESULT_OK, itenCaller);
        finish();
    }

    private void ExportTrack(TrackExportExt whichExt) {
        Log.i("TrackContent", "selected ext:" + whichExt.getExtension());

        showDialogSafely(DIALOG_WRITE_PROGRESS);
        // exportDialog.setMessage("正在匯出 " + whichExt.toString() + "...");
        progressDialog.progressDialog("正在匯出 " + whichExt.toString() + "...", null);

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

                UtilDialog uit = new UtilDialog(TrackContent.this);
                uit.showDialog_route_plan_choice(success ? getString(R.string.data_export_success_msg)
                        : getString(R.string.data_export_fail_msg), TrackContent.this.getString(message), getString(R.string.dialog_close_button_text), null);

                // AlertDialog dialog = null;
                // AlertDialog.Builder builder = new
                // AlertDialog.Builder(TrackContent.this);
                // builder.setMessage(TrackContent.this.getString(message));
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

    private void SetTrackUI() {
        TextView tvName = (TextView) findViewById(R.id.track_content_name);
        TextView tvStart = (TextView) findViewById(R.id.track_content_start);
        TextView tvEnd = (TextView) findViewById(R.id.track_content_end);
        TextView tvOrigin = (TextView) findViewById(R.id.track_content_origin);
        TextView tvDestanation = (TextView) findViewById(R.id.track_content_destination);
        TextView tvDistance = (TextView) findViewById(R.id.track_content_distance);
        TextView tvDescription = (TextView) findViewById(R.id.track_content_description);

        // tvAddress.setOnClickListener(this);

        tvName.setText(thisTrack.getName());
        tvStart.setText(thisTrack.getStartTime().toLocaleString());
        tvEnd.setText(thisTrack.getEndTime().toLocaleString());

        GeoPoint curPoint = new GeoPoint();
        curPoint.setLongitude(thisTrack.getTrackPoints().get(0).getLongitude());
        curPoint.setLatitude(thisTrack.getTrackPoints().get(0).getLatitude());
        tvOrigin.setText("起始座標－經度：" + curPoint.getLongitude() + ", 緯度：" + curPoint.getLatitude());

        curPoint.setLongitude(thisTrack.getTrackPoints().get(thisTrack.getTrackPoints().size() - 1).getLongitude());
        curPoint.setLatitude(thisTrack.getTrackPoints().get(thisTrack.getTrackPoints().size() - 1).getLatitude());
        tvDestanation.setText("結束座標－經度：" + curPoint.getLongitude() + ", 緯度：" + curPoint.getLatitude());

        double disTrack = thisTrack.CalculateDistance();
        double roundMeter = Math.round(disTrack);
        double roundDM = Math.round(disTrack * 100);
        tvDistance.setText("總距離： 約 "
                + (disTrack >= 1000 ? "" + (roundMeter / 1000) + "公里" : "" + (Math.round(roundDM / 100) + "公尺")));
        tvDescription.setText(thisTrack.getDescription());
    }
}