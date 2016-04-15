package com.kingwaytek.cpami.bykingTablet.app.poi;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.view.ListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.SearchMode;

import java.util.ArrayList;
import java.util.List;

/**
 * POI Query Select Method
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 * 
 */
public class POIMethodSelection extends ListActivity {

	private Intent itenCaller;
	private ListViewAdapter listAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		itenCaller = getIntent();
		setContentView(R.layout.selection_listview_layout);
		TextView tvTitle = (TextView) findViewById(R.id.selection_listview_title);
		tvTitle.setText(R.string.poi_search_method_prompt);
		TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
		titleBar.setText(R.string.byking_function_poi_search_title);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
		((TextView) findViewById(R.id.title_text2)).setText("");
	}

	@Override
	protected void onResume() {
		super.onResume();

		ShowList();
	}

	private List<String> fillMethodList() {
		List<String> methodList = new ArrayList<>(2);

		methodList.add(SearchMode.BY_SURROUNDING.getTitle());
		methodList.add(SearchMode.BY_KEYWORD.getTitle());

		return methodList;
	}

	/**
	 * onListItemClick Handler inheritance of ListActivity
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("CitySelection", "position = " + position + ", id = " + id);

		switch (listAdapter.getListMode()) {
		case SINGLE:
			listItem_Click(position);
			break;
		case MULTIPLE:
			CheckBox ckbSelect = (CheckBox) v.findViewById(R.id.selection_listview_item_checkbox);
			ckbSelect.toggle();
			listAdapter.getCheckBoxData().put(position, ckbSelect.isChecked());
		default:
			break;
		}

		super.onListItemClick(l, v, position, id);
	}

	protected void listItem_Click(int arg) {
		String methodName = listAdapter.getItem(arg).toString();

		Log.i("MothodSelection", methodName);

		Intent itenContent;
		switch (SearchMode.get(methodName)) {
		case BY_SURROUNDING://週邊查詢
			itenContent = new Intent(this, POISelectionView.class); // category
			itenContent.putExtra("Atv_Caller", ActivityCaller.POI);
			break;
		case BY_KEYWORD://關鍵字查詢
			itenContent = new Intent(this, KeywordInput.class); // keywrod
			break;
		default:
			itenContent = null;
			break;
		}
		itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
		startActivityForResult(itenContent, ActivityCaller.POI.getValue());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == ActivityCaller.POI.getValue()) {
			itenCaller.putExtra("Action", data.getSerializableExtra("POI_Action"));
			// itenCaller.putExtra("Name", data.getStringExtra("POI_Name"));
			// itenCaller.putExtra("Location", data
			// .getParcelableExtra("POI_Location"));
			// itenCaller.putExtra("Others", data
			// .getStringArrayExtra("POI_Others"));
			setResult(RESULT_OK, itenCaller);
			finish();
		}
        else if (resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}
	}

	private void ShowList() {
		List<String> methodList = fillMethodList();

		listAdapter = new ListViewAdapter(
                this, R.layout.selection_listview_item_poi,
                R.id.selection_listview_item_text, fillMethodList().toArray(new String[methodList.size()])
        );

		setListAdapter(listAdapter);
	}
}
