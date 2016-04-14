package com.kingwaytek.cpami.bykingTablet.app.poi;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.app.DataProgressDialog.DialogType;
import com.kingwaytek.cpami.bykingTablet.hardware.GPSListener;
import com.kingwaytek.cpami.bykingTablet.sql.POI;
import com.kingwaytek.cpami.bykingTablet.sql.Spoi;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.POIKindColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.SpoiColumn;
import com.kingwaytek.cpami.bykingTablet.view.ListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.SearchMode;

/**
 * POI Query POI Category List or SPOI Theme List
 *
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 */
public class POISelectionView extends ListActivity {

    private Intent itenCaller;

    // private CursorListType listType;
    private ActivityCaller listContent;
    private ListViewAdapter listAdapter;

    private static DialogType whichDialog = DialogType.NULL;
    //	private static Dialog mDialog = null;
    private UtilDialog progressDialog;

    private Button gohome;
    // Add by yawhaw
    private String spoi_catalog;

    /**
     * Called when this Activity started inheritance of Activity
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        itenCaller = getIntent();
        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.cursor_list_view);
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        // R.layout.title_bar);
        // setTitle(getString(R.string.title_default));
        // gohome = (Button) findViewById(R.id.go_home);
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

        // listType = (CursorListType) itenCaller
        // .getSerializableExtra("whichType");
        TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
        titleBar.setText(R.string.byking_function_poi_search_title);
        progressDialog = new UtilDialog(this);
        listContent = (ActivityCaller) itenCaller.getSerializableExtra("Atv_Caller");
        listAdapter = null;
        // Add by yawhaw
        spoi_catalog = itenCaller.getStringExtra("Spoi_Catalog");

        if (spoi_catalog != null && spoi_catalog.contains("台中市")) {
            spoi_catalog = "臺中市";
        }
        if (listContent == null) {
            throw new ActivityNotFoundException("List source is not valid.");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.title_text)).setText(title);
    }

    /**
     * Called after onCreate inheritance of Activity
     */
    @Override
    public void onResume() {
        super.onResume();

        Log.i("POISelectionView", "myContent : " + listContent);

        RelativeLayout lytSearch = (RelativeLayout) findViewById(R.id.list_search_box_layout);
        lytSearch.setVisibility(View.GONE);

        ShowLists();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("POI_CitySelection_onPause", "whichDialog:" + whichDialog);

        if (!whichDialog.equals(DialogType.NULL)) {
//			AlertDialogUtil.toggleDialogAsync(this, mDialog, ToggleSwitch.DISMISS);
            progressDialog.dismiss();

        }
    }

    /**
     * onListItemClick Handler inheritance of ListActivity
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("POISelectionView",
                "position = " + position + ", _id = " + id + ", item : "
                        + ((Object[]) listAdapter.getItem(position)).length);

        listItem_Click(position);

        super.onListItemClick(l, v, position, id);
    }

    private void listItem_Click(int args) {
        Log.i("POISelectionView", "ContentPosition :" + args);

        DialogHandler(DialogType.LOADING);
        // TODO put GPS location, category or theme
        Intent itenContent = new Intent(this, POIListView.class);
        itenContent.putExtra("POIList_Caller", listContent);
        // itenContent.putExtra("Point_Lon", 121.522069004011);
        // itenContent.putExtra("Point_Lat", 25.0270332995188);
        itenContent.putExtra("Point_Lon", GPSListener.lon);
        itenContent.putExtra("Point_Lat", GPSListener.lat);
        itenContent.putExtra("POI_Search", SearchMode.BY_SURROUNDING);

        String str = "";

        switch (listContent) {
            case POI:
                // itenContent.putExtra("POI_Category", POICategory.get(str)
                // .toString());
                str = ((Object[]) listAdapter.getItem(args))[1].toString();
                Log.i("POISelectionView", "str:" + str);
                itenContent.putExtra("POI_Category", str);
                break;
            case SPOI:
                str = ((Object[]) listAdapter.getItem(args))[0].toString();
                Log.i("POISelectionView", "str:" + str);
                itenContent.putExtra("Spoi_Theme", str);
                break;
            default:
                itenContent = null;
                break;
        }
        itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
        startActivityForResult(itenContent, listContent.getValue());
    }

    private void DialogHandler(DialogType type) {
        whichDialog = type;
        Log.i("POI_CitySelection_dialog_handler", "whichDialog:" + whichDialog);

        switch (type) {
            case LOADING:
//			Dialog dialog = new Dialog(this);
//			dialog.setTitle(R.string.dialog_loading_message);
//			mDialog = dialog;
//			mDialog.show();
                progressDialog.progressDialog(null, getString(R.string.dialog_loading_message));
                break;
            default:
                break;
        }
    }

    /**
     * Favorite Action results inheritance of Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == listContent.getValue()) {
            switch (listContent) {
                case POI:
                    itenCaller.putExtra("POI_Action", data.getSerializableExtra("POI_Action"));
                    // itenCaller
                    // .putExtra("POI_Name", data.getStringExtra("POI_Name"));
                    // itenCaller.putExtra("POI_Location", data
                    // .getParcelableExtra("POI_Location"));
                    // itenCaller.putExtra("POI_Others", dataz
                    // .getStringArrayExtra("POI_Others"));
                    setResult(RESULT_OK, itenCaller);
                    finish();
                    break;
                case SPOI:
                    itenCaller.putExtra("Action", data.getSerializableExtra("POI_Action"));
                    // itenCaller.putExtra("Name", data.getStringExtra("POI_Name"));
                    // itenCaller.putExtra("Location", data
                    // .getParcelableExtra("POI_Location"));
                    // itenCaller.putExtra("Others", data
                    // .getStringArrayExtra("POI_Others"));
                    setResult(RESULT_OK, itenCaller);
                    finish();
                    break;
                default:
                    break;
            }

        } else if (resultCode == RESULT_FIRST_USER) {
            setResult(RESULT_FIRST_USER);
            finish();
        }
    }

    /**
     * Show List Content
     */
    private void ShowLists() {
        final Cursor curListData;
        final String[] from;
        final int[] to = new int[] { R.id.cursor_row_text, R.id.cursor_row_ref };

        // TODO POI list item need to put data
        switch (listContent) {
            case POI:
                curListData = POI.GetCategoryList(this);
                from = new String[] { POIKindColumn.NAME.getName(), CursorColumn.ID.get() };
                break;
            case SPOI:
                curListData = Spoi.GetThemeList(this, spoi_catalog);
                from = new String[] { SpoiColumn.THEME.getName(), CursorColumn.ID.get() };
                break;
            default:
                curListData = null;
                from = null;
                break;
        }
        Log.i("POISelectionView", "list item count = " + curListData.getCount());
        if (listContent == ActivityCaller.POI) {// 周邊查詢
            listAdapter = new ListViewAdapter(this, R.layout.cursor_list_row_search, curListData, from, to);
        } else {
            listAdapter = new ListViewAdapter(this, R.layout.cursor_list_row, curListData, from, to);
        }
        curListData.close();

        listAdapter.getDataVisibilityStates().put(R.id.cursor_row_ref, false);
        setListAdapter(listAdapter);

    }
}