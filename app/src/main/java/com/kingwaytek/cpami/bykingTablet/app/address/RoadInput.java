package com.kingwaytek.cpami.bykingTablet.app.address;

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

import com.kingwaytek.cpami.bykingTablet.app.DataProgressDialog;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;
import com.kingwaytek.cpami.bykingTablet.R;
import com.sonavtek.sonav.sonav;

/**
 * Activity for Key-in road part in Address Search
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 *
 * 2016/04/13
 * Modified by Vincent.
 */
public class RoadInput extends Activity implements OnClickListener {

	private Intent itenCaller;
	private int townId;
	private String addressRest;
	private EditText etAddress;

	private static int whichDialog = 0;
	private UtilDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		itenCaller = getIntent();

        Log.i("RoadInput", "itenCaller: setpoint - " + itenCaller.getStringExtra("setpoint"));

		setContentView(R.layout.road_input);
		progressDialog = new UtilDialog(this);

		townId = itenCaller.getIntExtra("townID", -1);
		TextView titleBar = (TextView) findViewById(R.id.road_titlebar_text);
		titleBar.setText(R.string.address_search_input_prompt);
		TextView tvTitle = (TextView) findViewById(R.id.road_input_title);
		tvTitle.setText(itenCaller.getStringExtra("addressSelection"));
		etAddress = (EditText) findViewById(R.id.road_input_edit);
		Button btnLocate = (Button) findViewById(R.id.road_input_locate_button);
		btnLocate.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		addressRest = etAddress.getText().toString();
		DialogHandler(whichDialog);
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.i("RoadInput_pause", "whichDialog:" + whichDialog);
		if (whichDialog > 0)
			progressDialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		final View cView = v;

		switch (v.getId()) {
		case R.id.road_input_locate_button:
			addressRest = etAddress.getText().toString();

			if (addressRest.isEmpty()) {
				DialogHandler(DataProgressDialog.DIALOG_CONFIRM);
				return;
			}
			DialogHandler(DataProgressDialog.DIALOG_LOADING);

			Thread t = new Thread() {
				@Override
				public void run() {
					btnLocate_Click(cView);
					progressDialog.dismiss();
					whichDialog = 0;
				}
			};
			t.start();
			break;
		}
	}

	protected void btnLocate_Click(Object sender) {
        /**
         * 這裡是在鬧什麼啦!!! (ˋ_>ˊ)
         * by Vincent.
         */
		if (sender == null || !sender.getClass().getName().equals("android.widget.Button")) {
			throw new IllegalArgumentException("sender is not valid.");
		}
		Log.i("RoadInput", "Locate Button Clicked. sender is : " + sender.getClass().getName());
        /*********************/


		String addressPart = itenCaller.getStringExtra("addressSelection");
        Log.i("RoadInput", "addressPart: " + addressPart);

		double[] addressXY = sonav.getInstance().showaddrxy1(addressPart + addressRest);

		// has a result finish Activity here and put results
		if (addressXY[0] > 0.0) {
			Intent itenContent = new Intent(this, AddressContent.class);
			itenContent.putExtra("addressResult", addressPart + addressRest);
			itenContent.putExtra("addressLocation", addressXY);
			itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));

			Log.i("RoadInput", "got Location" + addressXY[0] + ", " + addressXY[1] + ", " + addressXY[2]);

			startActivityForResult(itenContent, ActivityCaller.ADDRESS.getValue());
			return;
		}

		// call RoadSelection for Last Result
		Intent itenContent = new Intent(this, RoadSelection.class);
		itenContent.putExtra("townID", townId);
		itenContent.putExtra("roadName", addressRest);
		itenContent.putExtra("addressSelection", addressPart);
		itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
		Log.i("RoadInput", "no Location Found." + addressXY[0] + ", " + addressXY[1] + ", " + addressXY[2]);
		startActivityForResult(itenContent, ActivityCaller.ADDRESS.getValue());
	}

    private void showContextSelection() {
        final String[] options = new String[] {
                ContextMenuOptions.DRAW_MAP.getTitle(),
                ContextMenuOptions.SET_ORIGIN.getTitle(),
                ContextMenuOptions.SET_DESTINATION.getTitle() };

        final DialogInterface.OnClickListener dlgListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                itenCaller.putExtra("addressAction", ContextMenuOptions.get(options[which]));
                setResult(RESULT_OK, itenCaller);
                finish();
            }
        };
    }

    private void DialogHandler(int which) {
        switch (which) {
            case DataProgressDialog.DIALOG_LOADING:
                progressDialog.progressDialog(null, getString(R.string.dialog_loading_message));
                break;

            case DataProgressDialog.DIALOG_SELECTION:
                showContextSelection();
                break;

            case DataProgressDialog.DIALOG_CONFIRM:

                break;
        }
        whichDialog = which;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == ActivityCaller.ADDRESS.getValue()) {
			ContextMenuOptions option = (ContextMenuOptions) data.getSerializableExtra("Action");

			String addressPart = data.getStringExtra("addressResult");
			double[] addressXY = data.getDoubleArrayExtra("addressLocation");

			itenCaller.putExtra("Action", option);
			itenCaller.putExtra("addressResult", addressPart);
			itenCaller.putExtra("addressLocation", addressXY);

			setResult(RESULT_OK, itenCaller);
			finish();
		}
        else if (resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}
	}
}
