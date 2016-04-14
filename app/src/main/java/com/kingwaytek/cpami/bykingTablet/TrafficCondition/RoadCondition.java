package com.kingwaytek.cpami.bykingTablet.TrafficCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;

public class RoadCondition extends ListActivity {
	// private Button gohome;
	// private Button goto_upload;
	// private Button goto_update;
	// private Button button;
	// private View view;
	// private PopupWindow pop;
	// static final int DATE_DIALOG_ID = 0;
	// private int mYear;
	// private int mMonth;
	// private int mDay;

	private List<String> list = null;
	Intent ReportIntent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roadcondition_main);
		showList();

		// String[] catalog =
		// getResources().getStringArray(R.array.roadcondition_array);
		// String[] catalog = new String[] {"路況回報","路況上傳"};
		// ArrayAdapter<String> arrayData = new ArrayAdapter<String>(this,
		// R.id.ttt, catalog);
		// setListAdapter(arrayData);
		// ListView lv = getListView();
		// lv.setTextFilterEnabled(true);
		// lv.setOnItemClickListener(new OnItemClickListener() {
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// // When clicked, show a toast with the TextView text
		// Toast.makeText(getApplicationContext(), ((TextView)
		// view).getText(),Toast.LENGTH_SHORT).show();
		// }
		// });

		/*
		 * 
		 * //setContentView(R.layout.roadcondition);
		 * 
		 * // get the current date final Calendar c = Calendar.getInstance();
		 * mYear = c.get(Calendar.YEAR); mMonth = c.get(Calendar.MONTH); mDay =
		 * c.get(Calendar.DAY_OF_MONTH);
		 * 
		 * // view = this.getLayoutInflater().inflate(R.layout.popupwindow,
		 * null); // button = (Button)view.findViewById(R.id.conform_button); //
		 * view.setBackgroundDrawable(getResources().getDrawable(R.drawable.
		 * rounded_corners_view)); // pop = new PopupWindow(view,300,300); //
		 * pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.
		 * rounded_corners_pop));
		 * 
		 * view = this.getLayoutInflater().inflate(R.layout.popupwindow2, null);
		 * pop = new PopupWindow(view,300,300);
		 * 
		 * // gohome = (Button)findViewById(R.id.go_home); //
		 * gohome.setOnClickListener(new OnClickListener() { // // @Override //
		 * public void onClick(View v) { // setResult(RESULT_FIRST_USER); //
		 * finish(); // return; // } // });
		 * 
		 * goto_update = (Button)findViewById(R.id.goto_update);
		 * goto_update.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * Log.i("RoadCondition.java","goto_update onClick"); // Intent
		 * ReportIntent = new Intent(RoadCondition.this, Update.class); //
		 * startActivity(ReportIntent);
		 * 
		 * // pop.showAtLocation(findViewById(R.id.road_condition),
		 * Gravity.CENTER, 20, 20);
		 * 
		 * showDialog(DATE_DIALOG_ID); return; } });
		 * 
		 * goto_upload = (Button)findViewById(R.id.goto_upload);
		 * goto_upload.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent ReportIntent = new
		 * Intent(RoadCondition.this, Report.class);
		 * startActivity(ReportIntent); return; } });
		 * 
		 * // button.setOnClickListener(new OnClickListener() { // // @Override
		 * // public void onClick(View v) { // pop.dismiss(); // return; // } //
		 * });
		 */
	}

	public void showList() {
		list = new ArrayList<String>();
		list.add("路況回報");
		list.add("路況上傳");

		// ArrayAdapter<String> la = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, list);
		// ArrayAdapter<String> la = new ArrayAdapter<String>(this,
		// R.layout.roadconditon_list_item, list);

		List<Map<String, Object>> data = null;
		String[] catalog = getResources().getStringArray(R.array.roadcondition_array);
		data = new ArrayList<Map<String, Object>>();
		Map<String, Object> item;
		item = new HashMap<String, Object>();
		item.put("type", catalog[0]);
		data.add(item);
		item = new HashMap<String, Object>();
		item.put("type", catalog[1]);
		data.add(item);

		ListAdapter adapter = new SimpleAdapter(
				this,// Context.
				data, R.layout.selection_listview_item, new String[] { "type" },
				new int[] { R.id.selection_listview_item_text });
		this.setListAdapter(adapter);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		if (position == 0) {
			ReportIntent = new Intent(RoadCondition.this, Report.class);
		} else {
			ReportIntent = new Intent(RoadCondition.this, TrafficUpdate.class);
		}

		UtilDialog uit = new UtilDialog(RoadCondition.this) {
			@Override
			public void click_btn_1() {
				super.click_btn_1();
				startActivity(ReportIntent);
				
			}
		};
		uit.showDialog_route_plan_choice(getString(R.string.dialog_web_message), null, "是", "否");
	}
}
