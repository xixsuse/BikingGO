package com.kingwaytek.cpami.bykingTablet.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.sql.Track;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ListMode;

/**
 * This Adapter will render a ListActivity with data that provided. Only Texts,
 * Icons, Check boxes will be rendered.<br>
 * <br>
 * Example: <br>
 * Map<Integer, Object[]> data = new Map<Integer, Object[]>();<br>
 * data.put(R.id.textview1, new String[]{"A", "B", "C"});<br>
 * data.put(R.id.textview2, new Object[]{"1", "2", "3"});<br>
 * 
 * ListViewAdapter listAdapter = new ListViewAdpater(activity,
 * R.layout.item_layout, data);<br>
 * <br>
 * listAdapter.setCheckBoxID(R.id.checkbox);<br>
 * 
 * listAdapter.AddIconData(R.id.imageview, new int[]{R.id.image1, R.id.image2}); <br>
 * listAdapter.setListMode(ListMode.MULTIPLE);<br>
 * listAdapter.getCheckBoxData().put(position, true);<br>
 * listAdatper.getDataVisibilityState().put(R.id.textview2, false);<br>
 * 
 * setAdapter(listAdapter);<br>
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 * 
 */
public class ListViewAdapter extends BaseAdapter {

	private final Context context;
	private final int layoutID;
	private final Map<Integer, Object[]> listData;

	private Map<Integer, Integer[]> iconData;
	private Map<Integer, Boolean> dataVisibility;
	private long[] cursorDataID;

	private int checkboxID;
	private Map<Integer, Boolean> ckbData;
	private ListMode whichMode;

	private ColorStateList spoiNameColor;

	private boolean isTrackList;
	private String isTrack = "";

	/**
	 * Adapter with Simple 1-dimension array of String data.
	 * 
	 * @param ctx
	 *            context that this adapter resides.
	 * @param layout
	 *            ID of the list item layout view.
	 * @param item
	 *            ID of the item that will display data
	 * @param data
	 *            string data to be rendered.
	 */
	public ListViewAdapter(final Context ctx, final int layout, final int item, final String[] data) {
		this.context = ctx;
		this.layoutID = layout;
		this.dataVisibility = new LinkedHashMap<Integer, Boolean>();
		this.listData = ListDataFactory(item, data);
		this.iconData = new LinkedHashMap<Integer, Integer[]>();
		this.checkboxID = 0;
		this.whichMode = ListMode.SINGLE;
	}

	/**
	 * Adapter that behave like SimpleCursorAdapter
	 * 
	 * @param ctx
	 *            context that this adapter resides.
	 * @param layout
	 *            ID of the list item layout view.
	 * @param cursor
	 *            cursor that contains data to be rendered.
	 * @param from
	 *            column names that wished to be shown.
	 * @param to
	 *            IDs of the items that will display data.
	 */
	public ListViewAdapter(final Context ctx, final int layout, final Cursor cursor, final String[] from, final int[] to) {
		this.context = ctx;
		this.layoutID = layout;
		this.dataVisibility = new LinkedHashMap<Integer, Boolean>();
		this.listData = ListDataFactory(cursor, from, to);
		this.iconData = new LinkedHashMap<Integer, Integer[]>();
		this.checkboxID = 0;
		this.whichMode = ListMode.SINGLE;
	}

	/* for track */
	public ListViewAdapter(final Context ctx, final int layout, final Cursor cursor, final String[] from,
			final int[] to, String isTrack) {
		this.context = ctx;
		this.layoutID = layout;
		this.dataVisibility = new LinkedHashMap<Integer, Boolean>();
		this.listData = ListDataFactory(cursor, from, to);
		this.iconData = new LinkedHashMap<Integer, Integer[]>();
		this.checkboxID = 0;
		this.whichMode = ListMode.SINGLE;
		this.isTrack = isTrack;
	}

	/**
	 * Adapter that will display more than one items in a layout
	 * 
	 * @param ctx
	 *            context that this adapter resides.
	 * @param layout
	 *            ID of the list item layout view.
	 * @param data
	 *            data in Map form that will be rendered.
	 */
	public ListViewAdapter(final Context ctx, final int layout, final Map<Integer, Object[]> data) {
		this.context = ctx;
		this.layoutID = layout;
		this.listData = data;
		this.iconData = new LinkedHashMap<Integer, Integer[]>();
		this.cursorDataID = null;
		this.dataVisibility = new LinkedHashMap<Integer, Boolean>();
		Iterator<Entry<Integer, Object[]>> itData = data.entrySet().iterator();
		while (itData.hasNext()) {
			Entry<Integer, Object[]> entry = itData.next();
			this.dataVisibility.put(entry.getKey(), true);
		}
		InitializeCheckboxData(data.entrySet().iterator().next().getValue().length);
		this.checkboxID = 0;
		this.whichMode = ListMode.SINGLE;
	}

	/**
	 * gets ID of the check box.
	 * 
	 * @return 0 if no check box assigned, the resource ID of the check box
	 *         otherwise.
	 */
	public int getCheckBoxID() {
		return this.checkboxID;
	}

	/**
	 * gets ListMode of the list view.
	 * 
	 * @return the ListMode
	 */
	public ListMode getListMode() {
		return this.whichMode;
	}

	/**
	 * gets the DataVisibility Collection
	 * 
	 * @return the DataVisibility Collection.
	 */
	public Map<Integer, Boolean> getDataVisibilityStates() {
		return this.dataVisibility;
	}

	/**
	 * gets the check box State Collection
	 * 
	 * @return the Check Box State Collection.
	 */
	public Map<Integer, Boolean> getCheckBoxData() {
		return this.ckbData;
	}

	/**
	 * sets the ListMode
	 * 
	 * @param mode
	 *            list mode to set.
	 */
	public void setListMode(ListMode mode) {
		this.whichMode = mode;
	}

	/**
	 * sets ID of the Check Box
	 * 
	 * @param checkbox
	 *            Resource ID of a check box to set.
	 */
	public void setCheckBoxID(int checkbox) {
		this.checkboxID = checkbox;
	}

	/**
	 * Reset check box data collection, set all state to unchecked .
	 */
	public void ResetCheckBoxData() {
		if (this.listData == null || this.listData.size() <= 0) {
			return;
		}
		InitializeCheckboxData(this.listData.size());
	}

	/**
	 * Add an Icon data to adapter.
	 * 
	 * @param item
	 *            ID of the item that will display icon.
	 * @param drawable
	 *            ID of the drawable to be rendered.
	 */
	public void AddIconData(final int item, final int drawable) {
		Integer[] repeatIcons = new Integer[getCount()];

		for (int i = 0; i < repeatIcons.length; i++) {
			repeatIcons[i] = drawable;
		}

		this.iconData.put(item, repeatIcons);
	}

	/**
	 * Add a collection of icon data to adapter.
	 * 
	 * @param item
	 *            ID of the item that will display icon.
	 * @param drawables
	 *            IDs of the drawable to be rendered.
	 */
	public void AddIconData(final int item, final int[] drawables) {
		if (drawables == null || drawables.length <= 0) {
			return;
		}
		if (drawables.length != getCount()) {
			throw new IllegalArgumentException("item count is invalid.");
		}
		Integer[] icons = new Integer[getCount()];

		for (int i = 0; i < getCount(); i++) {
			icons[i] = drawables[i];
		}

		this.iconData.put(item, icons);
	}

	/**
	 * Initialize the check box state to unchecked.
	 * 
	 * @param size
	 *            how many check boxes to be initialized.
	 */
	private void InitializeCheckboxData(int size) {
		ckbData = new LinkedHashMap<Integer, Boolean>(size);
		for (int i = 0; i < size; i++) {
			ckbData.put(i, false);
		}
	}

	/**
	 * Generate list Data Collection
	 * 
	 * @param itemID
	 *            ID of the list item layout.
	 * @param data
	 *            1-dimension of String data.
	 * @return list data Collection.
	 */
	private Map<Integer, Object[]> ListDataFactory(final int itemID, final String[] data) {
		if (data == null || data.length <= 0) {
			return null;
		}

		Map<Integer, Object[]> mapData = new LinkedHashMap<Integer, Object[]>(1);

		mapData.put(itemID, data);
		this.cursorDataID = null;
		InitializeCheckboxData(data.length);

		return mapData;
	}

	/**
	 * Generate list Data Collection
	 * 
	 * @param cursor
	 *            cursor contains data.
	 * @param from
	 *            column names to be displayed.
	 * @param to
	 *            IDs of items to be rendered.
	 * @return list data Collection.
	 */
	private Map<Integer, Object[]> ListDataFactory(final Cursor cursor, final String[] from, final int[] to) {
		if (cursor == null || from == null || to == null || cursor.getCount() <= 0 || from.length <= 0
				|| to.length <= 0) {
			return null;
		}
		if (from.length != to.length) {
			throw new IllegalArgumentException("from does not match to.");
		}
		final boolean hasIDColumn = cursor.getColumnIndex(CursorColumn.ID.get()) != -1;
		Map<Integer, Object[]> mapData = new LinkedHashMap<Integer, Object[]>(to.length);
		this.cursorDataID = hasIDColumn ? new long[cursor.getCount()] : null;
		// this.dataVisibility = new LinkedHashMap<Integer, Boolean>(to.length);

		// Initialize obj array and put visibilty data
		for (int i = 0; i < to.length; i++) {
			mapData.put(to[i], new Object[cursor.getCount()]);
			this.dataVisibility.put(to[i], true);
		}

		// rolling records in cursor
		cursor.moveToPosition(-1);
		while (cursor.moveToNext()) {
			for (int i = 0; i < to.length; i++) {
				mapData.get(to[i])[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(from[i]));
			}
			if (hasIDColumn) {
				this.cursorDataID[cursor.getPosition()] = cursor.getLong(cursor.getColumnIndex(CursorColumn.ID.get()));
			}
		}
		InitializeCheckboxData(cursor.getCount());

		cursor.moveToFirst();
		return mapData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCount() {
		return (listData == null ? 0 : listData.entrySet().iterator().next().getValue().length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getItem(int position) {
		if (listData == null || listData.size() <= 0 || position < 0) {
			return null;
		}
		final int itemCount = listData.values().size();
		final Iterator<Object[]> itItem = listData.values().iterator();
		Object[] thisItem = new Object[itemCount];

		int i = 0;
		while (itItem.hasNext()) {
			thisItem[i++] = itItem.next()[position];
		}

		return itemCount > 1 ? thisItem : thisItem[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getItemId(int position) {
		if (cursorDataID != null && cursorDataID.length > 0) {
			return cursorDataID[position];
		}
		return position;
	}

	// activity??麻瞏捍???桀??textcolor
	public void setSpoiNameColor(int spoiName) {
		this.spoiNameColor = context.getResources().getColorStateList(R.drawable.selection_text_yellow_black);
	}

	private String[] getDistanceAndTime(long id) {
		String[] result = new String[] { "", "" };

		Track track = null;
		try {
			track = new Track(context, (int) id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result[0] = String.valueOf(track.CalculateDistance());
		result[1] = track.getStartTime().toLocaleString();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(layoutID, null);
		}

		Log.i("ListView", "pos:" + position + ", view:" + String.valueOf(convertView != null));

		// Text Data
		if (listData != null && !listData.isEmpty()) {
			Iterator<Entry<Integer, Object[]>> dataEntry = listData.entrySet().iterator();
			int count = 0;
			while (dataEntry.hasNext()) {
				Entry<Integer, Object[]> entry = dataEntry.next();
				TextView tvTextData = (TextView) convertView.findViewById(entry.getKey());
				tvTextData.setText(entry.getValue()[position].toString());
				// spoi???秋撒?脰摰xtcolor

				if (isTrack.equals("isTrack")) {
					if (entry.getKey() == R.id.cursor_row_foot) {
						String ss[] = getDistanceAndTime(getItemId(position));
						double distance = Double.valueOf(ss[0]);
						distance = distance / 1000;
						DecimalFormat df = new DecimalFormat("#.#");
						tvTextData.setText(ss[1] + "   " + df.format(distance) + "?蟡?");
					}
				} else {
					if (spoiNameColor != null && count == 0) {
						tvTextData.setTextColor(spoiNameColor);
						count += 1;// ????????xtviw?????xtview
					}
				}

				boolean dataVisible = (dataVisibility == null || dataVisibility.size() <= 0 ? true : dataVisibility
						.get(entry.getKey()).booleanValue());
				tvTextData.setVisibility(dataVisible ? TextView.VISIBLE : TextView.GONE);
			}
		}

		// Icon Data
		if (iconData != null && !iconData.isEmpty()) {
			Iterator<Entry<Integer, Integer[]>> iconEntry = iconData.entrySet().iterator();
			while (iconEntry.hasNext()) {
				Entry<Integer, Integer[]> entry = iconEntry.next();
				ImageView ivIconData = (ImageView) convertView.findViewById(entry.getKey());
				ivIconData.setImageDrawable(context.getResources().getDrawable(entry.getValue()[position].intValue()));

				boolean dataVisible = (dataVisibility == null || dataVisibility.size() <= 0 ? true : dataVisibility
						.get(entry.getKey()).booleanValue());
				ivIconData.setVisibility(dataVisible ? TextView.VISIBLE : TextView.GONE);
			}
		}

		// CheckBox Data
		CheckBox ckbSelect = (CheckBox) convertView.findViewById(checkboxID);
		if (ckbSelect != null) {
			ckbSelect.setVisibility((whichMode == ListMode.MULTIPLE) ? CheckBox.VISIBLE : CheckBox.GONE);
			ckbSelect.setChecked(ckbData.get(position));
			ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow_imageView);
			if (arrow != null)
				arrow.setVisibility(View.GONE);
		}

		// Call from track
		if (isTrackList) {
			((ImageView) convertView.findViewById(R.id.list_cell_imageview)).setImageResource(R.drawable.green_bike);

			((ImageView) convertView.findViewById(R.id.imageView1)).setVisibility(View.GONE);

		}

		return convertView;
	}

	public void setIsTrackList(boolean is) {
		this.isTrackList = is;
	}

}
