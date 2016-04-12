package com.kingwaytek.cpami.bykingTablet.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

public class MapDownloadAdapter extends BaseAdapter {

	private String[] dataSource;
	private String[] dataSource_context;
	private LayoutInflater inflater;
	private int area;

	public MapDownloadAdapter(Context context, String[] dataSource, String[] dataSource_context, int area) {
		this.area = area;
		this.dataSource = dataSource;
		this.dataSource_context = dataSource_context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return dataSource.length;
	}

	@Override
	public Object getItem(int arg0) {
		return dataSource[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.cell_map_download, null);
		}
		TextView title = (TextView) arg1.findViewById(R.id.textView_title);
		TextView context = (TextView) arg1.findViewById(R.id.textView_context);

		if (arg0 == area) {
			title.setText(dataSource[arg0] + "(目前使用中)");
		} else {
			title.setText(dataSource[arg0]);
		}

		if (dataSource_context[arg0].equals("")) {
			context.setVisibility(View.GONE);
		} else {
			context.setVisibility(View.VISIBLE);
			context.setText(dataSource_context[arg0]);
		}

		return arg1;
	}
}
