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
import com.kingwaytek.cpami.bykingTablet.sql.Favorite;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.ContentType;
import com.kingwaytek.cpami.bykingTablet.view.ListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.view.UserDataListView;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.CursorListType;

public class MyFavorite extends Activity {

	private Intent itenCaller;

	private ListViewAdapter listAdapter;
	private Button gohome;
	private View POI_layout;
	private View Track_layout;
	private Intent itenFavoriteList;
	private Map<Integer, Object[]> data;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.favor);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title_bar);
		// setTitle(getString(R.string.title_default));
		//
		// gohome = (Button)findViewById(R.id.go_home);
		// gohome.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// setResult(RESULT_CANCELED);
		// finish();
		// return;
		// }
		// });

		itenCaller = getIntent();

		data = fillUserData();
		Iterator<Entry<Integer, Object[]>> dataEntry = data.entrySet().iterator();
		Entry<Integer, Object[]> entry = dataEntry.next();
		TextView POI_textView = (TextView) findViewById(R.id.POI_textView);
		POI_textView.setText(entry.getValue()[0].toString());
		TextView Track_textView = (TextView) findViewById(R.id.Track_textView);
		Track_textView.setText(entry.getValue()[1].toString());

		// setTitle(R.string.byking_function_favorite_title);
		itenFavoriteList = new Intent(this, UserDataListView.class);
		itenFavoriteList.putExtra("whichType", CursorListType.FAVORITE);

		listAdapter = null;
		POI_layout = (LinearLayout) findViewById(R.id.POI_layout);
		POI_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				itenFavoriteList.putExtra("whichContent", ContentType.POI);
				itenFavoriteList.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
				startActivityForResult(itenFavoriteList, ActivityCaller.FAVORITE.getValue());
			}
		});
		Track_layout = (LinearLayout) findViewById(R.id.Track_layout);
		Track_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itenFavoriteList.putExtra("whichContent", ContentType.TRACK);
				itenFavoriteList.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
				startActivityForResult(itenFavoriteList, ActivityCaller.FAVORITE.getValue());
			}
		});
		// TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
		// titleBar.setText(R.string.byking_function_favorite_title);
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

		Favorite myFavorite = new Favorite(this);
		Cursor curData = myFavorite.getFavoriteList(ContentType.POI, "");
		int result = curData.getCount();
		curData.close();
		title[0] = ContentType.POI.getName() + " (" + result + ")";
		type[0] = String.valueOf(ContentType.POI.getValue());
		curData = myFavorite.getFavoriteList(ContentType.TRACK, "");
		result = curData.getCount();
		curData.close();
		title[1] = ContentType.TRACK.getName() + " (" + result + ")";
		type[1] = String.valueOf(ContentType.TRACK.getValue());

		listItem.put(R.id.cursor_row_text, title);
		listItem.put(R.id.cursor_row_ref, type);

		return listItem;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == ActivityCaller.FAVORITE.getValue()) {
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
	// Log.i("MyFavorite", "position = " + position + ", id = " + id);
	// super.onListItemClick(l, v, position, id);
	//
	// Intent itenFavoriteList = new Intent(this, UserDataListView.class);
	// itenFavoriteList.putExtra("whichType", CursorListType.FAVORITE);
	//
	// switch (ContentType.get(position + 1)) {
	// case POI:
	// itenFavoriteList.putExtra("whichContent", ContentType.POI);
	// Log.i("MyFavorite", "Favorite List Activity Called.");
	// break;
	// case TRACK:
	// itenFavoriteList.putExtra("whichContent", ContentType.TRACK);
	// Log.i("MyFavorite", "Track List Activity Called.");
	// break;
	// default:
	// itenFavoriteList = null;
	// break;
	// }
	// itenFavoriteList.putExtra("setpoint",
	// itenCaller.getStringExtra("setpoint"));
	// startActivityForResult(itenFavoriteList,
	// ActivityCaller.FAVORITE.getValue());
	// }

	// private void ShowList() {
	// listAdapter = new ListViewAdapter(this, R.layout.cursor_list_row,
	// fillUserData());
	//
	// RelativeLayout lySearch = (RelativeLayout)
	// findViewById(R.id.list_search_box_layout);
	// lySearch.setVisibility(View.GONE);
	// listAdapter.getDataVisibilityStates().put(
	// R.id.cursor_row_ref, false);
	// setListAdapter(listAdapter);
	//
	// }
}
