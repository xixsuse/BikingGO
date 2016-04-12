package com.kingwaytek.cpami.bykingTablet.app;

import static com.kingwaytek.cpami.bykingTabletPad.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.kingwaytek.cpami.bykingTabletPad.CommonUtilities.EXTRA_MESSAGE;
import static com.kingwaytek.cpami.bykingTabletPad.CommonUtilities.SENDER_ID;
import static com.kingwaytek.cpami.bykingTabletPad.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTabletPad.ServerUtilities;
import com.kingwaytek.cpami.bykingTablet.TrafficCondition.RoadCondition;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.CommunicationBaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.InfomationActivity;
import com.kingwaytek.cpami.bykingTablet.app.address.CitySelection;
import com.kingwaytek.cpami.bykingTablet.app.poi.POIMethodSelection;
import com.kingwaytek.cpami.bykingTablet.app.poi.SpoiCatalog;
import com.kingwaytek.cpami.bykingTablet.app.rentInfo.RentInfoActivity;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackListView;
import com.kingwaytek.cpami.bykingTablet.bus.PublicTransport;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;

public class MainActivity extends CommunicationBaseActivity {
	private GridView gridView;
	private int[] image = { R.drawable.main_map_selector, R.drawable.main_address_selector,
			R.drawable.main_poi_selector, R.drawable.main_book_selector, R.drawable.main_photo_selector,
			R.drawable.main_track_selector, R.drawable.main_favor_selector, R.drawable.main_trans_selector,
			R.drawable.main_history_selector, R.drawable.main_condition_selector, R.drawable.main_info_selector,
			R.drawable.main_rent_selector, R.drawable.main_set_selector };
	private String[] imgText = { "導航", "地址查詢", "景點查詢", "景點書", "GPS相片", "單車軌跡", "我的最愛", "大眾運輸", "查詢紀錄", "車友通報", "活動資訊",
			"租停車", "設定" };
	private Intent itenCaller;
	public static HashMap<String, Intent> intentsHashMap = new HashMap<String, Intent>();
	private static String URL_GCM = "http://biking.cpami.gov.tw/Service/SetPushToken?";

	private AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.biking_main);
		itenCaller = getIntent();
		GCM();

		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < image.length; i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("image", image[i]);
			item.put("text", imgText[i]);
			items.add(item);
		}

		GridViewAdapter adapter = new GridViewAdapter(R.layout.cell_main_gridview, image, imgText);

		gridView = (GridView) findViewById(R.id.main_gridView);
		gridView.setNumColumns(3);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					finish();
					break;
				case 1:
					goToForResult(CitySelection.class, false, ActivityCaller.ADDRESS.getValue());
					break;
				case 2:
					goToForResult(POIMethodSelection.class, true, ActivityCaller.POI.getValue());
					break;
				case 3:
					Intent itenSpoi = new Intent(MainActivity.this, SpoiCatalog.class);
					itenSpoi.putExtra("Atv_Caller", ActivityCaller.SPOI_CATALOG);
					startActivityForResult(itenSpoi, ActivityCaller.SPOI_CATALOG.getValue());
					break;
				case 4:
					Intent PhotoIntent = new Intent(MainActivity.this, PhotoGallery.class);
					startActivityForResult(PhotoIntent, ActivityCaller.PHOTO.getValue());
					break;
				case 5:
					Intent itenTrack = new Intent(MainActivity.this, TrackListView.class);
					itenTrack.putExtra("TrackList_Caller", ActivityCaller.TRACK);
					startActivityForResult(itenTrack, ActivityCaller.TRACK.getValue());
					break;
				case 6:
					goToForResult(MyFavorite.class, false, ActivityCaller.FAVORITE.getValue());
					break;
				case 7:
					Intent publicTransportIntent = new Intent(MainActivity.this, PublicTransport.class);
					startActivity(publicTransportIntent);
					break;
				case 8:
					goToForResult(MyHistory.class, false, ActivityCaller.HISTORY.getValue());
					break;
				case 9:
					goTo(RoadCondition.class, false);
					break;
				case 10:
					goTo(InfomationActivity.class, false);
					break;
				case 11:
					goToForResult(RentInfoActivity.class, false, ActivityCaller.RENT.getValue());
					// goTo(RentInfoActivity.class, false);
					break;
				case 12:
					goToForResult(PreferenceActivity.class, false, ActivityCaller.RENT.getValue());
					// goTo(PreferenceActivity.class, false);
					break;

				default:
					break;
				}
			}

		});

	}

	public void goToForResult(Class<?> clazz, boolean clearTop, int requestCode) {
		Intent intent = new Intent(this, clazz);

		if (clearTop) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}

		startActivityForResult(intent, requestCode);
	}

	public void goTo(Class<?> clazz, boolean clearTop) {
		Intent intent = new Intent(this, clazz);

		if (clearTop) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}

		startActivity(intent);
	}

	private class GridViewAdapter extends BaseAdapter {

		// GridViewAdapter adapter = new GridViewAdapter(items,
		// R.layout.cell_main_gridview, new String[] { "image",
		private int[] image;
		private String[] imgText;
		private int layout;
		private List<Map<String, Object>> items;
		private DisplayMetrics metrics;

		public GridViewAdapter(int layout, int[] image, String[] imgText) {
			this.layout = layout;
			this.image = image;
			this.imgText = imgText;
			metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return image.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;

			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(parent.getContext()).inflate(this.layout, null);
				viewHolder.img = (ImageView) convertView.findViewById(R.id.main_cell_imageView);
				viewHolder.txv = (TextView) convertView.findViewById(R.id.main_cell_textView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.img.getLayoutParams().height = metrics.heightPixels / 8;
			viewHolder.img.getLayoutParams().width = metrics.heightPixels / 8;

			if (!convertView.isFocused()) {
				viewHolder.img.setBackgroundResource(image[position]);
			} else {

			}
			viewHolder.txv.setText(imgText[position]);

			return convertView;
		}

		private class ViewHolder {
			ImageView img;
			TextView txv;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			UtilDialog uit = new UtilDialog(MainActivity.this) {
				@Override
				public void click_btn_1() {
					super.click_btn_1();
					Intent intent = new Intent();
					intent.putExtra("FINISH", 1);
					setResult(-10, intent);
					finish();
					
				}
			};
			uit.showDialog_route_plan_choice(getString(R.string.confirm_close_app), null, "確定", "取消");
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == -10) {
			Intent intent = new Intent();
			intent.putExtra("FINISH", 1);
			setResult(-10, intent);
			finish();
		}

		if (resultCode == RESULT_OK) {
			itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
			setResult(RESULT_OK, itenCaller);
			// if (requestCode == ActivityCaller.ADDRESS.getValue()) {
			// ContextMenuOptions option = (ContextMenuOptions)
			// data.getSerializableExtra("Action");
			// String addressPart = data.getStringExtra("addressResult");
			// double[] addressXY = data.getDoubleArrayExtra("addressLocation");
			// itenCaller.putExtra("Action", option);
			// itenCaller.putExtra("Name", addressPart);
			// itenCaller.putExtra("Location", new GeoPoint(addressXY[1],
			// addressXY[2]));
			// setResult(RESULT_OK, itenCaller);
			//
			// } else if (requestCode == ActivityCaller.POI.getValue()) {
			// itenCaller.putExtra("Action",
			// data.getSerializableExtra("Action"));
			// setResult(RESULT_OK, itenCaller);
			//
			// } else if (requestCode == ActivityCaller.SPOI.getValue()
			// || requestCode == ActivityCaller.SPOI_CATALOG.getValue()) {
			// itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
			// setResult(RESULT_OK, itenCaller);
			// } else if (requestCode == ActivityCaller.FAVORITE.getValue()) {
			// itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
			// setResult(RESULT_OK, itenCaller);
			// } else if (requestCode == ActivityCaller.RENT.getValue()) {
			// itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
			// setResult(RESULT_OK, itenCaller);
			// } else if (resultCode == RESULT_FIRST_USER) {
			// setResult(RESULT_FIRST_USER);
			// }
			finish();
		}
	}

	private void GCM() {
		checkNotNull(SERVER_URL, "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		// setContentView(R.layout.main);
		// mDisplay = (TextView) findViewById(R.id.display);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId != "") {

		} else if (regId.equals("")) {
			// Automatically registers application on startup.
			// registerReceiver(mHandleMessageReceiver, new
			// IntentFilter(DISPLAY_MESSAGE_ACTION));
			GCMRegistrar.register(this, SENDER_ID);
		}// Device is already registered on GCM, check server.
		if (GCMRegistrar.isRegisteredOnServer(this)) {
			// Skips registration.
			// mDisplay.append(getString(R.string.already_registered) +
			// "\n");
		} else {
			// Try to register again, but not in the UI thread.
			// It's also necessary to cancel the thread onDestroy(),
			// hence the use of AsyncTask instead of a raw thread.
			final Context context = this;
			mRegisterTask = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					boolean registered = ServerUtilities.register(context, regId);
					Log.i("DEBUG", "" + registered);

					// At this point all attempts to register with the app
					// server failed, so we need to unregister the device
					// from GCM - the app will try to register again when
					// it is restarted. Note that GCM will send an
					// unregistered callback upon completion, but
					// GCMIntentService.onUnregistered() will ignore it.
					if (!registered) {
						GCMRegistrar.unregister(context);
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					mRegisterTask = null;
				}

			};
			mRegisterTask.execute(null, null, null);
		}
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config, name));
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// mDisplay.append(newMessage + "\n");
		}
	};

	@Override
	protected void onDestroy() {

		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}

		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(getApplicationContext());
		finish();
		super.onDestroy();
	}
	
}
