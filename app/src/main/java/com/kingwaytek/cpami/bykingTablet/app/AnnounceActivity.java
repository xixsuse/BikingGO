package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.sonavtek.sonav.sonav;

public class AnnounceActivity extends Activity {
	private ImageView declaration;
	private sonav engine;
	public String pv6 = "010601,010701,070401,071008,071415,080401";
	public String pv4 = "";
	public String pv2 = "01,02,03,04,05,06,07,08,09,10,11,12,13,14";

	// public void onCreate(Bundle icicle) {
	// super.onCreate(icicle);
	// Log.v("TAG","onCreate"); }
	// public void onStart() {
	// super.onStart();
	// Log.v("TAG","onStart");
	// }
	// public void onResume() {
	// super.onResume();
	// Log.v("TAG","onResume");
	// }
	// public void onPause() {
	// super.onPause();
	// Log.v("TAG","onPause"); }
	// public void onStop() {
	// super.onStop();
	// Log.v("TAG","onStop"); }
	// public void onRestart() {
	// super.onRestart();
	// Log.v("TAG","onReStart"); }
	// public void onDestroy() {
	// super.onDestroy();
	// Log.v("TAG","onDestroy"); }

	public void onCreate(Bundle icicle) {
		Log.i("AnnounceActivity.java", "onCreat");
		super.onCreate(icicle);

		engine = sonav.getInstance();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// int intScreenX = dm.widthPixels;
		int intScreenY = dm.heightPixels;
		if (PreferenceActivity.isAnnouncementEnabled(AnnounceActivity.this)
				.equalsIgnoreCase("false")) {
			/*
			 * 320*427是tattoo320*480是hero 320*569是moto 320*533是desire
			 */

			// if ((intScreenX == 320 && intScreenY == 427)
			// || (intScreenX == 320 && intScreenY == 480)) {
			// setContentView(R.layout.declaration_v);
			// } else if (intScreenX == 320 && intScreenY == 533) {
			// setContentView(R.layout.declaration_320_533);
			// } else {
			// setContentView(R.layout.declaration);
			//
			// if (intScreenX == 320 && intScreenY == 569) {
			//
			// declaration = (ImageView) findViewById(R.id.terms_of_use_image);
			// declaration.setImageResource(R.drawable.terms_of_use_fwvga);
			// } else if (intScreenX == 320 && intScreenY == 533) {
			//
			// declaration = (ImageView) findViewById(R.id.terms_of_use_image);
			// declaration.setImageResource(R.drawable.terms_of_use_wvga);
			// } else {
			// declaration = (ImageView) findViewById(R.id.terms_of_use_image);
			// declaration.setImageResource(R.drawable.terms_of_use);
			// }
			//
			// }

			this.setContentView(R.layout.activtiy_annonce);

			Button button = (Button) findViewById(R.id.startup_terms_of_use_summit);
			final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox_terms_of_use_summit);

			final float scale = this.getResources().getDisplayMetrics().density;
			checkbox.setPadding(checkbox.getPaddingLeft()
					+ (int) (10.0f * scale + 0.5f), checkbox.getPaddingTop(),
					checkbox.getPaddingRight(), checkbox.getPaddingBottom());

			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					engine.setpoiauto(0);
					engine.setpoivisible(pv6, pv4, pv2, 0);
					engine.setpoivisible(pv6, pv4, pv2, 1);
					engine.setpoivisible(pv6, pv4, pv2, 2);
					engine.setpoivisible(pv6, pv4, pv2, 3);
					engine.setpoivisible(pv6, pv4, pv2, 4);
					engine.setpoivisible(pv6, pv4, pv2, 5);
					/*** 清除marker ***/
					engine.setflagpoint(0, -1, -1);
					engine.setflagpoint(1, -1, -1);
					engine.setflagpoint(2, -1, -1);
					engine.setflagpoint(3, -1, -1);
					engine.setflagpoint(5, -1, -1);
					/***************/

					PreferenceActivity.setAnnouncementEnabled(
							AnnounceActivity.this, checkbox.isChecked());
					Bundle params = getIntent().getExtras();

					Intent intent = new Intent(AnnounceActivity.this,
							MapActivity.class);
					if (params != null) {
						intent.putExtra("SMSToMapActivity", 1);
						intent.putExtras(params);
					}
					startActivity(intent);
					finish();

				}
			});
		} else {
			engine.setpoiauto(0);
			engine.setpoivisible(pv6, pv4, pv2, 0);
			engine.setpoivisible(pv6, pv4, pv2, 1);
			engine.setpoivisible(pv6, pv4, pv2, 2);
			engine.setpoivisible(pv6, pv4, pv2, 3);
			engine.setpoivisible(pv6, pv4, pv2, 4);
			engine.setpoivisible(pv6, pv4, pv2, 5);
			/*** 清除marker ***/
			engine.setflagpoint(0, -1, -1);
			engine.setflagpoint(1, -1, -1);
			engine.setflagpoint(2, -1, -1);
			engine.setflagpoint(3, -1, -1);
			engine.setflagpoint(5, -1, -1);
			/***************/
			Bundle params = getIntent().getExtras();
			Intent intent = new Intent(AnnounceActivity.this, MapActivity.class);
			if (params != null) {
				intent.putExtra("SMSToMapActivity", 1);
				intent.putExtras(params);
			}
			startActivity(intent);
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			finish();
		}
	}

	public void onPause() {
		super.onPause();
		Log.v("TAG", "onPause");
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("AnnounceActivity.java", "onDestroy");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("MapActivity.java", "onKeyDown");

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			android.os.Process.killProcess(android.os.Process.myPid());
			finish();

			return false;
		} else {
			Log.i("MapAvtivity.java", "super.onKeyDown(keyCode, event);");
			return super.onKeyDown(keyCode, event);
		}
	}

}
