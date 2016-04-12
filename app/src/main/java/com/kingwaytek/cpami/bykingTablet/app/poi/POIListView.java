package com.kingwaytek.cpami.bykingTablet.app.poi;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ApplicationGlobal;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.sql.History;
import com.kingwaytek.cpami.bykingTablet.sql.POI;
import com.kingwaytek.cpami.bykingTablet.sql.SQLiteBot;
import com.kingwaytek.cpami.bykingTablet.sql.Spoi;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.ContentType;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.POIColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;
import com.kingwaytek.cpami.bykingTablet.view.ListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.POIMenu;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.SearchMode;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * POI Query POI List Activity
 *
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 */
public class POIListView extends ListActivity implements OnClickListener {

    private Intent itenCaller;

    // private CursorListType listType;
    private ActivityCaller listContent;
    private SearchMode poiSearchMode;
    private ListViewAdapter listAdapter;
    // private POIListAdapter poiListAdapter;
    private String searchString;

    private TextView tvEmpty;
    private Button btnSearch;
    // private Button btnDelete;
    // private Button btnCancel;
    private EditText edtSearch;
    private RelativeLayout lytSearch;

    private double lon;
    private double lat;
    private String keyWord;
    private String catString;
    private String cityString;
    private String themeString;
    private Button gohome;

    // List Data
    private Map<Integer, Object[]> listData;
    private Location loc;

    /**
     * Called when this Activity started inheritance of Activity
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.cursor_list_view);
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        // R.layout.title_bar);

        // setTitle(getString(R.string.byking_function_poi_search_title) + " ： "
        // + getString(R.string.poi_search_poiselection_prompt));
        // loc = ApplicationGlobal.gpsListener.getLastLocation();
        itenCaller = getIntent();

        listContent = (ActivityCaller) itenCaller
                .getSerializableExtra("POIList_Caller");
        poiSearchMode = (SearchMode) itenCaller
                .getSerializableExtra("POI_Search");
        listAdapter = null;
        searchString = "";
        lon = itenCaller.getDoubleExtra("Point_Lon", -1);
        lat = itenCaller.getDoubleExtra("Point_Lat", -1);
        keyWord = itenCaller.getStringExtra("POI_Keyword") == null ? ""
                : itenCaller.getStringExtra("POI_Keyword");
        catString = itenCaller.getStringExtra("POI_Category") == null ? ""
                : itenCaller.getStringExtra("POI_Category");
        cityString = itenCaller.getStringExtra("POI_City") == null ? ""
                : itenCaller.getStringExtra("POI_City");
        themeString = itenCaller.getStringExtra("Spoi_Theme") == null ? ""
                : itenCaller.getStringExtra("Spoi_Theme");

        if (listContent == null) {
            throw new ActivityNotFoundException("List source is not valid.");
        }

        tvEmpty = (TextView) findViewById(android.R.id.empty);
        btnSearch = (Button) findViewById(R.id.list_search_box_button);
        // btnDelete = (Button)
        // findViewById(R.id.cursor_list_view_delete_button);
        // btnCancel = (Button)
        // findViewById(R.id.cursor_list_view_cancel_button);
        edtSearch = (EditText) findViewById(R.id.list_search_box_edit);
        lytSearch = (RelativeLayout) findViewById(R.id.list_search_box_layout);
        btnSearch.setOnClickListener(this);
        // btnDelete.setOnClickListener(this);
        // btnCancel.setOnClickListener(this);
        edtSearch.setText(searchString);
        lytSearch.setVisibility(View.GONE);

        tvEmpty.setVisibility(TextView.GONE);

        listAdapter = (ListViewAdapter) getLastNonConfigurationInstance();
        if (listAdapter == null) {
            FirstRun();
        } else {
            setListAdapter(listAdapter);
        }

        TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
        titleBar.setText(R.string.spoi_select_title);
        // gohome = (Button) findViewById(R.id.go_home);
        // gohome.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // setResult(RESULT_FIRST_USER);
        // finish();
        // return;
        // }
        // });
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.title_text)).setText(title);
        ((TextView) findViewById(R.id.title_text2)).setText("");
    }

    /**
     * Called after onCreate inheritance of Activity
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i("POIListView", "myContent : " + listContent);
    }

    private void FirstRun() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                loadList();
                ShowLists();
            }
        });
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        // final LinkedHashMap<Integer, Object[]> data = (LinkedHashMap<Integer,
        // Object[]>) listData;
        final ListViewAdapter data = listAdapter;
        return data;
    }

    /**
     * onListItemClick Handler inheritance of ListActivity
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("CursorListView", "position = " + position + ", _id = " + id);

        listItem_Click(id);
        if (listContent.equals(ActivityCaller.POI)) {
            Object[] item = (Object[]) listAdapter.getItem(position);
            PutHistory(item[0].toString(), Long.valueOf(item[3].toString()));
        }
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        return; // Nothing to do.
    }

    private void listItem_Click(long args) {
        long id = Long.valueOf(((Object[]) listAdapter.getItem((int) args))[3]
                .toString());
        Log.i("CursorListView", "ContentID :" + args + ", ref:" + id);

        Intent itenContent;

        switch (listContent) {
            case POI:
                // itenContent = new Intent(this, POIContent.class);
                itenContent = new Intent(this, POIMapContent.class);
                itenContent.putExtra("POI_Caller", listContent);
                itenContent.putExtra("POI_ID", id);
                break;
            case SPOI:
                itenContent = new Intent(this, SpoiContent.class);
                itenContent.putExtra("Spoi_Caller", listContent);
                itenContent.putExtra("Spoi_ID", id);
                break;
            default:
                itenContent = null;
                break;
        }
        itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
        startActivityForResult(itenContent, listContent.getValue());
    }

    /**
     * Favorite Action results inheritance of Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == listContent.getValue()) {
            POIMenu action = (POIMenu) data.getSerializableExtra("POI_Action");
            if (action != null) {
                switch (action) {
                    case NAVIGATION:
                        itenCaller.putExtra("POI_Action",
                                ContextMenuOptions.NAVIGATION);
                        break;

                    default:
                        break;
                }
            }
            // switch ((POIMenu) data.getSerializableExtra("POI_Action")) {
            // case DRAW_MAP:
            // itenCaller.putExtra("POI_Action", ContextMenuOptions.DRAW_MAP);
            // break;
            // case SET_ORIGIN:
            // itenCaller
            // .putExtra("POI_Action", ContextMenuOptions.SET_ORIGIN);
            // break;
            // case SET_DESTINATION:
            // itenCaller.putExtra("POI_Action",
            // ContextMenuOptions.SET_DESTINATION);
            // break;
            // default:
            // break;
            // }
            // itenCaller.putExtra("POI_Name", data.getStringExtra("POI_Name"));
            // itenCaller.putExtra("POI_Location", data
            // .getParcelableExtra("POI_Location"));
            // itenCaller.putExtra("POI_Others", data
            // .getStringArrayExtra("POI_Others"));

            setResult(RESULT_OK, itenCaller);
            finish();
        } else if (resultCode == RESULT_FIRST_USER) {
            setResult(RESULT_FIRST_USER);
            finish();
        }
    }

    public void PutHistory(String name, long id) {
        History history = new History(this);
        history.setName(name);
        history.setItemID((int) id);
        history.setType(ContentType.POI.getValue());
        history.Put();
    }

    private Map<Integer, Object[]> ListDataFactory(final Cursor cursor) {
        if (cursor == null || cursor.getCount() <= 0) {
            return null;
        }
        Map<Integer, Object[]> listData = new LinkedHashMap<Integer, Object[]>();
        String[] titleData = new String[cursor.getCount()];
        String[] extData = new String[cursor.getCount()];
        String[] footData = new String[cursor.getCount()];
        String[] referData = new String[cursor.getCount()];
        boolean hasDistance = poiSearchMode.equals(SearchMode.BY_SURROUNDING);
        double destsqr = -1;

        cursor.moveToPosition(-1);
        int i = 0;
        String subBranch = "";
        while (cursor.moveToNext()) {

            destsqr = (hasDistance ? cursor.getDouble(cursor
                    .getColumnIndex("destsqr")) : -1);
            i = cursor.getColumnIndex(POIColumn.SUB_BRANCH.getName());
            subBranch = (i == -1) ? "" : cursor.getString(i);
            subBranch = (subBranch.trim().length() == 0) ? "" : "(" + subBranch
                    + ")";

            titleData[cursor.getPosition()] = cursor.getString(cursor
                    .getColumnIndex(POIColumn.NAME.getName())) + subBranch;
            String tempString;

            Location loc = null;
            if (ApplicationGlobal.gpsListener != null) {
                loc = ApplicationGlobal.gpsListener.getLastLocation();
            }

            if (loc == null) {
                tempString = "約--km";
            } else {
                DecimalFormat df = new DecimalFormat("#.#");
                tempString = "約 "
                        + String.valueOf(df.format((float) (Math.sqrt(destsqr)) / 1000))
                        + "KM";
            }
            extData[cursor.getPosition()] = tempString;
            footData[cursor.getPosition()] = cursor.getString(cursor
                    .getColumnIndex(POIColumn.ADDRESS.getName()));
            referData[cursor.getPosition()] = String.valueOf(cursor
                    .getInt(cursor.getColumnIndex(CursorColumn.ID.get())));

        }

        listData.put(R.id.cursor_row_text, titleData);
        listData.put(R.id.cursor_row_extention, extData);
        listData.put(R.id.cursor_row_foot, footData);
        listData.put(R.id.cursor_row_ref, referData);

        return listData;
    }

    private void loadList() {
        final Cursor curListData;

        // TODO rewrite keyword, citycode, geopoint, and category
        switch (poiSearchMode) {
            case BY_KEYWORD:
                curListData = POI.Search(this, keyWord, cityString);
                break;
            case BY_SURROUNDING:

                switch (listContent) {
                    case POI:

                        if (getIntent().getStringExtra("RentStation") != null
                                && getIntent().getStringExtra("RentStation").contains(
                                "Y")) {
                            String city = getIntent().getStringExtra("RentCity");
                            Cursor temp = null;
                            SQLiteBot sqliteDatabase;

                            sqliteDatabase = new SQLiteBot(
                                    this.getString(R.string.SQLite_App_Database_Name),
                                    this.getString(R.string.SQLite_App_Database_Path),
                                    TableName.POI);
                            // String sqlCommand =
                            // "SELECT * FROM poi WHERE  p_kind = 'POI_071008'  AND p_towncode like '%A%'";

                            GeoPoint point;
                            String sqlCommand;
                            Location loc = null;
                            if (ApplicationGlobal.gpsListener != null) {
                                loc = ApplicationGlobal.gpsListener.getLastLocation();
                            }

                            if (loc != null) {
                                point = new GeoPoint(loc.getLongitude(),
                                        loc.getLatitude());
                                sqlCommand = "select " + POIColumn.ID.getName() + " "
                                        + CursorColumn.ID.get() + ","
                                        + POIColumn.NAME.getName() + ","
                                        + POIColumn.ADDRESS.getName() + ","
                                        + POIColumn.TMX.getName() + ","
                                        + POIColumn.TMY.getName() + "," + "(abs("
                                        + point.getTmX() + "-"
                                        + POIColumn.TMX.getName() + ")*abs("
                                        + point.getTmX() + "-"
                                        + POIColumn.TMX.getName() + ")) + " + "(abs("
                                        + point.getTmY() + "-"
                                        + POIColumn.TMY.getName() + ")*abs("
                                        + point.getTmY() + "-"
                                        + POIColumn.TMY.getName() + ")) destsqr"
                                        + " , " + POIColumn.SUB_BRANCH.getName()
                                        + " from " + TableName.POI.getName()
                                        + " where " + POIColumn.CATEGORY.getName()
                                        + " like 'POI_071008%' AND p_towncode like '%"
                                        + city + "%' order by destsqr";
                            } else {
                                Toast.makeText(this, "無法取得當前位置", Toast.LENGTH_SHORT)
                                        .show();
                                sqlCommand = "select " + POIColumn.ID.getName() + " "
                                        + CursorColumn.ID.get() + ","
                                        + POIColumn.NAME.getName() + ","
                                        + POIColumn.ADDRESS.getName() + ","
                                        + POIColumn.TMX.getName() + ","
                                        + POIColumn.TMY.getName() + ","
                                        + "(-1) destsqr" + " , "
                                        + POIColumn.SUB_BRANCH.getName() + " from "
                                        + TableName.POI.getName() + " where "
                                        + POIColumn.CATEGORY.getName()
                                        + " like 'POI_071008%' AND p_towncode like '%"
                                        + city + "%' order by destsqr";
                            }

                            // select p_id
                            // _id,p_name,p_address,p_colx,p_coly,(abs(302708.53493973904-p_colx)*abs(302708.53493973904-p_colx))
                            // +
                            // (abs(2768867.2980233775-p_coly)*abs(2768867.2980233775-p_coly))
                            // destsqr , p_subbranch from poi where p_kind like
                            // 'POI_071008%' AND p_towncode like '%A%' order by destsqr

                            sqliteDatabase.setSQLCommand(sqlCommand);
                            temp = sqliteDatabase.QueryWithCommand();
                            curListData = temp;
                            // POI.Search(POIListView.this, new GeoPoint(lon,
                            // lat), catString);

                        } else if (getIntent().getStringExtra("ParkStation") != null
                                && getIntent().getStringExtra("ParkStation").contains(
                                "Y")) {
                            String city = getIntent().getStringExtra("ParkCity");
                            Cursor temp = null;
                            SQLiteBot sqliteDatabase;

                            sqliteDatabase = new SQLiteBot(
                                    this.getString(R.string.SQLite_App_Database_Name),
                                    this.getString(R.string.SQLite_App_Database_Path),
                                    TableName.POI);
                            // String sqlCommand =
                            // "SELECT * FROM poi WHERE  p_kind = 'POI_071008'  AND p_towncode like '%A%'";

                            GeoPoint point;
                            String sqlCommand;
                            if (ApplicationGlobal.gpsListener != null) {
                                Location loc = ApplicationGlobal.gpsListener
                                        .getLastLocation();
                            }
                            if (loc != null) {
                                point = new GeoPoint(loc.getLongitude(),
                                        loc.getLatitude());
                                sqlCommand = "select " + POIColumn.ID.getName() + " "
                                        + CursorColumn.ID.get() + ","
                                        + POIColumn.NAME.getName() + ","
                                        + POIColumn.ADDRESS.getName() + ","
                                        + POIColumn.TMX.getName() + ","
                                        + POIColumn.TMY.getName() + "," + "(abs("
                                        + point.getTmX() + "-"
                                        + POIColumn.TMX.getName() + ")*abs("
                                        + point.getTmX() + "-"
                                        + POIColumn.TMX.getName() + ")) + " + "(abs("
                                        + point.getTmY() + "-"
                                        + POIColumn.TMY.getName() + ")*abs("
                                        + point.getTmY() + "-"
                                        + POIColumn.TMY.getName() + ")) destsqr"
                                        + " , " + POIColumn.SUB_BRANCH.getName()
                                        + " from " + TableName.POI.getName()
                                        + " where " + POIColumn.CATEGORY.getName()
                                        + " like 'POI_070401%' AND p_towncode like '%"
                                        + city + "%' order by destsqr";
                            } else {
                                Toast.makeText(this, "無法取得當前位置", Toast.LENGTH_SHORT)
                                        .show();
                                sqlCommand = "select " + POIColumn.ID.getName() + " "
                                        + CursorColumn.ID.get() + ","
                                        + POIColumn.NAME.getName() + ","
                                        + POIColumn.ADDRESS.getName() + ","
                                        + POIColumn.TMX.getName() + ","
                                        + POIColumn.TMY.getName() + ","
                                        + "(-1) destsqr" + " , "
                                        + POIColumn.SUB_BRANCH.getName() + " from "
                                        + TableName.POI.getName() + " where "
                                        + POIColumn.CATEGORY.getName()
                                        + " like 'POI_071008%' AND p_towncode like '%"
                                        + city + "%' order by destsqr";
                            }

                            // select p_id
                            // _id,p_name,p_address,p_colx,p_coly,(abs(302708.53493973904-p_colx)*abs(302708.53493973904-p_colx))
                            // +
                            // (abs(2768867.2980233775-p_coly)*abs(2768867.2980233775-p_coly))
                            // destsqr , p_subbranch from poi where p_kind like
                            // 'POI_071008%' AND p_towncode like '%A%' order by destsqr

                            sqliteDatabase.setSQLCommand(sqlCommand);
                            temp = sqliteDatabase.QueryWithCommand();
                            curListData = temp;
                            // POI.Search(POIListView.this, new GeoPoint(lon,
                            // lat), catString);

                        } else {
                            curListData = POI.Search(POIListView.this, new GeoPoint(
                                    lon, lat), catString);
                        }
                        if (curListData.getCount() <= 0) {
                            tvEmpty.setText(getString(R.string.poi_search_by_distance_no_result));
                        }
                        break;
                    case SPOI:
                        curListData = Spoi.Search(this, new GeoPoint(lon, lat),
                                themeString);
                        if (curListData.getCount() <= 0) {
                            tvEmpty.setText(getString(R.string.poi_search_by_distance_no_result));
                        }
                        break;
                    default:
                        curListData = null;
                        break;
                }
                break;
            default:
                curListData = null;
                break;
        }
        Log.i("POIListView", "list item count:" + curListData.getCount());
        listData = ListDataFactory(curListData);

        curListData.close();
        return;
    }

    private void ShowLists() {
        if (listData == null) {
            tvEmpty.setVisibility(TextView.VISIBLE);
            return;
        }
        if (listContent == ActivityCaller.POI) {// 周邊查詢
            listAdapter = new ListViewAdapter(this,
                    R.layout.cursor_list_row_search, listData);
        } else {
            listAdapter = new ListViewAdapter(this, R.layout.cursor_list_row,
                    listData);
        }
        if (listAdapter.getCount() <= 0) {
            tvEmpty.setVisibility(TextView.VISIBLE);
        }
        if (poiSearchMode.equals(SearchMode.BY_KEYWORD)) {
            listAdapter.getDataVisibilityStates().put(
                    R.id.cursor_row_extention, false);
        }
        listAdapter.getDataVisibilityStates().put(R.id.cursor_row_ref, false);
        listAdapter.setSpoiNameColor(0);// 傳值進入listadapter提示設定textcolor
        setListAdapter(listAdapter);
        Log.i("POIListView", "adapter:" + listAdapter.getCount());
    }
}