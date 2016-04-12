package com.kingwaytek.cpami.bykingTablet.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ListMode;

/**
 * List Activity Adapter for General Text Only List
 * 
 * @author yawhaw_ou (andy.chiao@kingwaytek.com)
 */
public class ListSimpleAdapter extends SimpleAdapter {

	private Context adpContext;
	private int adpResource;
	private Map<Integer, Boolean> checkboxData;
	private ListMode whichMode;

	// private boolean[] PreferenceState ;

	// public ListSimpleAdapter (Context context, List<? extends Map<String, ?>>
	// data, int resource, String[] from, int[] to) {
	public ListSimpleAdapter(Context context, ArrayList<HashMap<String, Object>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);

		adpContext = context;
		adpResource = resource;
		checkboxData = null;
		whichMode = ListMode.MULTIPLE;
		initCheckboxData(5);

	}

	public Map<Integer, Boolean> getCheckBoxData() {
		return checkboxData;
	}

	public ListMode getListMode() {
		return whichMode;
	}

	public void setListMode(ListMode mode) {
		whichMode = (mode == null) ? ListMode.SINGLE : mode;
	}

	private void initCheckboxData(int size) {
		checkboxData = new LinkedHashMap<Integer, Boolean>(size);
		for (int i = 0; i < size; i++) {
			checkboxData.put(i, false);
		}
	}

	// public void setBindingData(String[] from) {
	// super.clear();
	//
	// if (from == null)
	// return;
	// for (String strData : from) {
	// super.add(strData);
	// }
	//
	// initCheckboxData(from.length);
	// }

	// public void setBindingData(List<String> from) {
	// super.clear();
	//
	// if (from == null)
	// return;
	// for (String strData : from) {
	// super.add(strData);
	// }
	//
	// initCheckboxData(from.size());
	// }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// if (convertView == null) {
		// convertView = LayoutInflater.from(adpContext).inflate(adpResource,
		// null);
		// }
		Log.i("ListSimpleAdapter.java", "getView()");
		String[] itemTitle = { "連線取得座標及氣象", "網路連線提示", "錄製軌跡提示", "安全及使用聲明", "活動訊息提示", "週邊查詢的距離" };
		String[] itemText = { "設定網路連線取得資訊", "顯示網路連線的提示視窗", "顯示錄製軌跡的提示視窗", "停止使用安全及使用聲明的提示視窗", "", "" };

		convertView = LayoutInflater.from(adpContext).inflate(adpResource, null);

		TextView TopTextView = (TextView) convertView.findViewById(R.id.topTextView);
		TextView BottomTextView = (TextView) convertView.findViewById(R.id.bottomTextView);

		TopTextView.setText(itemTitle[position]);
		BottomTextView.setText(itemText[position]);

		CheckBox ckbSelect = (CheckBox) convertView.findViewById(R.id.CheckBox01);

		// ckbSelect
		// .setVisibility((whichMode == ListMode.MULTIPLE) ? CheckBox.VISIBLE
		// : CheckBox.GONE);

		ckbSelect.setVisibility(CheckBox.VISIBLE);
		ckbSelect.setChecked(checkboxData.get(position));

		return convertView;// super.getView(position, convertView, parent);
	}

}