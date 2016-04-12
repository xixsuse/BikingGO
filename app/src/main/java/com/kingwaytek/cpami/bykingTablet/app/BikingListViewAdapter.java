package com.kingwaytek.cpami.bykingTablet.app;

import java.util.List;
import java.util.Map;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

public class BikingListViewAdapter extends BaseAdapter {

	private int image;
	private String[] imgText;
	private int layout;

	public BikingListViewAdapter(int layout, int image, String[] imgText) {
		this.layout = layout;
		this.image = image;
		this.imgText = imgText;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imgText.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(this.layout, null);
			viewHolder.img = (ImageView) convertView.findViewById(R.id.list_cell_imageview);
			viewHolder.txv = (TextView) convertView.findViewById(R.id.selection_listview_item_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.txv.setText(imgText[position]);
		viewHolder.img.setBackgroundResource(image);
		
		return convertView;
	}

	private class ViewHolder {
		ImageView img;
		TextView txv;
	}
}
