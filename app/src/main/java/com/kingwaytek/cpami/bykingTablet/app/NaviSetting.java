package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.actionsheet.ActionSheet;
import com.example.actionsheet.ActionSheet.ActionSheetButtonClickListener;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.sonavtek.sonav.sonav;

public class NaviSetting extends Activity {

	private RadioGroup mRadioGroup1, mRadioGroup2, mRadioGroup3;
	private RadioButton mRadio1, mRadio2, m2Radio1, m2Radio2, m3Radio1,
			m3Radio2, m3Radio3;
	private TextView mTextView1;
	private NaviSetting instance = this;
	private TextView TextView1, TextView2, TextView3, mapstyle_text;

	private View mapStyle_layout;
	private ActionSheet actionSheet;
	private int[][] sub_view;

	private String[] style = { "","都市叢林", "輕盈和風", "繽紛世界", "粉紅夢幻", "薰衣花園 ", "紅色警戒",
			"深沈藍調", "未眠之城", "時尚夜宴 ", "紫色神秘" ,""};

	private sonav instanceEeego;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navi_setting);

		instanceEeego = sonav.getInstance();

		mapstyle_text = (TextView) findViewById(R.id.mapstyle_text);
		int mapStyle = SettingManager.getMapStyle();
		mapstyle_text.setText(style[mapStyle]);

		mRadioGroup1 = (RadioGroup) findViewById(R.id.road_plan);
		mRadio1 = (RadioButton) findViewById(R.id.best_road);
		mRadio2 = (RadioButton) findViewById(R.id.shortest_road);

		mRadioGroup2 = (RadioGroup) findViewById(R.id.naviset_display_map);
		m2Radio1 = (RadioButton) findViewById(R.id.map_follow_road);
		m2Radio2 = (RadioButton) findViewById(R.id.map_north);

		mRadioGroup3 = (RadioGroup) findViewById(R.id.naviset_voice);
		m3Radio1 = (RadioButton) findViewById(R.id.chiness);
		m3Radio2 = (RadioButton) findViewById(R.id.local_chiness);
		m3Radio3 = (RadioButton) findViewById(R.id.dialect);

		mapStyle_layout = findViewById(R.id.mapstyle_layout);
		setActionSheet();

		mapStyle_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				actionSheet.show();
//				mapStyle_layout.setClickable(false);
			}
		});

		mRadioGroup1.setOnCheckedChangeListener(m1ChangeRadio);
		mRadioGroup2.setOnCheckedChangeListener(m2ChangeRadio);
		mRadioGroup3.setOnCheckedChangeListener(m3ChangeRadio);

		// TextView3.setText("PreferenceActivity.getString="+PreferenceActivity.getString(
		// instance,PreferenceActivity.PREF_SOUND_TYPE,"1"));
        int RoutingMethod = SettingManager.getRoutingMethod();
        int MapViewType = SettingManager.getMapViewType();
		int SoundType = SettingManager.getSoundType();

		displayCurrentState(RoutingMethod, MapViewType, SoundType);

		// TextView1.setText("getRoutingMethod="+RoutingMethod);
		// TextView2.setText("getMapViewType="+MapViewType);
		// TextView3.setText("getSoundType="+SoundType);
	}

	public void setActionSheet() {
		sub_view = new int[12][2];
		sub_view[0][0] = R.id.actionsheet_mapstyle01;//title
		sub_view[1][0] = R.id.actionsheet_mapstyle02;
		sub_view[2][0] = R.id.actionsheet_mapstyle03;
		sub_view[3][0] = R.id.actionsheet_mapstyle04;
		sub_view[4][0] = R.id.actionsheet_mapstyle05;
		sub_view[5][0] = R.id.actionsheet_mapstyle06;
		sub_view[6][0] = R.id.actionsheet_mapstyle07;
		sub_view[7][0] = R.id.actionsheet_mapstyle08;
		sub_view[8][0] = R.id.actionsheet_mapstyle09;
		sub_view[9][0] = R.id.actionsheet_mapstyle10;
		sub_view[10][0] = R.id.actionsheet_mapstyle11;
		sub_view[11][0] = R.id.actionsheet_mapstyle12;//取消

		actionSheet = (ActionSheet) findViewById(R.id.actionSheet_mapstyle);
		actionSheet.setContext(NaviSetting.this);
		actionSheet.setActionSheetLayout(R.layout.action_sheet_mapstyle,
				sub_view);
		actionSheet
				.setOnActionSheetButtonClickListener(new ActionSheetButtonClickListener() {

					@Override
					public void onButtonClick(ActionSheet actionsheet,
							int index, int id) {
						int flag = 0;

						if (index == 0 || index == 11) {

						} else {

							// cuber
                            SettingManager.setMapStyle(index);
							int mapstyle = index;
							if (mapstyle < 6) {
								instanceEeego.setmapstyle(0, mapstyle, 1);
							} else {
								mapstyle -= 5;
								instanceEeego.setmapstyle(1, 0, mapstyle);
							}

							instanceEeego.savenaviparameter();
							mapStyle_layout.setClickable(true);
							mapstyle_text.setText(style[index]);
						}
					}
				});
	}

	@Override
	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
		((TextView) findViewById(R.id.title_text2)).setText("");
	}

	private RadioGroup.OnCheckedChangeListener m1ChangeRadio = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == mRadio1.getId()) {
				/* 把mRadio1的內容傳到mTextView1 */
				// mTextView1.setText(mRadio1.getText());
				Log.i("navisetting.java", mRadio1.getText().toString());

                SettingManager.setRoutingMethod(1);
			}
            else if (checkedId == mRadio2.getId()) {
				/* 把mRadio2的內容傳到mTextView1 */
				// mTextView1.setText(mRadio2.getText());
				Log.i("navisetting.java", mRadio2.getText().toString());

                SettingManager.setRoutingMethod(2);
			}
		}
	};

	private RadioGroup.OnCheckedChangeListener m2ChangeRadio = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == m2Radio1.getId()) {
				/* 把mRadio1的內容傳到mTextView1 */
				// mTextView1.setText(m2Radio1.getText());
				Log.i("navisetting.java", m2Radio1.getText().toString());

                SettingManager.setMapViewType(1);
			}
            else if (checkedId == m2Radio2.getId()) {
				/* 把mRadio2的內容傳到mTextView1 */
				// mTextView1.setText(m2Radio2.getText());
				Log.i("navisetting.java", m2Radio2.getText().toString());

                SettingManager.setMapViewType(2);
			}
		}
	};

	private RadioGroup.OnCheckedChangeListener m3ChangeRadio = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == m3Radio1.getId()) {
				/* 把mRadio1的內容傳到mTextView1 */
				// mTextView1.setText(m3Radio1.getText());
				Log.i("navisetting.java", m3Radio1.getText().toString());
				// PreferenceActivity.setString( instance,
				// PreferenceActivity.PREF_SOUND_TYPE,m3Radio1.getText().toString());
                SettingManager.setSoundType(1);
			}
            else if (checkedId == m3Radio2.getId()) {
				/* 把mRadio2的內容傳到mTextView1 */
				// mTextView1.setText(m3Radio2.getText());
				Log.i("navisetting.java", m3Radio2.getText().toString());
				// PreferenceActivity.setString( instance,
				// PreferenceActivity.PREF_SOUND_TYPE,m3Radio2.getText().toString());
                SettingManager.setSoundType(2);
			}
            else if (checkedId == m3Radio3.getId()) {
				/* 把mRadio2的內容傳到mTextView1 */
				// mTextView1.setText(m3Radio3.getText());
				Log.i("navisetting.java", m3Radio3.getText().toString());
				// PreferenceActivity.setString( instance,
				// PreferenceActivity.PREF_SOUND_TYPE,m3Radio3.getText().toString());
                SettingManager.setSoundType(3);
			}
			instanceEeego.setlangvoice(SettingManager.getSoundType());
		}

	};

    public void displayCurrentState(int Routing, int ViewType, int SoundType) {
        switch (Routing) {
            case 1:
                mRadio1.setChecked(true);
                break;
            case 2:
                mRadio2.setChecked(true);
                break;
            default:
                break;
        }

        switch (ViewType) {
            case 1:
                m2Radio1.setChecked(true);
                break;
            case 2:
                m2Radio2.setChecked(true);
                break;
            default:
                break;
        }

        switch (SoundType) {
            case 1:
                m3Radio1.setChecked(true);
                break;
            case 2:
                m3Radio2.setChecked(true);
                break;
            case 3:
                m3Radio3.setChecked(true);
                break;
            default:
                break;
        }
    }
}
