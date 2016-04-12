package com.kingwaytek.cpami.bykingTablet.app.track;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.Toast;

import com.example.actionsheet.ActionSheet;
import com.example.actionsheet.ActionSheet.ActionSheetButtonClickListener;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.AlertDialogUtil;
import com.kingwaytek.cpami.bykingTablet.app.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackContent.TrackExportExt;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackEngine.TrackRecordingStatus;
import com.kingwaytek.cpami.bykingTablet.sql.Favorite;
import com.kingwaytek.cpami.bykingTablet.sql.History;
import com.kingwaytek.cpami.bykingTablet.sql.Track;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.ContentType;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TrackColumn;
import com.kingwaytek.cpami.bykingTablet.view.ListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.view.UserDataListView;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.CursorListMenu;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.CursorListMode;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ListMode;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.TrackMenu;

public class TrackListView extends ListActivity implements OnClickListener {

	private Intent itenCaller;

	private ActivityCaller listContent;
	private CursorListMode listMode;
	private ListViewAdapter trackListAdapter;
	private String searchString;

	private Button btnSearch;
	private Button btnConfirm;
	private Button btnCancel;
	private EditText edtSearch;
	private Button top_left_button;
	private Button top_right_button;

	private RelativeLayout lytSearch;

	private ProgressDialog progress;
	private final int REQUEST_CODE_TRACKDOWNLOAD = 1;

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

		itenCaller = getIntent();
		setContentView(R.layout.history_list);

		listContent = (ActivityCaller) itenCaller.getSerializableExtra("TrackList_Caller");
		listMode = CursorListMode.NORMAL;
		trackListAdapter = null;
		searchString = "";

		if (listContent == null) {
			throw new ActivityNotFoundException("List source is not valid.");
		}

		// LayoutInflater factorytemp = LayoutInflater.from(this);
		// View footer_del_cancel = factorytemp.inflate(
		// R.layout.cursor_list_view_del_cancel_button, null);
		// getListView().addFooterView(footer_del_cancel);

		btnSearch = (Button) findViewById(R.id.history_search_btn);
		btnSearch.setOnClickListener(this);
		Log.i("TrackListView.java", "btnConfirm=" + String.valueOf(btnConfirm != null));
		// btnConfirm = (Button)
		// findViewById(R.id.cursor_list_view_delete_button);
		// btnCancel = (Button)
		// findViewById(R.id.cursor_list_view_cancel_button);
		edtSearch = (EditText) findViewById(R.id.history_search_edit);
		lytSearch = (RelativeLayout) findViewById(R.id.list_search_box_layout);
		top_left_button = (Button) findViewById(R.id.history_top_left);
		top_right_button = (Button) findViewById(R.id.history_top_right);
		top_left_button.setOnClickListener(this);
		top_right_button.setOnClickListener(this);

		Track.EraseBrokenTracks(this);

		TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
		titleBar.setText(R.string.byking_function_track_title);

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

	/**
	 * Called after onCreate inheritance of Activity
	 */
	@Override
	public void onResume() {
		super.onResume();

		Log.i("TrackListView", "myContent : " + listContent);
		Log.i("TrackListView.java", "btnConfirm in onResume=" + String.valueOf(btnConfirm != null));

		// btnConfirm.setOnClickListener(this);
		// btnCancel.setOnClickListener(this);
		edtSearch.setText(searchString);
		registerForContextMenu(getListView());// ?ontextMenu?隍istview?秋撩???
		ShowLists();
		if (trackListAdapter.getCount() <= 0) {
			lytSearch.setVisibility(RelativeLayout.GONE);
		}

		if (!TrackEngine.getInstance().getRecordingStatus().equals(TrackRecordingStatus.STOPED)) {
			Toast.makeText(this, R.string.track_record_in_progress_text, Toast.LENGTH_LONG).show();
		}
	}

	public void setActionSheet() {
		sub_view = new int[6][2];
		sub_view[0][0] = R.id.actionsheet_trackList_01;
		sub_view[1][0] = R.id.actionsheet_trackList_02;
		sub_view[2][0] = R.id.actionsheet_trackList_03;
		sub_view[3][0] = R.id.actionsheet_trackList_04;
		sub_view[4][0] = R.id.actionsheet_trackList_05;
		sub_view[5][0] = R.id.actionsheet_trackList_06;

		actionSheet = (ActionSheet) findViewById(R.id.actionSheet_his_fav_list);
		actionSheet.setContext(TrackListView.this);
		actionSheet.setActionSheetLayout(R.layout.action_sheet_track_list, sub_view);
		actionSheet.setOnActionSheetButtonClickListener(new ActionSheetButtonClickListener() {

			@Override
			public void onButtonClick(ActionSheet actionsheet, int index, int id) {
				int flag = 0;
				switch (index) {
				case 0:// ????賣?
					Intent itenRecord = new Intent(TrackListView.this, TrackRecord.class);
					startActivity(itenRecord);
					break;
				case 1:// ??????
					menuImport_Select();
					break;
				case 2:// ?叟垓???穿
					menuMultiExport_Select();
					break;
				case 3:// ?叟垓???畸?
					menuMultiDelete_Select();
					break;
				case 4:// ??賂??畸?
					menuDeleteAll_Select();
					break;
				default:// ?謘?
					break;
				}
				actionsheet_btn.setClickable(true);
			}
		});
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
		Object[] item = (Object[]) trackListAdapter.getItem(position);// ?謘潘item
		final Track trk;
		try {
			trk = new Track(this, Integer.parseInt((item[1].toString())));
			uitrename = new UtilDialog(TrackListView.this) {
				@Override
				public void click_btn_1() {
					String rename = uitrename.getRename();
					if (rename.length() > 0) {// ?潘撓貔?蝞?
						trk.setName(rename);// ?竣??
						trk.Update();// commit????
					}
					ShowLists();// reload???
					super.click_btn_1();
				}
			};
			uitrename.editDataName("??謆???", item[0].toString(), "????", "?謘?");
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
			// rename_dialog.findViewById(R.id.name_edit)).getText()
			// .toString().trim();
			// if (rename.length() > 0) {// ?潘撓貔?蝞?
			// trk.setName(rename);// ?竣??
			// trk.Update();// commit????
			// }
			// ShowLists();// reload???
			// }
			// }).create();
			//
			// dlg.show();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * onListItemClick Handler inheritance of ListActivity
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("TrackListView", "position = " + position + ", _id = " + id);

		switch (listMode) {
		case NORMAL:
			listItem_Click(id);
			PutHistory(((Object[]) trackListAdapter.getItem(position))[0].toString(), id);
			break;
		case DELETE:
			CheckBox ckbSelect = (CheckBox) v.findViewById(R.id.cursor_row_checkbox);
			ckbSelect.toggle();
			trackListAdapter.getCheckBoxData().put(position, ckbSelect.isChecked());
			break;
		default:
			break;
		}

		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onClick(View v) {
		Log.i("TrackListView", "Activity triggered an onClick Event. sender is :" + v.getClass().getName());

		switch (v.getId()) {
		case R.id.history_top_right:
			btnConfirm_Click(v);
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

	// private void menuImport_Select(Object sender) {
	private void menuImport_Select() {
		// if (sender == null ||
		// !sender.getClass().getName().equals("com.android.internal.view.menu.MenuItemImpl"))
		// {
		// throw new IllegalArgumentException("sender is not valid.");
		// }
		// Log.i("TrackListView", "Menu Import Selected. sender is : " +
		// sender.getClass().getName());

		try {
			// TODO : accept web service data
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		Intent downloadIntent = new Intent(this, TrackTypeSelectionActivity.class);
		startActivityForResult(downloadIntent, REQUEST_CODE_TRACKDOWNLOAD);
	}

	// private void menuMultiExport_Select(Object sender) {
	private void menuMultiExport_Select() {
		// if (sender == null ||
		// !sender.getClass().getName().equals("com.android.internal.view.menu.MenuItemImpl"))
		// {
		// throw new IllegalArgumentException("sender is not valid.");
		// }
		// Log.i("TrackListView", "Menu MultiExport Selected. sender is : " +
		// sender.getClass().getName());

		listMode = CursorListMode.DELETE;
		// btnConfirm.setVisibility(View.VISIBLE);
		top_right_button.setText(R.string.dialog_export_button_text);
		top_right_button.setVisibility(View.VISIBLE);
		top_left_button.setVisibility(View.VISIBLE);
		// btnCancel.setVisibility(View.VISIBLE);
		lytSearch.setVisibility(View.GONE);

		ShowLists();
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
		// Log.i("TrackListView", "Menu MultiDelete Selected. sender is : " +
		// sender.getClass().getName());

		listMode = CursorListMode.DELETE;
		// btnConfirm.setVisibility(View.VISIBLE);
		top_right_button.setText(R.string.dialog_delete_button_text);
		// btnCancel.setVisibility(View.VISIBLE);
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
		// if (sender == null ||
		// !sender.getClass().getName().equals("com.android.internal.view.menu.MenuItemImpl"))
		// {
		// throw new IllegalArgumentException("sender is not valid.");
		// }
		// Log.i("TrackListView", "Menu Delete All Selected. sender is : " +
		// sender.getClass().getName());

		if (trackListAdapter.getCount() == 0) {

			UtilDialog uit = new UtilDialog(TrackListView.this);
			uit.showDialog_route_plan_choice(getString(R.string.data_no_selection_msg), null,
					getString(R.string.dialog_close_button_text), null);

			// AlertDialogUtil.showMsgWithConfirm(this,
			// getString(R.string.data_no_selection_msg),
			// getString(R.string.dialog_close_button_text));
			return;
		}

		UtilDialog uit = new UtilDialog(TrackListView.this) {
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
		Log.i("TrackListView", "ContentID :" + args);

		// TODO
		String[] result = getDistanceAndTime(args);
		
		 Log.i("TrackView.java","CalculateDistance()="+String.valueOf(getDistanceAndTime(args)[0]));
		 Log.i("TrackView.java","CalculateTime()="+String.valueOf(getDistanceAndTime(args)[1]));
		Intent itenContent;
		itenContent = new Intent(this, TrackMapContent.class);
		itenContent.putExtra("Track_Caller", listContent);
		itenContent.putExtra("Track_ID", args);
		itenContent.putExtra("Track_Distance", result[0]);
		itenContent.putExtra("Track_Time", result[1]);

		itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
		startActivityForResult(itenContent, ActivityCaller.TRACK.getValue());
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
		Log.i("TrackListView", "Search Button Clicked. sender is : " + sender.getClass().getName());

		searchString = edtSearch.getText().toString();
		ShowLists();
	}

	/**
	 * Handles Confirm Button Click
	 * 
	 * @param sender
	 */
	protected void btnConfirm_Click(Object sender) {
		if (sender == null || !sender.getClass().getName().equals("android.widget.Button")) {
			throw new IllegalArgumentException("sender is not valid.");
		}
		Log.i("TrackListView", "Confirm Button Clicked. sender is : " + sender.getClass().getName());

		boolean hasSelection = false;

		for (int i = 0; i < trackListAdapter.getCheckBoxData().size(); i++) {
			if (!trackListAdapter.getCheckBoxData().get(i)) {
				continue;
			}
			hasSelection = true;
			break;
		}

		if (!hasSelection) {
			UtilDialog uit = new UtilDialog(TrackListView.this);
			uit.showDialog_route_plan_choice(getString(R.string.data_no_selection_msg), null,
					getString(R.string.dialog_close_button_text), null);
			return;
		}

		if (top_right_button.getText().equals(getString(R.string.dialog_delete_button_text))) {

			UtilDialog uit = new UtilDialog(TrackListView.this) {
				@Override
				public void click_btn_1() {
					dlgConfirm_Click(dialog, CursorListMenu.MULTI_DELETE);
					super.click_btn_1();
				}
			};
			uit.showDialog_route_plan_choice(CursorListMenu.MULTI_DELETE.getTitle(),
					getString(R.string.data_delete_selection_confirm_msg), "????", "?謘?");

			return;
		}
		if (top_right_button.getText().equals(getString(R.string.dialog_export_button_text))) {
			// fake for validation
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			dlgConfirm_Click(ab.create(), CursorListMenu.MULTI_EXPORT);
		}
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
		Log.i("TrackListView", "Cancel Button Clicked. sender is : " + sender.getClass().getName());

		listMode = CursorListMode.NORMAL;
		// btnConfirm.setVisibility(View.GONE);
		top_right_button.setText(R.string.dialog_delete_button_text);
		// btnCancel.setVisibility(View.GONE);
		top_right_button.setVisibility(View.INVISIBLE);
		top_left_button.setVisibility(View.INVISIBLE);
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
		// if (sender == null ||
		// !sender.getClass().getName().equals("android.app.AlertDialog")) {
		// throw new IllegalArgumentException("sender is not valid.");
		// }
		Log.i("TrackListView", "Delete Confirm Dialog Clicked. sender is : " + sender.getClass().getName()
				+ ", Argumant : " + args.toString());

		String conditions = "";
		String[] trackIDs = null;
		switch (args) {
		case MULTI_DELETE:
			for (int i = 0; i < trackListAdapter.getCount(); i++) {
				conditions += trackListAdapter.getCheckBoxData().get(i) ? String.valueOf(trackListAdapter.getItemId(i))
						+ "," : "";
			}
			Log.i("TrackListView", "Multiple Delete Confirm OK Clicked : " + conditions);

			listMode = CursorListMode.NORMAL;
			// btnConfirm.setVisibility(View.GONE);
			// btnCancel.setVisibility(View.GONE);
			top_right_button.setVisibility(View.GONE);
			top_left_button.setVisibility(View.GONE);
			lytSearch.setVisibility(View.VISIBLE);
			break;
		case ALL_DELETE:
			for (int i = 0; i < trackListAdapter.getCount(); i++) {
				conditions += String.valueOf(trackListAdapter.getItemId(i)) + ",";
			}
			Log.i("TrackListView", "Delete All Confirm OK Clicked : " + conditions);
			break;
		case MULTI_EXPORT:
			for (int i = 0; i < trackListAdapter.getCount(); i++) {
				conditions += trackListAdapter.getCheckBoxData().get(i) ? String.valueOf(trackListAdapter.getItemId(i))
						+ "," : "";
			}
			Log.i("TrackListView", "Multi Export Confirm OK Clicked : " + conditions);
			trackIDs = conditions.substring(0, conditions.length() - 1).split(",");
			break;
		default:
			break;
		}

		if (!conditions.equals("")) {
			switch (args) {
			case MULTI_DELETE:
			case ALL_DELETE:
				// ??rack_id?殉????喉History??〔moveTracks()??輯???鈭槐story??餈系rack_id?鞈?????
				History.RemoveTracks(this, conditions.substring(0, conditions.length() - 1));
				// ??rack_id?殉????喉Favorite??〔moveTracks()??輯???鈭vorite??餈系rack_id?鞈?????
				Favorite.RemoveTracks(this, conditions.substring(0, conditions.length() - 1));
				Track.Erase(this, conditions.substring(0, conditions.length() - 1));
				break;
			case MULTI_EXPORT:
				ShowExportSelection(trackIDs);
				listMode = CursorListMode.NORMAL;
				// btnConfirm.setVisibility(View.GONE);
				// btnCancel.setVisibility(View.GONE);
				top_right_button.setVisibility(View.GONE);
				top_left_button.setVisibility(View.GONE);
				lytSearch.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}

		ShowLists();
	}

	private void ShowExportSelection(final String[] trackIDs) {
		final String[] options = new String[] { TrackExportExt.CSV.toString(), TrackExportExt.GPX.toString(),
				TrackExportExt.KML.toString() };
		DialogInterface.OnClickListener dlgListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ExportTracks(TrackExportExt.get(which), trackIDs);
			}
		};

		AlertDialogUtil.showContextSelection(this, getString(R.string.track_export_format_tilte), options, dlgListener);
	}

	private void makeProgressDialog(final int trackCount) {
		String exportMsg = CursorListMenu.MULTI_EXPORT.getTitle();
		progress = new ProgressDialog(this);
		progress.setIcon(android.R.drawable.ic_dialog_info);
		progress.setTitle(exportMsg);
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setMax(trackCount);
		progress.setProgress(0);
		progress.show();
	}

	private void ExportTracks(TrackExportExt whichExt, final String[] trackIDs) {
		Log.i("TrackContent", "selected ext:" + whichExt.getExtension());
		if (trackIDs == null || trackIDs.length <= 0) {
			return;
		}
		Log.i("TrackContent", "selected id count:" + trackIDs.length);

		try {
			runOnUiThread(new Runnable() {
				public void run() {
					makeProgressDialog(trackIDs.length);
				}
			});

			for (int i = 0; i < trackIDs.length; i++) {
				final int status = i;
				runOnUiThread(new Runnable() {
					public void run() {
						synchronized (this) {
							if (progress == null) {
								return;
							}
							progress.setProgress(status);
						}
					}
				});

				int id = Integer.valueOf(trackIDs[i]);
				Track track = new Track(this, id);
				boolean sucessed = true;
				int error = 0;
				Log.i("TrackContent_MultiExport", "current id:" + track.getID());
				switch (whichExt) {
				case CSV:
					final CsvWriter wrCsv = new CsvWriter(this, track);
					wrCsv.writeTrack();
					sucessed = wrCsv.wasSuccess();
					error = wrCsv.getErrorMsg();
					break;
				case GPX:
					final GpxWriter wrGpx = new GpxWriter(this, track);
					wrGpx.writeTrack();
					sucessed = wrGpx.wasSuccess();
					error = wrGpx.getErrorMsg();
					break;
				case KML:
					final KmlWriter wrKml = new KmlWriter(this, track);
					wrKml.writeTrack();
					sucessed = wrKml.wasSuccess();
					error = wrKml.getErrorMsg();
					break;
				default:
					break;
				}

				if (!sucessed) {
					Toast.makeText(this, error, Toast.LENGTH_LONG).show();
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (progress != null) {
				synchronized (this) {
					progress.dismiss();
					progress = null;
				}
			}
		}
	}

	private void PutHistory(String name, long id) {
		History history = new History(this);
		history.setName(name);
		history.setItemID((int) id);
		history.setType(ContentType.TRACK.getValue());
		history.Put();
	}

	/**
	 * Favorite Action results inheritance of Activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK && requestCode == ActivityCaller.TRACK.getValue()) {
			TrackMenu action = (TrackMenu) data.getSerializableExtra("Track_Action");
			if (action != null) {
				switch (action) {
				case NAVIGATION:
					itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
					setResult(RESULT_OK, itenCaller);
					finish();
					break;
				case DELETE:

					UtilDialog uit = new UtilDialog(TrackListView.this);
					uit.showDialog_route_plan_choice(
							data.getLongExtra("Remove_Result", -1) <= 0 ? getString(R.string.data_delete_fail_msg)
									: getString(R.string.data_delete_success_msg), null,
							getString(R.string.dialog_ok_button_text), null);

					ShowLists();
					break;
				default:
					break;
				}
			}
		} else if (resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}
	}

	/**
	 * Show List Content
	 */
	private void ShowLists() {
		final Cursor curListData = Track.getTrackList(this, searchString);
		final String from[] = new String[] { TrackColumn.NAME.getName(), CursorColumn.ID.get(),
				TrackColumn.START.getName() };
		final int to[] = new int[] { R.id.cursor_row_text, R.id.cursor_row_ref, R.id.cursor_row_foot };

		trackListAdapter = new ListViewAdapter(this, R.layout.cursor_list_row, curListData, from, to,"isTrack");
		trackListAdapter.setIsTrackList(true);
		trackListAdapter.getDataVisibilityStates().put(R.id.cursor_row_ref, false);
		trackListAdapter.setCheckBoxID(R.id.cursor_row_checkbox);
		trackListAdapter.setListMode(listMode.equals(CursorListMode.DELETE) ? ListMode.MULTIPLE : ListMode.SINGLE);

		Log.i("TrackListView", "list item count = " + curListData.getCount());

		curListData.close();
		setListAdapter(trackListAdapter);
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

}
