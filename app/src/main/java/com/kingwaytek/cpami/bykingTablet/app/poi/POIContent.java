package com.kingwaytek.cpami.bykingTablet.app.poi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.POI_SMS;
import com.kingwaytek.cpami.bykingTablet.app.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.sql.Favorite;
import com.kingwaytek.cpami.bykingTablet.sql.History;
import com.kingwaytek.cpami.bykingTablet.sql.POI;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.ContentType;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.POIMenu;

/**
 * POI Query POI Content
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 * 
 */
public class POIContent extends Activity {

	private Intent itenCaller;
	private ActivityCaller myCaller;
	private int itemId;
	private POI thisPOI;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		itenCaller = getIntent();
		setContentView(R.layout.poi_content);

		myCaller = (ActivityCaller) itenCaller.getSerializableExtra("POI_Caller");
		itemId = (int) itenCaller.getLongExtra("POI_ID", -1);

		Log.i("POIContent", "My Caller : " + myCaller + ", POI id : " + itemId);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (itemId <= 0)
			return;

		// fetch spoi here.
		thisPOI = new POI(this, itemId);
		if (thisPOI.getID() <= 0) {
			Log.e("POIContent", "poi item is null.");
			return;
		}
		Log.i("Spoi", "this poi id : " + thisPOI.getID() + ", poi name : " + thisPOI.getName());

		SetPOIUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// getMenuInflater().inflate(R.layout.menu_poi_favorite, menu);

		menu.add(0, POIMenu.SET_ORIGIN.getId(), 0, POIMenu.SET_ORIGIN.getTitle());
		menu.add(0, POIMenu.SET_DESTINATION.getId(), 1, POIMenu.SET_DESTINATION.getTitle());
		switch (myCaller) {
		case FAVORITE:
		case HISTORY:
			menu.add(0, POIMenu.DRAW_MAP.getId(), 2, POIMenu.DRAW_MAP.getTitle());
			menu.add(0, POIMenu.SHARE.getId(), 3, POIMenu.SHARE.getTitle());
			menu.add(0, POIMenu.DELETE.getId(), 4, POIMenu.DELETE.getTitle());
			break;
		case NAVIGATION:
			break;
		case POI:
		case SPOI:
			menu.add(0, POIMenu.DRAW_MAP.getId(), 2, POIMenu.DRAW_MAP.getTitle());
			menu.add(0, POIMenu.SHARE.getId(), 3, POIMenu.SHARE.getTitle());
			menu.add(0, POIMenu.ADD_FAVORITE.getId(), 4, POIMenu.ADD_FAVORITE.getTitle());
			break;
		default:
			break;
		}

		return true;
	}

	/**
	 * This Activity will finish after a valid menu action occurred
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.i("POIContent", "Menu item clicked : " + item.getItemId());

		switch (POIMenu.get(item.getItemId())) {
		case ADD_FAVORITE:
			AddToFavorite();
			return true;
		case DELETE: // cause finish
			RemoveItem(myCaller);
			return true;
		case DRAW_MAP: // cause finish
			DrawMap();
			Log.i("POIContent", "finish activities and back to map to draw.");
			return true;
		case SET_ORIGIN: // cause finish
			SetLocation(POIMenu.SET_ORIGIN);
			Log.i("POIContent", "use item as start Point for navigation.");
			return true;
		case SET_DESTINATION:
			SetLocation(POIMenu.SET_DESTINATION);
			Log.i("POIContent", "use item as end Point for navigation.");
			return true;
		case SHARE:
			Log.i("POIContent", "share this poi via SMS.");
			Intent itenSMS = new Intent(this, POI_SMS.class);
			itenSMS.putExtra("POI_Name", thisPOI.getName());
			itenSMS.putExtra("POI_Lon", String.valueOf(thisPOI.getPOIPoint().getLongitude()));
			itenSMS.putExtra("POI_Lat", String.valueOf(thisPOI.getPOIPoint().getLatitude()));
			startActivity(itenSMS);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * Add Favorites
	 */
	private void AddToFavorite() {

		Favorite poiFavorite = new Favorite(this);
		poiFavorite.setName(thisPOI.getName() + "(" + thisPOI.getSubBranch() + ")");
		poiFavorite.setType(ContentType.POI.getValue());
		poiFavorite.setItemID(thisPOI.getID());

		String alertMsg = "";
		// check duplication
		try {
			if (poiFavorite.isItemInList()) {
				alertMsg = getString(R.string.favorite_duplicate_poi_msg);
			} else {
				long result = poiFavorite.Add();
				Log.i("POIContent", "add favorite result = " + result);
				if (result <= 0)
					alertMsg = getString(R.string.favorite_add_fail_msg);
				else
					alertMsg = getString(R.string.favorite_add_success_msg);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		UtilDialog uit = new UtilDialog(POIContent.this);
		uit.showDialog_route_plan_choice(alertMsg, null, getString(R.string.dialog_ok_button_text), null);
	}

	/**
	 * Delete Favorites
	 */
	private void RemoveItem(ActivityCaller what) {
		// TODO delete completion
		long result = -1;
		long sourceID = itenCaller.getLongExtra("item_ID", -1);

		try {
			switch (what) {
			case FAVORITE:
				result = Favorite.Remove(this, (int) sourceID);
				Log.i("POIContent", "delete favorite id:" + sourceID);
				break;
			case HISTORY:
				result = History.Remove(this, (int) sourceID);
				Log.i("POIContent", "delete history id:" + sourceID);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// AlertDialogUtil.showMsgWithConfirm(this, alertMsg, "????");

		itenCaller.putExtra("POI_Action", POIMenu.DELETE);
		itenCaller.putExtra("Remove_Result", result);
		setResult(RESULT_OK, itenCaller);
		finish();
	}

	private void DrawMap() {
		itenCaller.putExtra("POI_Action", POIMenu.DRAW_MAP);
		itenCaller.putExtra("POI_Name", thisPOI.getName());
		itenCaller.putExtra("POI_Location", thisPOI.getPOIPoint());
		String[] poiOthers = new String[2];
		poiOthers[0] = thisPOI.getAddress();
		poiOthers[1] = thisPOI.getTelNumber();
		itenCaller.putExtra("POI_Others", poiOthers);
		setResult(RESULT_OK, itenCaller);
		finish();
	}

	private void SetLocation(POIMenu action) {
		switch (action) {
		case SET_ORIGIN:
			itenCaller.putExtra("POI_Action", POIMenu.SET_ORIGIN);
			break;
		case SET_DESTINATION:
			itenCaller.putExtra("POI_Action", POIMenu.SET_DESTINATION);
			break;
		default:
			break;
		}
		itenCaller.putExtra("POI_Name", thisPOI.getName());
		itenCaller.putExtra("POI_Location", thisPOI.getPOIPoint());
		setResult(RESULT_OK, itenCaller);
		finish();
	}

	private void SetPOIUI() {
		TextView tvName = (TextView) findViewById(R.id.poi_content_name);
		TextView tvAddress = (TextView) findViewById(R.id.poi_content_address);
		TextView tvTel = (TextView) findViewById(R.id.poi_content_phone);
		TextView tvHours = (TextView) findViewById(R.id.poi_content_bhours);
		TextView tvModel = (TextView) findViewById(R.id.poi_content_bmodel);
		TextView tvAnnot = (TextView) findViewById(R.id.poi_content_annot);
		LinearLayout lyHours = (LinearLayout) findViewById(R.id.poi_content_bhours_layout);
		LinearLayout lyModel = (LinearLayout) findViewById(R.id.poi_content_bmodel_layout);
		LinearLayout lyAnnot = (LinearLayout) findViewById(R.id.poi_content_annot_layout);

		// tvAddress.setOnClickListener(this);

		String subBranch = thisPOI.getSubBranch();
		tvName.setText(thisPOI.getName() + (subBranch == null || subBranch.length() == 0 ? "" : "(" + subBranch + ")"));
		tvAddress.setText(thisPOI.getAddress());
		tvTel.setText(thisPOI.getTelNumber());
		tvHours.setText(thisPOI.getBusineseHour());
		tvModel.setText(thisPOI.getDescription());
		tvAnnot.setText(thisPOI.getAnnotation());
		tvTel.setText(thisPOI.getTelNumber());

		lyHours.setVisibility((thisPOI.getBusineseHour() == null || thisPOI.getBusineseHour().length() == 0 ? View.GONE
				: View.VISIBLE));
		lyModel.setVisibility((thisPOI.getDescription() == null || thisPOI.getDescription().length() == 0 ? View.GONE
				: View.VISIBLE));
		lyAnnot.setVisibility((thisPOI.getAnnotation() == null || thisPOI.getAnnotation().length() == 0 ? View.GONE
				: View.VISIBLE));
	}
}
