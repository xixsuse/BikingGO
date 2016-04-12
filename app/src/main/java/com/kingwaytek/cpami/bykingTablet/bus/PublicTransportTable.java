package com.kingwaytek.cpami.bykingTablet.bus;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

public class PublicTransportTable extends TabActivity {
	private Intent intent;
	private int RouteSize;
	private TabHost tabHost;
	private TabHost.TabSpec spec;
	TabHost.TabSpec spec2;
	TabHost.TabSpec spec3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.public_transport_table);
		intent = this.getIntent();
		String URL = intent.getStringExtra("TrackPointString");
		RouteSize = intent.getIntExtra("RouteSize", 1);
		Resources res = getResources();
		tabHost = getTabHost();

		Intent intent;
		intent = new Intent().setClass(this, PublicTransportList.class);
		intent.putExtra("TrackPointString", URL);
		intent.putExtra("whichPlan", 0);

		spec = tabHost.newTabSpec("方案一")
				.setIndicator(this.createTabView("方案一")).setContent(intent);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, PublicTransportList.class);
		intent.putExtra("TrackPointString", URL);
		intent.putExtra("whichPlan", 1);

		spec2 = tabHost.newTabSpec("方案二")
				.setIndicator(this.createTabView("方案二")).setContent(intent);

		intent = new Intent().setClass(this, PublicTransportList.class);
		intent.putExtra("TrackPointString", URL);
		intent.putExtra("whichPlan", 2);

		spec3 = tabHost.newTabSpec("方案三")
				.setIndicator(this.createTabView("方案三")).setContent(intent);

		addTable(RouteSize);
		tabHost.setCurrentTab(0);
	}

	private void addTable(int Size) {
		if (Size == 1) {
			tabHost.addTab(spec);
		} else if (Size == 2) {
			tabHost.addTab(spec);
			tabHost.addTab(spec2);
		} else if (Size == 3) {
			tabHost.addTab(spec);
			tabHost.addTab(spec2);
			tabHost.addTab(spec3);
		}
	}

	private View createTabView(String title) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View tabView = inflater.inflate(R.layout.tab_costume, null);

		((TextView) tabView.findViewById(R.id.textView1)).setText(title);

		return tabView;
	}
}
