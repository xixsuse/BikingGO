package com.kingwaytek.cpami.bykingTablet.app;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.sql.History;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.ContentType;
import com.kingwaytek.cpami.bykingTablet.view.ListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.view.UserDataListView;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.CursorListType;

public class MyHistory extends Activity {

	private Intent itenCaller;
	private Button gohome;
	private View POI_layout;
	private View Track_layout;
	private Intent itenHistoryList;
	private Map<Integer, Object[]> data;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.history);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title_bar);
		// setTitle(getString(R.string.title_default));
		// gohome = (Button)findViewById(R.id.go_home);
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
		itenCaller = getIntent();
		itenHistoryList = new Intent(this, UserDataListView.class);
		itenHistoryList.putExtra("whichType", CursorListType.HISTORY);
		data = fillUserData();
		Iterator<Entry<Integer, Object[]>> dataEntry = data.entrySet().iterator();
		Entry<Integer, Object[]> entry = dataEntry.next();
		TextView POI_textView = (TextView) findViewById(R.id.POI_textView);
		POI_textView.setText(entry.getValue()[0].toString());
		TextView Track_textView = (TextView) findViewById(R.id.Track_textView);
		Track_textView.setText(entry.getValue()[1].toString());
		
		
		POI_layout = (LinearLayout) findViewById(R.id.POI_layout);
		POI_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				itenHistoryList.putExtra("whichContent", ContentType.POI);
				Log.i("MyHistory", "Favorite List Activity Called.");
				itenHistoryList.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
				startActivityForResult(itenHistoryList, ActivityCaller.HISTORY.getValue());
			}
		});
		Track_layout = (LinearLayout) findViewById(R.id.Track_layout);
		Track_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itenHistoryList.putExtra("whichContent", ContentType.TRACK);
				Log.i("MyHistory", "Track List Activity Called.");
				itenHistoryList.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
				startActivityForResult(itenHistoryList, ActivityCaller.HISTORY.getValue());
			}
		});

		// setTitle(R.string.byking_function_history_title);
		TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
		titleBar.setText(R.string.byking_function_history_title);
		// TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
		// titleBar.setText(R.string.byking_function_history_title);
	}

	@Override
	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
		((TextView) findViewById(R.id.title_text2)).setText("");
	}

	@Override
	protected void onResume() {
		super.onResume();

		// ShowList();
	}

	private Map<Integer, Object[]> fillUserData() {
		Map<Integer, Object[]> listItem = new LinkedHashMap<Integer, Object[]>(2);
		String[] title = new String[2];
		String[] type = new String[2];

		History myHistory = new History(this);
		Cursor curData = myHistory.getHistoryList(ContentType.POI, "");
		int result = curData.getCount();
		curData.close();
		title[0] = ContentType.POI.getName() + " (" + result + ")";
		type[0] = String.valueOf(ContentType.POI.getValue());
		curData = myHistory.getHistoryList(ContentType.TRACK, "");
		result = curData.getCount();
		title[1] = ContentType.TRACK.getName() + " (" + result + ")";
		type[1] = String.valueOf(ContentType.TRACK.getValue());

		listItem.put(R.id.cursor_row_text, title);
		listItem.put(R.id.cursor_row_ref, type);

		curData.close();
		return listItem;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == ActivityCaller.HISTORY.getValue()) {
			itenCaller.putExtra("Action", data.getSerializableExtra("Action"));
			// itenCaller.putExtra("Name", data.getStringExtra("Name"));
			// itenCaller
			// .putExtra("Location", data.getParcelableExtra("Location"));
			// itenCaller.putExtra("Track", data.getIntExtra("Track", -1));
			// itenCaller.putExtra("LocationExt", data
			// .getParcelableExtra("LocationExt"));
			setResult(RESULT_OK, itenCaller);
			finish();
		} else if (resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}
	}

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	//
	// Log.i("MyHistory", "position = " + position + ", id = " + id);
	// super.onListItemClick(l, v, position, id);
	//
	// Intent itenHistoryList = new Intent(this, UserDataListView.class);
	// itenHistoryList.putExtra("whichType", CursorListType.HISTORY);
	// switch (ContentType.get(position + 1)) {
	// case POI:
	// itenHistoryList.putExtra("whichContent", ContentType.POI);
	// Log.i("MyHistory", "Favorite List Activity Called.");
	// break;
	// case TRACK:
	// itenHistoryList.putExtra("whichContent", ContentType.TRACK);
	// Log.i("MyHistory", "Track List Activity Called.");
	// break;
	// default:
	// itenHistoryList = null;
	// break;
	// }
	// itenHistoryList.putExtra("setpoint",
	// itenCaller.getStringExtra("setpoint"));
	// startActivityForResult(itenHistoryList,
	// ActivityCaller.HISTORY.getValue());
	// }
	//
	// private void ShowList() {
	// listAdapter = new ListViewAdapter(this, R.layout.cursor_list_row_history,
	// fillUserData());
	//
	// RelativeLayout lySearch = (RelativeLayout)
	// findViewById(R.id.list_search_box_layout);
	// lySearch.setVisibility(View.GONE);
	// listAdapter.getDataVisibilityStates().put(R.id.cursor_row_ref, false);
	// setListAdapter(listAdapter);
	// }
}
