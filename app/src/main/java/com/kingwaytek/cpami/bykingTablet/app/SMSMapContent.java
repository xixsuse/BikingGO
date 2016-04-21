package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.maps.IMapView;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.sonav;

public class SMSMapContent extends Activity {
	private sonav engine;

	private IMapView mapView;
	private RelativeLayout rlMapOption;
	private RelativeLayout rlMapZoom;
	private RelativeLayout rlMap;
	
	private Intent itenCaller;
	private GeoPoint point;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		itenCaller = getIntent();
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		//setContentView(R.layout.smsmap);
		setContentView(R.layout.map);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);
		setTitle(getString(R.string.title_default));
		
		AddFloatContentView();
		InitFixedMapView();
		
		
	}
	@Override
	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		setViews();
	}
	
	private void InitFixedMapView() {
		//mapView = (IMapView) findViewById(R.id.smsmapView);
		mapView = (IMapView) findViewById(R.id.mapView);
		mapView.setViewType(MapView.VIEW_2D);
		// mapView.setCenter(new GeoPoint(121.522069004011, 25.0270332995188));
		point=new GeoPoint(itenCaller.getDoubleExtra("Lon", 121.522069004011),
				itenCaller.getDoubleExtra("Lat", 25.0270332995188));

		mapView.setCenter(point);

		rlMapOption = (RelativeLayout) findViewById(R.id.option_view);
		rlMapZoom = (RelativeLayout) findViewById(R.id.mapView_zoom_layout);
		rlMap = (RelativeLayout) findViewById(R.id.mapLayout);

		rlMapOption.setVisibility(RelativeLayout.GONE);
		rlMapZoom.setVisibility(RelativeLayout.GONE);

		engine = sonav.getInstance();
		
		int mapStyle = SettingManager.getMapStyle();
		if (mapStyle < 6) {
			engine.setmapstyle(0, mapStyle, 1);
		}else{
			mapStyle-=5;
			engine.setmapstyle(1, 0, mapStyle);
		}
		engine.savenaviparameter();
		engine.savenaviparameter();
		final View emptyView = new View(this);
		emptyView.setMinimumWidth(engine.getMapWidth());
		emptyView.setMinimumHeight(engine.getMapHeight());
		emptyView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		rlMap.addView(emptyView);

	}
	private void AddFloatContentView() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		View optionView = inflater.inflate(R.layout.map_data_content, null);
		optionView.setId(R.id.poi_content_view);

		layout.addView(optionView);
	}
	private void setViews() {
		//point=new GeoPoint(itenCaller.getDoubleExtra("Lon", 121.522069004011),
		//		itenCaller.getDoubleExtra("Lat", 25.0270332995188));

		//mapView.setCenter(point);
		engine.setflagpoint(MapView.SELECTED_POINT, mapView.getCenter()
				.getLongitude(), mapView.getCenter().getLatitude());
		
		TextView tvName = (TextView) findViewById(R.id.map_data_content_view_name);
		TextView tvAddr = (TextView) findViewById(R.id.map_data_content_view_address);
		TextView tvTel = (TextView) findViewById(R.id.map_data_content_view_tel);
		Button btnCall = (Button) findViewById(R.id.map_data_content_view_call_button);
		
		
		tvName.setText(itenCaller.getStringExtra("name"));
		tvAddr.setText("Lon:"+String.valueOf(point.getLongitude()));
		tvTel.setText("Lat:"+String.valueOf(point.getLatitude()));
		btnCall.setVisibility(View.GONE);
	
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	// ??秋??怨????抆???ack??      
    if (keyCode == KeyEvent.KEYCODE_BACK) {
    	this.finish();
	    return true;     
	}else {
		return super.onKeyDown(keyCode, event);
		} 
    } 

}
