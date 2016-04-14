package com.kingwaytek.cpami.bykingTablet.app.poi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.Util;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.app.DataProgressDialog.DialogType;
import com.kingwaytek.cpami.bykingTablet.sql.POI;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;

/**
 * POI Query Input Keyword
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 * 
 */
public class KeywordInput extends Activity implements OnClickListener {

	private Intent itenCaller;
	private String poiKeyword;
	EditText etKeyword;
	private Button gohome;

	private static DialogType whichDialog = DialogType.NULL;
	// private static Dialog mDialog = null;
	private UtilDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		itenCaller = getIntent();
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.poi_keyword_input);
		progressDialog = new UtilDialog(this);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title_bar);
		// setTitle(getString(R.string.byking_function_poi_search_title) + ">"
		// + getString(R.string.poi_search_keyword_prompt));
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

		etKeyword = (EditText) findViewById(R.id.poi_keyword_input);
		Button btnSearch = (Button) findViewById(R.id.poi_keyword_search);
		btnSearch.setOnClickListener(this);

		// TODO remove this setText
		// etKeyword.setText("麥當勞");
	}

	@Override
	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
		((TextView) findViewById(R.id.title_text2)).setText("");
	}

	@Override
	protected void onResume() {
		super.onResume();

		DialogHandler(whichDialog);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (!whichDialog.equals(DialogType.NULL)) {
			// AlertDialogUtil.toggleDialogAsync(this, mDialog,
			// ToggleSwitch.DISMISS);
			progressDialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		final View cView = v;

		switch (v.getId()) {
		case R.id.poi_keyword_search:
			poiKeyword = etKeyword.getText().toString();
			// TODO check
			if (poiKeyword == null || poiKeyword.length() <= 0) {
				DialogHandler(DialogType.CONFIRM);
				return;
			}
			DialogHandler(DialogType.LOADING);

			new Thread() {
				@Override
				public void run() {
					btnSearch_Click(cView);
					// AlertDialogUtil.toggleDialogAsync(KeywordInput.this,
					// mDialog, ToggleSwitch.DISMISS);
					// mDialog.dismiss();
					progressDialog.dismiss();
					whichDialog = DialogType.NULL;
				}
			}.start();
			break;
		default:
			break;
		}
	}

	protected void btnSearch_Click(Object sender) {
		if (sender == null || !sender.getClass().getName().equals("android.widget.Button")) {
			throw new IllegalArgumentException("sender is not valid.");
		}
		Log.i("KeywordInput", "Search Button Clicked. sender is : " + sender.getClass().getName());
		Log.i("poi_KeywordInput", "keyword = " + poiKeyword);

		if (Util.city_sort == null) {
			Util.getSortPOICity(this);
		}
		int[] poiCount = null;
		if (Util.city_sort != null) {
			poiCount = new int[Util.city_sort.size()];
			for (int i = 0; i < Util.city_sort.size(); i++) {
				poiCount[i] = POI.SearchCount(this, poiKeyword, Util.city_sort.get(i).getCityCode());
			}
		}
		// call CitySelection for Keyword Result
		Intent itenContent = new Intent(this, CitySelection.class);
		itenContent.putExtra("POI_Keyword", poiKeyword);
		itenContent.putExtra("POICount", poiCount);
		itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
		startActivityForResult(itenContent, ActivityCaller.POI.getValue());

	}

	private void DialogHandler(DialogType type) {
		Log.i("POI_KeywordInput_dialog_handler", "whichDialog:" + whichDialog);
		DialogInterface.OnCancelListener dlgCancelListener = new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				whichDialog = DialogType.NULL;
			}
		};

		whichDialog = type;
		switch (type) {
		case LOADING:
			// mDialog = DataProgressDialog.create(this, "",
			// getString(R.string.dialog_loading_message));
			// mDialog.show();
			progressDialog.progressDialog(null, getString(R.string.dialog_loading_message));
			break;
		case CONFIRM:
			// mDialog = AlertDialogUtil.createMsgWithConfirm(this,
			// getString(R.string.poi_search_keyword_prompt),
			// getString(R.string.dialog_goback_button_text));
			// mDialog.setOnCancelListener(dlgCancelListener);
			// AlertDialogUtil.toggleDialogAsync(this, mDialog,
			// ToggleSwitch.SHOW);
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
}
