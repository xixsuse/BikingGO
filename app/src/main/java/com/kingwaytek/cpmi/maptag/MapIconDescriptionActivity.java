package com.kingwaytek.cpmi.maptag;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;

public class MapIconDescriptionActivity extends Activity implements
		OnClickListener {

	/* View */
	private View view1;
	private View view2;
	private View view3;
	private ListView listView;
	private MapIconDescriptionAdapter adapter;

	/* Data */
	private ArrayList<MapIconDescriptionObject> dataSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_icon_description);

		this.initialDataSource();
		this.findViews();
	}

	private void initialDataSource() {
		dataSource = MapIconDataSourceCreater.getDataSource();
		adapter = new MapIconDescriptionAdapter(this, dataSource);
	}

	private void findViews() {
		listView = (ListView) this.findViewById(R.id.listView1);
		listView.setAdapter(adapter);

		view1 = (View) this.findViewById(R.id.view1);
		view2 = (View) this.findViewById(R.id.view2);
		view3 = (View) this.findViewById(R.id.view3);
		view1.setOnClickListener(this);
		view2.setOnClickListener(this);
		view3.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		this.onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		this.finish();
	}

}
