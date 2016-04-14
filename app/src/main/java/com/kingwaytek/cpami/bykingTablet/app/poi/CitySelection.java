package com.kingwaytek.cpami.bykingTablet.app.poi;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.AlertDialogUtil;
import com.kingwaytek.cpami.bykingTablet.utilities.Util;
import com.kingwaytek.cpami.bykingTablet.app.AlertDialogUtil.ToggleSwitch;
import com.kingwaytek.cpami.bykingTablet.app.DataProgressDialog.DialogType;
import com.kingwaytek.cpami.bykingTablet.view.ListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.SearchMode;

/**
 * POI Query Select City
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 * 
 */
public class CitySelection extends ListActivity {

	private Intent itenCaller;
	private ListViewAdapter listAdapter;
	private String poiKeyword;
	private Map<Integer, String> cityMap;

	private TextView tvTitle;

	private static DialogType whichDialog = DialogType.NULL;
	private static Dialog mDialog = null;
	private Button gohome;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		itenCaller = getIntent();
		setContentView(R.layout.selection_listview_layout);
		TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
		titleBar.setText(R.string.address_search_city_prompt);
		poiKeyword = itenCaller.getStringExtra("POI_Keyword");
		tvTitle = (TextView) findViewById(R.id.selection_listview_title);
		tvTitle.setText(getString(R.string.byking_term_search) + "(" + poiKeyword + ")"
				+ getString(R.string.byking_term_result));
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

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("POI_CitySelection_onPause", "whichDialog:" + whichDialog);

		if (!whichDialog.equals(DialogType.NULL)) {
			AlertDialogUtil.toggleDialogAsync(this, mDialog, ToggleSwitch.DISMISS);
		}
	}

	private String[] fillCityList() {
		// cityMap = new LinkedHashMap<Integer, String>(2);
		cityMap = new LinkedHashMap<Integer, String>(8);

		int[] result = itenCaller.getIntArrayExtra("POICount");

		for (int i = 0; i < result.length; i++) {
			cityMap.put(Util.city_sort.get(i).getCityID(), Util.city_sort.get(i).getCityName() + " (" + result[i] + ")");
		}

		return cityMap.values().toArray(new String[cityMap.size()]);
	}

	/**
	 * onListItemClick Handler inheritance of ListActivity
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("CitySelection", "position = " + position + ", id = " + id);

		switch (listAdapter.getListMode()) {
		case SINGLE:
			DialogHandler(DialogType.LOADING);
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
		int cityID = cityMap.keySet().toArray(new Integer[cityMap.size()])[arg];
		String cityName = cityMap.get(cityID);
		Log.i("CitySelection", cityName + ", " + cityID);

		Intent itenContent = new Intent(this, POIListView.class);
		itenContent.putExtra("POIList_Caller", ActivityCaller.POI);
		itenContent.putExtra("POI_Search", SearchMode.BY_KEYWORD);
		itenContent.putExtra("POI_Keyword", poiKeyword);
		itenContent.putExtra("POI_City", Util.city_sort.get(arg).getCityCode());
		itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
		startActivityForResult(itenContent, ActivityCaller.POI.getValue());
	}

	private void DialogHandler(DialogType type) {
		whichDialog = type;
		Log.i("POI_CitySelection_dialog_handler", "whichDialog:" + whichDialog);

		switch (type) {
		case LOADING:
			Dialog dialog = new Dialog(this);
			dialog.setTitle(R.string.dialog_loading_message);
			mDialog = dialog;
			mDialog.show();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == ActivityCaller.POI.getValue()) {
			itenCaller.putExtra("POI_Action", data.getSerializableExtra("POI_Action"));
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

	private void ShowList() {
		listAdapter = new ListViewAdapter(this, R.layout.selection_listview_item_poi,
				R.id.selection_listview_item_text, fillCityList());
		setListAdapter(listAdapter);
	}

}
