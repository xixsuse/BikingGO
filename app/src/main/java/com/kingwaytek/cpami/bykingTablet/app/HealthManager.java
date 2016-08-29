package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;

public class HealthManager extends Activity {
	private SeekBar SeekBar_age;
	private SeekBar SeekBar_height;
	private SeekBar SeekBar_weight;
	private TextView ageText;
	private TextView heightText;
	private TextView weightText;
	private RadioGroup Radio_sex;
	private RadioButton Health_Radio_boy;
	private RadioButton Health_Radio_girl;
	private int SeekBar_height_min_vaule = 50; // 設定height_SeekBar之最小值
	private int SeekBar_weight_min_vaule = 20; // 設定weight_SeekBar之最小值
	private int SeekBar_age_min_vaule = 5; // 設定age_SeekBar之最小值

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.health_manager);

		Radio_sex = (RadioGroup) findViewById(R.id.Health_RadioGroup_sex);
		Health_Radio_boy = (RadioButton) findViewById(R.id.Health_Radio_boy);
		Health_Radio_girl = (RadioButton) findViewById(R.id.Health_Radio_girl);

		SeekBar_age = (SeekBar) findViewById(R.id.seekBar_age);
		SeekBar_height = (SeekBar) findViewById(R.id.seekBar_height);
		SeekBar_weight = (SeekBar) findViewById(R.id.seekBar_weight);

		ageText = (TextView) findViewById(R.id.age_dipalyText);
		heightText = (TextView) findViewById(R.id.height_dipalyText);
		weightText = (TextView) findViewById(R.id.weight_dipalyText);

		int age = SettingManager.getUserAge();
		int height = SettingManager.getUserHeight();
		int weight = SettingManager.getUserWeight();

		SeekBar_age.setProgress(age);
		SeekBar_height.setProgress(height);
		SeekBar_weight.setProgress(weight);
		ageText.setText(age + "歲");
		weightText.setText(weight + "公斤");
		heightText.setText(height + "公分");

        switch (SettingManager.getUserSex()) {
            case 1:
                Health_Radio_boy.setChecked(true);
                break;
            case 2:
                Health_Radio_girl.setChecked(true);
                break;
            default:
                break;
        }

		/*****************************************************************************************/
		SeekBar_age.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int IntAge = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
				if (progress < SeekBar_age_min_vaule) {
					SeekBar_age.setProgress(SeekBar_age_min_vaule);
				} else {
					ageText.setText(progress + "歲");
					IntAge = progress;
				}

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				Log.i("HealthManager.java", "on_Start_TrackingTouch");
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
                SettingManager.setUserAge(IntAge);
			}

		});
		/*****************************************************************************************/
		SeekBar_weight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					int IntWeight = 0;

					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
						if (progress < SeekBar_weight_min_vaule)
							SeekBar_weight.setProgress(SeekBar_weight_min_vaule);
                        else {
							weightText.setText(progress + "公斤");
							IntWeight = progress;
						}
					}

					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					public void onStopTrackingTouch(SeekBar seekBar) {
                        SettingManager.setUserWeight(IntWeight);
					}
				});
		/*****************************************************************************************/
		SeekBar_height.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					int IntHeight = 0;

					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
						if (progress < SeekBar_height_min_vaule)
							SeekBar_height.setProgress(SeekBar_height_min_vaule);
                        else {
							heightText.setText(progress + "公分");
							IntHeight = progress;
						}

					}

					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					public void onStopTrackingTouch(SeekBar seekBar) {
                        SettingManager.setUserHeight(IntHeight);
					}
				});

		Radio_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == Health_Radio_boy.getId()) {
                            SettingManager.setUserSex(1);
						}
                        else if (checkedId == Health_Radio_girl.getId()) {
                            SettingManager.setUserSex(2);
						}
					}
				});

	}

	@Override
	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
		((TextView) findViewById(R.id.title_text2)).setText("");
	}
}
