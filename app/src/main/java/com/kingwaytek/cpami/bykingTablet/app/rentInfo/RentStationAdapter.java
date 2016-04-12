package com.kingwaytek.cpami.bykingTablet.app.rentInfo;

import java.util.ArrayList;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.CityObject;
import com.kingwaytek.cpami.bykingTablet.app.Util;

import android.R.integer;
import android.R.raw;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RentStationAdapter extends BaseAdapter {
	private int layout;
	private Context context;
	private ArrayList<CityObject> city_sort;

	public RentStationAdapter(int layout, Context context, ArrayList<CityObject> city_sort) {
		this.layout = layout;
		this.context = context;
		this.city_sort = city_sort;
	}

	@Override
	public int getCount() {
		return city_sort.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(this.layout, null);
		TextView title = (TextView) view.findViewById(R.id.rentStation_cell_title);

		title.setText(city_sort.get(position).getCityName());
		view.setTag(city_sort.get(position).getCityCode());

		return view;
	}
}
