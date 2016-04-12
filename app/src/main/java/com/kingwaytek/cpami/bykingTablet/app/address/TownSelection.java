package com.kingwaytek.cpami.bykingTablet.app.address;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kingwaytek.anchorpoint.POIEngine;
import com.kingwaytek.cpami.bykingTablet.data.ITown;
import com.kingwaytek.cpami.bykingTablet.view.ListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;
import com.kingwaytek.cpami.bykingTablet.R;
import com.sonavtek.sonav.sonav;

/**
 * Activity for Select a Town in Address Search
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 * 
 */
public class TownSelection extends ListActivity {

	private Intent itenCaller;
	private ListViewAdapter listAdapter;
	// private SelectionListAdapter listAdapter;
	private int cityId;
	private Map<Integer, String> townMap;
	private Button gohome;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		itenCaller = getIntent();
		setContentView(R.layout.selection_listview_layout);

		cityId = itenCaller.getIntExtra("cityID", -1);
		TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
		titleBar.setText(R.string.address_search_town_prompt);
		TextView tvTitle = (TextView) findViewById(R.id.selection_listview_title);
		tvTitle.setText(itenCaller.getStringExtra("addressSelection"));
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

	private String[] fillTownList() {
		if (cityId <= 0)
			throw new IllegalArgumentException("cityID is not valid.");

		POIEngine poiEngine = new POIEngine(sonav.getInstance());
		ITown[] townList = poiEngine.getTowns(cityId);

		if (townList == null || townList.length <= 0)
			return null;

		townMap = new LinkedHashMap<Integer, String>(townList.length - 1);

		for (int i = 1; i < townList.length; i++) {
			townMap.put(townList[i].getId(), townList[i].getName());
		}

		return townMap.values().toArray(new String[townMap.size()]);
	}

	/**
	 * onListItemClick Handler inheritance of ListActivity
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("TownSelection", "position = " + position + ", id = " + id);

		switch (listAdapter.getListMode()) {
		case SINGLE:
			listItem_Click(position);
			break;
		case MULTIPLE:
			CheckBox ckbSelect = (CheckBox) v
					.findViewById(R.id.selection_listview_item_checkbox);
			ckbSelect.toggle();
			listAdapter.getCheckBoxData().put(position, ckbSelect.isChecked());
		default:
			break;
		}

		super.onListItemClick(l, v, position, id);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK
				&& requestCode == ActivityCaller.ADDRESS.getValue()) {
			ContextMenuOptions option = (ContextMenuOptions) data
					.getSerializableExtra("Action");
			String addressPart = data.getStringExtra("addressResult");
			double[] addressXY = data.getDoubleArrayExtra("addressLocation");
			itenCaller.putExtra("Action", option);
			itenCaller.putExtra("addressResult", addressPart);
			itenCaller.putExtra("addressLocation", addressXY);
			setResult(RESULT_OK, itenCaller);
			finish();
		} else if (resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}
	}

	protected void listItem_Click(int arg) {
		String townName = listAdapter.getItem(arg).toString();
		int townID = townMap.keySet().toArray(new Integer[townMap.size()])[arg];
		String addressPart = itenCaller.getStringExtra("addressSelection")
				+ townName;
		Log.i("TownSelection", townName + ", " + townID + ", " + addressPart);

		Intent itenContent = new Intent(this, RoadInput.class); // roadRestinput
		itenContent.putExtra("townID", townID);
		itenContent.putExtra("townName", townName);
		itenContent.putExtra("addressSelection", addressPart);
		itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
		startActivityForResult(itenContent, ActivityCaller.ADDRESS.getValue());
	}

	private void ShowList() {
		// listAdapter = new SelectionListAdapter(this,
		// R.layout.selection_listview_item,
		// R.id.selection_listview_item_text);
		//
		// listAdapter.setBindingData(fillTownList());
		listAdapter = new ListViewAdapter(this,
				R.layout.selection_listview_item_address,
				R.id.selection_listview_item_text, fillTownList());

		setListAdapter(listAdapter);
	}
}
