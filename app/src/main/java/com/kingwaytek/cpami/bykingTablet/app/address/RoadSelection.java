package com.kingwaytek.cpami.bykingTablet.app.address;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.DataProgressDialog;
import com.kingwaytek.cpami.bykingTablet.app.PreferenceActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.view.ListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;
import com.sonavtek.sonav.XLIST;
import com.sonavtek.sonav.sonav;

public class RoadSelection extends ListActivity {

	private Intent itenCaller;
	private ListViewAdapter listAdapter;
	private int townId;
	private String addressRest;
	// private Map<Integer, String> roadMapOri;
	private Map<Integer, Map<String, Object>> roadMap;

	private TextView tvTitle;
	private static int whichDialog = 0;
	// private static Dialog mDialog = null;
	private UtilDialog progressDialog;

	private sonav engine;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		itenCaller = getIntent();
		setContentView(R.layout.selection_listview_layout);
		progressDialog = new UtilDialog(this);
		engine = sonav.getInstance();
		int mapstyle = Integer.valueOf(PreferenceActivity.getMapStyle(this));
		if (mapstyle < 6) {
			engine.setmapstyle(0, mapstyle, 1);
		} else {
			mapstyle -= 5;
			engine.setmapstyle(1, 0, mapstyle);
		}
		engine.savenaviparameter();
		TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
		titleBar.setText(R.string.address_search_road_prompt);
		townId = itenCaller.getIntExtra("townID", -1);
		addressRest = itenCaller.getStringExtra("roadName");
		tvTitle = (TextView) findViewById(R.id.selection_listview_title);
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
		if (roadMap == null) {
			tvTitle.append(addressRest + "\n"
					+ getString(R.string.address_search_no_result));
		}
		DialogHandler(whichDialog);
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.i("RoadSelection_pause", "whichDialog:" + whichDialog);
		if (whichDialog > 0) {
			// mDialog.dismiss();
			progressDialog.dismiss();
		}
	}

	private String[] fillRoadList() {
		// roadKeywords
		// if (townId <= 0)
		// throw new IllegalArgumentException("townID is not valid.");

		XLIST[] roadList = sonav.getInstance().findlistroad1(townId,
				addressRest, 70);
		Log.i("RoadSelection", "Road = " + roadList.length);

		if (roadList == null || roadList.length <= 0) {
			return null;
		}

		// roadMapOri = new LinkedHashMap<Integer, String>();
		roadMap = new LinkedHashMap<Integer, Map<String, Object>>();
		Map<Integer, String> nameMap = new LinkedHashMap<Integer, String>();

		// String roadTemp = "";
		// int j = 0;
		Map<String, Object> roadData;

		for (int i = 0; i < roadList.length; i++) {
			// roadTemp = roadList[i].getName();
			// if (roadTemp.endsWith("??) || roadTemp.endsWith("??))
			// if (roadTemp.endsWith("??) || roadTemp.endsWith("??)) {
			// // continue;
			// roadMap.put(j++, roadList[i].getName());
			// }
			// String Town =
			// engine.showcitytownname(roadList[i].getLongitude(),roadList[i].getLatitude());
			nameMap.put(i, roadList[i].getName());

			roadData = new LinkedHashMap<String, Object>(3);
			roadData.put("Name", roadList[i].getName());
			Log.i("RoadSelection",
					"roadList[i].getName()=" + roadList[i].getName());
			roadData.put("Longitude", roadList[i].getLongitude());
			roadData.put("Latitude", roadList[i].getLatitude());
			roadMap.put(i, roadData);

			// j++;
			// roadMapOri.put(j++, roadList[i].getName());
		}

		Log.i("RoadSelection", "roads filter = " + roadMap.size());
		return nameMap.values().toArray(new String[nameMap.size()]);
	}

	/**
	 * onListItemClick Handler inheritance of ListActivity
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("TownSelection", "position = " + position + ", id = " + id);

		switch (listAdapter.getListMode()) {
		case SINGLE:
			final int pos = position;
			DialogHandler(DataProgressDialog.DIALOG_LOCATING);

			Thread t = new Thread() {
				@Override
				public void run() {
					listItem_Click(pos);
				}
			};
			t.start();
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

	protected void listItem_Click(int arg) {
		// Log.i("TownSelection", "Enter Town.");
		String roadName = listAdapter.getItem(arg).toString();
		String addressPart = itenCaller.getStringExtra("addressSelection");
		// double[] addressXY = eeego.getInstance().showaddrxy1(
		// addressPart + roadName);
		//
		// Log.i("RoadSelection", "0:" + addressXY[0] + ", 1:" + addressXY[1]
		// + ", 2:" + addressXY[2]);
		// if (addressXY[0] == 0) {
		// DialogHandler(DataProgressDialog.DIALOG_CONFIRM);
		// mDialog.cancel();
		// return;
		// }
		double[] addressXY = new double[3];
		Map<String, Object> roadData = roadMap.get(arg);

		addressXY[0] = 1;
		addressXY[1] = Double.valueOf(roadData.get("Longitude").toString())
				.doubleValue();
		addressXY[2] = Double.valueOf(roadData.get("Latitude").toString())
				.doubleValue();

		Intent itenContent = new Intent(this, AddressContent.class);
		itenContent.putExtra("addressResult", addressPart + roadName);
		itenContent.putExtra("addressLocation", addressXY);
		itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));

		// itenCaller.putExtra("addressResult", addressPart + roadName);
		// itenCaller.putExtra("addressLocation", addressXY);
		// Log.i("RoadInput", "got Location" + addressXY[0] + ", " +
		// addressXY[1]
		// + ", " + addressXY[2]);
		// DialogHandler(DataProgressDialog.DIALOG_SELECTION);
		// mDialog.cancel();
		whichDialog = 0;

		startActivityForResult(itenContent, ActivityCaller.ADDRESS.getValue());
	}

	private void ShowNoResultDialog(
			final DialogInterface.OnCancelListener dlgCancelListener) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				UtilDialog uit = new UtilDialog(RoadSelection.this);
				uit.showDialog_route_plan_choice(
						getString(R.string.address_search_no_locate), null,
						getString(R.string.dialog_goback_button_text), null);

				// mDialog = AlertDialogUtil.showMsgWithConfirm(
				// RoadSelection.this,
				// getString(R.string.address_search_no_locate),
				// getString(R.string.dialog_goback_button_text));
				// mDialog.setOnCancelListener(dlgCancelListener);
				whichDialog = DataProgressDialog.DIALOG_CONFIRM;
			}
		});
	}

	private void showContextSelection(
			final DialogInterface.OnCancelListener dlgCancelListener) {
		final String[] options = new String[] {
				ContextMenuOptions.DRAW_MAP.getTitle(),
				ContextMenuOptions.SET_ORIGIN.getTitle(),
				ContextMenuOptions.SET_DESTINATION.getTitle() };
		final DialogInterface.OnClickListener dlgListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				itenCaller.putExtra("addressAction",
						ContextMenuOptions.get(options[which]));
				setResult(RESULT_OK, itenCaller);
				finish();
			}
		};

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// mDialog = AlertDialogUtil.showContextSelection(
				// RoadSelection.this,
				// getString(R.string.address_search_result_options),
				// options, dlgListener);
				// mDialog.setOnCancelListener(dlgCancelListener);
				// whichDialog = DataProgressDialog.DIALOG_SELECTION;
			}
		});
	}

	private void DialogHandler(int which) {
		Log.i("RoadSelection_dialog_handler", "whichDialog:" + whichDialog);
		DialogInterface.OnCancelListener dlgCancelListener = new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				whichDialog = 0;
			}
		};

		switch (which) {
		case DataProgressDialog.DIALOG_LOADING:
			// mDialog = DataProgressDialog.show(this, "",
			// getString(R.string.dialog_loading_message));
			progressDialog.progressDialog(null,
					getString(R.string.dialog_loading_message));
			break;
		case DataProgressDialog.DIALOG_LOCATING:
			// mDialog = DataProgressDialog.show(this, "",
			// getString(R.string.dialog_locating_message));
			progressDialog.progressDialog(null,
					getString(R.string.dialog_loading_message));
			whichDialog = which;
			break;
		case DataProgressDialog.DIALOG_SELECTION:
			showContextSelection(dlgCancelListener);
			break;
		case DataProgressDialog.DIALOG_CONFIRM:
			ShowNoResultDialog(dlgCancelListener);
			break;
		default:
			break;
		}
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
		} else if (resultCode == RESULT_CANCELED) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	private void ShowList() {
		DialogHandler(DataProgressDialog.DIALOG_LOADING);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				listAdapter = new ListViewAdapter(RoadSelection.this,
						R.layout.selection_listview_item_address,
						R.id.selection_listview_item_text, fillRoadList());
				setListAdapter(listAdapter);
				// mDialog.dismiss();
				progressDialog.dismiss();
			}
		});
	}
}
