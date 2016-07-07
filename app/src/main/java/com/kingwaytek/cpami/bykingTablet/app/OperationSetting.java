package com.kingwaytek.cpami.bykingTablet.app;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.CommunicationBaseActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.view.ListSimpleAdapter;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;

public class OperationSetting extends CommunicationBaseActivity {

	private CheckBox mCheckBox1;
	private ListSimpleAdapter listItemAdapter;
	private SeekBar AroundDistance;
	private TextView distance;
	private boolean[] preferenceState = new boolean[6];

	private int SeekBar_POI_min_value = 1000; // 設定POI_SeekBar之最小值
	private ListView list;
	//private static String URL_GCM = "http://biking.cpami.gov.tw/Service/SetPushToken?";
	private boolean savePreferenceSate[] = { false, false, false, false, false, false };
	private View view_footer;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.operation);

		this.initialFooterView();
		int range = SettingManager.getSurroundRange();
		Log.i("OperationSetting.java", "range=" + range);

		AroundDistance.setProgress(range);
		distance.setText(range + "公尺");

        preferenceState[0] = SettingManager.isMeteorologyEnabled();
		preferenceState[1] = SettingManager.isInternetConfirmEnabled();
		preferenceState[2] = SettingManager.isTrackConfirmEnabled();
		preferenceState[3] = SettingManager.isAnnouncementNecessary();
		preferenceState[5] = SettingManager.isPOIEnabled();
		preferenceState[4] = SettingManager.isGCMEnabled();

		for (int i = 0; i < preferenceState.length; i++) {
			if (preferenceState[i]) {
				Log.i("OperationSetting.java", "true");
				savePreferenceSate[i] = true;
			}
            else {
				Log.i("OperationSetting.java", "false");
				savePreferenceSate[i] = false;
			}
		}

		String[] itemTitle = { "連線取得座標及氣象", "網路連線提示", "錄製軌跡提示", "安全及使用聲明", "活動訊息提示", "週邊查詢的距離" };
		String[] itemText = { "設定網路連線取得資訊", "顯示網路連線的提示視窗", "顯示錄製軌跡的提示視窗", "停止使用安全及使用聲明的提示視窗", "", "" };

		ArrayList<HashMap<String, Object>> listitem = new ArrayList<>();

		for (int i = 0; i < itemTitle.length; i++) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("ItemTitle", itemTitle[i]);
			map.put("ItemText", itemText[i]);
			listitem.add(map);
		}

		list = (ListView) findViewById(R.id.list);
		this.addFooterView();

		listItemAdapter = new ListSimpleAdapter(this, listitem, R.layout.listview_style, new String[] { "ItemTitle", "ItemText" },
                new int[] {R.id.topTextView, R.id.bottomTextView });

		for (int i = 0; i < savePreferenceSate.length; i++) {
			listItemAdapter.getCheckBoxData().put(i, savePreferenceSate[i]);
		}

		showPOIAroundDistanceSeekBar();

		list.setAdapter(listItemAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				mCheckBox1 = (CheckBox) view.findViewById(R.id.CheckBox01);

				mCheckBox1.toggle();
				final Boolean is_checked = mCheckBox1.isChecked();

				writeIntoPreferences(position, is_checked);
				listItemAdapter.getCheckBoxData().put(position, is_checked);

				// 是否顯示POI周邊查詢距離的seekbar
				if (position == 5 && is_checked) {
					distance.setVisibility(View.VISIBLE);
					AroundDistance.setVisibility(View.VISIBLE);

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							list.setSelection(listItemAdapter.getCount() - 1);
						}
					}, 100);
					setSeekBarListener();
				}
                else if (position == 5) {
					distance.setVisibility(View.GONE);
					AroundDistance.setVisibility(View.GONE);
				}
                else if (position == 4) {
					// 推播
					mCheckBox1.setChecked(!is_checked);// 回復checkBox狀態

					UtilDialog uit = new UtilDialog(OperationSetting.this) {
						@Override
						public void click_btn_1() {

							super.click_btn_1();
						}
					};
					uit.showDialog_route_plan_choice("推播功能需使用網路,\n您可能需要支付網路費,\n是否要繼續?", null, "確定", "取消");
				}
			}
		});
		Log.i("OperationSetting.java", "onCreate---end");
	}

	@Override
	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
		((TextView) findViewById(R.id.title_text2)).setText("");
	}

    private void setSeekBarListener() {

        AroundDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int Intdistance = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                if (progress < SeekBar_POI_min_value)
                    AroundDistance.setProgress(SeekBar_POI_min_value);
                else {
                    distance.setText(progress + "公尺");
                    Intdistance = progress;
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("OperationSetting.java", "on_Start_TrackingTouch");
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                SettingManager.setSurroundRange(Intdistance);
            }
        });
    }

	private void writeIntoPreferences(int i, Boolean isCheck) {
		switch (i) {
		case 0:
			SettingManager.setMeteorologyEnabled(isCheck);
			break;
		case 1:
			SettingManager.setInternetConfirmEnabled(isCheck);
			break;
		case 2:
			SettingManager.setTrackConfirmEnabled(isCheck);
			break;
		case 3:
			SettingManager.setAnnouncementNecessary(isCheck);
			break;
		case 5:
			SettingManager.setPOIEnabled(isCheck);
			break;
		case 4:
			SettingManager.setGCMEnabled(isCheck);
			break;
		default:
			break;
		}
	}

	private void showPOIAroundDistanceSeekBar() {
		if (preferenceState[5]) {
			distance.setVisibility(View.VISIBLE);
			AroundDistance.setVisibility(View.VISIBLE);
			setSeekBarListener();
		}
	}
/*
	private String generateMD5(int service_ID) {
		Date date = new Date();
		return CreateMD5Code.createMD5((String.valueOf(((date.getMonth() + 1) +
                date.getHours()) * (service_ID + date.getDate())) + "Kingway").getBytes());
	}
*/
	@Override
	public void didFinishWithGetRequest(String requestString, String resultString, Header[] respondHeaders) {
		// TODO Auto-generated method stub
		super.didFinishWithGetRequest(requestString, resultString, respondHeaders);

		if (resultString.equals("1\n")) {
			savePreferenceSate[4] = true;
			listItemAdapter.getCheckBoxData().put(4, savePreferenceSate[4]);
			listItemAdapter.notifyDataSetChanged();
		}
        else if (resultString.equals("0\n")) {
			savePreferenceSate[4] = false;
			listItemAdapter.getCheckBoxData().put(4, savePreferenceSate[4]);
			listItemAdapter.notifyDataSetChanged();
		}
        else if (resultString.contains("switch success")) {
			if (savePreferenceSate[4]) {
				savePreferenceSate[4] = false;
            }
            else if (!savePreferenceSate[5]) {
				savePreferenceSate[4] = true;
			}
			listItemAdapter.getCheckBoxData().put(4, savePreferenceSate[4]);
			listItemAdapter.notifyDataSetChanged();
		}
        else if (requestString.contains("isOpen") && resultString.contains("ERROR")) {
			UtilDialog uit = new UtilDialog(OperationSetting.this);
			uit.showDialog_route_plan_choice("操作失敗", null, "確定", null);
		}
        else if (resultString.contains("null")) {
			UtilDialog uit = new UtilDialog(OperationSetting.this);
			uit.showDialog_route_plan_choice("伺服器異常!", null, "確定", null);
		}
	}

	@Override
	public void didFailWithGetRequest(String requestString, String resultString) {
		// TODO Auto-generated method stub
		super.didFailWithGetRequest(requestString, resultString);

		if (requestString.contains("isOpen")) {
			UtilDialog uit = new UtilDialog(OperationSetting.this);
			uit.showDialog_route_plan_choice("連線失敗", null, "確定", null);
		}
	}

	private void initialFooterView() {
		view_footer = LayoutInflater.from(this).inflate(R.layout.view_operation_setting, null);
		distance = (TextView) view_footer.findViewById(R.id.around_distance_text_kilometer);
		AroundDistance = (SeekBar) view_footer.findViewById(R.id.seekBar_around_distance);
	}

	private void addFooterView() {
		list.addFooterView(view_footer);
	}
}
