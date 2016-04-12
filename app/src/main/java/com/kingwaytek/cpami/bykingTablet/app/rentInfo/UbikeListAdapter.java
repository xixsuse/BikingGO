package com.kingwaytek.cpami.bykingTablet.app.rentInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.kingwaytek.cpami.bykingTablet.R;

import android.R.raw;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class UbikeListAdapter extends BaseAdapter {

	private Context context;
	private int layout;
	private ArrayList<UbikeObject> Ubike_list;
	private ArrayList<DistenceObject> distence_list;

	public UbikeListAdapter(Context context, int layout, ArrayList<UbikeObject> Ubike_list,
			ArrayList<DistenceObject> distence_list) {

		this.context = context;
		this.layout = layout;
		this.Ubike_list = Ubike_list;
		this.distence_list = distence_list;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Ubike_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(this.layout, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.ubike_cell_title);
			viewHolder.dis = (TextView) convertView.findViewById(R.id.ubike_cell_dis);
			viewHolder.sbi = (TextView) convertView.findViewById(R.id.ubike_cell_sbi);
			viewHolder.bemp = (TextView) convertView.findViewById(R.id.ubike_cell_bemp);
			viewHolder.myfavorButton = (Button) convertView.findViewById(R.id.ubike_cell_myfavor);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.myfavorButton.setVisibility(View.GONE);
		if (distence_list.size() == 0) {
			// unknow Location and sort by Ubike_list
			viewHolder.title.setText(Ubike_list.get(position).getsna());
			viewHolder.sbi.setText("" + Ubike_list.get(position).getsbi());
			viewHolder.bemp.setText("" + Ubike_list.get(position).getbemp());
			viewHolder.dis.setText("--km");
		} else {
			// got Location and sort by distence_list
			double temp_dis = distence_list.get(position).getDis();
			int temp_sno = distence_list.get(position).getSno();
			DecimalFormat df = new DecimalFormat("#.##");

			for (int i = 0; i < Ubike_list.size(); i++) {
				if (Ubike_list.get(i).getsno() == distence_list.get(position).getSno()) {
					viewHolder.title.setText(Ubike_list.get(i).getsna());
					viewHolder.sbi.setText("" + Ubike_list.get(i).getsbi());
					viewHolder.bemp.setText("" + Ubike_list.get(i).getbemp());
					break;
				}
			}
			viewHolder.dis.setText("" + df.format(distence_list.get(position).getDis()) + "km");
		}
		return convertView;
	}

	private class ViewHolder {
		TextView title;
		TextView dis;
		TextView sbi;
		TextView bemp;
		Button myfavorButton;
	}

}
