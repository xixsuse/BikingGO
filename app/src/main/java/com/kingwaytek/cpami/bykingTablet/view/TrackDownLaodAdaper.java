package com.kingwaytek.cpami.bykingTablet.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackListObject;

/**
 * List Activity Adapter for General Text Only List
 * 
 * @author yawhaw_ou (yawhaw@kingwaytek.com)
 */

public class TrackDownLaodAdaper extends BaseAdapter {

	private ArrayList<TrackListObject> dataSource;

	private LayoutInflater inflater;

	public TrackDownLaodAdaper(Context context, ArrayList<TrackListObject> dataSource) {

		this.dataSource = dataSource;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.cell_track_download, null);
		}

		TrackListObject object = dataSource.get(position);

		((TextView) convertView.findViewById(R.id.textView1)).setText(object.getRouteName());

		if (!object.getLength().contains("null")) {
			((TextView) convertView.findViewById(R.id.textView2)).setText(object.getTime() + "  " + object.getLength()
					+ "公里");
		} else {
			((TextView) convertView.findViewById(R.id.textView2)).setText(object.getTime());
		}
		((TextView) convertView.findViewById(R.id.textView3)).setText(object.getRouteID());

		return convertView;
	}

	@Override
	public int getCount() {
		return dataSource.size();
	}

	@Override
	public Object getItem(int arg0) {
		return dataSource.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}
