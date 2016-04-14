package com.kingwaytek.cpami.bykingTablet.view;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.actionsheet.ActionSheet;
import com.example.actionsheet.ActionSheet.ActionSheetButtonClickListener;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.AlertDialogUtil;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.app.poi.POIMapContent;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackMapContent;
import com.kingwaytek.cpami.bykingTablet.sql.Favorite;
import com.kingwaytek.cpami.bykingTablet.sql.History;
import com.kingwaytek.cpami.bykingTablet.sql.POI;
import com.kingwaytek.cpami.bykingTablet.sql.Track;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.ContentType;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.FavoriteColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.HistoryColumn;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.CursorListMenu;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.CursorListMode;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.CursorListType;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ListMode;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.POIMenu;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.TrackMenu;

/**
 * User Data : Favorites List or History List
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 */
public class UserDataListView extends ListActivity implements OnClickListener {

	private Intent itenCaller;

	private CursorListType listType;
	private ContentType listContent;
	private CursorListMode listMode;
	private ListViewAdapter listAdapter;
	private String searchString;

	private Button btnSearch;
	private Button btnDelete;
	private Button btnCancel;
	private Button top_left_button;
	private Button top_right_button;
	private TextView titlebar_text;

	private EditText edtSearch;
	private RelativeLayout lytSearch;

	private Object listTypeClass;
	private Button gohome;

	private ActionSheet actionSheet;
	private ImageView actionsheet_btn;
	private int[][] sub_view;
	public UtilDialog uitrename;

	/**
	 * Called when this Activity started inheritance of Activity
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Log.i("UserDataListView.java", "onCreate");
		itenCaller = getIntent();
		setContentView(R.layout.history_list);

		top_left_button = (Button) findViewById(R.id.history_top_left);
		top_right_button = (Button) findViewById(R.id.history_top_right);
		top_left_button.setOnClickListener(this);
		top_right_button.setOnClickListener(this);

		listType = (CursorListType) itenCaller.getSerializableExtra("whichType");
		listContent = (ContentType) itenCaller.getSerializableExtra("whichContent");
		listMode = CursorListMode.NORMAL;
		listAdapter = null;
		searchString = "";

		titlebar_text = (TextView) findViewById(R.id.titlebar_text);
		if (listType.getValue() == CursorListType.FAVORITE.getValue()) {
			titlebar_text.setText(R.string.byking_function_favorite_title);
		} else if (listType.getValue() == CursorListType.HISTORY.getValue()) {
			titlebar_text.setText(R.string.byking_function_history_title);
		}

		if (listType == null || listContent == null) {
			throw new ActivityNotFoundException("List source is not valid.");
		}

		btnSearch = (Button) findViewById(R.id.history_search_btn);
		btnSearch.setOnClickListener(this);
		edtSearch = (EditText) findViewById(R.id.history_search_edit);
		lytSearch = (RelativeLayout) findViewById(R.id.list_search_box_layout);

		CreateItemInstance(-1);

		setActionSheet();
		actionsheet_btn = (ImageView) findViewById(R.id.actionsheet_btn);
		actionsheet_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				actionSheet.show();
				actionsheet_btn.setClickable(false);
			}
		});
	}

	public void setActionSheet() {
		sub_view = new int[3][2];
		sub_view[0][0] = R.id.actionsheet_his_favor_list01;
		sub_view[1][0] = R.id.actionsheet_his_favor_list02;
		sub_view[2][0] = R.id.actionsheet_his_favor_list03;

		actionSheet = (ActionSheet) findViewById(R.id.actionSheet_his_fav_list);
		actionSheet.setContext(UserDataListView.this);
		actionSheet.setActionSheetLayout(R.layout.action_sheet_his_favor_list, sub_view);
		actionSheet.setOnActionSheetButtonClickListener(new ActionSheetButtonClickListener() {

			@Override
			public void onButtonClick(ActionSheet actionsheet, int index, int id) {
				int flag = 0;
				switch (index) {
				case 0:// ?叟垓???畸?
					menuMultiDelete_Select();
					break;
				case 1:// ??賂??畸?
					menuDeleteAll_Select();
					break;
				default:// ?謘?
					break;
				}
				actionsheet_btn.setClickable(true);
			}
		});
	}

	/**
	 * Called after onCreate inheritance of Activity
	 */
	@Override
	public void onResume() {
		super.onResume();

		Log.i("CursorListView", "listtype : " + listType + ", listContent : " + listContent);

		// btnDelete.setOnClickListener(this);
		// btnCancel.setOnClickListener(this);
		registerForContextMenu(getListView());// ?ontextMenu?隍istview?秋撩???
		ShowLists();

	}

	// //???????綽???剁?殉??dialog?遴鬥???///
	private AdapterContextMenuInfo lastMenuInfo = null;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);

		lastMenuInfo = (AdapterContextMenuInfo) menuInfo;
		// long id=lastMenuInfo.id;
		int position = lastMenuInfo.position;// item??穎sition
		Object[] item = (Object[]) listAdapter.getItem(position);// ?謘潘item

		if (listType == CursorListType.HISTORY) {

		} else {
			final Favorite fav = new Favorite(this, Integer.parseInt(item[1].toString()));// ?謘潘sql??琦vroite

			uitrename = new UtilDialog(UserDataListView.this) {
				@Override
				public void click_btn_1() {
					String rename = uitrename.getRename();
					if (rename.length() > 0) {// ?潘撓貔?蝞?
						fav.setName(rename);// ?竣??
						fav.Update();// commit????
					}
					ShowLists();// reload???
					super.click_btn_1();
				}
			};
			uitrename.editDataName("??謆???", item[0].toString(), "????", "?謘?");
		}
		// LayoutInflater factory = LayoutInflater.from(this);//
		// ?□?layoutInflater1
		// final View rename_dialog =
		// factory.inflate(R.layout.cursor_list_view_rename_dialog, null);//
		// ?梁??layoutInflater??ew
		// ((TextView)
		// rename_dialog.findViewById(R.id.name_view)).setText(item[0].toString());//
		// ?□?view?????
		//
		// AlertDialog dlg = new
		// AlertDialog.Builder(this).setTitle("??謆???").setView(rename_dialog)
		// .setPositiveButton("????", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// String rename = ((EditText)
		// rename_dialog.findViewById(R.id.name_edit)).getText().toString()
		// .trim();
		// if (rename.length() > 0) {// ?潘撓貔?蝞?
		// fav.setName(rename);// ?竣??
		// fav.Update();// commit????
		// }
		// ShowLists();// reload???
		// }
		// }).create();
		// dlg.show();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		menu.setGroupEnabled(0, listMode == CursorListMode.NORMAL);
		menu.setGroupVisible(0, listMode == CursorListMode.NORMAL);

		return true;
	}

	/**
	 * onMenuItemSelected Handler inheritance of Activity
	 */
	// @Override
	// public boolean onMenuItemSelected(int featureId, MenuItem item) {
	// Log.i("CursorListView", "Menu item clicked : " + item.getItemId() +
	// ", feature id : " + featureId);
	//
	// switch (CursorListMenu.get(item.getItemId())) {
	// case MULTI_DELETE:
	// Log.i("CursorListView", "Multi Delete Clicked.");
	// menuMultiDelete_Select(item);
	// return true;
	// case ALL_DELETE: // cause finish
	// Log.i("CursorListView", "ALL Delete Clicked.");
	// menuDeleteAll_Select(item);
	// return true;
	// // case MENU_FILTER_SEARCH: // pop up search dialog
	// // Log.i("FavoriteList", "Filter search Clicked.");
	// // return true;
	// }
	//
	// return super.onMenuItemSelected(featureId, item);
	// }

	/**
	 * onListItemClick Handler inheritance of ListActivity
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("CursorListView", "position = " + position + ", _id = " + id);

		switch (listMode) {
		case NORMAL:
			listItem_Click(id);
			break;
		case DELETE:
			CheckBox ckbSelect = (CheckBox) v.findViewById(R.id.cursor_row_checkbox);
			ckbSelect.toggle();
			listAdapter.getCheckBoxData().put(position, ckbSelect.isChecked());
			break;
		default:
			break;
		}

		super.onListItemClick(l, v, position, id);
	}

	/**
	 * Handles onClick Event in this Activity implementation of OnClickListener
	 */
	@Override
	public void onClick(View v) {
		Log.i("CursorListView", "Activity triggered an onClick Event. sender is :" + v.getClass().getName());

		switch (v.getId()) {
		case R.id.history_top_right:
			btnDelete_Click(v);
			break;
		case R.id.history_top_left:
			btnCancel_Click(v);
			break;
		case R.id.history_search_btn:
			btnSearch_Click(v);
			break;
		default:
			break;
		}
	}

	private long CreateItemInstance(int arg) {
		long result = -1;
		listTypeClass = null;

		try {
			switch (listType) {
			case FAVORITE:
				listTypeClass = Class.forName(Favorite.class.getName()).getConstructor(Context.class, Integer.class)
						.newInstance(this, arg);
				result = ((Favorite) listTypeClass).getItemID();
				break;
			case HISTORY:
				listTypeClass = Class.forName(History.class.getName()).getConstructor(Context.class, Integer.class)
						.newInstance(this, arg);
				result = ((History) listTypeClass).getItemID();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.w("CursorListView", "listTypeClass instanciate error.");
			listTypeClass = null;
			e.printStackTrace();
		}
		Log.i("CursorListView", "itemclass valid : " + String.valueOf(listTypeClass != null) + ", result : " + result);
		return result;
	}

	/**
	 * Handles Menu Multi Delete Selected
	 * 
	 * @param sender
	 */
	// private void menuMultiDelete_Select(Object sender) {
	private void menuMultiDelete_Select() {
		// if (sender == null ||
		// !sender.getClass().getName().equals("com.android.internal.view.menu.MenuItemImpl"))
		// {
		// throw new IllegalArgumentException("sender is not valid.");
		// }
		// Log.i("CursorListView", "Menu MultiDelete Selected. sender is : " +
		// sender.getClass().getName());

		listMode = CursorListMode.DELETE;
		top_right_button.setVisibility(View.VISIBLE);
		top_left_button.setVisibility(View.VISIBLE);
		lytSearch.setVisibility(View.GONE);

		ShowLists();
	}

	/**
	 * Handles Menu Delete All Selected
	 * 
	 * @param sender
	 */
	// private void menuDeleteAll_Select(Object sender) {
	private void menuDeleteAll_Select() {

		if (listAdapter.getCount() == 0) {

			UtilDialog uit = new UtilDialog(UserDataListView.this);
			uit.showDialog_route_plan_choice(getString(R.string.data_empty_msg), null,
					getString(R.string.dialog_close_button_text), null);
			return;
		}

		UtilDialog uit = new UtilDialog(UserDataListView.this) {
			@Override
			public void click_btn_1() {
				dlgConfirm_Click(dialog, CursorListMenu.ALL_DELETE);
				super.click_btn_1();
			}

		};
		uit.showDialog_route_plan_choice(CursorListMenu.ALL_DELETE.getTitle(),
				getString(R.string.data_delete_all_confirm_msg), "????", "?謘?");
	}

	private void listItem_Click(long args) {
		Log.i("CursorListView", "ContentID :" + args);

		// Update item instance and get id of clicked item
		long itemId = CreateItemInstance((int) args);
		Intent itenContent;

		switch (listContent) {
		case POI:
			// itenContent = new Intent(this, POIContent.class);
			try {
				if (POI.isItemInList(this, (int) itemId) == false) {

					UtilDialog uit = new UtilDialog(UserDataListView.this);
					uit.showDialog_route_plan_choice("???", "??謒豯佇冪???瞏??ｇ????!", "????", null);

				} else {
					itenContent = new Intent(this, POIMapContent.class);
					itenContent.putExtra("POI_Caller", ActivityCaller.get(listType.getValue()));
					itenContent.putExtra("POI_ID", itemId);
					itenContent.putExtra("item_ID", args);
					itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
					startActivityForResult(itenContent, ContentType.POI.getValue());
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case TRACK:
			// itenContent = new Intent(this, TrackContent.class);
			try {
				if (Track.isItemInList(this, (int) itemId) == false) {

					UtilDialog uit = new UtilDialog(UserDataListView.this);
					uit.showDialog_route_plan_choice("???", "??謒豯佇冪???瞏??ｇ????!", "????", null);
				} else {
					String[] result = getDistanceAndTime(itemId);
					itenContent = new Intent(this, TrackMapContent.class);
					itenContent.putExtra("Track_Caller", ActivityCaller.get(listType.getValue()));
					itenContent.putExtra("Track_ID", itemId);
					itenContent.putExtra("item_ID", args);
					itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
					itenContent.putExtra("Track_Distance", result[0]);
					itenContent.putExtra("Track_Time", result[1]);
					startActivityForResult(itenContent, ContentType.TRACK.getValue());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case ADDRESS:
			itenContent = null;
			break;
		default:
			break;
		}
	}

	/**
	 * Handles Keyword Search Button Click
	 * 
	 * @param sender
	 */
	protected void btnSearch_Click(Object sender) {
		if (sender == null || !sender.getClass().getName().equals("android.widget.Button")) {
			throw new IllegalArgumentException("sender is not valid.");
		}
		Log.i("CursorListView", "Search Button Clicked. sender is : " + sender.getClass().getName());

		searchString = edtSearch.getText().toString();
		ShowLists();
	}

	/**
	 * Handles Delete Button Click
	 * 
	 * @param sender
	 */
	protected void btnDelete_Click(Object sender) {
		if (sender == null || !sender.getClass().getName().equals("android.widget.Button")) {
			throw new IllegalArgumentException("sender is not valid.");
		}
		Log.i("CursorListView", "Delete Button Clicked. sender is : " + sender.getClass().getName());

		boolean hasSelection = false;

		for (int i = 0; i < listAdapter.getCheckBoxData().size(); i++) {
			if (!listAdapter.getCheckBoxData().get(i)) {
				continue;
			}
			hasSelection = true;
			break;
		}

		if (!hasSelection) {

			UtilDialog uit = new UtilDialog(UserDataListView.this);
			uit.showDialog_route_plan_choice(getString(R.string.data_no_selection_msg), null,
					getString(R.string.dialog_close_button_text), null);
			return;
		}

		AlertDialogUtil.showConfirmDialog(this, CursorListMenu.MULTI_DELETE.getTitle(),
				getString(R.string.data_delete_selection_confirm_msg), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Deletion Confirmed
						if (which == DialogInterface.BUTTON_POSITIVE) {
							dlgConfirm_Click(dialog, CursorListMenu.MULTI_DELETE);
						}
					}
				});
	}

	/**
	 * Handles Cancel Button Click
	 * 
	 * @param sender
	 */
	protected void btnCancel_Click(Object sender) {
		if (sender == null || !sender.getClass().getName().equals("android.widget.Button")) {
			throw new IllegalArgumentException("sender is not valid.");
		}
		Log.i("CursorListView", "Cancel Button Clicked. sender is : " + sender.getClass().getName());

		listMode = CursorListMode.NORMAL;
		top_left_button.setVisibility(View.INVISIBLE);
		top_right_button.setVisibility(View.INVISIBLE);
		lytSearch.setVisibility(View.VISIBLE);

		ShowLists();
	}

	/**
	 * Handles Confirm Dialog Selection
	 * 
	 * @param sender
	 * @param args
	 */
	private void dlgConfirm_Click(Object sender, CursorListMenu args) {
		if (sender == null || !sender.getClass().getName().equals("android.app.AlertDialog")) {
			throw new IllegalArgumentException("sender is not valid.");
		}
		Log.i("FavoriteList", "Delete Confirm Dialog Clicked. sender is : " + sender.getClass().getName()
				+ ", Argumant : " + args.toString());

		String conditions = "";
		switch (args) {
		case MULTI_DELETE:
			for (int i = 0; i < listAdapter.getCount(); i++) {
				conditions += listAdapter.getCheckBoxData().get(i) ? String.valueOf(listAdapter.getItemId(i)) + ","
						: "";
			}
			Log.i("CursorListView", "Multiple Delete Confirm OK Clicked : " + conditions);

			listMode = CursorListMode.NORMAL;
			top_left_button.setVisibility(View.INVISIBLE);
			top_right_button.setVisibility(View.INVISIBLE);
			lytSearch.setVisibility(View.VISIBLE);
			break;
		case ALL_DELETE:
			for (int i = 0; i < listAdapter.getCount(); i++) {
				conditions += String.valueOf(listAdapter.getItemId(i)) + ",";
			}
			Log.i("CursorListView", "Delete All Confirm OK Clicked : " + conditions);
			break;
		default:
			break;
		}

		if (!conditions.equals("")) {
			switch (listType) {
			case FAVORITE:
				Favorite.Remove(this, conditions.substring(0, conditions.length() - 1));
				break;
			case HISTORY:
				History.Remove(this, conditions.substring(0, conditions.length() - 1));
				break;
			default:
				break;
			}
		}

		ShowLists();
	}

	/**
	 * Favorite Action results inheritance of Activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			switch (ContentType.get(requestCode)) {
			case POI:
				resultPOI_Received(data);
				break;
			case TRACK:
				resultTrack_Received(data);
				break;
			default:
				break;
			}
		} else if (resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}
	}

	private void resultPOI_Received(Intent data) {
		POIMenu action = (POIMenu) data.getSerializableExtra("POI_Action");
		if (action != null) {
			switch (action) {
			case NAVIGATION:
				itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
				setResult(RESULT_OK, itenCaller);
				finish();
				break;
			case DELETE:

				UtilDialog uit = new UtilDialog(UserDataListView.this);
				uit.showDialog_route_plan_choice(
						data.getLongExtra("Remove_Result", -1) <= 0 ? getString(R.string.data_delete_fail_msg)
								: getString(R.string.data_delete_success_msg), null,
						getString(R.string.dialog_ok_button_text), null);

				ShowLists();
				break;
			default:
				break;
			}
		} else {
			setResult(RESULT_OK, itenCaller);
			finish();
		}
		// switch ((POIMenu) data.getSerializableExtra("POI_Action")) {
		// case DELETE:
		// AlertDialogUtil
		// .showMsgWithConfirm(
		// this,
		// data.getLongExtra("Remove_Result", -1) <= 0 ?
		// getString(R.string.data_delete_fail_msg)
		// : getString(R.string.data_delete_success_msg),
		// getString(R.string.dialog_ok_button_text));
		// ShowLists();
		// break;
		// case DRAW_MAP:
		// itenCaller.putExtra("Action", ContextMenuOptions.DRAW_MAP);
		// itenCaller.putExtra("Name", data.getStringExtra("POI_Name"));
		// itenCaller.putExtra("Location", data
		// .getParcelableExtra("POI_Location"));
		// setResult(RESULT_OK, itenCaller);
		// finish();
		// break;
		// case SET_ORIGIN:
		// itenCaller.putExtra("Action", ContextMenuOptions.SET_ORIGIN);
		// itenCaller.putExtra("Name", data.getStringExtra("POI_Name"));
		// itenCaller.putExtra("Location", data
		// .getParcelableExtra("POI_Location"));
		// setResult(RESULT_OK, itenCaller);
		// finish();
		// break;
		// case SET_DESTINATION:
		// itenCaller.putExtra("Action", ContextMenuOptions.SET_DESTINATION);
		// itenCaller.putExtra("Name", data.getStringExtra("POI_Name"));
		// itenCaller.putExtra("Location", data
		// .getParcelableExtra("POI_Location"));
		// setResult(RESULT_OK, itenCaller);
		// finish();
		// break;
		// }
	}

	private void resultTrack_Received(Intent data) {
		TrackMenu action = (TrackMenu) data.getSerializableExtra("Track_Action");
		if (action != null) {
			switch (action) {
			case NAVIGATION:
				itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
				setResult(RESULT_OK, itenCaller);
				finish();
				break;
			case DELETE:

				UtilDialog uit = new UtilDialog(UserDataListView.this);
				uit.showDialog_route_plan_choice(
						data.getLongExtra("Remove_Result", -1) <= 0 ? getString(R.string.data_delete_fail_msg)
								: getString(R.string.data_delete_success_msg), null,
						getString(R.string.dialog_ok_button_text), null);
				ShowLists();
				break;
			default:
				break;
			}
		} else {
			setResult(RESULT_OK, itenCaller);
			finish();
		}
		// switch ((TrackMenu) data.getSerializableExtra("Track_Action")) {
		// case DELETE:
		// AlertDialogUtil
		// .showMsgWithConfirm(
		// this,
		// data.getLongExtra("Remove_Result", -1) <= 0 ?
		// getString(R.string.data_delete_fail_msg)
		// : getString(R.string.data_delete_success_msg),
		// getString(R.string.dialog_ok_button_text));
		// ShowLists();
		// break;
		// case SHOW:
		// itenCaller.putExtra("Action", ContextMenuOptions.DRAW_TRACK);
		// itenCaller.putExtra("Name", data.getStringExtra("Track_Name"));
		// itenCaller
		// .putExtra("Track", data.getIntExtra("Track_Location", -1));
		// setResult(RESULT_OK, itenCaller);
		// finish();
		// break;
		// case SET_LOCATION:
		// itenCaller.putExtra("Action", ContextMenuOptions.SET_LOCATION);
		// itenCaller.putExtra("Name", data.getStringExtra("Track_Name"));
		// itenCaller.putExtra("Location", data
		// .getParcelableExtra("Track_Start_Location"));
		// itenCaller.putExtra("LocationExt", data
		// .getParcelableExtra("Track_End_Location"));
		// setResult(RESULT_OK, itenCaller);
		// finish();
		// break;
		// }
	}

	/**
	 * Show List Content
	 */
	private void ShowLists() {
		final Cursor curListData;
		final String[] from;
		final int[] to;

		switch (listType) {
		case FAVORITE:
			curListData = ((Favorite) listTypeClass).getFavoriteList(listContent, searchString);
			from = new String[] { FavoriteColumn.NAME.getName(), CursorColumn.ID.get() };
			break;
		case HISTORY:
			curListData = ((History) listTypeClass).getHistoryList(listContent, searchString);
			from = new String[] { HistoryColumn.NAME.getName(), CursorColumn.ID.get() };
			break;
		default:
			curListData = null;
			from = null;
			break;
		}
		Log.i("CursorListView", "list item count = " + curListData.getCount());

		to = new int[] { R.id.cursor_row_text, R.id.cursor_row_ref };

		listAdapter = new ListViewAdapter(this, R.layout.cursor_list_row_history, curListData, from, to);
		if (curListData.getCount() > 0) {
			listAdapter.getDataVisibilityStates().put(R.id.cursor_row_ref, false);
		}
		listAdapter.setCheckBoxID(R.id.cursor_row_checkbox);
		listAdapter.setListMode(listMode.equals(CursorListMode.DELETE) ? ListMode.MULTIPLE : ListMode.SINGLE);

		curListData.close();

		setListAdapter(listAdapter);
	}

	/**
	 * Calculate Distance
	 */
	private String[] getDistanceAndTime(long id) {
		String[] result = new String[] { "", "" };

		Track track = null;
		try {
			track = new Track(this, (int) id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result[0] = String.valueOf(track.CalculateDistance());
		result[1] = track.getStartTime().toLocaleString();
		return result;
	}

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // ??秋??怨????抆???ack??
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// setResult(RESULT_FIRST_USER);
	// this.finish();
	// return true;
	// }else {
	// return super.onKeyDown(keyCode, event);
	// }
	// }

}
