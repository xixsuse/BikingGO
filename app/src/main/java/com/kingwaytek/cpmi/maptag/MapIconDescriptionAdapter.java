package com.kingwaytek.cpmi.maptag;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

public class MapIconDescriptionAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MapIconDescriptionObject> dataSource;

	public MapIconDescriptionAdapter(Context context,
			ArrayList<MapIconDescriptionObject> dataSource) {
		this.context = context;
		this.dataSource = dataSource;
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

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if (arg1 == null) {
			arg1 = LayoutInflater.from(context).inflate(R.layout.cell_map_tag,
					null);
		}

		((TextView) arg1.findViewById(R.id.textView1)).setText(dataSource.get(
				arg0).getDescription());

		((TextView) arg1.findViewById(R.id.textView1))
				.setCompoundDrawablesWithIntrinsicBounds(dataSource.get(arg0)
						.getIcon(), 0, 0, 0);

		return arg1;
	}

}
