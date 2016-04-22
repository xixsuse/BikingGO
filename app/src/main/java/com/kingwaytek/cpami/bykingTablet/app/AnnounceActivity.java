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

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
import com.sonavtek.sonav.sonav;

public class AnnounceActivity extends Activity {
	private sonav engine;
	public String pv6 = "010601,010701,070401,071008,071415,080401";
	public String pv4 = "";
	public String pv2 = "01,02,03,04,05,06,07,08,09,10,11,12,13,14";

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		engine = sonav.getInstance();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		if (SettingManager.isAnnouncementNecessary()) {

			this.setContentView(R.layout.activity_announce);

			Button button = (Button) findViewById(R.id.startup_terms_of_use_summit);
			final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox_terms_of_use_summit);

			final float scale = this.getResources().getDisplayMetrics().density;

			checkbox.setPadding(checkbox.getPaddingLeft() + (int) (10.0f * scale + 0.5f), checkbox.getPaddingTop(),
                    checkbox.getPaddingRight(), checkbox.getPaddingBottom());

			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SettingManager.setAnnouncementNecessary(!checkbox.isChecked());
					goToMapActivity();
				}
			});
		}
        else
			goToMapActivity();
	}

    private void goToMapActivity() {
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

        Bundle params = getIntent().getExtras();    //這裡 always是 NULL!!!
        Intent intent = new Intent(this, MapActivity.class);

        if (params != null) {
            intent.putExtra("SMSToMapActivity", 1);
            intent.putExtras(params);
        }
        startActivity(intent);
        finish();
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            Utility.forceCloseTask();
			return false;
		}
        else {
			Log.i("MapAvtivity.java", "super.onKeyDown(keyCode, event);");
			return super.onKeyDown(keyCode, event);
		}
	}
}
