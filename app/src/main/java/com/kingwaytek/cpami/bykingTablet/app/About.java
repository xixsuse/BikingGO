package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		TextView SoftwareVersionTextView = (TextView) findViewById(R.id.software_version_text);
		TextView DataVersionTextView = (TextView) findViewById(R.id.data_version_text);
		String versionName = "";
		try {
			versionName = getPackageManager()
				    .getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SoftwareVersionTextView.setText(versionName);

		DataVersionTextView.setText(StartupActivity.DataVersion);
	}
}
