package com.kingwaytek.cpami.bykingTablet.app.Infomation;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

public class InfomationListAdapter extends BaseAdapter {

	private ArrayList<InfomationObject> objects;

	private LayoutInflater inflater;

	public InfomationListAdapter(Context context,
			ArrayList<InfomationObject> objects) {

		this.objects = objects;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int arg0) {
		return objects.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.cell_infomation_list, null);
		}

		InfomationObject object = objects.get(arg0);

		// Title
		((TextView) arg1.findViewById(R.id.textView1))
				.setText(object.getName());

		return arg1;
	}

}
