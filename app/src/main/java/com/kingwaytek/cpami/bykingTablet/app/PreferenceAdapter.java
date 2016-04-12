package com.kingwaytek.cpami.bykingTablet.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

public class PreferenceAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private int[] imageSource = new int[] { R.drawable.pref_operate_selector,
			R.drawable.pref_navi_selector, R.drawable.pref_health_selector,
			R.drawable.pref_update_selector, R.drawable.pref_about_selector,
			R.drawable.pref_map_selector };

	private int[] textSource = new int[] { R.string.preference_operation,
			R.string.preference_navi, R.string.preference_health,
			R.string.preference_update, R.string.preference_about,
			R.string.map_download };

	public PreferenceAdapter(Context contex) {
		inflater = (LayoutInflater) contex
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return imageSource.length;
	}

	@Override
	public Object getItem(int arg0) {
		return textSource[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.cell_pref, null);
		}

		((TextView) arg1.findViewById(R.id.textView1))
				.setText(textSource[arg0]);

		((TextView) arg1.findViewById(R.id.textView1))
				.setCompoundDrawablesWithIntrinsicBounds(0, imageSource[arg0],
						0, 0);

		return arg1;
	}

}
