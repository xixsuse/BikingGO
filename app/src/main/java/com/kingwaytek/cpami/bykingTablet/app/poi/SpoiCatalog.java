package com.kingwaytek.cpami.bykingTablet.app.poi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.BikingListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.sql.SQLiteBot;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.POIKindColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;

import android.R.integer;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * Query SPOI category List
 * 
 * @author yawhaw.ou
 */

public class SpoiCatalog extends ListActivity {

	private Button gohome;
	private Intent itenCaller;
	private ArrayList<String> cityStrings;
	private ArrayList<String> cur_city_name_sort;
	private String[] catalog;
	private String citySort[] = { "台北市", "新北市", "宜蘭縣", "基隆市", "桃園縣", "新竹縣", "新竹市", "苗栗縣", "臺中市", "彰化縣", "南投縣", "雲林縣",
			"金門縣" };

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		itenCaller = getIntent();
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.cursor_list_view);
		cityStrings = new ArrayList<String>();
		TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
		titleBar.setText(R.string.byking_function_spoi_title);

	}

	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
		((TextView) findViewById(R.id.title_text2)).setText("");
	}

	@Override
	protected void onResume() {
		super.onResume();
		RelativeLayout lytSearch = (RelativeLayout) findViewById(R.id.list_search_box_layout);
		lytSearch.setVisibility(View.GONE);

		SQLiteBot sqliteDatabase = new SQLiteBot(this.getString(R.string.SQLite_App_Database_Name),
				this.getString(R.string.SQLite_App_Database_Path), TableName.POI);

		String sqlCommand = "select s_theme from spoi ";

		sqliteDatabase.setSQLCommand(sqlCommand);
		Cursor cursor = sqliteDatabase.QueryWithCommand();
		try {
			for (int i = 0; i < cursor.getCount(); i++) {

				cursor.moveToPosition(i);
				String s_theme = cursor.getString(cursor.getColumnIndex("s_theme")).split("_")[0];
				if (cityStrings.size() == 0) {
					cityStrings.add(s_theme);
				} else {
					for (int j = 0; j < cityStrings.size(); j++) {
						if (cityStrings.get(j).contains(s_theme)) {
							break;
						}
						if (j == cityStrings.size() - 1) {
							cityStrings.add(s_theme);
						}

					}
				}
			}
			cur_city_name_sort = new ArrayList<String>();
			for (int i = 0; i < citySort.length; i++) {

				for (int j = 0; j < cityStrings.size(); j++) {

					if (cityStrings.get(j).equals(citySort[i])) {

						if (cityStrings.get(j).contains("臺中市")) {
							cur_city_name_sort.add("台中市");
						} else {
							cur_city_name_sort.add(cityStrings.get(j));
						}
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		ShowList();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Intent itenSpoi = new Intent(this, POISelectionView.class);
		itenSpoi.putExtra("Atv_Caller", ActivityCaller.SPOI);
		itenSpoi.putExtra("Spoi_Catalog", catalog[position]);
		startActivityForResult(itenSpoi, ActivityCaller.SPOI.getValue());
		super.onListItemClick(l, v, position, id);
	}

	private void ShowList() {

		List<Map<String, Object>> data = null;
		catalog = new String[cur_city_name_sort.size()];
		for (int i = 0; i < cur_city_name_sort.size(); i++) {
			catalog[i] = cur_city_name_sort.get(i);
		}

		// String[] catalog =
		// getResources().getStringArray(R.array.location_array);

		// data = new ArrayList<Map<String, Object>>();
		// Map<String, Object> item;
		// for(int i=0; i<catalog.length;i++){
		// item = new HashMap<String, Object>();
		// item.put("catalog", catalog[i]);
		// data.add(item);
		// }

		BikingListViewAdapter adapter = new BikingListViewAdapter(

		R.layout.selection_listview_item_catalog, R.drawable.catalog_cell, catalog);
		this.setListAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			if (requestCode == ActivityCaller.ADDRESS.getValue()) {
				ContextMenuOptions option = (ContextMenuOptions) data.getSerializableExtra("Action");
				String addressPart = data.getStringExtra("addressResult");
				double[] addressXY = data.getDoubleArrayExtra("addressLocation");
				itenCaller.putExtra("Action", option);
				itenCaller.putExtra("Name", addressPart);
				itenCaller.putExtra("Location", new GeoPoint(addressXY[1], addressXY[2]));
				setResult(RESULT_OK, itenCaller);
				finish();

			} else if (requestCode == ActivityCaller.SPOI.getValue()) {
				itenCaller.putExtra("Action", data.getSerializableExtra("Action"));
				setResult(RESULT_OK, itenCaller);
				finish();
			} else if (resultCode == RESULT_FIRST_USER) {
				setResult(RESULT_FIRST_USER);
				finish();
			}
		}
	}
}
